package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;

import java.time.LocalDateTime;

public class ForwardAuctionStrategy implements AuctionStrategy {
    private static ForwardAuctionStrategy instance = null;

    private ForwardAuctionStrategy() {
        // Private constructor for singleton
    }

    public static ForwardAuctionStrategy getInstance() {
        if (instance == null) {
            instance = new ForwardAuctionStrategy();
        }
        return instance;
    }

    @Override
    public boolean isEnding(Auction auction) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime auctionEndTime =
            auction.getStartTime().plusSeconds(auction.getDuration());
        return currentTime.isAfter(auctionEndTime);
    }
}
