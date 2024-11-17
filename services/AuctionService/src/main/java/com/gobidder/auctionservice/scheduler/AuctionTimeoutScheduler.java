package com.gobidder.auctionservice.scheduler;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionService;
import com.gobidder.auctionservice.auction.strategy.AuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.factory.AuctionStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuctionTimeoutScheduler {

    /**
     * 1000 milliseconds * 60 seconds = 1 minute
     */
    private static final int AUCTION_CHECKING_RATE_MILLISECONDS = 60 * 1000;

    private final AuctionService auctionService;

    @Autowired
    public AuctionTimeoutScheduler(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    /**
     * Checks every minute whether to end any of the active auctions.
     */
    @Scheduled(fixedRate = AUCTION_CHECKING_RATE_MILLISECONDS)
    public void checkAuctionTimeout() {
        List<Auction> activeAuctions = this.auctionService.getAllActiveAuctions();
        AuctionStrategy strategy;
        AuctionStrategyFactory strategyFactory = AuctionStrategyFactory.getInstance();
        for (Auction auction : activeAuctions) {
            strategy = strategyFactory.getAuctionStrategy(auction);
            if (strategy.isEnding(auction)) {
                this.auctionService.endAuction(auction);
            }
        }
    }
}
