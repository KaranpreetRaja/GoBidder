package com.gobidder.auctionservice.bidder;

import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BidderService {
    private final BidderRepository bidderRepository;

    @Autowired
    public BidderService(BidderRepository bidderRepository) {
        this.bidderRepository = bidderRepository;
    }

    public Bidder getHighestBidder(Long auctionId) {
        return this.bidderRepository.findByAuctionId(auctionId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Auction with id " + auctionId + " does not exist"
            ));
    }

    public Bidder setHighestBidder(Long auctionId, HighestBidderDto highestBidderDto) {
        Bidder bidder = this.getHighestBidder(auctionId);
        bidder.setUserId(highestBidderDto.getUserId());
        bidder.setBidderPrice(highestBidderDto.getBid());
        return this.bidderRepository.save(bidder);
    }
}
