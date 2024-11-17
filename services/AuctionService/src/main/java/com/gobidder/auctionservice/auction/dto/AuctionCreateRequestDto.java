package com.gobidder.auctionservice.auction.dto;

import com.gobidder.auctionservice.auction.AuctionTypeEnum;

import java.time.LocalDateTime;
import java.util.List;

public class AuctionCreateRequestDto {
    private String name;
    private String description;
    private List<String> tags;
    private String currencyType;
    private AuctionTypeEnum type;
    private String auctionImageUrl;
    private Long auctionOwnerId;
    private String location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AuctionCreateRequestDto() {
        // Empty constructor for Spring Boot serializing/deserializing into JSON
    }

    public AuctionCreateRequestDto(
        String name,
        String description,
        List<String> tags,
        String currencyType,
        AuctionTypeEnum type,
        String auctionImageUrl,
        Long auctionOwnerId,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime
    ) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.currencyType = currencyType;
        this.type = type;
        this.auctionImageUrl = auctionImageUrl;
        this.auctionOwnerId = auctionOwnerId;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public AuctionTypeEnum getType() {
        return type;
    }

    public void setType(AuctionTypeEnum type) {
        this.type = type;
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

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
