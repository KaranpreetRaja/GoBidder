package com.gobidder.bid_service.service;

import com.gobidder.bid_service.dto.AuctionPriceRequest;
import com.gobidder.bid_service.dto.AuctionPriceResponse;
import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.model.AuctionCacheModel;
import com.gobidder.bid_service.repository.AuctionCacheRepository;
import com.gobidder.bid_service.proto.*;
import com.gobidder.bid_service.service.strategy.AuctionStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BidService {
    private final AuctionCacheRepository auctionCacheRepository;
    private final KafkaProducerService kafkaProducerService;
    private final AuctionGrpcClient auctionGrpcClient;
    private final AuctionStrategyFactory auctionStrategyFactory;

    private static final Logger logger = LoggerFactory.getLogger(BidService.class);
    private final TaskScheduler taskScheduler;
    private AuctionService auctionService;
    private RedisTemplate<String, Object> redisTemplate;

    // TODO: Maybe remove this constructor
    public BidService(TaskScheduler taskScheduler, RedisTemplate<String, Object> redisTemplate, AuctionGrpcClient auctionGrpcClient) {
        this.taskScheduler = taskScheduler;
        this.redisTemplate = redisTemplate;
        this.auctionGrpcClient = auctionGrpcClient;
    }


    public BidResponse processBid(BidRequest bidRequest) {
        // Step 1: Get or fetch auction data
        AuctionCacheModel auctionCache = getOrFetchAuctionData(bidRequest.getAuctionId());

        if (!auctionCache.isActive()) {
            return createBidResponse("FAILED", "Auction is not active. The auctionId is: " + auctionCache.getAuctionId() + ", isActive: " + auctionCache.isActive() + ", lastUpdateTimestamp: " + auctionCache.getLastUpdateTimestamp());
        }

        // Step 2: Get appropriate auction strategy
        var strategy = auctionStrategyFactory.getStrategy(auctionCache.getAuctionType());

        // Step 3: Check if bid is possible
        if (!strategy.isBidPossible(auctionCache, bidRequest)) {
            return createBidResponse("FAILED", "Bid is not valid for this auction where \n\nauction data is: auctionId: " + auctionCache.getAuctionId() + ", currentPrice: " + auctionCache.getCurrentPrice() + ", currentWinningBidderId: " + auctionCache.getCurrentWinningBidderId() + ", isActive: " + auctionCache.isActive() + ", lastUpdateTimestamp: " + auctionCache.getLastUpdateTimestamp() + ", totalAuctionBids: " + auctionCache.getTotalAuctionBids() + ", auctionType: " + auctionCache.getAuctionType() + "\n\n And bid information is: userId: " + bidRequest.getUserId() + ", price: " + bidRequest.getPrice() + ", auctionId: " + bidRequest.getAuctionId());
        }

        // Step 4: Process the bid using appropriate strategy
        return strategy.publishBid(auctionCache, bidRequest);
    }

    private BidResponse createBidResponse(String status, String message) {
        BidResponse response = new BidResponse();
        response.setStatus(status);
        response.setMessage(message);
        return response;
    }

    public AuctionCacheModel getOrFetchAuctionData(String auctionId) {
        Optional<AuctionCacheModel> auctionCache = auctionCacheRepository.findById(auctionId);

        // TODO: Uncomment this code after implementing proper dutch auction caching strategy
//        if (auctionCache.isPresent()){
//            return auctionCache.get();
//        }

        // Fetch from auction service using gRPC since not in redis cache
        GetAuctionResponse auctionResponse = auctionGrpcClient.getAuction(auctionId);

        AuctionCacheModel newCache = getAuctionCacheModel(auctionResponse);

        return auctionCacheRepository.save(newCache);
    }

    private static AuctionCacheModel getAuctionCacheModel(GetAuctionResponse auctionResponse) {
        AuctionCacheModel newCache = new AuctionCacheModel();
        newCache.setAuctionId(auctionResponse.getAuctionId());
        newCache.setAuctionType(auctionResponse.getAuctionType());
        newCache.setCurrentPrice(auctionResponse.getCurrentPrice());
        newCache.setActive(auctionResponse.getIsActive());
        newCache.setCurrentWinningBidderId(auctionResponse.getCurrentWinningBidderId());
        newCache.setLastUpdateTimestamp(auctionResponse.getLastUpdateTimestamp());
        newCache.setTotalAuctionBids(auctionResponse.getTotalAuctionBids());
        return newCache;
    }

    public AuctionPriceResponse getAuctionPrice(AuctionPriceRequest auctionPriceRequest) {
        GetAuctionResponse auctionResponse = auctionGrpcClient.getAuction(auctionPriceRequest.getAuctionId());

        AuctionPriceResponse auctionPriceResponse = new AuctionPriceResponse();
        auctionPriceResponse.setAuctionId(auctionResponse.getAuctionId());
        auctionPriceResponse.setPrice(auctionResponse.getCurrentPrice());
        auctionPriceResponse.setWinningUserId(auctionResponse.getCurrentWinningBidderId());
        return auctionPriceResponse;
    }

    // TODO: Change the parameters/fields so they reference the proto buffer
    public void startDutchCountdown(Long auctionId, double priceDecrease, int intervalSeconds) {
        Instant nextDecreaseInstant = LocalDateTime.now()
                .plusSeconds(intervalSeconds)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        this.taskScheduler.schedule(
            () -> {
                try {
                    logger.info("Reducing price of auction id {} by {}",
                    a.getId(), PRICE_DECREASE_AMOUNT);

                    double updatedPrice = auctionGrpcClient.decreasePrice(auctionId, priceDecrease);
                    redisTemplate.opsForValue().set("auction_price_" + auctionId, updatedPrice);
                    logger.info("Updated auction price for ID {} stored in Redis: {}", auctionId, updatedPrice);

                    auctionGrpcClient.performBidUpdate(auctionId, updatedPrice);
                    if (updatedPrice > 0) {
                        startDutchAuctionCountdown(auctionId, priceDecrease, intervalSeconds);
                    }
                    else {
                        logger.info("Dutch auction id {} not active, not scheduling further", auctionId);
                    }
                } catch (Exception e) {
                    logger.error("Error during Dutch auction countdown for ID {}: {}", auctionId, e.getMessage());
                }
            }, nextDecreaseInstant);

            logger.info("Scheduled Dutch auction countdown for ID {} at {}", auctionId, nextDecreaseInstant);
        
        /*
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
        */
    }

}
