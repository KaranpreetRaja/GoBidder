package com.gobidder.bid_service.service;

import com.gobidder.bid_service.model.AuctionCacheModel;
import com.gobidder.bid_service.repository.AuctionCacheRepository;
import com.gobidder.bid_service.proto.AuctionServiceGrpc;
import com.gobidder.bid_service.proto.GetAuctionRequest;
import com.gobidder.bid_service.proto.GetAuctionResponse;
import com.gobidder.bid_service.proto.InitAuctionRequest;
import com.gobidder.bid_service.proto.InitAuctionResponse;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@GrpcService
@RequiredArgsConstructor
public class AuctionGrpcServer extends AuctionServiceGrpc.AuctionServiceImplBase {
    private static final Logger logger = LoggerFactory.getLogger(AuctionGrpcServer.class);
    private final AuctionCacheRepository auctionCacheRepository;
    private final BidService bidService;

    @Override
    public void initAuction(InitAuctionRequest request, StreamObserver<InitAuctionResponse> responseObserver) {
        try {
            logger.info("Received request to initialize auction with ID: {} and type: {}",
                    request.getAuctionId(), request.getAuctionType());
            // save auction in cache
            AuctionCacheModel auctionCache = new AuctionCacheModel();
            auctionCache.setAuctionId(request.getAuctionId());
            auctionCache.setAuctionType(request.getAuctionType());
            auctionCache.setCurrentPrice(request.getStartingPrice());
            auctionCache.setActive(true);
            auctionCache.setLastUpdateTimestamp(System.currentTimeMillis());
            auctionCache.setTotalAuctionBids(0);

            auctionCacheRepository.save(auctionCache);

            // if auction is DUTCH, start dutch countdown
            if ("DUTCH".equalsIgnoreCase(request.getAuctionType())) {
                logger.info("Starting Dutch auction countdown for auction ID: {}", request.getAuctionId());
                bidService.startDutchCountdown(
                        request.getAuctionId(),
                        request.getStartingPrice(),
                        request.getDutchAuctionStepSize(),
                        request.getDutchAuctionMinimumPrice(),
                        10 // default interval in seconds for auction price decreasing
                );
            }

            responseObserver.onNext(InitAuctionResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Auction initialized successfully")
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(InitAuctionResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed to initialize auction: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getAuction(GetAuctionRequest request, StreamObserver<GetAuctionResponse> responseObserver) {
        try {
            AuctionCacheModel auction = auctionCacheRepository.findById(request.getAuctionId())
                    .orElseThrow(() -> new RuntimeException("Auction not found"));

            responseObserver.onNext(GetAuctionResponse.newBuilder()
                    .setAuctionId(auction.getAuctionId())
                    .setAuctionType(auction.getAuctionType())
                    .setCurrentPrice(auction.getCurrentPrice())
                    .setIsActive(auction.isActive())
                    .setCurrentWinningBidderId(auction.getCurrentWinningBidderId())
                    .setLastUpdateTimestamp(auction.getLastUpdateTimestamp())
                    .setTotalAuctionBids(auction.getTotalAuctionBids())
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }
}