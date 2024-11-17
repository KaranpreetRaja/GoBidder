package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.auction.builder.AuctionBuilder;
import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
import com.gobidder.auctionservice.auction.strategy.AuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.factory.AuctionStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class AuctionService {
    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);

    private static final Double PRICE_DECREASE_AMOUNT = 1.0;
    private static final Integer PRICE_DECREASE_INTERVAL_SECONDS = 1;

    private final AuctionRepository auctionRepository;
    private final TaskScheduler taskScheduler;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, TaskScheduler taskScheduler) {
        this.auctionRepository = auctionRepository;
        this.taskScheduler = taskScheduler;
    }

    public Auction create(AuctionCreateRequestDto auctionCreateRequestDto) {
        AuctionTypeEnum auctionType = auctionCreateRequestDto.getType();
        AuctionBuilder auctionBuilder = AuctionBuilder.builder(auctionType);

        if (auctionType.equals(AuctionTypeEnum.FORWARD)) {
            auctionBuilder.duration(auctionCreateRequestDto.getDuration());
        } else if (auctionType.equals(AuctionTypeEnum.DUTCH)) {
            auctionBuilder.minimumPrice(auctionCreateRequestDto.getMinimumPrice());
        }

        Auction auction = auctionBuilder
            .name(auctionCreateRequestDto.getName())
            .description(auctionCreateRequestDto.getDescription())
            .currency(auctionCreateRequestDto.getCurrencyType())
            .auctionImageUrl(auctionCreateRequestDto.getAuctionImageUrl())
            .auctionOwnerId(auctionCreateRequestDto.getAuctionOwnerId())
            .location(auctionCreateRequestDto.getLocation())
            .startTime(auctionCreateRequestDto.getStartTime())
            .initialPrice(auctionCreateRequestDto.getInitialPrice())
            .build();

        auction = this.auctionRepository.save(auction);

        return auction;
    }

    public Auction get(Long id) {
        return this.auctionRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Example not found"
            )
        );
    }

    public Auction startAuction(Long auctionId) {
        Auction auction = this.get(auctionId);
        if (auction.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Auction already started");
        }
        if (auction.getStatus().equals(AuctionStatusEnum.CANCELLED)
                || auction.getStatus().equals(AuctionStatusEnum.WON)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Auction already over");
        }
        auction.setStartTime(LocalDateTime.now());
        auction.setStatus(AuctionStatusEnum.ACTIVE);
        auction = this.auctionRepository.save(auction);

        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            this.scheduleForwardAuctionEnd(auction);
        } else if (auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            this.scheduleAuctionPriceDecrease(auction);
        }

        return auction;
    }

    public synchronized void decreasePrice(Auction auction, double priceToDecrease) {
        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot decrease auction price when auction type is forward"
            );
        }
        if (auction.getCurrentPrice() - priceToDecrease < 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot decrease auction price below 0"
            );
        }
        auction.setCurrentPrice(auction.getCurrentPrice() - priceToDecrease);
        this.auctionRepository.save(auction);
    }

    public synchronized void endAuction(Auction auction) {
        auction.setStatus(AuctionStatusEnum.CANCELLED);
        this.auctionRepository.save(auction);
    }

    public void scheduleAuctionPriceDecrease(Auction auction) {
        if (!auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only dutch auctions can have scheduled price decreases"
            );
        }

        Instant auctionPriceDecreaseInstant = LocalDateTime.now()
            .plusSeconds(PRICE_DECREASE_INTERVAL_SECONDS)
            .atZone(ZoneId.systemDefault())
            .toInstant();

        this.taskScheduler.schedule(
            () -> {
                logger.info("Reducing price of auction id {} by {}",
                    auction.getId(), PRICE_DECREASE_AMOUNT);

                // Get strategy
                AuctionStrategyFactory factory = AuctionStrategyFactory.getInstance();
                AuctionStrategy strategy = factory.getAuctionStrategy(auction);

                if (auction.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
                    if (strategy.isEnding(auction)) {
                        logger.info("Ending Dutch auction id {}", auction.getId());
                        // End auction
                        this.endAuction(auction);
                    } else {
                        // Decrease price
                        this.decreasePrice(
                            auction,
                            PRICE_DECREASE_AMOUNT
                        );
                        logger.info("Scheduling another Dutch price decrease for auction id {}",
                            auction.getId());
                        // Reschedule task
                        this.scheduleAuctionPriceDecrease(auction);
                    }
                }
            },
            auctionPriceDecreaseInstant
        );
        logger.info("Scheduled auction price decrease for auction id {} at {}",
            auction.getId(), auctionPriceDecreaseInstant);
    }

    public void scheduleForwardAuctionEnd(Auction auction) {
        if (!auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only forward auctions can have scheduled auction end times"
            );
        }

        Instant auctionEndInstant = LocalDateTime.now()
            .plusSeconds(auction.getDuration())
            .atZone(ZoneId.systemDefault())
            .toInstant();

        this.taskScheduler.schedule(
            () -> {
                logger.info("Preparing to end auction id {} (timeout)", auction.getId());

                // Get strategy
                AuctionStrategyFactory factory = AuctionStrategyFactory.getInstance();
                AuctionStrategy strategy = factory.getAuctionStrategy(auction);

                if (auction.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
                    if (strategy.isEnding(auction)) {
                        logger.info("Ending auction id {} (timeout)", auction.getId());
                        // End auction
                        this.endAuction(auction);
                    } else {
                        logger.info("Auction id {} not ending, rescheduling", auction.getId());
                        // Reschedule task
                        this.scheduleForwardAuctionEnd(auction);
                    }
                }
            },
            auctionEndInstant
        );
        logger.info("Scheduled auction timeout for auction id {} at {}",
            auction.getId(), auctionEndInstant);
    }
}
