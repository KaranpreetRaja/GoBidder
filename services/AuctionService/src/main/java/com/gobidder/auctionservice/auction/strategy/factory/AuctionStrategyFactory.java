package com.gobidder.auctionservice.auction.strategy.factory;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import com.gobidder.auctionservice.auction.strategy.AuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.DutchAuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.ForwardAuctionStrategy;

public class AuctionStrategyFactory {
    private static AuctionStrategyFactory instance = null;

    private AuctionStrategyFactory() {
        // Private constructor for singleton
    }

    public static AuctionStrategyFactory getInstance() {
        if (instance == null) {
            instance = new AuctionStrategyFactory();
        }
        return instance;
    }

    public AuctionStrategy getAuctionStrategy(Auction auction) {
        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            return ForwardAuctionStrategy.getInstance();
        } else if (auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            return DutchAuctionStrategy.getInstance();
        } else {
            throw new IllegalArgumentException("Unknown auction type: " + auction.getType());
        }
    }

}
