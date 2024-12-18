package com.gobidder.bid_service.config;

import net.devh.boot.grpc.server.config.GrpcServerProperties;
import io.grpc.ServerBuilder;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcServerConfig {

    @Bean
    public GrpcServerConfigurer serverConfigurer() {
        return serverBuilder -> {
            if (serverBuilder instanceof ServerBuilder) {
                ((ServerBuilder<?>) serverBuilder)
                        .maxInboundMessageSize(10 * 1024 * 1024)
                        .maxInboundMetadataSize(4096)
                        .directExecutor();
            }
        };
    }
}