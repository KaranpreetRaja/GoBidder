package com.gobidder.auctionservice.auction.repository;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionTypeEnum;
import com.gobidder.auctionservice.auction.builder.AuctionBuilder;
import com.gobidder.auctionservice.kafka.service.BidUpdateKafkaConsumer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuctionDatabasePopulator {

    private static final Logger logger = LoggerFactory.getLogger(AuctionDatabasePopulator.class);

    private final AuctionRepository auctionRepository;
    private final List<Auction> innitialAuctions;

    @Autowired
    public AuctionDatabasePopulator(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
        Auction auction1 = AuctionBuilder.builder(AuctionTypeEnum.FORWARD)
            .duration(5000)
            .name("Motorcycle")
            .description("My Honda bike in pretty good condition.")
            .currency("CAD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/5rIAAOSwSKpmcs7U/s-l500.jpg")
            .auctionOwnerId(0L)
            .location("Toronto, ON")
            .startTime(LocalDateTime.now().minusMinutes(2))
            .initialPrice(1200.0)
            .build();
        Auction auction2 = AuctionBuilder.builder(AuctionTypeEnum.DUTCH)
            .minimumPrice(100.0)
            .name("Toronto Raptors Jersey")
            .description("I'm no longer a fan after last year. Selling the jersey :(.")
            .currency("CAD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/rJkAAOSwWLJnVheM/s-l500.jpg")
            .auctionOwnerId(0L)
            .location("Barrie, ON")
            .startTime(LocalDateTime.now().minusMinutes(4))
            .initialPrice(1500.0)
            .build();
        Auction auction3 = AuctionBuilder.builder(AuctionTypeEnum.FORWARD)
            .duration(5000)
            .name("Vintage Vinyl Record Player")
            .description("Fully functional 1970s vinyl record player. Perfect for music lovers.")
            .currency("USD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/tmkAAOSwcVRmi5HN/s-l500.jpg")
            .auctionOwnerId(1L)
            .location("New York, NY")
            .startTime(LocalDateTime.now().minusMinutes(3))
            .initialPrice(300.0)
            .build();
        Auction auction4 = AuctionBuilder.builder(AuctionTypeEnum.DUTCH)
            .minimumPrice(50.0)
            .name("Classic Comic Book Collection")
            .description("A collection of Marvel comics from the 1980s.")
            .currency("USD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/G9wAAOSwK~9e4kxl/s-l500.jpg")
            .auctionOwnerId(2L)
            .location("Chicago, IL")
            .startTime(LocalDateTime.now().minusMinutes(5))
            .initialPrice(2000.0)
            .build();
        Auction auction5 = AuctionBuilder.builder(AuctionTypeEnum.FORWARD)
            .duration(90)
            .name("Gaming Laptop")
            .description("High-performance gaming laptop with NVIDIA GTX 3080 and 32GB RAM.")
            .currency("USD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/hKUAAOSw1DhnWnkM/s-l500.jpg")
            .auctionOwnerId(3L)
            .location("Los Angeles, CA")
            .startTime(LocalDateTime.now().plusMinutes(10))
            .initialPrice(1500.0)
            .build();
        Auction auction6 = AuctionBuilder.builder(AuctionTypeEnum.DUTCH)
            .minimumPrice(30.0)
            .name("Electric Guitar")
            .description("Beginner-friendly electric guitar in great condition.")
            .currency("USD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/BZEAAOSwZENm9Ile/s-l500.jpg")
            .auctionOwnerId(4L)
            .location("Nashville, TN")
            .startTime(LocalDateTime.now().plusMinutes(8))
            .initialPrice(250.0)
            .build();
        Auction auction7 = AuctionBuilder.builder(AuctionTypeEnum.FORWARD)
            .duration(60)
            .name("Antique Wooden Dining Table")
            .description("Beautiful hand-carved dining table, perfect for a vintage-style home.")
            .currency("CAD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/fVwAAOSw6xRlzizV/s-l500.jpg")
            .auctionOwnerId(5L)
            .location("Montreal, QC")
            .startTime(LocalDateTime.now().plusMinutes(15))
            .initialPrice(700.0)
            .build();
        Auction auction8 = AuctionBuilder.builder(AuctionTypeEnum.DUTCH)
            .minimumPrice(20.0)
            .name("Basketball Shoes")
            .description("Nike Air Jordans, lightly used and in great condition.")
            .currency("CAD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/dc0AAOSwpRlmjsUg/s-l500.jpg")
            .auctionOwnerId(6L)
            .location("Vancouver, BC")
            .startTime(LocalDateTime.now().plusMinutes(12))
            .initialPrice(120.0)
            .build();
        Auction auction9 = AuctionBuilder.builder(AuctionTypeEnum.FORWARD)
            .duration(120)
            .name("Mountain Bike")
            .description("Trek X-Caliber mountain bike, excellent for off-road adventures.")
            .currency("USD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/d7EAAOSwJ0VnTKBq/s-l500.jpg")
            .auctionOwnerId(7L)
            .location("Denver, CO")
            .startTime(LocalDateTime.now().plusMinutes(20))
            .initialPrice(900.0)
            .build();
        Auction auction10 = AuctionBuilder.builder(AuctionTypeEnum.DUTCH)
            .minimumPrice(80.0)
            .name("Smartwatch")
            .description("Apple Watch Series 6, barely used with original packaging.")
            .currency("USD")
            .auctionImageUrl("https://i.ebayimg.com/images/g/HJsAAOSwdPRnPYPN/s-l500.jpg")
            .auctionOwnerId(8L)
            .location("San Francisco, CA")
            .startTime(LocalDateTime.now().plusMinutes(18))
            .initialPrice(300.0)
            .build();
        this.innitialAuctions = new ArrayList<>();
        this.innitialAuctions.add(auction1);
        this.innitialAuctions.add(auction2);
        this.innitialAuctions.add(auction3);
        this.innitialAuctions.add(auction4);
        this.innitialAuctions.add(auction5);
        this.innitialAuctions.add(auction6);
        this.innitialAuctions.add(auction7);
        this.innitialAuctions.add(auction8);
        this.innitialAuctions.add(auction9);
        this.innitialAuctions.add(auction10);
    }

    @PostConstruct
    public void initDatabase() {
        logger.info("Populating auctions in database");
        for (Auction auction : this.innitialAuctions) {
            if (!this.auctionRepository.existsByName(auction.getName())) {
                logger.info("Creating auction {}", auction.getName());
                this.auctionRepository.create(auction);
                logger.info("Created auction {}", auction.getName());
            }
        }
    }
}
