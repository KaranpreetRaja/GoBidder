package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.auction.builder.AuctionBuilder;
import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
import com.gobidder.auctionservice.auction.dto.CurrentHighestBidderDto;
import com.gobidder.auctionservice.auction.strategy.AuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.factory.AuctionStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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
            auctionBuilder.endTime(auctionCreateRequestDto.getEndTime());
        }

        Auction auction = auctionBuilder
            .name(auctionCreateRequestDto.getName())
            .description(auctionCreateRequestDto.getDescription())
            .currency(auctionCreateRequestDto.getCurrencyType())
            .auctionImageUrl(auctionCreateRequestDto.getAuctionImageUrl())
            .auctionOwnerId(auctionCreateRequestDto.getAuctionOwnerId())
            .location(auctionCreateRequestDto.getLocation())
            .build();

        auction = this.auctionRepository.save(auction);

        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            this.scheduleForwardAuctionEnd(auction);
        } else if (auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            this.scheduleAuctionPriceDecrease(auction);
        }

        return auction;
    }

    public Auction get(Long id) {
        return this.auctionRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Example not found")
        );
    }

    public List<Auction> getAllActiveAuctions() {
        return this.auctionRepository.findAuctionsByStatus(AuctionStatusEnum.ACTIVE);
    }

    public CurrentHighestBidderDto getHighestBidder(Auction auction) {
        return null;
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
        this.taskScheduler.schedule(
            () -> {
                logger.info("Scheduled auction price decrease for auction id {}", auction.getId());

                // Get strategy
                AuctionStrategyFactory factory = AuctionStrategyFactory.getInstance();
                AuctionStrategy strategy = factory.getAuctionStrategy(auction);

                if (auction.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
                    if (strategy.isEnding(auction)) {
                        // End auction
                        this.endAuction(auction);
                    } else {
                        // Decrease price
                        this.decreasePrice(
                            auction,
                            PRICE_DECREASE_AMOUNT
                        );
                        // Reschedule task
                        this.scheduleAuctionPriceDecrease(auction);
                    }
                }
            },
            Date.from(
                LocalDateTime.now()
                    .plusSeconds(PRICE_DECREASE_INTERVAL_SECONDS)
                    .atZone(ZoneId.systemDefault()).toInstant())
                .toInstant()
        );
    }

    public void scheduleForwardAuctionEnd(Auction auction) {
        if (!auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only forward auctions can have scheduled auction end times"
            );
        }
        this.taskScheduler.schedule(
            () -> {
                logger.info("Scheduled auction timeout for auction id {}", auction.getId());

                // Get strategy
                AuctionStrategyFactory factory = AuctionStrategyFactory.getInstance();
                AuctionStrategy strategy = factory.getAuctionStrategy(auction);

                if (auction.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
                    if (strategy.isEnding(auction)) {
                        // End auction
                        this.endAuction(auction);
                    } else {
                        // Reschedule task
                        this.scheduleForwardAuctionEnd(auction);
                    }
                }
            },
            Date.from(
                    auction.getEndTime()
                        .atZone(ZoneId.systemDefault()).toInstant())
                .toInstant()
        );
    }
}
