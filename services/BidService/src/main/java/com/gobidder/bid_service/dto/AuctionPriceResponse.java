package com.gobidder.bid_service.dto;

import lombok.Data;

@Data
public class AuctionPriceResponse {
    private String auctionId;
    private Double price;
    private String winningUserId;
}
