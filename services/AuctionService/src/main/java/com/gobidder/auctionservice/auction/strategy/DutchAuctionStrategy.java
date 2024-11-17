package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;

public class DutchAuctionStrategy implements AuctionStrategy {
    private static DutchAuctionStrategy instance = null;

    private DutchAuctionStrategy() {
        // Private constructor for singleton
    }

    public static DutchAuctionStrategy getInstance() {
        if (instance == null) {
            instance = new DutchAuctionStrategy();
        }
        return instance;
    }

    @Override
    public boolean isEnding(Auction auction) {
        return auction.getCurrentPrice() <= auction.getMinimumPrice();
    }
}
