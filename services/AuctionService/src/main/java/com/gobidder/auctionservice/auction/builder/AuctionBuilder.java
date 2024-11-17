package com.gobidder.auctionservice.auction.builder;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;

import java.time.LocalDateTime;

public abstract class AuctionBuilder {

    protected String name;
    protected String description;
    protected String currency;
    protected String auctionImageUrl;
    protected Long auctionOwnerId;
    protected String location;
    protected LocalDateTime startTime;

    public static AuctionBuilder builder(AuctionTypeEnum auctionTypeEnum) {
        if (auctionTypeEnum.equals(AuctionTypeEnum.FORWARD)) {
            return new ForwardAuctionBuilder();
        } else if (auctionTypeEnum.equals(AuctionTypeEnum.DUTCH)) {
            return new DutchAuctionBuilder();
        } else {
            throw new IllegalArgumentException("Unsupported auction type: " + auctionTypeEnum);
        }
    }

    public abstract Auction build();

    public abstract AuctionBuilder endTime(LocalDateTime endTime);

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

}
