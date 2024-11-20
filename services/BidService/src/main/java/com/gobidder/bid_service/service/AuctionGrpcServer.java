package com.gobidder.bid_service.service;

import com.gobidder.bid_service.model.AuctionCacheModel;
import com.gobidder.bid_service.repository.AuctionCacheRepository;
import io.grpc.stub.StreamObserver;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

@GrpcService
@RequiredArgsConstructor
public class AuctionGrpcServer extends AuctionServiceImplBase {
    private final AuctionCacheRepository auctionCacheRepository;

    @Override
    public void initAuction(InitAuctionRequest request, StreamObserver<InitAuctionResponse> responseObserver) {
        try {
            AuctionCacheModel auctionCache = new AuctionCacheModel();
            auctionCache.setAuctionId(request.getAuctionId());
            auctionCache.setAuctionType(request.getAuctionType());
            auctionCache.setCurrentPrice(request.getStartingPrice());
            auctionCache.setActive(true);
            auctionCache.setLastUpdateTimestamp(System.currentTimeMillis());
            auctionCache.setTotalAuctionBids(0);

            auctionCacheRepository.save(auctionCache);

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