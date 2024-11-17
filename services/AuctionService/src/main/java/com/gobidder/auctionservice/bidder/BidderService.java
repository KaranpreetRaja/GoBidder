package com.gobidder.auctionservice.bidder;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionService;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import com.gobidder.auctionservice.bidder.dto.HighestBidderDto;
import com.gobidder.auctionservice.bidder.strategy.DutchAuctionHighestBidderStrategy;
import com.gobidder.auctionservice.bidder.strategy.ForwardAuctionHighestBidderStrategy;
import com.gobidder.auctionservice.bidder.strategy.HighestBidderStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BidderService {
    private final BidderRepository bidderRepository;
    private final AuctionService auctionService;
    private final ForwardAuctionHighestBidderStrategy forwardAuctionHighestBidderStrategy;
    private final DutchAuctionHighestBidderStrategy dutchAuctionHighestBidderStrategy;

    @Autowired
    public BidderService(
        BidderRepository bidderRepository,
        AuctionService auctionService,
        ForwardAuctionHighestBidderStrategy forwardAuctionHighestBidderStrategy,
        DutchAuctionHighestBidderStrategy dutchAuctionHighestBidderStrategy
    ) {
        this.bidderRepository = bidderRepository;
        this.auctionService = auctionService;
        this.forwardAuctionHighestBidderStrategy = forwardAuctionHighestBidderStrategy;
        this.dutchAuctionHighestBidderStrategy = dutchAuctionHighestBidderStrategy;
    }

    /**
     * Get the singleton instance of a highest bidder auction strategy.
     *
     * @param auction The auction to apply the strategy to.
     *
     * @return The auction highest bidder strategy.
     */
    private HighestBidderStrategy getHighestBidderStrategy(Auction auction) {
        if (auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            return this.forwardAuctionHighestBidderStrategy;
        } else if (auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            return this.dutchAuctionHighestBidderStrategy;
        } else {
            throw new IllegalStateException("Unknown auction type " + auction.getType());
        }
    }

    /**
     * Get the highest bidder of an auction.
     * <p>
     * This method is synchronized to prevent dirty reads.
     *
     * @param auctionId The ID of the auction to get the highest bidder of.
     *
     * @return The highest bidder of the auction.
     *
     * @throws ResponseStatusException If the auction does not exist or if the
     *                                 auction does not have a highest bidder.
     */
    public synchronized Bidder getHighestBidder(Long auctionId) {
        this.auctionService.assertAuctionExists(auctionId);

        return this.bidderRepository.findByAuctionId(auctionId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No highest bidder for auction id " + auctionId
            ));
    }

    /**
     * Sets the highest bidder of an auction.
     * <p>
     * This method is synchronized to prevent race conditions.
     *
     * @param auctionId The ID of the auction to set the highest bidder of.
     * @param highestBidderDto The new highest bidder of the auction.
     *
     * @return The new highest bidder of the auction.
     *
     * @throws ResponseStatusException If the auction does not exist, if the
     *                                 auction is not active, or if the bidder
     *                                 did not bid high enough.
     */
    public synchronized Bidder setHighestBidder(Long auctionId, HighestBidderDto highestBidderDto) {
        Auction auction = this.auctionService.get(auctionId);
        if (!auction.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Auction with id " + auctionId + " is not active"
            );
        }

        HighestBidderStrategy highestBidderStrategy = getHighestBidderStrategy(auction);
        return highestBidderStrategy.setHighestBidder(highestBidderDto, auction);
    }
}
