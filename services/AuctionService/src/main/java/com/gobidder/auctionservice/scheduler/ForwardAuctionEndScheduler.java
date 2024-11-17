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
public class ForwardAuctionEndScheduler {
    private final TaskScheduler taskScheduler;
    private final AuctionService auctionService;

    @Autowired
    public ForwardAuctionEndScheduler(TaskScheduler taskScheduler, AuctionService auctionService) {
        this.taskScheduler = taskScheduler;
        this.auctionService = auctionService;
    }

    public void scheduleForwardAuctionEnd(Auction auction) {
        if (!auction.getType().equals(AuctionTypeEnum.FORWARD)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only forward auctions can have scheduled auction end times"
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
                        // Reschedule task
                        this.scheduleForwardAuctionEnd(auction);
                    }
                }
            },
            Date.from(
                auction.getEndTime()
                    .atZone(ZoneId.systemDefault()).toInstant())
                .toInstant()
        );
    }

}
