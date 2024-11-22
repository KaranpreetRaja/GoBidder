package com.gobidder.auctionservice.bidder.strategy;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionService;
import com.gobidder.auctionservice.bidder.Bidder;
import com.gobidder.auctionservice.bidder.BidderRepository;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Strategy object for Dutch auction-specific highest-bidder behaviors.
 */
@Component
public class ForwardAuctionHighestBidderStrategy implements HighestBidderStrategy {
    private final BidderRepository bidderRepository;
    private final AuctionService auctionService;

    @Autowired
    public ForwardAuctionHighestBidderStrategy(
        BidderRepository bidderRepository,
        AuctionService auctionService
    ) {
        this.bidderRepository = bidderRepository;
        this.auctionService = auctionService;
    }

    @Override
    public Bidder setHighestBidder(HighestBidderDto newHighestBidder, Auction auction) {
        Optional<Bidder> bidderOptional =
            this.bidderRepository.findByAuctionId(auction.getId());
        Bidder bidder;
        if (bidderOptional.isPresent()) {
            bidder = bidderOptional.get();
            if (bidder.getBidderPrice() >= newHighestBidder.getBid()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "New bid must be higher than highest bidder price"
                );
            }
            bidder.setUserId(newHighestBidder.getUserId());
            bidder.setBidderPrice(newHighestBidder.getBid());
        } else {
            if (auction.getCurrentPrice() > newHighestBidder.getBid()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bid must be higher than or equal to auction price"
                );
            }

            bidder = new Bidder();
            bidder.setUserId(newHighestBidder.getUserId());
            bidder.setBidderPrice(newHighestBidder.getBid());

        }
        bidder = this.bidderRepository.save(bidder);
        this.auctionService.updateHighestBidder(auction, bidder);
        return bidder;
    }
}
