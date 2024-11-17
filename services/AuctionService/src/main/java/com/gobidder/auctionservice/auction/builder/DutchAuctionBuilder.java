package com.gobidder.auctionservice.auction.builder;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DutchAuctionBuilder extends AuctionBuilder {
    private Double minimumPrice;

    public AuctionBuilder duration(Integer duration) {
        throw new IllegalStateException(
            "duration supplied to Dutch auction builder: Dutch auctions do not" +
                " have a duration"
        );
    }

    @Override
    public AuctionBuilder minimumPrice(Double minimumPrice) {
        this.minimumPrice = minimumPrice;
        return this;
    }

    @Override
    public Auction build() {
        if (this.minimumPrice == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "minimumPrice is null: cannot build Dutch auction"
            );
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
            AuctionTypeEnum.DUTCH,
            AuctionStatusEnum.NOT_STARTED,
            this.startTime,
            null, // No duration for Dutch auctions
            this.minimumPrice
        );
    }
}
