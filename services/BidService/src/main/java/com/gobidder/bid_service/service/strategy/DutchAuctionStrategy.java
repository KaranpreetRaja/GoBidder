package com.gobidder.bid_service.service.strategy;

import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.model.AuctionCacheModel;

public class DutchAuctionStrategy implements AuctionStrategy {
    // Private Constructor
    private DutchAuctionStrategy() {
    }

    // Singleton Implementation
    private static final DutchAuctionStrategy INSTANCE = new DutchAuctionStrategy();
    public static DutchAuctionStrategy getInstance() {
        return INSTANCE;
    }

    // Implement interface
    @Override
    public boolean isBidPossible(AuctionCacheModel auctionModel, BidRequest bidRequest) {
        return auctionModel.isActive();
    }

    @Override
    public BidResponse publishBid(AuctionCacheModel auctionModel, BidRequest bidRequest) {

    }




}
