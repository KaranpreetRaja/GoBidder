package com.gobidder.auctionservice.scheduler;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionService;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import com.gobidder.auctionservice.auction.strategy.AuctionStrategy;
import com.gobidder.auctionservice.auction.strategy.factory.AuctionStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class DutchAuctionDecreasePriceScheduler {
    private static final Double PRICE_DECREASE_AMOUNT = 1.0;
    private static final Integer PRICE_DECREASE_INTERVAL_SECONDS = 1;

    private final TaskScheduler taskScheduler;
    private final AuctionService auctionService;

    @Autowired
    public DutchAuctionDecreasePriceScheduler(TaskScheduler taskScheduler, AuctionService auctionService) {
        this.taskScheduler = taskScheduler;
        this.auctionService = auctionService;
    }

    public void scheduleAuctionPriceDecrease(Auction auction) {
        if (!auction.getType().equals(AuctionTypeEnum.DUTCH)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only dutch auctions can have scheduled price decreases"
            );
        }
        this.taskScheduler.schedule(
            () -> {
                // Get strategy
                AuctionStrategyFactory factory = AuctionStrategyFactory.getInstance();
                AuctionStrategy strategy = factory.getAuctionStrategy(auction);

                if (auction.getStatus().equals(AuctionStatusEnum.ACTIVE)) {
                    if (strategy.isEnding(auction)) {
                        // End auction
                        this.auctionService.endAuction(auction);
                    } else {
                        // Decrease price
                        this.auctionService.decreasePrice(
                            auction,
                            PRICE_DECREASE_AMOUNT
                        );
                        // Reschedule task
                        this.scheduleAuctionPriceDecrease(auction);
                    }
                }
            },
            Date.from(LocalDateTime.now()
                .plusSeconds(PRICE_DECREASE_INTERVAL_SECONDS)
                .atZone(ZoneId.systemDefault()).toInstant())
                .toInstant()
        );
    }
}
