package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;

import java.time.LocalDateTime;

public class ForwardAuctionStrategy implements AuctionStrategy {
    private static ForwardAuctionStrategy instance = null;

    private ForwardAuctionStrategy() {
        // Private constructor for singleton
    }

    /**
     * Get the forward auction strategy as a singleton, creating the instance if
     * it does not yet exist.
     *
     * @return The singleton forward auction strategy instance.
     */
    public static ForwardAuctionStrategy getInstance() {
        if (instance == null) {
            instance = new ForwardAuctionStrategy();
        }
        return instance;
    }

    @Override
    public boolean isEnding(Auction auction) {
        // A forward auction ends when the current time is after the auction's
        // end time
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime auctionEndTime =
            auction.getStartTime().plusSeconds(auction.getDuration());
        return currentTime.isAfter(auctionEndTime);
    }
}
