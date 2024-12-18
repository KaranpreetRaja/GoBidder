package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.auction.builder.AuctionBuilder;
import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
import com.gobidder.auctionservice.auction.dto.BidUpdateMessage;
import com.gobidder.auctionservice.auction.repository.AuctionJpaRepository;
import com.gobidder.auctionservice.auction.repository.AuctionRepository;
import com.gobidder.auctionservice.auction.strategy.AuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.factory.AuctionStrategyFactory;
import com.gobidder.auctionservice.grpc.AuctionGrpcClient;
import com.gobidder.auctionservice.bidder.Bidder;
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
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AuctionService {
    private static final Logger logger = LoggerFactory.getLogger(AuctionService.class);

    private static final Double PRICE_DECREASE_AMOUNT = 1.0;
    private static final Integer PRICE_DECREASE_INTERVAL_SECONDS = 1;

    private final AuctionRepository auctionRepository;
    private final TaskScheduler taskScheduler;
    private final AuctionGrpcClient auctionGrpcClient;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository, TaskScheduler taskScheduler, AuctionGrpcClient auctionGrpcClient) {
        this.auctionRepository = auctionRepository;
        this.taskScheduler = taskScheduler;
        this.auctionGrpcClient = auctionGrpcClient;
    }

    /**
     * Creates an auction in the database.
     *
     * @param auctionCreateRequestDto The request body of the auction create
     *                                request.
     *
     * @return The newly created auction.
     */
    public Auction create(AuctionCreateRequestDto auctionCreateRequestDto) {
        AuctionTypeEnum auctionType = auctionCreateRequestDto.getType();
        AuctionBuilder auctionBuilder = AuctionBuilder.builder(auctionType);

        if (auctionType.equals(AuctionTypeEnum.FORWARD)) {
            // Forward auctions have a duration
            auctionBuilder.duration(auctionCreateRequestDto.getDuration());
        } else if (auctionType.equals(AuctionTypeEnum.DUTCH)) {
            // Dutch auctions have a minimum price
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

        auction = this.auctionRepository.create(auction);

        return auction;
    }

    /**
     * Deletes an auction from the database.
     * 
     * @param id The ID of the auction to delete
     */
    public void delete(Long id) {
        // Verify auction exists before deleting
        this.assertAuctionExists(id);
        this.auctionRepository.delete(id);
    }

    /**
     * Get an auction from the database by its ID.
     * <p>
     * This method is synchronized to prevent dirty reads.
     *
     * @param id The ID of the auction to retrieve.
     *
     * @return The auction from the database corresponding to the given ID.
     *
     * @throws ResponseStatusException If the auction with the given ID does not
     *                                 exist in the database.
     */
    public Auction get(Long id) {
        return this.auctionRepository.findById(id);
    }

    /**
     * Get all auctions from the database.
     * 
     * @return List of all auctions
     */
    public List<Auction> getAllAuctions() {
        return this.auctionRepository.findAll();
    }

    /**
     * Checks if an auction exists in the database. If it exists, nothing
     * happens. If it does not exist, an exception is thrown.
     *
     * @param auctionId The ID of the auction to check.
     *
     * @throws ResponseStatusException If the auction with the given ID does not
     *                                 exist in the database.
     */
    public void assertAuctionExists(Long auctionId) {
        this.get(auctionId);
    }

    /**
     * Starts an auction.
     * <p>
     * This method is synchronized to prevent race conditions.
     *
     * @param auctionId The ID of the auction to start.
     *
     * @return The auction from the database after starting.
     */
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
        auction = this.auctionRepository.startAuction(auctionId, LocalDateTime.now());

        // Schedule auction task for the future
        // TODO: Remove as this is the old implementation
//        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
//            this.scheduleForwardAuctionEnd(auction);
//        } else if (auction.getType().equals(AuctionTypeEnum.DUTCH)) {
//            this.scheduleDutchAuctionPriceDecrease(auction);
//        }

        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            this.scheduleForwardAuctionEnd(auction);
        }

        AuctionTypeEnum auctionType = auction.getType();

        // calculate values for the init auction GRPC call
        Long endTimeUnix = null;
        Double dutchAuctionStepSize = null;
        Double dutchAuctionMinimumPrice = null;

        if (auctionType.equals(AuctionTypeEnum.FORWARD)) {
            endTimeUnix = auction.getStartTime()
                    .plusSeconds(auction.getDuration())
                    .toEpochSecond(ZoneOffset.UTC);
        } else if (auctionType.equals(AuctionTypeEnum.DUTCH)) {
            dutchAuctionStepSize = 1.0; // TODO: change to the value stored in database
            dutchAuctionMinimumPrice = auction.getMinimumPrice();
        }

        // Notify bid service via gRPC
        try {
            auctionGrpcClient.initAuction(
                    auction.getId().toString(),
                    auction.getType().toString(),
                    auction.getInitialPrice(),
                    endTimeUnix,
                    dutchAuctionStepSize,
                    dutchAuctionMinimumPrice
            );
            logger.info("Successfully initialized auction {} in bid_service", auction.getId());
        } catch (Exception e) {
            logger.error("Failed to initialize auction {} in bid_service", auction.getId(), e);
        }

        return auction;
    }

    /**
     * Decreases the price of a Dutch auction.
     * <p>
     * This method is synchronized to prevent race conditions.
     *
     * @param auction The auction whose price is being decreased.
     * @param priceToDecrease The amount to decrease the auction's price by.
     *
     * @throws ResponseStatusException If the auction is a forward auction.
     */
    public void decreasePrice(Auction auction, double priceToDecrease) {
        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Cannot decrease auction price when auction type is forward"
            );
        }
        Double currentPrice;
        if (auction.getCurrentPrice() - priceToDecrease < 0) {
            currentPrice = 0.0;
        } else {
            currentPrice = auction.getCurrentPrice() - priceToDecrease;
        }
        this.auctionRepository.updatePrice(auction.getId(), currentPrice);
    }

    /**
     * Update an auction's highest bidder.
     * <p>
     * This method is synchronized to prevent race conditions.
     *
     * @param auction The auction to update.
     * @param bidder The new highest bidder of the auction.
     *
     * @return The auction from the database after updating.
     */
    public Auction updateHighestBidder(Auction auction, Bidder bidder) {
        auction.setCurrentPrice(bidder.getBidderPrice());
        auction.setHighestBidder(bidder.getUserId(), bidder.getBidderPrice());
        return this.auctionRepository.updateHighestBidder(auction.getId(), bidder);
    }

    /**
     * Ends an auction.
     * <p>
     * This method is synchronized to prevent race conditions.
     *
     * @param auction The auction to end.
     */
    public void endAuction(Auction auction) {
        AuctionStatusEnum status;
        if (auction.getHighestBidderId() == null) {
            status = AuctionStatusEnum.CANCELLED;
        } else {
            status = AuctionStatusEnum.WON;
        }
        this.auctionRepository.updateStatus(auction.getId(), status);
    }

    public Auction updateHighestBidder(Long auctionId, BidUpdateMessage message) {
        return this.auctionRepository.updateHighestBidder(auctionId, message);
    }

    /**
     * Schedule's a Dutch auction's price decrease.
     *
     * @param auction The auction to schedule the price decrease of.
     */
    public void scheduleDutchAuctionPriceDecrease(Auction auction) {
        
        if (!auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only dutch auctions can have scheduled price decreases"
            );
        }

        // NOTE: This is the old implementation of the price decreases on the auction service. This functionality has now been moved to the bid service

        Instant auctionPriceDecreaseInstant = LocalDateTime.now()
            .plusSeconds(PRICE_DECREASE_INTERVAL_SECONDS)
            .atZone(ZoneId.systemDefault())
            .toInstant();

        this.taskScheduler.schedule(
            () -> {
                Auction a = this.get(auction.getId());

                logger.info("Reducing price of auction id {} by {}",
                    a.getId(), PRICE_DECREASE_AMOUNT);

                // Get strategy
                AuctionStrategyFactory factory = AuctionStrategyFactory.getInstance();
                AuctionStrategy strategy = factory.getAuctionStrategy(a);

                if (a.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
                    if (strategy.isEnding(a)) {
                        logger.info("Ending Dutch auction id {}", a.getId());
                        // End auction
                        this.endAuction(a);
                    } else {
                        // Decrease price
                        this.decreasePrice(
                            a,
                            PRICE_DECREASE_AMOUNT
                        );
                        logger.info("Scheduling another Dutch price decrease for auction id {}",
                            a.getId());
                        // Reschedule task
                        this.scheduleDutchAuctionPriceDecrease(a);
                    }
                } else {
                    logger.info("Dutch auction id {} not active, not scheduling further",
                        a.getId());
                }
            },
            auctionPriceDecreaseInstant
        );
        logger.info("Scheduled auction price decrease for auction id {} at {}",
            auction.getId(), auctionPriceDecreaseInstant);

    }

    /**
     * Schedule's a forward auction's end.
     *
     * @param auction The auction to schedule the end of.
     */
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
                Auction a = this.get(auction.getId());

                logger.info("Preparing to end auction id {} (timeout)", a.getId());

                // Get strategy
                AuctionStrategyFactory factory = AuctionStrategyFactory.getInstance();
                AuctionStrategy strategy = factory.getAuctionStrategy(a);

                if (a.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
                    if (strategy.isEnding(a)) {
                        logger.info("Ending auction id {} (timeout)", a.getId());
                        // End auction
                        this.endAuction(a);
                    } else {
                        logger.info("Auction id {} not ending, rescheduling", a.getId());
                        // Reschedule task
                        this.scheduleForwardAuctionEnd(a);
                    }
                } else {
                    logger.info("Forward auction id {} not active, not scheduling further",
                        a.getId());
                }
            },
            auctionEndInstant
        );
        logger.info("Scheduled auction timeout for auction id {} at {}",
            auction.getId(), auctionEndInstant);
    }
}
