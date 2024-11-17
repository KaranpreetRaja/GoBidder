package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;

public interface AuctionStrategy {
    boolean isEnding(Auction auction);
}
