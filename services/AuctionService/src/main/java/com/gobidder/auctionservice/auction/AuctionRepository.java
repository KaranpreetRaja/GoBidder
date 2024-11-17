package com.gobidder.auctionservice.auction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findAuctionsByStatus(AuctionStatusEnum status);
}
