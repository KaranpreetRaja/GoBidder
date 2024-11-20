import React, { useState, useEffect } from "react";
import "./DutchAuction.css";

interface DutchAuctionItem {
    name: string;
    timeLeft: number;
    currentPrice: number;
    decrementPrice: number; // This is the amount the price will be lowered by
}

const Auction: React.FC = () => {
    const [auctionItem, setAuctionItem] = useState<DutchAuctionItem>({
        // This is where the logic for getting the Java auction information is

    });

    const [newBid, setNewBid] = useState<number | string>("");
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);

    useEffect(() => {
        const timer = setInterval(() => {
           setDutchAuctionItem((prev) => {
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

    useEffect(() => {
        if (DutchAuctionItem.timeLeft > 0 && !timerId) {
            const id = setInterval(() => {
                setDutchAuctionItem((prev) => ({
                    ...prev,
                    currentPrice: prev.currentPrice - decrementPrice,
                }));
            }, incrementInterval * 1000);
            setTimerId(id);
        }

        return () => {
            if (timerId) clearInterval(timerId);
        };
    }, [DutchAuctionItem.timeLeft, timerId]);

    const purchase = () => {
        try {
            setLoading(true);
            setError(null);

            setTimeout(() => {
                setDutchAuctionItem((prev) => ({
                   ...prev,
                   timeLeft: 0,
                }));

                if (timerId) {
                    clearInterval(timerId);
                    // Stops decrementer
                }
            setTimerId(null);
            setLoading(false);
            }, 500);
        } catch {
            setError("Error buying item.");
            setLoading(false);
        }
    };

    const formatTimeLeft = (seconds: number) => {
        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = seconds % 60;
        return '${hours}h ${minutes}m ${secs}s';
    };

    return (
        <div className="dutchAuction-container">
            <h2>Auction</h2>
            <div className="item-name">{DutchAuctionItem.name}</div>
            <div className="time-left">
                Time Left: {formatTimeLeft(DutchAuctionItem.timeLeft)}
            </div>
            <div className="current-price">
                Current Price: ${DutchAuctionItem.currentPrice.toFixed(2)}
            </div>
            {error && <div className="error-message">{error}</div>}
            <button
                className="bid-button"
                onClick={purchase}
                disabled={loading || auctionItem.timeLeft === 0}
            >
                {loading ? "Finalizing..." : "Place Bid"}
            </button>
        </div>

    );
};