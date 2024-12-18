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
    private final RedisTemplate<String, Object> redisTemplate;



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

        // Use cached data if available
        if (auctionCache.isPresent()) {
            return auctionCache.get();
        }

        // Only fetch from auction service if not in cache
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

    // Modified initAuction handling in AuctionGrpcServer
    public void handleInitAuction(String auctionId, String auctionType,
                                  double startingPrice, Long endTimeUnix,
                                  Double dutchAuctionStepSize, Double dutchAuctionMinimumPrice) {

        // Save initial auction state
        AuctionCacheModel auctionCache = new AuctionCacheModel();
        auctionCache.setAuctionId(auctionId);
        auctionCache.setAuctionType(auctionType);
        auctionCache.setCurrentPrice(startingPrice);
        auctionCache.setActive(true);
        auctionCache.setLastUpdateTimestamp(System.currentTimeMillis());
        auctionCache.setTotalAuctionBids(0);
        auctionCache.setCurrentWinningBidderId("");

        auctionCacheRepository.save(auctionCache);

        // Update Redis cache
        redisTemplate.opsForValue().set("auction_" + auctionId, auctionCache);
        redisTemplate.opsForValue().set("auction_price_" + auctionId, startingPrice);

        // Schedule appropriate end handling based on auction type
        if ("DUTCH".equalsIgnoreCase(auctionType)) {
            logger.info("Starting Dutch auction countdown for auction ID: {}", auctionId);
            startDutchCountdown(
                    auctionId,
                    startingPrice,
                    dutchAuctionStepSize,
                    dutchAuctionMinimumPrice,
                    10 // default interval
            );
        } else if ("FORWARD".equalsIgnoreCase(auctionType) && endTimeUnix != null) {
            logger.info("Scheduling Forward auction end for auction ID: {}", auctionId);
            scheduleForwardAuctionEnd(auctionId, endTimeUnix);
        }
    }

    // Start of new forward auction handling
    public void scheduleForwardAuctionEnd(String auctionId, long endTimeUnix) {
        logger.info("Scheduling forward auction end for auction ID {} at Unix time {}",
                auctionId, endTimeUnix);

        // unix timestamp needs to be converted to Instant
        Instant endInstant = Instant.ofEpochSecond(endTimeUnix);

        this.taskScheduler.schedule(() -> {
            try {
                AuctionCacheModel auction = getOrFetchAuctionData(auctionId);

                if (!auction.isActive()) {
                    logger.info("Forward auction {} is no longer active, not proceeding with end schedule",
                            auctionId);
                    return;
                }

                // Check if current time is after end time
                if (Instant.now().isAfter(endInstant)) {
                    logger.info("Ending forward auction {} as scheduled time has been reached",
                            auctionId);

                    // Mark auction as inactive
                    auction.setActive(false);
                    auction.setLastUpdateTimestamp(System.currentTimeMillis());
                    auctionCacheRepository.save(auction);

                    // Notify via Kafka about auction end
                    kafkaProducerService.sendBidUpdate(
                            auctionId,
                            auction.getCurrentPrice(),
                            auction.getCurrentWinningBidderId(),
                            auction.getLastUpdateTimestamp(),
                            auction.getTotalAuctionBids()
                    );
                } else {
                    // If not yet time to end, reschedule check
                    logger.info("Forward auction {} not yet ending, rescheduling check",
                            auctionId);
                    scheduleForwardAuctionEnd(auctionId, endTimeUnix);
                }
            } catch (Exception e) {
                logger.error("Error during forward auction end check for ID {}: {}",
                        auctionId, e.getMessage(), e);
            }
        }, endInstant);
    }

    public void startDutchCountdown(String auctionId, double startingPrice, double stepSize, double minimumPrice, int intervalSeconds) {
        logger.info("Start Dutch auction countdown for auction ID {} with starting price {}, step size {}, min price {}, interval {}s",
                auctionId, startingPrice, stepSize, minimumPrice, intervalSeconds);

        Instant nextDecreaseInstant = LocalDateTime.now()
                .plusSeconds(intervalSeconds)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        this.taskScheduler.schedule(() -> {
            try {
                // Use cached data to ensure we're working with our last stored price
                AuctionCacheModel auction = getOrFetchAuctionData(auctionId);

                if (!auction.isActive()) {
                    logger.info("Dutch auction {} is no longer active, stopping price decreases", auctionId);
                    return;
                }

                // Calculate new price from the current cached price
                double currentPrice = auction.getCurrentPrice();
                double newPrice = currentPrice - stepSize;

                logger.info("Dutch auction {} decreasing price from {} to {}",
                        auctionId, currentPrice, newPrice);

                if (newPrice < minimumPrice) {
                    logger.info("Dutch auction {} reached min price ({}) as current price is {}, ending auction",
                            auctionId, minimumPrice, newPrice);
                    auction.setActive(false);
                    auction.setCurrentPrice(minimumPrice);  // Set to minimum price instead of going below
                    auction.setLastUpdateTimestamp(System.currentTimeMillis());
                    auctionCacheRepository.save(auction);

                    // Send final price update
                    kafkaProducerService.sendBidUpdate(
                            auctionId,
                            minimumPrice,
                            auction.getCurrentWinningBidderId(),
                            System.currentTimeMillis(),
                            auction.getTotalAuctionBids()
                    );
                    return;
                }

                // Update auction model with new price
                auction.setCurrentPrice(newPrice);
                auction.setLastUpdateTimestamp(System.currentTimeMillis());
                auctionCacheRepository.save(auction);

                // send kafka bid update before scheduling next decrease
                kafkaProducerService.sendBidUpdate(
                        auctionId,
                        newPrice,
                        auction.getCurrentWinningBidderId(),
                        auction.getLastUpdateTimestamp(),
                        auction.getTotalAuctionBids()
                );

                logger.info("Updated Dutch auction {} price to {}", auctionId, newPrice);

                // Schedule next price decrease
                startDutchCountdown(auctionId, newPrice, stepSize, minimumPrice, intervalSeconds);

            } catch (Exception e) {
                logger.error("Error during Dutch auction countdown for ID {}: {}", auctionId, e.getMessage());
            }
        }, nextDecreaseInstant);

        logger.info("Scheduled Dutch auction countdown for ID {} at {}", auctionId, nextDecreaseInstant);
    }
}
