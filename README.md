# GoBidder
An open market auction website that facilitates conventional and Dutch auctions.

## Project Structure
```
.
├── frontend/                # React frontend application
│   ├── app.py               # Flask app
│   └── templates/           # Frontend templates
├── services/                # Backend microservices
│   ├── AuctionService/      # Handles auction operations
│   ├── AuthService/         # User authentication
|   ├── BidService/          # Handles bidding operations
│   ├── PaymentService/      # Payment processing
│   ├── Kafka/               # Message broker service
│   └── ReverseProxy/        # Nginx reverse proxy
└── docker-compose.yml  
```

## Prerequisites
- Docker and Docker Compose
- Git (for cloning the repository)

# Installation
1. Install Docker

```
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install docker.io docker-compose

# Windows/Mac
Download and install Docker Desktop from https://www.docker.com/products/docker-desktop
```

2. Clone the repository
```
git clone https://github.com/KaranpreetRaja/GoBidder.git
cd GoBidder
```

3. Start the application
```
docker compose up -d
```

## Services and Ports
- Frontend: http://localhost:3000
- Auth Service: http://localhost:8081
- Payment Service: http://localhost:8082
- Auction Service: http://localhost:8083
- Bid Service: http://localhost:8084
- Reverse Proxy: http://localhost:8080
- Kafka: http://localhost:9092

## Architecture
The application consists of several microservices:

- Auth Service: Handles user authentication and authorization
- Payment Service: Processes payment transactions
- Auction Service: Manages auction operations
- Bid Service: Manages bidding operations
- Reverse Proxy: Routes requests and handles CORS
- Kafka: Message broker for inter-service communication
- Frontend: User interface

## Development
To run individual services:
```
# Start specific service
docker compose up <service-name> -d

# Available services:
# - frontend
# - auth-service
# - payment-service
# - auction-service
# - bid-service
# - reverse-proxy
# - kafka
```

## Building from Source
Each service can be built individually using Maven (for Java services) or npm (for frontend):

```
# Java services
cd services/<ServiceName>
mvn clean package

# Frontend
cd frontend
npm install
npm run build
```
