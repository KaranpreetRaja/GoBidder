"use client"

import { useEffect, useState } from "react";
import { useRouter } from "next/compat/router";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

import { auctionAxios, authAxios } from "@/lib/axios";
import { isAxiosError } from "axios";
import Link from "next/link";

import { useAuth } from "@/lib/auth_context";
import Image from "next/image";
import { Input } from "@/components/ui/input";

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

export default function AuctionDisplay({
  params,
}: {
  params: { auctionId: string }
}) {
  const { isAuthenticated } = useAuth();

  const auctionId = params.auctionId;

  console.log(`!!!: ${auctionId}`);

  const [auction, setAuction] = useState<AuctionType | undefined>(undefined);
  const [bidAmount, setBidAmount] = useState<number>(0);

  const router = useRouter();

  const getAuction = async () => {
    try {
      const response = await auctionAxios.get(
        `/api/auction/${auctionId}`
      );

      setAuction(response.data);
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
    }
  };

  const startAuction = async () => {
    const response = await auctionAxios.post(
      `/api/auction/${auctionId}/start`
    );
  };

  const placeBid = async () => {
    const userResponse = await authAxios.get("/api/users/me");
    const userId = userResponse.data.id;
    console.log(userId);
    const response = await auctionAxios.post(
      `/auction/${auctionId}/bid`,
      {
        "userId": userId,
        "bid": bidAmount
      }
    )
    console.log(response.data)
  };

  useEffect(() => {
    getAuction();
  }, []);

  if (auction == undefined) {
    return (
      <div>
        <p>Loading...</p>
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-5xl font-bold">{auction.name}</h1>
      <p className="text-xl">{auction.description}</p>
      <Image
        src={auction.auctionImageUrl ?? "/no_image.png"}
        style={{ width: "50%" }}
        width={500}
        height={500}
        alt={`Auction image for auction ${auction.name}`}
      />
      <p><span className="font-bold">Type: </span>{auction.type}</p>
      <p><span className="font-bold">Status: </span>{auction.status}</p>
      <p><span className="font-bold">Current Price: </span>{auction.currentPrice}</p>
      {auction.status == "NOT_STARTED"
        ? (<Button onClick={startAuction}>Start Auction</Button>)
        : (
          <>
            <Input
              value={bidAmount}
              onChange={(e) => setBidAmount(Number(e.target.value))}
            />
            <Button onClick={placeBid}>Place Bid</Button>
          </>
        )
      }
    </div>
  );
}
