package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionService auctionService;

    @Autowired
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("")
    public Auction createExample(@Valid @RequestBody AuctionCreateRequestDto auctionCreateRequestDto) {
        return this.auctionService.create(auctionCreateRequestDto);
    }

    @GetMapping("/{id}")
    public Auction getAuction(@PathVariable Long id) {
        return this.auctionService.get(id);
    }

    @PostMapping("/{id}/start")
    public Auction startAuction(@PathVariable Long id) {
        return this.auctionService.startAuction(id);
    }
}
