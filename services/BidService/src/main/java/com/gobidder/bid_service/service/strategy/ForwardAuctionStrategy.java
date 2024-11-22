package com.gobidder.bid_service.service.strategy;

import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.model.AuctionCacheModel;
import com.gobidder.bid_service.repository.AuctionCacheRepository;
import com.gobidder.bid_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Objects;

@RequiredArgsConstructor
public class ForwardAuctionStrategy implements AuctionStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ForwardAuctionStrategy.class);
    private final AuctionCacheRepository auctionCacheRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public boolean isBidPossible(AuctionCacheModel auctionModel, BidRequest bidRequest) {
        boolean isPriceHigher = bidRequest.getPrice() > auctionModel.getCurrentPrice();
        boolean isNotCurrentBidder = !Objects.equals(bidRequest.getUserId(), auctionModel.getCurrentWinningBidderId());
        boolean isAuctionActive = auctionModel.isActive();

        logger.debug("bidRequest.getUserId(): {}", bidRequest.getUserId());
        logger.debug("auctionModel.getCurrentWinningBidderId(): {}", auctionModel.getCurrentWinningBidderId());
        logger.debug("isPriceHigher: {}", isPriceHigher);
        logger.debug("isNotCurrentBidder: {}", isNotCurrentBidder);
        logger.debug("isAuctionActive: {}", isAuctionActive);

        return isPriceHigher && isNotCurrentBidder && isAuctionActive;
    }

    @Override
    public BidResponse publishBid(AuctionCacheModel auctionModel, BidRequest bidRequest) {
        try {
            // Update auction cache with new bid
            auctionModel.setCurrentPrice(bidRequest.getPrice());
            auctionModel.setCurrentWinningBidderId(bidRequest.getUserId());
            auctionModel.setLastUpdateTimestamp(System.currentTimeMillis());
            auctionModel.setTotalAuctionBids(auctionModel.getTotalAuctionBids() + 1);

            // Save updated auction cache
            auctionCacheRepository.save(auctionModel);

            // Publish bid update to Kafka
            kafkaProducerService.sendBidUpdate(
                    auctionModel.getAuctionId(),
                    bidRequest.getPrice(),
                    bidRequest.getUserId(),
                    auctionModel.getLastUpdateTimestamp(),
                    auctionModel.getTotalAuctionBids()
            );

            return createSuccessResponse();
        } catch (Exception e) {
            return createErrorResponse("Failed to process bid: " + e.getMessage());
        }
    }

    private BidResponse createSuccessResponse() {
        BidResponse response = new BidResponse();
        response.setStatus("SUCCESS");
        response.setMessage("Bid successfully placed");
        return response;
    }

    private BidResponse createErrorResponse(String message) {
        BidResponse response = new BidResponse();
        response.setStatus("FAILED");
        response.setMessage(message);
        return response;
    }
}