package com.gobidder.auctionservice.grpc;

import com.gobidder.auctionservice.proto.*;
import io.grpc.ManagedChannel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionGrpcClient {
    private final ManagedChannel managedChannel;
    private AuctionServiceGrpc.AuctionServiceBlockingStub blockingStub;

    @PostConstruct
    private void init() {
        blockingStub = AuctionServiceGrpc.newBlockingStub(managedChannel);
    }

    public InitAuctionResponse initAuction(
            String auctionId,
            String auctionType,
            double startingPrice,
            Long endTimeUnix,
            Double dutchAuctionStepSize,
            Double dutchAuctionMinimumPrice) {

        InitAuctionRequest.Builder requestBuilder = InitAuctionRequest.newBuilder()
                .setAuctionId(auctionId)
                .setAuctionType(auctionType)
                .setStartingPrice(startingPrice);

        if (endTimeUnix != null) {
            requestBuilder.setEndTimeUnix(endTimeUnix);
        }
        if (dutchAuctionStepSize != null) {
            requestBuilder.setDutchAuctionStepSize(dutchAuctionStepSize);
        }
        if (dutchAuctionMinimumPrice != null) {
            requestBuilder.setDutchAuctionMinimumPrice(dutchAuctionMinimumPrice);
        }

        try {
            log.info("Sending InitAuction request for auction ID: {}", auctionId);
            return blockingStub.initAuction(requestBuilder.build());
        } catch (Exception e) {
            log.error("Error initializing auction via gRPC for auctionId: {}", auctionId, e);
            throw new RuntimeException("Failed to initialize auction via gRPC", e);
        }
    }
}