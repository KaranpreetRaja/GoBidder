package com.gobidder.auctionservice.auction.dto;

public class CurrentHighestBidderDto {
    private int userID;
    private int bid;

    public CurrentHighestBidderDto(int userID, int bid) {
        this.userID = userID;
        this.bid = bid;
    }

    public void setUserID (int id) {
        this.userID = id;
    }

    public void setBid (int bid) {
        this.bid = bid;
    }

    public int getUser() {
        return this.userID;
    }

    public int getBid() {
        return this.bid;
    }
}
