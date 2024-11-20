package com.gobidder.auctionservice.auction.builder;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;

import java.time.LocalDateTime;

/**
 * Builder class for {@link Auction}.
 * <p>
 * There's a lot of parameters to build an {@link Auction} object with some
 * custom validation, so this class handles all that.
 */
public abstract class AuctionBuilder {

    protected String name;
    protected String description;
    protected String currency;
    protected String auctionImageUrl;
    protected Long auctionOwnerId;
    protected Double initialPrice;
    protected String location;
    protected LocalDateTime startTime;

    /**
     * Create a builder object for an auction.
     *
     * @param auctionTypeEnum The type of auction being built (forward or Dutch).
     *
     * @return The auction builder.
     */
    public static AuctionBuilder builder(AuctionTypeEnum auctionTypeEnum) {
        if (auctionTypeEnum.equals(AuctionTypeEnum.FORWARD)) {
            return new ForwardAuctionBuilder();
        } else if (auctionTypeEnum.equals(AuctionTypeEnum.DUTCH)) {
            return new DutchAuctionBuilder();
        } else {
            throw new IllegalArgumentException("Unsupported auction type: " + auctionTypeEnum);
        }
    }

    /**
     * Build the auction. Also validates the auction before building.
     *
     * @return The auction being built.
     */
    public abstract Auction build();

    /**
     * Set the duration of the auction. Only works for forward auctions
     *
     * @param duration The duration of the forward auction.
     *
     * @throws IllegalStateException If this is used to build a Dutch auction.
     *
     * @return This builder object so that building can continue.
     */
    public abstract AuctionBuilder duration(Integer duration);

    /**
     * Set the minimum price of the auction. Only works for Dutch auctions
     *
     * @param minimumPrice The duration of the Dutch auction.
     *
     * @throws IllegalStateException If this is used to build a forward auction.
     *
     * @return This builder object so that building can continue.
     */
    public abstract AuctionBuilder minimumPrice(Double minimumPrice);

    public AuctionBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AuctionBuilder description(String description) {
        this.description = description;
        return this;
    }

    public AuctionBuilder currency(String currency) {
        this.currency = currency;
        return this;
    }

    public AuctionBuilder auctionImageUrl(String auctionImageUrl) {
        this.auctionImageUrl = auctionImageUrl;
        return this;
    }

    public AuctionBuilder auctionOwnerId(Long auctionOwnerId) {
        this.auctionOwnerId = auctionOwnerId;
        return this;
    }

    public AuctionBuilder location(String location) {
        this.location = location;
        return this;
    }

    public AuctionBuilder startTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public AuctionBuilder initialPrice(Double initialPrice) {
        this.initialPrice = initialPrice;
        return this;
    }

}
