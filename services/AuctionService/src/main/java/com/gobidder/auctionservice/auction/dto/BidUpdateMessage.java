package com.gobidder.auctionservice.auction.dto;
import lombok.Data;

@Data
public class BidUpdateMessage {
    private String auctionId;
    private Double newAmount;
    private String bidderId;
    private Long timestamp;
    private int totalAuctionBids;
}
