package com.gobidder.bid_service.model;

import org.springframework.data.annotation.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash("AuctionCache")
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
