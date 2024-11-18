package com.gobidder.bid_service.controller;


import com.gobidder.bid_service.dto.BidResponse;
import com.gobidder.bid_service.dto.BidRequest;
import com.gobidder.bid_service.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidService bidService;

    @Autowired
    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<BidResponse> placeBid(@RequestBody BidRequest bidRequest) {
        return ResponseEntity.ok(bidService.processBid(bidRequest))
    }

}
