package com.gobidder.auctionservice.bidder;

import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BidderController {
    private final BidderService bidderService;

    @Autowired
    public BidderController(BidderService bidderService) {
        this.bidderService = bidderService;
    }

    @GetMapping("/bidder/{auctionId}")
    public Bidder getHighestBidder(@PathVariable Long auctionId) {
        return this.bidderService.getHighestBidder(auctionId);
    }

    @PostMapping("/bidder/{auctionId}")
    public Bidder setHighestBidder(@PathVariable Long auctionId, @RequestBody HighestBidderDto highestBidderDto) {
        return this.bidderService.setHighestBidder(auctionId, highestBidderDto);
    }
}
