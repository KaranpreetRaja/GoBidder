package com.gobidder.auctionservice;

import org.springframework.data.jpa.repository.JpaRepository;
public interface AuctionRepository extends JpaRepository<Auction, Long> {
}
