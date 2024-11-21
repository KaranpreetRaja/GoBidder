#!/bin/sh

# Define services to wait for
SERVICES="auction-service auth-service payment-service frontend"

# Function to check if a service is reachable
check_service() {
    ping -c1 $1 &>/dev/null
}

# Wait for all services
for service in $SERVICES; do
    echo "Waiting for $service..."
    until check_service $service; do
        echo "$service is not ready yet..."
        sleep 3
    done
    echo "$service is ready!"
done

# Start nginx in foreground
echo "All services are ready - starting nginx"
nginx -g 'daemon off;'