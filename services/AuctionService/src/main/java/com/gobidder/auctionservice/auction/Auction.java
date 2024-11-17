package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.bidder.Bidder;
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
    private Double initialPrice;
    private Double currentPrice;
    private AuctionTypeEnum type;
    private AuctionStatusEnum status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime priceUpdatedAt;
    @OneToOne
    @JoinColumn(name="bidderId", referencedColumnName = "id")
    private Bidder highestBidder;

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
        Double initialPrice,
        Double currentPrice,
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
        this.initialPrice = initialPrice;
        this.currentPrice = currentPrice;
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priceUpdatedAt = startTime;
    }

    public Auction(
        Long id,
        String name,
        String description,
        String currency,
        String auctionImageUrl,
        Long auctionOwnerId,
        String location,
        Double initialPrice,
        Double currentPrice,
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
        this.initialPrice = initialPrice;
        this.currentPrice = currentPrice;
        this.type = type;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.priceUpdatedAt = startTime;
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

    public Double getInitialPrice() {
        return initialPrice;
    }

    public void setInitialPrice(Double initialPrice) {
        this.initialPrice = initialPrice;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getPriceUpdatedAt() {
        return priceUpdatedAt;
    }

    public void setPriceUpdatedAt(LocalDateTime priceUpdatedAt) {
        this.priceUpdatedAt = priceUpdatedAt;
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

    public Bidder getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(Bidder highestBidder) {
        this.highestBidder = highestBidder;
    }
}
