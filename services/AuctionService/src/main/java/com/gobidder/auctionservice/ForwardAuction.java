package com.gobidder.auctionservice;

public class ForwardAuction implements AuctionStrategy {
    private currentHighest currentWinner;

    @Override
    public currentHighest getHighest() {
        return currentWinner;
    }

    @Override
    public void setHighest(int userID, int bid) {
        // Update winning bid so far
        if (currentWinner.getBid() < bid) {
            currentWinner.setUserID(userID);
            currentWinner.setBid(bid);
        }
        // Bid is less than the current winner
        else {

        }
    }
}
