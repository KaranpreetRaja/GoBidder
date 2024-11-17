package com.gobidder.auctionservice.auction.strategy;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.dto.CurrentHighestBidderDto;
import com.gobidder.auctionservice.auction.exception.BidTooLowException;

import java.time.LocalDateTime;

public class ForwardAuctionStrategy implements AuctionStrategy {
    private static ForwardAuctionStrategy instance = null;

    private CurrentHighestBidderDto currentWinner;

    private ForwardAuctionStrategy() {
        // Private constructor for singleton
    }

    public static ForwardAuctionStrategy getInstance() {
        if (instance == null) {
            instance = new ForwardAuctionStrategy();
        }
        return instance;
    }

    @Override
    public CurrentHighestBidderDto getHighest() {
        return currentWinner;
    }

    @Override
    public void setHighest(int userID, int bid) {
        // Update winning bid so far
        if (currentWinner.getBid() < bid) {
            currentWinner.setUserID(userID);
            currentWinner.setBid(bid);
        }
        // Bid is less than the current winner
        else {
            throw new BidTooLowException();
        }
    }

    @Override
    public boolean isEnding(Auction auction) {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isAfter(auction.getEndTime());
    }
}
