package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;

/**
 * Strategy object for auction-type-specific behaviors.
 */
public interface AuctionStrategy {
    /**
     * Checks if the auction should be ended.
     *
     * @param auction The auction to check.
     *
     * @return True if the auction should be ended, false otherwise.
     */
    boolean isEnding(Auction auction);
}
