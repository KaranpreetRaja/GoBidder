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


    public BidResponse processBid(BidRequest bidRequest) {
        // Step 1: Get or fetch auction data
        AuctionCacheModel auctionCache = getOrFetchAuctionData(bidRequest.getAuctionId());

        if (!auctionCache.isActive()) {
            return createBidResponse("FAILED", "Auction is not active");
        }

        // Step 2: Get appropriate auction strategy
        var strategy = auctionStrategyFactory.getStrategy(auctionCache.getAuctionType());

        // Step 3: Check if bid is possible
        if (!strategy.isBidPossible(auctionCache, bidRequest)) {
            return createBidResponse("FAILED", "Bid is not valid for this auction");
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

        if (auctionCache.isPresent()){
            return auctionCache.get();
        }

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

}
