import React, { useState, useEffect } from "react";
import "./ForwardAuction.css";

interface ForwardAuctionItem {
    id: number;
    name: string;
    timeLeft: number;
    highestBid: number;
    status: string;
    duration: number;
    startTime: string;
}

const Auction: React.FC = () => {
    const [auctionItem, setAuctionItem] = useState<ForwardAuctionItem>({
        id: 0,
        name: "",
        timeLeft: 0,
        highestBid: 0,
        status: "",
        duration: 0,
        startTime: ""
    });

    const [newBid, setNewBid] = useState<number | string>("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        const fetchAuctionData = async () => {
            try {
                const response = await fetch('http://localhost:8083/auction/1');
                if (!response.ok) {
                    throw new Error('Failed to fetch auction data');
                }
                const data = await response.json();
                
                setAuctionItem({
                    id: data.id,
                    name: data.name,
                    timeLeft: calculateTimeLeft(data.startTime, data.duration),
                    highestBid: data.currentPrice,
                    status: data.status,
                    duration: data.duration,
                    startTime: data.startTime
                });
            } catch (err) {
                setError("Failed to load auction data");
            }
        };

        fetchAuctionData();
        const pollInterval = setInterval(fetchAuctionData, 5000);
        
        return () => clearInterval(pollInterval);
    }, []);

    const calculateTimeLeft = (startTime: string, duration: number) => {
        const start = new Date(startTime).getTime();
        const now = new Date().getTime();
        const end = start + (duration * 1000);
        return Math.max(0, Math.floor((end - now) / 1000));
    };

    const handlePlaceBid = async () => {
        try {
            setLoading(true);
            setError(null);

            const bidAmount = Number(newBid);
            if (bidAmount <= auctionItem.highestBid) {
                throw new Error('Bid must be higher than current highest bid');
            }

            const response = await fetch(`http://localhost:8083/auction/${auctionItem.id}/bid`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: 1, // Should come from auth system
                    bid: bidAmount
                })
            });

            if (!response.ok) {
                throw new Error('Failed to place bid');
            }

            const updatedAuction = await response.json();
            setAuctionItem(prev => ({
                ...prev,
                highestBid: updatedAuction.currentPrice
            }));
            setNewBid("");

        } catch (err) {
            setError(err instanceof Error ? err.message : "Error placing bid");
        } finally {
            setLoading(false);
        }
    };

    const formatTimeLeft = (seconds: number) => {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;
        return `${hours}h ${minutes}m ${secs}s`;
    };

    return (
        <div className="forwardAuction-container">
            <h2>Auction</h2>
            <div className="item-name">{auctionItem.name}</div>
            <div className="time-left">
                Time Left: {formatTimeLeft(auctionItem.timeLeft)}
            </div>
            <div className="highest-bid">
                Highest Bid: ${auctionItem.highestBid.toFixed(2)}
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
                    min={auctionItem.highestBid + 1}
                    placeholder={`Your bid must be higher than: $${auctionItem.highestBid}`}
                />
            </div>
            <button
                className="bid-button"
                onClick={handlePlaceBid}
                disabled={loading || auctionItem.timeLeft === 0}
            >
                {loading ? "Placing bid..." : "Place Bid"}
            </button>
        </div>
    );
};

export default Auction;