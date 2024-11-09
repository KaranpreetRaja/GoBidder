package com.gobidder.auctionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionService auctionService;

    @Autowired
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/auction")
    public Auction createExample(@RequestBody String name, @RequestBody String startTime, @RequestBody String endTime) {
        return this.auctionService.create(name, startTime, endTime);
    }

    @GetMapping("/auction/{id}")
    public Auction getAuction(@PathVariable Long id) {
        return this.auctionService.get(id);
    }
}
