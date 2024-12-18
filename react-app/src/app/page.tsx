"use client";

import "./wave.css";
import "./invert.css"
import "./bounce.css"

import Head from "next/head";

import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label"
import { useRouter } from "next/navigation";
import Link from "next/link";

import {
  Carousel,
  CarouselContent,
  CarouselItem,
  CarouselNext,
  CarouselPrevious,
} from "@/components/ui/carousel"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

import { CircleGauge, Scale, Lock, ChevronDown } from "lucide-react";

export default function Home() {
  const router = useRouter();
  return (
    <div className="flex flex-col items-center justify-center">
      <div className="flex flex-col gap-4 items-center justify-center h-lvh">
        <h1 className="text-5xl font-bold invert-text">Bid Fast. Play Safe. Win Big.</h1>
        <p className="text-xl invert-text">Join the most reliable auction platform.</p>
        <Link href="/signup">
          <Button className="text-xl font-bold invert-text" variant="link">Sign up now</Button>
        </Link>
        <div>
          <div className="arrow-div">
            <Link href="#features-preview" className="arrow">
              <ChevronDown className="invert-text" size={128} />
            </Link>
          </div>
        </div>
      </div>
      <div id="features-preview" className="w-1/2">
        <Carousel>
          <CarouselContent>
            <CarouselItem>
              <div className="p-1">
                <div className="flex items-center justify-center p-6">
                  <CircleGauge size={64} className="pr-4" />
                  <div>
                    <span className="text-3xl font-semibold">Lightning-fast Bidding</span>
                    <p>The latest cutting-edge technologies allow GoBidder to work faster than its competitors.</p>
                  </div>
                </div>
              </div>
            </CarouselItem>
            <CarouselItem>
              <div className="p-1">
                <div className="flex items-center justify-center p-6">
                  <Scale size={64} className="pr-4" />
                  <div>
                    <span className="text-3xl font-semibold">Always Reliable.</span>
                    <p>Ensuring uptime and fairness in auctions is at the top of the Gobidder development team's mind.</p>
                  </div>
                </div>
              </div>
            </CarouselItem>
            <CarouselItem>
              <div className="p-1">
                <div className="flex items-center justify-center p-6">
                  <Lock size={64} className="pr-4" />
                  <div>
                    <span className="text-3xl font-semibold">Secure Transactions</span>
                    <p>GoBidder guarantees transaction safety for its users through technology and user agreements.</p>
                  </div>
                </div>
              </div>
            </CarouselItem>
          </CarouselContent>
          <CarouselPrevious />
          <CarouselNext />
        </Carousel>
      </div>
      <Card style={{ marginTop: "250px" }} className="w-1/2 mt-8">
        <CardHeader>
          <CardTitle>How it works.</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="grid grid-cols-5 gap-3">
            <div>
              <p>
                <span className="border-solid border-4 border-black rounded-full p-2 text-xl font-bold">
                  1
                </span>
              </p>
            </div>
            <div className="col-span-4">
              <h2 className="font-bold text-xl">
                Make an account.
              </h2>
              <p>
                You need an email and a password, plus payment/shipping details.
              </p>
            </div>
          </div>
          <div className="grid grid-cols-5 gap-3 mt-8">
            <div>
              <p>
                <span className="border-solid border-4 border-black rounded-full p-2 text-xl font-bold">
                  2
                </span>
              </p>
            </div>
            <div className="col-span-4">
              <h2 className="font-bold text-xl">
                Browse auctions.
              </h2>
              <p>
                GoBidder has anything you can think of: from sneakers to motorcycles, someone's selling it here.
              </p>
            </div>
          </div>
          <div className="grid grid-cols-5 gap-3 mt-8">
            <div>
              <p>
                <span className="border-solid border-4 border-black rounded-full p-2 text-xl font-bold">
                  3
                </span>
              </p>
            </div>
            <div className="col-span-4">
              <h2 className="font-bold text-xl">
                Place your bid.
              </h2>
              <p>
                GoBidder has anything you can think of: from sneakers to motorcycles, someone's selling it here.
              </p>
              <br />
              <p>GoBidder holds two types of auctions:</p>
              <ul className="list-disc list-inside">
                <li>
                  <span className="font-bold">Forward Auctions:</span> bidders increase their bid until all other bidders stop. Highest bidder wins.
                </li>
                <li>
                  <span className="font-bold">Dutch Auctions:</span> price decreases periodically until someone chooses to bid. First bidder wins.
                </li>
              </ul>
              <br />
              <p>Simple, easy, and safe. You only pay if you win the auction.</p>
            </div>
          </div>
        </CardContent>
      </Card>
      <div style={{ marginTop: "300px", marginBottom: "200px" }} className="flex flex-col gap-4 items-center justify-center">
        <h1 className="text-5xl font-bold invert-text mb-8">What are you waiting for?</h1>
        <Link href="/signup">
          <Button className="text-3xl font-bold invert-text p-8">Sign up</Button>
        </Link>
      </div>
      <div className="wave"></div>
      <div className="wave"></div>
      <div className="wave"></div>
    </div>
  );
}
