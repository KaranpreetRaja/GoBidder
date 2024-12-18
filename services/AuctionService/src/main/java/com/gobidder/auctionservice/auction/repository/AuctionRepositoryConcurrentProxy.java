package com.gobidder.auctionservice.auction.repository;

import com.gobidder.auctionservice.auction.Auction;
import com.gobidder.auctionservice.auction.AuctionStatusEnum;
import com.gobidder.auctionservice.auction.dto.BidUpdateMessage;
import com.gobidder.auctionservice.bidder.Bidder;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;
import java.util.List;

public class AuctionRepositoryConcurrentProxy implements AuctionRepository {
    private final ReentrantLock lock = new ReentrantLock(true);
    private final AuctionRepository repository;

    public AuctionRepositoryConcurrentProxy(AuctionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Auction create(Auction auction) {
        try {
            lock.lock();
            return repository.create(auction);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(Long id) {
        try {
            lock.lock();
            repository.delete(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Auction findById(Long auctionId) {
        try {
            lock.lock();
            return repository.findById(auctionId);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean existsByName(String name) {
        try {
            lock.lock();
            return repository.existsByName(name);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Auction> findAll() {
        try {
            lock.lock();
            return repository.findAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Auction updateStatus(Long auctionId, AuctionStatusEnum status) {
        try {
            lock.lock();
            return repository.updateStatus(auctionId, status);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Auction startAuction(Long auctionId, LocalDateTime start) {
        try {
            lock.lock();
            return repository.startAuction(auctionId, start);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Auction updatePrice(Long auctionId, Double price) {
        try {
            lock.lock();
            return repository.updatePrice(auctionId, price);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Auction updateHighestBidder(Long auctionId, Bidder bidder) {
        try {
            lock.lock();
            return repository.updateHighestBidder(auctionId, bidder);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Auction updateHighestBidder(Long auctionId, BidUpdateMessage message) {
        try {
            lock.lock();
            return repository.updateHighestBidder(auctionId, message);
        } finally {
            lock.unlock();
        }
    }
}
