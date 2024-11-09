package com.gobidder.auctionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Auction create(String name, String startTime, String endTime) {
        Auction auction = new Auction(name, startTime, endTime);
        this.auctionRepository.save(auction);
        return auction;
    }

    public Auction get(Long id) {
        return this.auctionRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Example not found")
        );
    }
}
