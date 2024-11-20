package com.gobidder.bid_service.service.strategy;

import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.model.AuctionCacheModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AuctionStrategyFactory{
    private static final AuctionStrategyFactory INSTANCE = new AuctionStrategyFactory();

    private static final Map<String, AuctionStrategy> strategies = new ConcurrentHashMap<String, AuctionStrategy>();

    private AuctionStrategyFactory() {
        strategies.put("Forward", ForwardAuctionStrategy.getInstance());
        strategies.put("Dutch", DutchAuctionStrategy.getInstance());
    }

    public static AuctionStrategyFactory getInstance() {
        return INSTANCE;
    }

    public static AuctionStrategy getStrategy(String strategyName) {
        AuctionStrategy strategy = strategies.get(strategyName.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }
        return strategy;
    }
}
