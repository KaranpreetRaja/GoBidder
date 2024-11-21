package com.gobidder.auctionservice.bidder.strategy;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.bidder.Bidder;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;

/**
 * Strategy object for auction-type-specific highest-bidder behaviors.
 */
public interface HighestBidderStrategy {
    /**
     * Sets the highest bidder of an auction.
     * <p>
     * This has different behavior for forward and Dutch auctions because
     * forward auctions will continue with a new increased price and Dutch
     * auctions will end immediately.
     *
     * @param newHighestBidder The new highest bidder of the auction.
     * @param auction The auction to set the highest bidder of.
     *
     * @return The new highest bidder of the auction from the database.
     */
    Bidder setHighestBidder(HighestBidderDto newHighestBidder, Auction auction);
}
