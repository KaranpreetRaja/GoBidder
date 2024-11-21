package com.gobidder.auctionservice.bidder;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.gobidder.auctionservice.auction.Auction;
import jakarta.persistence.*;

@Entity
public class Bidder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long userId;
    private Double bidderPrice;
    @OneToOne(mappedBy = "highestBidder", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Auction auction;

    public Bidder() {
        // Empty constructor for Spring Data JPA
    }

    public Bidder(Long id, Long userId, Double bidderPrice) {
        this.id = id;
        this.userId = userId;
        this.bidderPrice = bidderPrice;
    }

    public Bidder(Long userId, Double bidderPrice) {
        this.userId = userId;
        this.bidderPrice = bidderPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long bidderId) {
        this.userId = bidderId;
    }

    public Double getBidderPrice() {
        return bidderPrice;
    }

    public void setBidderPrice(Double bidderPrice) {
        this.bidderPrice = bidderPrice;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }
}
