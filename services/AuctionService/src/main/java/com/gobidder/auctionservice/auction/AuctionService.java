package com.gobidder.auctionservice.auction;

import com.gobidder.auctionservice.auction.builder.AuctionBuilder;
import com.gobidder.auctionservice.auction.dto.AuctionCreateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionService {
    private final AuctionRepository auctionRepository;

    @Autowired
    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public Auction create(AuctionCreateRequestDto auctionCreateRequestDto) {
        AuctionTypeEnum auctionType = auctionCreateRequestDto.getType();
        AuctionBuilder auctionBuilder = AuctionBuilder.builder(auctionType);

        if (auctionType.equals(AuctionTypeEnum.FORWARD)) {
            auctionBuilder.endTime(auctionCreateRequestDto.getEndTime());
        }

        Auction auction = auctionBuilder
            .name(auctionCreateRequestDto.getName())
            .description(auctionCreateRequestDto.getDescription())
            .currency(auctionCreateRequestDto.getCurrencyType())
            .auctionImageUrl(auctionCreateRequestDto.getAuctionImageUrl())
            .auctionOwnerId(auctionCreateRequestDto.getAuctionOwnerId())
            .location(auctionCreateRequestDto.getLocation())
            .build();

        return this.auctionRepository.save(auction);
    }

    public Auction get(Long id) {
        return this.auctionRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Example not found")
        );
    }

    public List<Auction> getAllActiveAuctions() {
        return this.auctionRepository.findAuctionsByStatus(AuctionStatusEnum.ACTIVE);
    }

    public synchronized void endAuction(Auction auction) {
        auction.setStatus(AuctionStatusEnum.CANCELLED);
        this.auctionRepository.save(auction);
    }
}
