package com.gobidder.auctionservice.grpc.service;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.gobidder.auctionservice.proto.*;

import java.time.ZoneOffset;

@GrpcService
public class AuctionGrpcService extends AuctionServiceGrpc.AuctionServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(AuctionGrpcService.class);
    private final AuctionService auctionService;

    @Autowired
    public AuctionGrpcService(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @Override
    public void getAuction(GetAuctionRequest request, StreamObserver<GetAuctionResponse> responseObserver) {
        try {
            if (request == null || request.getAuctionId() == null || request.getAuctionId().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Auction ID is required")
                        .asRuntimeException());
                return;
            }

            logger.info("Received gRPC request for auction ID: {}", request.getAuctionId());

            Long auctionId;
            try {
                auctionId = Long.valueOf(request.getAuctionId());
            } catch (NumberFormatException e) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription("Invalid auction ID format")
                        .asRuntimeException());
                return;
            }

            Auction auction = auctionService.get(auctionId);

            if (auction == null) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Auction not found")
                        .asRuntimeException());
                return;
            }

            GetAuctionResponse.Builder responseBuilder = GetAuctionResponse.newBuilder()
                    .setAuctionId(auction.getId().toString())
                    .setAuctionType(auction.getType().toString())
                    .setCurrentPrice(auction.getCurrentPrice())
                    .setIsActive(auction.getStatus().toString().equals("ACTIVE"))
                    .setTotalAuctionBids(auction.getTotalBids());

            // Set last update timestamp if available
            if (auction.getPriceUpdatedAt() != null) {
                responseBuilder.setLastUpdateTimestamp(
                        auction.getPriceUpdatedAt().toEpochSecond(ZoneOffset.UTC)
                );
            }

            // Set current winning bidder if exists
            if (auction.getHighestBidderId() != null) {
                responseBuilder.setCurrentWinningBidderId(
                        auction.getHighestBidderId().toString()
                );
            } else {
                responseBuilder.setCurrentWinningBidderId("");
            }

            GetAuctionResponse response = responseBuilder.build();
            logger.info("Sending gRPC response for auction ID: {}", request.getAuctionId());
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (Exception e) {
            logger.error("Error processing gRPC request for auction ID: {}",
                    request.getAuctionId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void initAuction(InitAuctionRequest request, StreamObserver<InitAuctionResponse> responseObserver) {
        responseObserver.onNext(InitAuctionResponse.newBuilder()
                .setSuccess(false)
                .setMessage("InitAuction not implemented")
                .build());
        responseObserver.onCompleted();
    }
}