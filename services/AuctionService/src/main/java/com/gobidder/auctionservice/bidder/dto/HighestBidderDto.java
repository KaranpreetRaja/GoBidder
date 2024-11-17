package com.gobidder.auctionservice.bidder.dto;

public class HighestBidderDto {
    private Long userId;
    private Double bid;

    public HighestBidderDto(Long userId, Double bid) {
        this.userId = userId;
        this.bid = bid;
    }

    public void setUserId(Long id) {
        this.userId = id;
    }

    public void setBid (Double bid) {
        this.bid = bid;
    }

    public Long getUserId() {
        return this.userId;
    }

    public Double getBid() {
        return this.bid;
    }
}
