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

    public GetAuctionResponse getAuction(String auctionId){
        GetAuctionRequest request = GetAuctionRequest.newBuilder().setAuctionId(auctionId).build();

        try {
            return blockingStub.getAuction(request);
        }
        catch (Exception e){
            log.error("Bid Service: Auction GRPC Client: Error getting auction details for auctionId: {} ", auctionId, e);
            throw new RuntimeException("Bid Service: Auction GRPC Client: ", e);
        }
    }
}
