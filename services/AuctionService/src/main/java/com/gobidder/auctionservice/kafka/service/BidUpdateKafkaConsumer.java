package com.gobidder.auctionservice.kafka.service;

import com.gobidder.auctionservice.auction.AuctionService;
import com.gobidder.auctionservice.auction.dto.BidUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BidUpdateKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BidUpdateKafkaConsumer.class);
    private final AuctionService auctionService;

    @Autowired
    public BidUpdateKafkaConsumer(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @KafkaListener(topics = "bid-updates", groupId = "auction-service-group")
    public void consumeBidUpdate(BidUpdateMessage message) {
        if (message == null) {
            logger.error("Received null bid update message");
            return;
        }

        try {
            if (message.getAuctionId() == null || message.getAuctionId().isEmpty()) {
                logger.error("Received bid update message with null or empty auction ID");
                return;
            }

            if (message.getNewAmount() == null) {
                logger.error("Received bid update message with null amount for auction: {}",
                        message.getAuctionId());
                return;
            }

            // Determine if this is a bidder update or automatic price update
            if (message.getBidderId() == null || message.getBidderId().isEmpty()) {
                logger.info("Received automatic price update message for auction ID: {} with new amount: {}",
                        message.getAuctionId(), message.getNewAmount());
            } else {
                logger.info("Received bid update message for auction ID: {} from bidder: {} with amount: {}",
                        message.getAuctionId(), message.getBidderId(), message.getNewAmount());
            }

            // Process the update regardless of bidder presence
            Long auctionId = Long.valueOf(message.getAuctionId());
            auctionService.updateHighestBidder(auctionId, message);

            logger.info("Successfully processed price update for auction ID: {}", auctionId);

        } catch (NumberFormatException e) {
            logger.error("Invalid numeric format in bid update message: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error processing bid update message: {}", e.getMessage(), e);
        }
    }
}