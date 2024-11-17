package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.dto.CurrentHighestBidderDto;

public interface AuctionStrategy {
    public CurrentHighestBidderDto getHighest();
    public void setHighest(int userID, int bid);
    public boolean isEnding(Auction auction);
}
