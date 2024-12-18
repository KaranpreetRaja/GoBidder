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
import org.springframework.scheduling.TaskScheduler;
import org.springframework.data.redis.core.RedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.Instant;
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
    private RedisTemplate<String, Object> redisTemplate;



    public BidResponse processBid(BidRequest bidRequest) {
        // Step 1: Get or fetch auction data
        AuctionCacheModel auctionCache = getOrFetchAuctionData(bidRequest.getAuctionId());

        logger.info("Processing bid for auction ID: {}", bidRequest.getAuctionId());

        if (!auctionCache.isActive()) {
            logger.info("Auction is not active. The auctionId is: {}, isActive: {}, lastUpdateTimestamp: {}", auctionCache.getAuctionId(), auctionCache.isActive(), auctionCache.getLastUpdateTimestamp());
            return createBidResponse("FAILED", "Auction is not active. The auctionId is: " + auctionCache.getAuctionId() + ", isActive: " + auctionCache.isActive() + ", lastUpdateTimestamp: " + auctionCache.getLastUpdateTimestamp());
        }

        // Step 2: Get appropriate auction strategy
        var strategy = auctionStrategyFactory.getStrategy(auctionCache.getAuctionType());

        // Step 3: Check if bid is possible
        if (!strategy.isBidPossible(auctionCache, bidRequest)) {
            logger.info("Bid is not valid for this auction where auction data is: auctionId: {}, currentPrice: {}, currentWinningBidderId: {}, isActive: {}, lastUpdateTimestamp: {}, totalAuctionBids: {}, auctionType: {} And bid information is: userId: {}, price: {}, auctionId: {}", auctionCache.getAuctionId(), auctionCache.getCurrentPrice(), auctionCache.getCurrentWinningBidderId(), auctionCache.isActive(), auctionCache.getLastUpdateTimestamp(), auctionCache.getTotalAuctionBids(), auctionCache.getAuctionType(), bidRequest.getUserId(), bidRequest.getPrice(), bidRequest.getAuctionId());
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
    public void startDutchCountdown(String auctionId, double currentPrice, double stepSize, double minimumPrice, int intervalSeconds) {
        logger.info("Start Dutch auction countdown for auction ID {} with current price {}, step size {}, min price {}, interval {}s", auctionId, currentPrice, stepSize, minimumPrice, intervalSeconds);

        Instant nextDecreaseInstant = LocalDateTime.now()
                .plusSeconds(intervalSeconds)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        this.taskScheduler.schedule(() -> {
                try {
                    // gets current auction state from redis cache
                    AuctionCacheModel auction = getOrFetchAuctionData(auctionId);

                    if (!auction.isActive()) {
                        logger.info("Dutch auction {} is no longer active, stopping price decreases", auctionId);
                        return;
                    }

                    // calculates new price
                    double newPrice = auction.getCurrentPrice() - stepSize;

                    if (newPrice < minimumPrice) {
                        logger.info("Dutch auction {} reached min price ({}) as current price is {}, ending auction" , auctionId, minimumPrice, newPrice);
                        auction.setActive(false);
                        auctionCacheRepository.save(auction);
                        return;
                    }

                    // update auction model with new price
                    auction.setCurrentPrice(newPrice);
                    auction.setLastUpdateTimestamp(System.currentTimeMillis());
                    auctionCacheRepository.save(auction);

                    // update redis cache
                    redisTemplate.opsForValue().set("auction_price_" + auctionId, newPrice);

                    // send kafka bid update
                    kafkaProducerService.sendBidUpdate(
                            auctionId,
                            newPrice,
                            auction.getCurrentWinningBidderId(),
                            auction.getLastUpdateTimestamp(),
                            auction.getTotalAuctionBids()
                    );

                    logger.info("Updated Dutch auction {} price to {}", auctionId, newPrice);

                    // schedule next price decrease
                    startDutchCountdown(auctionId, newPrice, stepSize, minimumPrice, intervalSeconds);
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
