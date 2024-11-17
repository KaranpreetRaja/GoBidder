package com.gobidder.auctionservice.auction.builder;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;

import java.time.LocalDateTime;

public class DutchAuctionBuilder extends AuctionBuilder {
    public AuctionBuilder endTime(LocalDateTime endTime) {
        throw new IllegalStateException(
            "endTime supplied to Dutch auction builder: Dutch auctions do not" +
                " have an end time"
        );
    }

    @Override
    public Auction build() {
        return new Auction(
            this.name,
            this.description,
            this.currency,
            this.auctionImageUrl,
            this.auctionOwnerId,
            this.location,
            this.initialPrice,
            this.initialPrice,
            AuctionTypeEnum.DUTCH,
            AuctionStatusEnum.NOT_STARTED,
            this.startTime,
            null // No end time for Dutch auctions
        );
    }
}
