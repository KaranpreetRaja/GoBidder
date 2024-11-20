package com.gobidder.bid_service.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GrpcConfig {
    @Value("${grpc.auction.host:auction-service}")
    private String host;

    @Value("${grpc.auction.port:9090}")
    private int port;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress("bid-service", 9090).usePlaintext().build();
    }
}
