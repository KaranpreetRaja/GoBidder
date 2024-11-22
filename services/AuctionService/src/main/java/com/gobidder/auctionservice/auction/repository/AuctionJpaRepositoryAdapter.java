package com.gobidder.auctionservice.auction.repository;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.dto.BidUpdateMessage;
import com.gobidder.auctionservice.bidder.Bidder;
import com.gobidder.auctionservice.bidder.BidderRepository;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.Logger;
import java.time.LocalDateTime;
import java.util.List;

public class AuctionJpaRepositoryAdapter implements AuctionRepository {
    private final AuctionJpaRepository repository;
    private final BidderRepository bidderRepository; // Add this

    private static final Logger logger = LoggerFactory.getLogger(AuctionJpaRepositoryAdapter.class);

    public AuctionJpaRepositoryAdapter(AuctionJpaRepository repository, BidderRepository bidderRepository) { // Modify constructor
        this.repository = repository;
        this.bidderRepository = bidderRepository;
    }
    @Override
    public Auction create(Auction auction) {
        return this.repository.save(auction);
    }

    @Override
    public void delete(Long id) {
        this.repository.deleteById(id);
    }

    @Override
    public List<Auction> findAll() {
        return this.repository.findAll();
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
        auction.setHighestBidder(bidder.getUserId(), bidder.getBidderPrice());
        auction.setCurrentPrice(bidder.getBidderPrice());
        auction.setTotalBids(auction.getTotalBids() + 1);
        return this.repository.save(auction);
    }

    @Override
    public Auction updateHighestBidder(Long auctionId, BidUpdateMessage message) {
        try {
            logger.debug("Updating highest bidder for auction {} with message: {}", auctionId, message);

            // Convert string ID to Long safely
            Long parsedAuctionId = Long.parseLong(message.getAuctionId());
            Long parsedBidderId = Long.parseLong(message.getBidderId());

            // Get auction
            Auction auction = this.findById(parsedAuctionId);

            // Create or update bidder
            Bidder bidder = new Bidder();
            bidder.setUserId(parsedBidderId);
            bidder.setBidderPrice(message.getNewAmount());

            // Set bidder details
            bidder.setUserId(parsedBidderId);
            bidder.setBidderPrice(message.getNewAmount());

            // Update and save the auction
            auction.setHighestBidder(bidder.getUserId(), bidder.getBidderPrice());
            auction.setTotalBids(auction.getTotalBids() + 1);
            auction.setCurrentPrice(message.getNewAmount());

            logger.debug("Saving auction with updated bidder: {}", auction);
            return this.repository.save(auction);

        } catch (NumberFormatException e) {
            logger.error("Failed to parse ID in bid update message. AuctionId: {}, BidderId: {}",
                    message.getAuctionId(), message.getBidderId(), e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid ID format in bid update message: " + e.getMessage()
            );
        } catch (Exception e) {
            logger.error("Error updating highest bidder for auction {}", auctionId, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update highest bidder: " + e.getMessage()
            );
        }
    }
}