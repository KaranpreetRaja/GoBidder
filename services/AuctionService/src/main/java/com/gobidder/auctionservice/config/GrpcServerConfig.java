package com.gobidder.auctionservice.config;

import io.grpc.ServerBuilder;
import net.devh.boot.grpc.server.config.GrpcServerProperties;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {

    @Bean
    public GrpcServerConfigurer serverConfigurer() {
        return serverBuilder -> {
            if (serverBuilder != null) {
                ((ServerBuilder<?>) serverBuilder)
                        .maxInboundMessageSize(10 * 1024 * 1024) // 10MB
                        .maxInboundMetadataSize(4096) // 4KB
                        .directExecutor();
            }
        };
    }
}