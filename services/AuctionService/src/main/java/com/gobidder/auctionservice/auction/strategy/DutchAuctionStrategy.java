package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;

/**
 * Strategy object for Dutch auction-specific behaviors.
 */
public class DutchAuctionStrategy implements AuctionStrategy {
    private static DutchAuctionStrategy instance = null;

    private DutchAuctionStrategy() {
        // Private constructor for singleton
    }

    /**
     * Get the Dutch auction strategy as a singleton, creating the instance if
     * it does not yet exist.
     *
     * @return The singleton Dutch auction strategy instance.
     */
    public static DutchAuctionStrategy getInstance() {
        if (instance == null) {
            instance = new DutchAuctionStrategy();
        }
        return instance;
    }

    @Override
    public boolean isEnding(Auction auction) {
        // A Dutch auction ends when its price falls below its minimum price
        // threshold
        return auction.getCurrentPrice() <= auction.getMinimumPrice();
    }
}
