package com.gobidder.auctionservice.config;

import com.gobidder.auctionservice.auction.repository.AuctionJpaRepository;
import com.gobidder.auctionservice.auction.repository.AuctionJpaRepositoryAdapter;
import com.gobidder.auctionservice.auction.repository.AuctionRepository;
import com.gobidder.auctionservice.auction.repository.AuctionRepositoryConcurrentProxy;
import com.gobidder.auctionservice.bidder.BidderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuctionRepositoryConfig {

    private final AuctionJpaRepository auctionJpaRepository;
    private final BidderRepository bidderRepository;

    @Autowired
    public AuctionRepositoryConfig(
            AuctionJpaRepository auctionJpaRepository,
            BidderRepository bidderRepository
    ) {
        this.auctionJpaRepository = auctionJpaRepository;
        this.bidderRepository = bidderRepository;
    }

    @Bean
    public AuctionRepository auctionRepository() {
        return new AuctionRepositoryConcurrentProxy(
                new AuctionJpaRepositoryAdapter(
                        this.auctionJpaRepository,
                        this.bidderRepository
                )
        );
    }
}