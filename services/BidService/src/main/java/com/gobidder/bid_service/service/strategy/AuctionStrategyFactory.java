package com.gobidder.bid_service.service.strategy;

import com.gobidder.bid_service.repository.AuctionCacheRepository;
import com.gobidder.bid_service.service.KafkaProducerService;
import org.springframework.stereotype.Component;

@Component
public class AuctionStrategyFactory {
    private final AuctionCacheRepository auctionCacheRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ForwardAuctionStrategy forwardAuctionStrategy;
    private final DutchAuctionStrategy dutchAuctionStrategy;

    public AuctionStrategyFactory(AuctionCacheRepository auctionCacheRepository,
                                  KafkaProducerService kafkaProducerService) {
        this.auctionCacheRepository = auctionCacheRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.forwardAuctionStrategy = new ForwardAuctionStrategy(auctionCacheRepository, kafkaProducerService);
        this.dutchAuctionStrategy = new DutchAuctionStrategy(auctionCacheRepository, kafkaProducerService);
    }

    public AuctionStrategy getStrategy(String strategyName) {
        if (strategyName == null) {
            throw new IllegalArgumentException("Strategy name cannot be null");
        }

        return switch (strategyName.toUpperCase()) {
            case "FORWARD" -> forwardAuctionStrategy;
            case "DUTCH" -> dutchAuctionStrategy;
            default -> throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        };
    }
}