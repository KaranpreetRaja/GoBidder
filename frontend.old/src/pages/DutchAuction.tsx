import React, { useState, useEffect } from "react";
import "./DutchAuction.css";

interface DutchAuctionItem {
    name: string;
    timeLeft: number;
    currentPrice: number;
    status: string;
    initialPrice: number;
    minimumPrice: number;
}

const Auction: React.FC = () => {
    const [auctionItem, setAuctionItem] = useState<DutchAuctionItem>({
        name: "",
        timeLeft: 0,
        currentPrice: 0,
        status: "",
        initialPrice: 0,
        minimumPrice: 0
    });

    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [timerId, setTimerId] = useState<number | null>(null);

    // Fetch auction data initially and set up polling
    useEffect(() => {
        const fetchAuctionData = async () => {
            try {
                const response = await fetch('http://localhost:8083/auction/1');
                if (!response.ok) {
                    throw new Error('Failed to fetch auction data');
                }
                const data = await response.json();
                
                setAuctionItem({
                    name: data.name,
                    timeLeft: calculateTimeLeft(data.startTime),
                    currentPrice: data.currentPrice,
                    status: data.status,
                    initialPrice: data.initialPrice,
                    minimumPrice: data.minimumPrice
                });
            } catch (err) {
                setError("Failed to load auction data");
            }
        };

        fetchAuctionData();
        // Poll for updates every 5 seconds
        const pollInterval = setInterval(fetchAuctionData, 5000);
        
        return () => clearInterval(pollInterval);
    }, []);

    const calculateTimeLeft = (startTime: string) => {
        const start = new Date(startTime).getTime();
        const now = new Date().getTime();
        return Math.max(0, Math.floor((start - now) / 1000));
    };

    const purchase = async () => {
        try {
            setLoading(true);
            setError(null);

            const response = await fetch(`http://localhost:8083/auction/1/bid`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    userId: 1,
                    bid: auctionItem.currentPrice
                })
            });

            if (!response.ok) {
                throw new Error('Failed to place bid');
            }

            // Update auction state after successful bid
            const updatedAuction = await response.json();
            setAuctionItem(prev => ({
                ...prev,
                status: updatedAuction.status,
                currentPrice: updatedAuction.currentPrice
            }));

        } catch (err) {
            setError("Error placing bid");
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
        <div className="dutchAuction-container">
            <h2>Auction</h2>
            <div className="item-name">{auctionItem.name}</div>
            <div className="time-left">
                Time Left: {formatTimeLeft(auctionItem.timeLeft)}
            </div>
            <div className="current-price">
                Current Price: ${auctionItem.currentPrice.toFixed(2)}
            </div>
            {error && <div className="error-message">{error}</div>}
            <button
                className="bid-button"
                onClick={purchase}
                disabled={loading || auctionItem.status !== 'ACTIVE'}
            >
                {loading ? "Finalizing..." : "Place Bid"}
            </button>
        </div>
    );
};

export default Auction;