package com.gobidder.auctionservice.auction.builder;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Builds a forward auction.
 */
public class ForwardAuctionBuilder extends AuctionBuilder {
    private Integer duration;

    @Override
    public AuctionBuilder duration(Integer duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public AuctionBuilder minimumPrice(Double minimumPrice) {
        throw new IllegalStateException(
            "minimumPrice supplied to forward auction builder: forward " +
                "auctions do not have a minimumPrice"
        );
    }

    @Override
    public Auction build() {
        // Duration required for forward auctions
        if (this.duration == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "duration is null: cannot build forward auction"
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
            AuctionTypeEnum.FORWARD,
            AuctionStatusEnum.NOT_STARTED,
            this.startTime,
            this.duration,
            null // No minimum price for forward auctions
        );
    }
}
