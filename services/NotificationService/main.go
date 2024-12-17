package main

import (
    "context"
    "encoding/json"
    "log"
    "net/http"
    "os"
    "sync"
    "sync/atomic"
    "time"
    "strconv"

    "github.com/gorilla/websocket"
    "github.com/segmentio/kafka-go"
)

// BidUpdateMessage which is sent by the Kafka producer in the BidService
type BidUpdateMessage struct {
    AuctionId        string    `json:"auctionId"`
    NewAmount        float64   `json:"newAmount"`
    BidderId         string    `json:"bidderId"`
    Timestamp        int64     `json:"timestamp"`
    TotalAuctionBids int       `json:"totalAuctionBids"`
}

type BatchedMessage struct {
    messages [][]byte
    timer    *time.Timer
}

type Metrics struct {
    activeConnections  int64
    messagesSent      int64
    messagesReceived  int64
    errors            int64
}

type Client struct {
    conn *websocket.Conn
    send chan []byte
}

type NotificationService struct {
    subscribers map[string]map[*Client]bool
    mu         sync.RWMutex
    metrics    Metrics
    batchBuffer map[string]*BatchedMessage
    batchMu    sync.RWMutex
}
