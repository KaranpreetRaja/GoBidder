package com.gobidder.auctionservice.kafka.service;

import com.gobidder.auctionservice.auction.dto.BidUpdateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaConsumerService {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public KafkaConsumerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendBidUpdate(String auctionId, Double newAmount, String bidderId, Long timestamp, int totalAuctionBids) {
        BidUpdateMessage message = new BidUpdateMessage();
        message.setAuctionId(auctionId);
        message.setNewAmount(newAmount);
        message.setBidderId(bidderId);
        message.setTimestamp(timestamp);
        message.setTotalAuctionBids(totalAuctionBids);

        CompletableFuture<SendResult<String, Object>> future =
            kafkaTemplate.send("bid-updates", auctionId, message);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent bid message=[{}] with offset=[{}]",
                    message,
                    result.getRecordMetadata().offset());
            } else {
                log.error("Unable to send message=[{}] due to : {}",
                    message,
                    ex.getMessage());
            }
        });
    }
}
