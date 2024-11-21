package com.gobidder.bid_service.service.strategy;

import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.model.AuctionCacheModel;

public interface AuctionStrategy {
    boolean isBidPossible(AuctionCacheModel auctionModel, BidRequest bidRequest);

    BidResponse publishBid(AuctionCacheModel auctionModel, BidRequest bidRequest);
}
