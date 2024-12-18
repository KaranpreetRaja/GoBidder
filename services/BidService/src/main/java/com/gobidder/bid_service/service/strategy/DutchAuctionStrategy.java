package com.gobidder.bid_service.service.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.model.AuctionCacheModel;
import com.gobidder.bid_service.repository.AuctionCacheRepository;
import com.gobidder.bid_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DutchAuctionStrategy implements AuctionStrategy {
    private final AuctionCacheRepository auctionCacheRepository;
    private final KafkaProducerService kafkaProducerService;
    private static final Logger logger = LoggerFactory.getLogger(DutchAuctionStrategy.class);

    @Override
    public boolean isBidPossible(AuctionCacheModel auctionModel, BidRequest bidRequest) {
        boolean isPriceEqualOrHigher = auctionModel.getCurrentPrice() - bidRequest.getPrice() <= 0;
        boolean isAuctionActive = auctionModel.isActive();

        // Check if current winner is null or empty string
        String currentWinner = auctionModel.getCurrentWinningBidderId();
        boolean isNoCurrentBidder = currentWinner == null || currentWinner.trim().isEmpty();

        logger.debug("Checking Dutch auction bid validity:");
        logger.debug("Current price: {}, Bid price: {}", auctionModel.getCurrentPrice(), bidRequest.getPrice());
        logger.debug("Price match: {}", isPriceEqualOrHigher);
        logger.debug("Is auction active: {}", isAuctionActive);
        logger.debug("Current winner: '{}', No current bidder: {}", currentWinner, isNoCurrentBidder);

        boolean isValid = isPriceEqualOrHigher && isAuctionActive && isNoCurrentBidder;
        logger.info("Dutch auction bid validity result: {} (price match: {}, active: {}, no winner: {})",
                isValid, isPriceEqualOrHigher, isAuctionActive, isNoCurrentBidder);

        return isValid;
    }

    @Override
    public BidResponse publishBid(AuctionCacheModel auctionModel, BidRequest bidRequest) {
        try {
            logger.info("Processing bid for Dutch auction ID: {}, price: {}, bidder: {}",
                    auctionModel.getAuctionId(), bidRequest.getPrice(), bidRequest.getUserId());

            String currentWinner = auctionModel.getCurrentWinningBidderId();
            if (currentWinner != null && !currentWinner.trim().isEmpty()) {
                logger.error("Auction already has a winning bid from bidder: {}", currentWinner);
                return createErrorResponse("Auction already has a winning bid");
            }

            // Update auction cache with winning bid
            auctionModel.setCurrentWinningBidderId(bidRequest.getUserId());
            auctionModel.setLastUpdateTimestamp(System.currentTimeMillis());
            auctionModel.setTotalAuctionBids(auctionModel.getTotalAuctionBids() + 1);
            auctionModel.setActive(false); // Dutch auction ends with first valid bid

            // Save updated auction cache
            auctionCacheRepository.save(auctionModel);

            // Publish bid update to Kafka
            kafkaProducerService.sendBidUpdate(
                    auctionModel.getAuctionId(),
                    auctionModel.getCurrentPrice(),
                    bidRequest.getUserId(),
                    auctionModel.getLastUpdateTimestamp(),
                    auctionModel.getTotalAuctionBids()
            );

            logger.info("Successfully processed winning bid for Dutch auction ID: {}", auctionModel.getAuctionId());
            return createSuccessResponse();
        } catch (Exception e) {
            logger.error("Failed to process Dutch auction bid: {}", e.getMessage(), e);
            return createErrorResponse("Failed to process bid: " + e.getMessage());
        }
    }

    private BidResponse createSuccessResponse() {
        BidResponse response = new BidResponse();
        response.setStatus("SUCCESS");
        response.setMessage("Bid successfully placed. You have won the Dutch auction!");
        return response;
    }

    private BidResponse createErrorResponse(String message) {
        BidResponse response = new BidResponse();
        response.setStatus("FAILED");
        response.setMessage(message);
        return response;
    }

    // Performs update by copying lines 48-58, publish bid to kafka and redis cache
    public void updateAuctionPrice(AuctionCacheModel auctionModel, double newPrice) {
        try {
            auctionModel.setCurrentPrice(newPrice);
            auctionModel.setLastUpdateTimestamp(System.currentTimeMillis());

            logger.info("Updated auction price for auction ID {} to new price: {}", auctionModel.getAuctionId(), newPrice);

            auctionCacheRepository.save(auctionModel);

            kafkaProducerService.sendBidUpdate(
                    auctionModel.getAuctionId(),
                    auctionModel.getCurrentPrice(),
                    auctionModel.getCurrentWinningBidderId(),
                    auctionModel.getLastUpdateTimestamp(),
                    auctionModel.getTotalAuctionBids()
            );
            logger.info("Published updated auction price to Kafka for auction ID {}", auctionModel.getAuctionId());
        } catch (Exception e) {
            logger.error("Failed to update Dutch auction price for auction ID {}: {}", auctionModel.getAuctionId(), e.getMessage(), e);
        }
    }
}