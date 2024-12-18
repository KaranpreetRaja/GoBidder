"use server"

import { useEffect, useState } from "react";
import { useRouter } from "next/router";
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

export default async function Home({
  params,
}: {
  params: Promise<{ auctionId: string }>
}) {
  const { isAuthenticated } = useAuth();
  const auctionId = (await params).auctionId;
  const [auction, setAuction] = useState<AuctionType | undefined>(undefined);

  const router = useRouter();

  const getAuction = async () => {
    try {
      const response = await auctionAxios.get(
        `/api/auction/${router?.query?.slug}`
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
      <h1>{auction.name}</h1>
      <p>{auction.description}</p>
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
    </div>
  );
}
