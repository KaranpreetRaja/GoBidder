package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.dto.CurrentHighestBidderDto;

public class DutchAuctionStrategy implements AuctionStrategy {
    private static final Double STEP_AMOUNT = 10.0;

    private static DutchAuctionStrategy instance = null;

    private CurrentHighestBidderDto currentWinner;
    private int stepAmount;
    private int lowestPrice;

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
    public CurrentHighestBidderDto getHighest() {
        return currentWinner;
    }

    @Override
    public void setHighest(int userID, int bid) {
        currentWinner.setUserID(userID);
        currentWinner.setBid(bid);
    }

    @Override
    public boolean isEnding(Auction auction) {
        return auction.getCurrentPrice() <= auction.getMinimumPrice();
    }

    public void lowerPrice() {
        int newPrice = currentWinner.getBid() - this.stepAmount;
        if (newPrice <= lowestPrice) {
            newPrice = this.lowestPrice;
        }
        currentWinner.setBid(newPrice);
    }
}
