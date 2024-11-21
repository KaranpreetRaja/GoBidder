package com.gobidder.bid_service.service;

import com.gobidder.bid_service.dto.BidUpdateMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, Object> kafkaTemplate;

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
