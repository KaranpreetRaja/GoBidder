import websocket
import json
import time
import requests
from threading import Thread

class NotificationServiceTest:
    def __init__(self, base_url="http://localhost:8085"):
        self.base_url = base_url
        self.ws_url = f"ws://localhost:8085/ws/subscribe"

    def test_health(self):
        response = requests.get(f"{self.base_url}/health")
        print(f"Health check status: {response.status_code}")
        return response.status_code == 200

    def test_metrics(self):
        response = requests.get(f"{self.base_url}/metrics")
        if response.status_code == 200:
            print("Metrics:", json.dumps(response.json(), indent=2))
        return response.status_code == 200

    def websocket_client(self, auction_id):

        # process messages received from the WebSocket
        def on_message(ws, message):
            try:
                bid_update = json.loads(message)
                print("\nReceived bid update:")
                print(f"Auction ID: {bid_update['auctionId']}")
                print(f"New Amount: ${bid_update['newAmount']:.2f}")
                print(f"Bidder ID: {bid_update['bidderId']}")
                print(f"Total Bids: {bid_update['totalAuctionBids']}")
            except Exception as e:
                print(f"Error processing message: {e}")
                print(f"Raw message: {message}")

        def on_error(ws, error):
            print(f"WebSocket Error: {error}")

        def on_close(ws, close_status_code, close_msg):
            print(f"WebSocket closed. Code: {close_status_code}, Message: {close_msg}")

        def on_open(ws):
            print(f"WebSocket connected for auction {auction_id}")

        websocket.enableTrace(False)
        ws = websocket.WebSocketApp(
            f"{self.ws_url}?auction_id={auction_id}",
            on_message=on_message,
            on_error=on_error,
            on_close=on_close,
            on_open=on_open
        )
        
        while True:
            try:
                ws.run_forever()
                print("Connection was lost. Reconnecting in 5 seconds...")
                time.sleep(5)
            except KeyboardInterrupt:
                print("Stopping WebSocket client...")
                break
            except Exception as e:
                print(f"Error: {e}")
                time.sleep(5)

def main():
    tester = NotificationServiceTest()
    
    print("Testing health endpoint")
    tester.test_health()
    
    print("\n\nTesting metrics endpoint")
    tester.test_metrics()
    
    print("\n\nstarted websocket client")
    try:
        tester.websocket_client("55") # testing with auction id 55
    except KeyboardInterrupt:
        print("\nTests completed.")

if __name__ == "__main__":
    main()