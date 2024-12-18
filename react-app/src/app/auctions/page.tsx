"use client"

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { auctionAxios } from "@/lib/axios";
import { isAxiosError } from "axios";
import Link from "next/link";

import { useAuth } from "@/lib/auth_context";
import Image from "next/image";

interface AuctionType {
  id: number,
  name: string,
  description: string,
  status: string,
  type: string,
  currentPrice: number,
  auctionImageUrl: string | null,
  totalBids: number
};

export default function Home() {
  const { isAuthenticated } = useAuth();

  const [auctions, setAuctions] = useState<AuctionType[]>([]);

  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const getAuctions = async () => {
    setLoading(true);

    try {
      const response = await auctionAxios.get(
        '/api/auction/'
      );

      setAuctions(response.data);
    } catch (error) {
      if (isAxiosError(error)) {
        // Narrowed down to AxiosError
        console.error('Get auctions failed:', error.response?.data || error.message);
        alert(error.response?.data?.message || 'An error occurred.');
      } else if (error instanceof Error) {
        // Handle other types of errors
        console.error('Unexpected error:', error.message);
        alert('An unexpected error occurred. Please try again.');
      } else {
        console.error('Unknown error:', error);
        alert('An unknown error occurred.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getAuctions();
  }, []);

  return (
    <div>
      <h1 className="text-5xl text-center font-bold">Auctions</h1>
      <div className="grid grid-cols-4">
        {auctions.map((auction, index) => (
          <Card key={index}>
            <CardHeader>
              <CardTitle>{auction.name}</CardTitle>
              <CardDescription>
                {auction.description}
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Image
                src={auction.auctionImageUrl ?? "/no_image.png"}
                style={{ width: "100%" }}
                width={500}
                height={500}
                alt={`Auction image for auction ${auction.name}`}
              />
              <p><span className="font-bold">Type: </span>{auction.type}</p>
              <p><span className="font-bold">Status: </span>{auction.status}</p>
              <p><span className="font-bold">Current Price: </span>{auction.currentPrice}</p>
              <Link href={`/auctions/${auction.id}`}>
                <Button>View Details</Button>
              </Link>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  );
}
