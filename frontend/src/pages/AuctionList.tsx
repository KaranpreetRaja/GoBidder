import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./AuctionList.css";

interface AuctionItem {
    id: string;
    name: string;
    highestBid: number;
    timeLeft: number;
}

const AuctionList: React.FC = () => {
    const [auctionItems, setAuctionItems] = useState<AuctionItem[]>([]);

    useEffect(() => {
        const fetchAuctionItems = async () => {
            try {
                const response = await fetch("http://localhost:8081/auction/items");
                const data = await response.json();
                setAuctionItems(data);
            } catch (error) {
                console.error("Error fetching items list: ", error);
            }
        };

        fetchAuctionItems();
    },[]);

    const formatTimeLeft = (seconds: number) => {
            const hours = Math.floor(seconds / 3600);
            const minutes = Math.floor((seconds % 3600) / 60);
            const secs = seconds % 60;
            return '${hours}h ${minutes}m ${secs}s';
    };

    return (
        <div className="auction-list-container">
            <h1>Current Auctions</h1>
            <div className="auction-grid">
                {auctionItems.map((item) => (
                    <Link
                        to={'/auction/${item.id}'}
                        className="auction-card"
                        key={item.id}

                        <h2>{item.name}</h2>
                        <p>Highest Bid/Current Price: ${item.highestBid.toFixed(2)}</p>
                        <p>Time Left: {formatTimeLeft(item.timeLeft)}</p>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default AuctionList;