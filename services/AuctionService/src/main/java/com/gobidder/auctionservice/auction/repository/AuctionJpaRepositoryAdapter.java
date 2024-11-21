package com.gobidder.auctionservice.auction.repository;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.dto.BidUpdateMessage;
import com.gobidder.auctionservice.bidder.Bidder;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

public class AuctionJpaRepositoryAdapter implements AuctionRepository {
    private final AuctionJpaRepository repository;

    public AuctionJpaRepositoryAdapter(AuctionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Auction create(Auction auction) {
        if (this.repository.existsById(auction.getId())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Auction with id " + auction.getId() + " already exists"
            );
        }
        return this.repository.save(auction);
    }

    @Override
    public Auction findById(Long auctionId) {
        return this.repository.findById(auctionId).orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Auction with id " + auctionId + " not found"
            )
        );
    }

    @Override
    public Auction updateStatus(Long auctionId, AuctionStatusEnum status) {
        Auction auction = this.findById(auctionId);
        auction.setStatus(status);
        return this.repository.save(auction);
    }

    @Override
    public Auction startAuction(Long auctionId, LocalDateTime start) {
        Auction auction = this.findById(auctionId);
        auction.setStartTime(start);
        auction.setStatus(AuctionStatusEnum.ACTIVE);
        return this.repository.save(auction);
    }

    @Override
    public Auction updatePrice(Long auctionId, Double price) {
        Auction auction = this.findById(auctionId);
        auction.setCurrentPrice(price);
        return this.repository.save(auction);
    }

    @Override
    public Auction updateHighestBidder(Long auctionId, Bidder bidder) {
        Auction auction = this.findById(auctionId);
        auction.setHighestBidder(bidder);
        auction.setCurrentPrice(bidder.getBidderPrice());
        auction.setTotalBids(auction.getTotalBids() + 1);
        return this.repository.save(auction);
    }

    @Override
    public Auction updateHighestBidder(Long auctionId, BidUpdateMessage message) {
        Auction auction = this.findById(Long.valueOf(message.getAuctionId()));
        Bidder bidder = auction.getHighestBidder();
        bidder.setUserId(Long.valueOf(message.getBidderId()));
        bidder.setBidderPrice(bidder.getBidderPrice());
        auction.setTotalBids(auction.getTotalBids() + 1);
        return this.repository.save(auction);
    }
}
