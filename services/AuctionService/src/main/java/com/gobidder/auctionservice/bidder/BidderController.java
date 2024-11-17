package com.gobidder.auctionservice.bidder;

import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auction")
public class BidderController {
    private final BidderService bidderService;

    @Autowired
    public BidderController(BidderService bidderService) {
        this.bidderService = bidderService;
    }

    @GetMapping("/{auctionId}/bid")
    public Bidder getHighestBidder(@PathVariable Long auctionId) {
        return this.bidderService.getHighestBidder(auctionId);
    }

    @PostMapping("/{auctionId}/bid")
    public Bidder setHighestBidder(@PathVariable Long auctionId, @RequestBody HighestBidderDto highestBidderDto) {
        return this.bidderService.setHighestBidder(auctionId, highestBidderDto);
    }
}
