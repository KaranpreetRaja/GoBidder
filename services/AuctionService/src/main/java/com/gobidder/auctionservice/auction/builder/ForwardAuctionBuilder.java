package com.gobidder.auctionservice.auction.builder;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

public class ForwardAuctionBuilder extends AuctionBuilder {
    private LocalDateTime endTime;

    @Override
    public AuctionBuilder endTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public Auction build() {
        if (this.endTime == null) {
            throw new IllegalStateException("endTime is null: cannot build forward auction");
        }
        return new Auction(
            this.name,
            this.description,
            this.currency,
            this.auctionImageUrl,
            this.auctionOwnerId,
            this.location,
            this.initialPrice,
            this.initialPrice,
            AuctionTypeEnum.FORWARD,
            AuctionStatusEnum.NOT_STARTED,
            this.startTime,
            this.endTime
        );
    }
}
