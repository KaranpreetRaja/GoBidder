package com.gobidder.bid_service.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class GrpcClientConfig {
    @Value("${grpc.auction.host}")
    private String host;

    @Value("${grpc.auction.port}")
    private int port;

    @Bean
    public ManagedChannel managedChannel() {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }
}