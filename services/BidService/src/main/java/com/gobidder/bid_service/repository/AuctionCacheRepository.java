package com.gobidder.bid_service.repository;

import com.gobidder.bid_service.model.AuctionCacheModel;
import org.springframework.data.repository.CrudRepository;

public interface AuctionCacheRepository extends CrudRepository<AuctionCacheModel, String> {
}