package com.gobidder.auctionservice.bidder.dto;

import jakarta.validation.constraints.NotBlank;

public class HighestBidderDto {
    @NotBlank(message = "Highest bidder userId must not be empty")
    private Long userId;
    @NotBlank(message = "Highest bidder bid must not be empty")
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