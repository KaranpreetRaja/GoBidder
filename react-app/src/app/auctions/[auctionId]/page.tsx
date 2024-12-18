"use server"

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

import { auctionAxios } from "@/lib/axios";
import { isAxiosError } from "axios";
import Link from "next/link";

import { useAuth } from "@/lib/auth_context";
import Image from "next/image";
import AuctionDisplay from "./display";

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
  const auctionId = (await params).auctionId;
  console.log(`GO: ${auctionId}`);

  return (
    <AuctionDisplay params={{ auctionId }} />
  );
}
