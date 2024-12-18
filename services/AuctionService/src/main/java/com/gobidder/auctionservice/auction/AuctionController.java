package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionService auctionService;

    @Autowired
    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @RequestMapping(value={"","/"}, method = RequestMethod.POST)
    public Auction createExample(@Valid @RequestBody AuctionCreateRequestDto auctionCreateRequestDto) {
        return this.auctionService.create(auctionCreateRequestDto);
    }

    @GetMapping("/{id}")
    public Auction getAuction(@PathVariable Long id) {
        return this.auctionService.get(id);
    }

    @RequestMapping(value={"","/"}, method = RequestMethod.GET)
    public List<Auction> getAllAuctions() {
        return this.auctionService.getAllAuctions();
    }

    @PostMapping("/{id}/start")
    public Auction startAuction(@PathVariable Long id) {
        return this.auctionService.startAuction(id);
    }

    @DeleteMapping("/{id}")
    public void deleteAuction(@PathVariable Long id) {
        this.auctionService.delete(id);
    }
}
