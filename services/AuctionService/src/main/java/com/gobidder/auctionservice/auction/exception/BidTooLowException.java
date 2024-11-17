package com.gobidder.auctionservice.auction.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bid is too low")
public class BidTooLowException extends RuntimeException {

}
