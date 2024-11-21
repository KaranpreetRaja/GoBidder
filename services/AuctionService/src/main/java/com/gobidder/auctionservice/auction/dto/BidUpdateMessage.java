package com.gobidder.auctionservice.auction.dto;

public class BidUpdateMessage {
    private String auctionId;
    private Double newAmount;
    private String bidderId;
    private Long timestamp;
    private int totalAuctionBids;

    public BidUpdateMessage(String auctionId, Double newAmount, String bidderId, Long timestamp, int totalAuctionBids) {
        this.auctionId = auctionId;
        this.newAmount = newAmount;
        this.bidderId = bidderId;
        this.timestamp = timestamp;
        this.totalAuctionBids = totalAuctionBids;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public Double getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(Double newAmount) {
        this.newAmount = newAmount;
    }

    public String getBidderId() {
        return bidderId;
    }

    public void setBidderId(String bidderId) {
        this.bidderId = bidderId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public int getTotalAuctionBids() {
        return totalAuctionBids;
    }

    public void setTotalAuctionBids(int totalAuctionBids) {
        this.totalAuctionBids = totalAuctionBids;
    }
}
