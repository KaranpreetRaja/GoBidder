package com.gobidder.auctionservice.auction.dto;

import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class AuctionCreateRequestDto {
    @NotBlank(message = "Auction name cannot be empty")
    private String name;
    @NotBlank(message = "Auction description cannot be empty")
    private String description;
    private List<String> tags;
    private String currencyType;
    @NotNull(message = "Auction type cannot be empty")
    private AuctionTypeEnum type;
    private String auctionImageUrl;
    @NotNull(message = "Auction must have an owner")
    private Long auctionOwnerId;
    @NotNull(message = "Auction must have an initial price")
    private Double initialPrice;
    private String location;
    @NotNull(message = "Auction must have a start time")
    private LocalDateTime startTime;
    private Integer duration;
    private Double minimumPrice;

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
        Double initialPrice,
        String location,
        LocalDateTime startTime,
        Integer duration,
        Double minimumPrice
    ) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.currencyType = currencyType;
        this.type = type;
        this.auctionImageUrl = auctionImageUrl;
        this.auctionOwnerId = auctionOwnerId;
        this.initialPrice = initialPrice;
        this.location = location;
        this.startTime = startTime;
        this.duration = duration;
        this.minimumPrice = minimumPrice;
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

    public Double getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(Double initialPrice) {
        this.initialPrice = initialPrice;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Double getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(Double minimumPrice) {
        this.minimumPrice = minimumPrice;
    }
}
