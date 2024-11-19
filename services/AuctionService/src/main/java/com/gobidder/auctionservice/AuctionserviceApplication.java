package com.gobidder.auctionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuctionserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuctionserviceApplication.class, args);
	}

}
