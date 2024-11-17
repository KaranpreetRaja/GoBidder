package com.gobidder.auctionservice.bidder.strategy;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.bidder.Bidder;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;

public interface HighestBidderStrategy {
    Bidder setHighestBidder(HighestBidderDto newHighestBidder, Auction auction);
}
