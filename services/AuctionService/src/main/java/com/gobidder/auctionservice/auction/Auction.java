package com.gobidder.auctionservice.auction;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String currency;
    private String auctionImageUrl;
    private Long auctionOwnerId;
    private String location;
    private AuctionTypeEnum type;
    private AuctionStatusEnum status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Auction() {
        // Empty constructor for Spring Data JPA
    }

    public Auction(
        String name,
        String description,
        String currency,
        String auctionImageUrl,
        Long auctionOwnerId,
        String location,
        AuctionTypeEnum type,
        AuctionStatusEnum status,
        LocalDateTime startTime,
        LocalDateTime endTime
    ) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.auctionImageUrl = auctionImageUrl;
        this.auctionOwnerId = auctionOwnerId;
        this.location = location;
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Auction(
        Long id,
        String name,
        String description,
        String currency,
        String auctionImageUrl,
        Long auctionOwnerId,
        String location,
        AuctionTypeEnum type,
        AuctionStatusEnum status,
        LocalDateTime startTime,
        LocalDateTime endTime
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.auctionImageUrl = auctionImageUrl;
        this.auctionOwnerId = auctionOwnerId;
        this.location = location;
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AuctionTypeEnum getType() {
        return type;
    }

    public void setType(AuctionTypeEnum type) {
        this.type = type;
    }

    public AuctionStatusEnum getStatus() {
        return status;
    }

    public void setStatus(AuctionStatusEnum status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAuctionImageUrl() {
        return auctionImageUrl;
    }

    public void setAuctionImageUrl(String auctionImageUrl) {
        this.auctionImageUrl = auctionImageUrl;
    }

    public Long getAuctionOwnerId() {
        return auctionOwnerId;
    }

    public void setAuctionOwnerId(Long auctionOwnerId) {
        this.auctionOwnerId = auctionOwnerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
