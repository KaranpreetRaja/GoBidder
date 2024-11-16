package com.gobidder.auctionservice;

public class DutchAuction implements AuctionStrategy {
    private currentHighest currentWinner;
    private int stepAmount;
    private int lowestPrice;

    @Override
    public currentHighest getHighest() {
        return currentWinner;
    }

    @Override
    public void setHighest(int userID, int bid) {
        currentWinner.setUserID(userID);
        currentWinner.setBid(bid);
    }

    public void lowerPrice() {
        int newPrice = currentWinner.getBid() - this.stepAmount;
        if (newPrice <= lowestPrice) {
            newPrice = this.lowestPrice;
        }
        currentWinner.setBid(newPrice);
    }
}
