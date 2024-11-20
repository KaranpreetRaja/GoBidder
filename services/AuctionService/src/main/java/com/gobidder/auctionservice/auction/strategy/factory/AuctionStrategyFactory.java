package com.gobidder.auctionservice.auction.strategy.factory;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import com.gobidder.auctionservice.auction.strategy.AuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.DutchAuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.ForwardAuctionStrategy;

/**
 * Creates and manages {@link AuctionStrategy} implementations.
 */
public class AuctionStrategyFactory {
    private static AuctionStrategyFactory instance = null;

    private AuctionStrategyFactory() {
        // Private constructor for singleton
    }

    /**
     * Get the auction strategy factory as a singleton, creating the instance if
     * it does not yet exist.
     *
     * @return The singleton auction strategy factory instance.
     */
    public static AuctionStrategyFactory getInstance() {
        if (instance == null) {
            instance = new AuctionStrategyFactory();
        }
        return instance;
    }

    /**
     * Get the auction strategy based on the auction. The strategy used is based
     * on the auction's type.
     *
     * @param auction The auction to create the strategy for.
     *
     * @return The auction strategy corresponding to the given auction.
     */
    public AuctionStrategy getAuctionStrategy(Auction auction) {
        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            return ForwardAuctionStrategy.getInstance();
        } else if (auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            return DutchAuctionStrategy.getInstance();
        } else {
            throw new IllegalArgumentException("Unknown auction type: " + auction.getType());
        }
    }

}
