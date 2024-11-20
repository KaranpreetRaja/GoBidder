import React, { useState, useEffect } from "react";
import "./auction.css";

interface ForwardAuctionItem {
    name: string;
    timeLeft: number;
    currentBid: number;
}

const Auction: React.FC = () => {
    const [auctionItem, setAuctionItem] = useState<ForwardAuctionItem>({
        // This is where the logic for getting the Java auction information is

    });

    const [newBid, setNewBid] = useState<number | string>("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        const timer = setInterval(() => {
           setForwardAuctionItem((prev) => {
               if (prev.timeLeft > 0) {
                   return { ...prev, timeLeft: prev.timeLeft-1 };
               }
               else {
                   clearInterval(timer);
                   return prev;
               }
           });
        }, 1000);
        return () => clearInterval(timer);
    }, []);

    const formatTimeLeft = (seconds: number) => {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;
        return '${hours}h ${minutes}m ${secs}s';
    };

    return (
        <div className="forwardAuction-container">
            <h2>Auction</h2>
            <div className="item-name">{ForwardAuctionItem.name}</div>
            <div className="time-left">
                Time Left: {formatTimeLeft(ForwardAuctionItem.timeLeft)}
            </div>
            <div className="highest-bid">
                Highest Bid: ${ForwardAuctionItem.currentBid.toFixed(2)}
            </div>
            {error && <div className="error-message">{error}</div>}
            <div className="form-group">
                <label htmlFor="bid">Your Bid:</label>
                <input
                    id="bid"
                    name="bid"
                    type="number"
                    value={newBid}
                    onChange={(e) => setNewBid(e.target.value)}
                    min={auctionItem.currentBid+1}
                    placeholder={'Your bid must be higher than: $${ForwardAuctionItem.currentBid}'}
                />
            </div>
            <button
                className="place-bid-button"
                onClick={handlePlaceBid}
                disabled={loading || ForwardAuctionItem.timeLeft === 0}
            >
                {loading ? "Placing bid..." : "Place Bid"}
            </button>
        </div>

    );
};