package com.gobidder.bid_service.model;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@RedishHash("AuctionCache")
public class AuctionCacheModel {
    @Id
    private String auctionId;
    private String auctionType;
    private boolean isActive;
    private Double currentPrice;
    private String currentWinningBidderId;
    private Long lastUpdateTimestamp;
    private int totalAuctionBids;
}
