package com.gobidder.bid_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BidServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BidServiceApplication.class, args);
	}

}
