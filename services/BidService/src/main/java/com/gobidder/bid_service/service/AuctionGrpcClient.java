package com.gobidder.bid_service.service;

import io.grpc.ManagedChannel;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.gobidder.bid_service.proto.*;

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

    public GetAuctionResponse getAuction(String auctionId) {
        // Hardcoded test auctions
        switch (auctionId) {
            case "auction1":
                return GetAuctionResponse.newBuilder()
                        .setAuctionId("auction1")
                        .setAuctionType("FORWARD")
                        .setCurrentPrice(100.00)
                        .setIsActive(true)
                        .setCurrentWinningBidderId("")
                        .setLastUpdateTimestamp(System.currentTimeMillis())
                        .setTotalAuctionBids(0)
                        .build();

            case "auction2":
                return GetAuctionResponse.newBuilder()
                        .setAuctionId("auction2")
                        .setAuctionType("DUTCH")
                        .setCurrentPrice(500.00)
                        .setIsActive(true)
                        .setCurrentWinningBidderId("")
                        .setLastUpdateTimestamp(System.currentTimeMillis())
                        .setTotalAuctionBids(0)
                        .build();

            case "auction3":
                return GetAuctionResponse.newBuilder()
                        .setAuctionId("auction3")
                        .setAuctionType("FORWARD")
                        .setCurrentPrice(250.00)
                        .setIsActive(true)
                        .setCurrentWinningBidderId("user123")
                        .setLastUpdateTimestamp(System.currentTimeMillis())
                        .setTotalAuctionBids(3)
                        .build();

            default:
                // For all other auction IDs, make the gRPC call
                GetAuctionRequest request = GetAuctionRequest.newBuilder()
                        .setAuctionId(auctionId)
                        .build();

                try {
                    log.info("Bid Service: Auction GRPC Client: Attempting to get auction");
                    return blockingStub.getAuction(request);
                } catch (Exception e) {
                    log.error("Bid Service: Auction GRPC Client: Error getting auction details for auctionId: {} ", auctionId, e);
                    throw new RuntimeException("Bid Service: Auction GRPC Client: ", e);
                }
        }
    }

//    public GetAuctionResponse getAuction(String auctionId){
//        GetAuctionRequest request = GetAuctionRequest.newBuilder().setAuctionId(auctionId).build();
//
//        try {
//            return blockingStub.getAuction(request);
//        }
//        catch (Exception e){
//            log.error("Bid Service: Auction GRPC Client: Error getting auction details for auctionId: {} ", auctionId, e);
//            throw new RuntimeException("Bid Service: Auction GRPC Client: ", e);
//        }
//    }
}
