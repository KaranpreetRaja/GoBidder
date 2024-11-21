package com.gobidder.auctionservice.auction.repository;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.dto.BidUpdateMessage;
import com.gobidder.auctionservice.bidder.Bidder;

import java.time.LocalDateTime;

public interface AuctionRepository {

    Auction create(Auction auction);
    Auction findById(Long auctionId);
    Auction updateStatus(Long auctionId, AuctionStatusEnum status);
    Auction startAuction(Long auctionId, LocalDateTime start);
    Auction updatePrice(Long auctionId, Double price);
    Auction updateHighestBidder(Long auctionId, Bidder bidder);
    Auction updateHighestBidder(Long auctionId, BidUpdateMessage message);

}
