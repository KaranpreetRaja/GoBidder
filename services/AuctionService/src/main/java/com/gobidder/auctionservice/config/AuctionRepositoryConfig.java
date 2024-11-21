package com.gobidder.auctionservice.config;

import com.gobidder.auctionservice.auction.repository.AuctionJpaRepository;
import com.gobidder.auctionservice.auction.repository.AuctionJpaRepositoryAdapter;
import com.gobidder.auctionservice.auction.repository.AuctionRepository;
import com.gobidder.auctionservice.auction.repository.AuctionRepositoryConcurrentProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuctionRepositoryConfig {

    private final AuctionJpaRepository auctionJpaRepository;

    @Autowired
    public AuctionRepositoryConfig(AuctionJpaRepository auctionJpaRepository) {
        this.auctionJpaRepository = auctionJpaRepository;
    }

    @Bean
    public AuctionRepository auctionRepository() {
        return new AuctionRepositoryConcurrentProxy(
            new AuctionJpaRepositoryAdapter(
                this.auctionJpaRepository
            )
        );
    }
}
