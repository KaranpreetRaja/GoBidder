package com.gobidder.auctionservice.config;

import io.grpc.Context;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class GrpcContextConfig {

    @PostConstruct
    public void init() {
        Context.current();
    }
}