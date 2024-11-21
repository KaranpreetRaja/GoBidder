package com.gobidder.bid_service.service.strategy;

import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.model.AuctionCacheModel;
import com.gobidder.bid_service.repository.AuctionCacheRepository;
import com.gobidder.bid_service.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ForwardAuctionStrategy implements AuctionStrategy {
    private final AuctionCacheRepository auctionCacheRepository;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public boolean isBidPossible(AuctionCacheModel auctionModel, BidRequest bidRequest) {
        return bidRequest.getPrice() > auctionModel.getCurrentPrice() &&
                !bidRequest.getUserId().equals(auctionModel.getCurrentWinningBidderId()) &&
                auctionModel.isActive();
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