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

var (
    upgrader = websocket.Upgrader{
        ReadBufferSize:  1024,
        WriteBufferSize: 1024,
        CheckOrigin: func(r *http.Request) bool {
            return true
        },
    }
)

func NewNotificationService() *NotificationService {
    return &NotificationService{
        subscribers: make(map[string]map[*Client]bool),
        batchBuffer: make(map[string]*BatchedMessage),
    }
}

func (ns *NotificationService) handleWebSocket(w http.ResponseWriter, r *http.Request) {
    auctionId := r.URL.Query().Get("auction_id")
    if auctionId == "" {
        http.Error(w, "auction_id is required", http.StatusBadRequest)
        return
    }

    conn, err := upgrader.Upgrade(w, r, nil)
    if err != nil {
        atomic.AddInt64(&ns.metrics.errors, 1)
        log.Printf("Failed to upgrade connection: %v", err)
        return
    }

    client := &Client{
        conn: conn,
        send: make(chan []byte, 256),
    }

    ns.subscribe(auctionId, client)
    atomic.AddInt64(&ns.metrics.activeConnections, 1)
    log.Printf("New client subscribed to auction %s", auctionId)

    go client.writePump(ns)
    go client.readPump(ns, auctionId)
}

func (ns *NotificationService) subscribe(auctionId string, client *Client) {
    ns.mu.Lock()
    defer ns.mu.Unlock()

    if ns.subscribers[auctionId] == nil {
        ns.subscribers[auctionId] = make(map[*Client]bool)
    }
    ns.subscribers[auctionId][client] = true
}

func (ns *NotificationService) unsubscribe(auctionId string, client *Client) {
    ns.mu.Lock()
    defer ns.mu.Unlock()

    if clients, ok := ns.subscribers[auctionId]; ok {
        delete(clients, client)
        close(client.send)
        if len(clients) == 0 {
            delete(ns.subscribers, auctionId)
        }
    }
    atomic.AddInt64(&ns.metrics.activeConnections, -1)
}

func (ns *NotificationService) batchAndBroadcast(auctionId string, message []byte) {
    ns.batchMu.Lock()
    defer ns.batchMu.Unlock()

    batch, exists := ns.batchBuffer[auctionId]
    if !exists {
        batch = &BatchedMessage{
            messages: make([][]byte, 0),
            timer: time.NewTimer(100 * time.Millisecond),
        }
        ns.batchBuffer[auctionId] = batch
        
        go func() {
            <-batch.timer.C
            ns.processBatch(auctionId)
        }()
    }
    
    batch.messages = append(batch.messages, message)
}

func (ns *NotificationService) processBatch(auctionId string) {
    ns.batchMu.Lock()
    batch := ns.batchBuffer[auctionId]
    delete(ns.batchBuffer, auctionId)
    ns.batchMu.Unlock()

    if len(batch.messages) > 0 {
        ns.mu.RLock()
        clients := ns.subscribers[auctionId]
        ns.mu.RUnlock()

        for client := range clients {
            for _, msg := range batch.messages {
                select {
                case client.send <- msg:
                    atomic.AddInt64(&ns.metrics.messagesSent, 1)
                    log.Printf("Batched message sent to client for auction %s", auctionId)
                default:
                    ns.unsubscribe(auctionId, client)
                    client.conn.Close()
                    atomic.AddInt64(&ns.metrics.errors, 1)
                }
            }
        }
    }
}

func (c *Client) writePump(ns *NotificationService) {
    ticker := time.NewTicker(60 * time.Second)
    defer func() {
        ticker.Stop()
        c.conn.Close()
    }()

    for {
        select {
        case message, ok := <-c.send:
            if !ok {
                c.conn.WriteMessage(websocket.CloseMessage, []byte{})
                return
            }
            if err := c.conn.WriteMessage(websocket.TextMessage, message); err != nil {
                log.Printf("Error writing message: %v", err)
                atomic.AddInt64(&ns.metrics.errors, 1)
                return
            }
        case <-ticker.C:
            if err := c.conn.WriteMessage(websocket.PingMessage, nil); err != nil {
                return
            }
        }
    }
}

func (c *Client) readPump(ns *NotificationService, auctionId string) {
    defer func() {
        ns.unsubscribe(auctionId, c)
        c.conn.Close()
    }()

    c.conn.SetReadDeadline(time.Now().Add(60 * time.Second))
    c.conn.SetPongHandler(func(string) error {
        c.conn.SetReadDeadline(time.Now().Add(60 * time.Second))
        return nil
    })

    for {
        _, _, err := c.conn.ReadMessage()
        if err != nil {
            if websocket.IsUnexpectedCloseError(err, websocket.CloseGoingAway, websocket.CloseAbnormalClosure) {
                log.Printf("error: %v", err)
                atomic.AddInt64(&ns.metrics.errors, 1)
            }
            break
        }
    }
}

func (ns *NotificationService) handleMetrics(w http.ResponseWriter, r *http.Request) {
    metrics := map[string]int64{
        "active_connections": atomic.LoadInt64(&ns.metrics.activeConnections),
        "messages_sent":      atomic.LoadInt64(&ns.metrics.messagesSent),
        "messages_received":  atomic.LoadInt64(&ns.metrics.messagesReceived),
        "errors":            atomic.LoadInt64(&ns.metrics.errors),
    }
    
    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(metrics)
}

func (ns *NotificationService) startKafkaConsumer() {
    kafkaServer := os.Getenv("KAFKA_BOOTSTRAP_SERVERS")
    if kafkaServer == "" {
        kafkaServer = "kafka:29092" // default to Kafka service in Docker Compose
    }

    batchSize := 10
    if bs := os.Getenv("KAFKA_BATCH_SIZE"); bs != "" {
        if i, err := strconv.Atoi(bs); err == nil {
            batchSize = i
        }
    }

    reader := kafka.NewReader(kafka.ReaderConfig{
        Brokers:   []string{kafkaServer},
        Topic:     "bid-updates",
        GroupID:   "notification-service-group",
        MinBytes:  10e3,
        MaxBytes:  10e6,
        BatchSize: batchSize,
    })

    defer reader.Close()

    for {
        msg, err := reader.ReadMessage(context.Background())
        if err != nil {
            log.Printf("Error reading Kafka message: %v", err)
            atomic.AddInt64(&ns.metrics.errors, 1)
            continue
        }

        atomic.AddInt64(&ns.metrics.messagesReceived, 1)

        var bidUpdate BidUpdateMessage
        if err := json.Unmarshal(msg.Value, &bidUpdate); err != nil {
            log.Printf("Error unmarshaling message: %v", err)
            log.Printf("Raw message: %s", string(msg.Value))
            atomic.AddInt64(&ns.metrics.errors, 1)
            continue
        }

        log.Printf("Received bid update for auction %s: %.2f from bidder %s", 
            bidUpdate.AuctionId, bidUpdate.NewAmount, bidUpdate.BidderId)

        ns.batchAndBroadcast(bidUpdate.AuctionId, msg.Value)
    }
}

func main() {
    log.Printf("Notification Service Starting...")
    
    port := os.Getenv("PORT")
    if port == "" {
        port = "8085" // default to port 8085
    }

    ns := NewNotificationService()

    go ns.startKafkaConsumer()
    log.Printf("Kafka consumer started")

    // Subscribe Endpoint: GET /ws/subscribe?auction_id=123 (WebSocket)
    http.HandleFunc("/ws/subscribe", ns.handleWebSocket)

    // Health Endpoint: GET /health (HTTP)
    http.HandleFunc("/health", func(w http.ResponseWriter, r *http.Request) {
        w.WriteHeader(http.StatusOK)
        w.Write([]byte("OK"))
    })

    // Metrics Endpoint: GET /metrics (HTTP)
    http.HandleFunc("/metrics", ns.handleMetrics)
    
    
    log.Printf("Starting server on :%s", port)
    if err := http.ListenAndServe(":"+port, nil); err != nil {
        log.Fatal("ListenAndServe: ", err)
    }
}