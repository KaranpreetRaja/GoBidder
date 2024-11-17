package com.gobidder.auctionservice.bidder.strategy;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionRepository;
import com.gobidder.auctionservice.auction.AuctionService;
import com.gobidder.auctionservice.bidder.Bidder;
import com.gobidder.auctionservice.bidder.BidderRepository;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Component
public class DutchAuctionHighestBidderStrategy implements HighestBidderStrategy {
    private final BidderRepository bidderRepository;
    private final AuctionService auctionService;

    @Autowired
    public DutchAuctionHighestBidderStrategy(
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
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Dutch auction has highest bidder already"
            );
        } else {
            if (auction.getCurrentPrice() > newHighestBidder.getBid()) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Bid is lower than auction price"
                );
            }

            bidder = new Bidder();
            bidder.setUserId(newHighestBidder.getUserId());
            bidder.setBidderPrice(newHighestBidder.getBid());
            bidder.setAuction(auction);

            bidder = this.bidderRepository.save(bidder);

            auction = this.auctionService.updateHighestBidder(auction, bidder);
            this.auctionService.endAuction(auction);
        }
        return bidder;
    }
}
