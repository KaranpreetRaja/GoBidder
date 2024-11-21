package com.gobidder.auctionservice.auction.repository;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuctionJpaRepository extends JpaRepository<Auction, Long> {
    List<Auction> findAuctionsByStatus(AuctionStatusEnum status);
}
