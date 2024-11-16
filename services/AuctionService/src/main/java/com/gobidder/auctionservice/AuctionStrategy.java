package com.gobidder.auctionservice;

public interface AuctionStrategy {
    public currentHighest getHighest();
    public void setHighest(int userID, int bid);
}
