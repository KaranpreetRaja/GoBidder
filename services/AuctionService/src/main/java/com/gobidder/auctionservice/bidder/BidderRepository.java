package com.gobidder.auctionservice.bidder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BidderRepository extends JpaRepository<Bidder, Long> {
    Optional<Bidder> findByAuctionId(Long auctionid);
}
