"use client";

import Image from "next/image";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import Link from "next/link";

export default function Home() {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <Card className="w-full max-w-4xl p-6">
        <CardHeader>
          <CardTitle>Sign Up</CardTitle>
        </CardHeader>
        <CardContent>
          <form>
            {/* Two-column layout */}
            <div className="grid grid-cols-2 gap-8">
              {/* Left Column */}
              <div className="flex flex-col space-y-4">
              <h2 className="text-2xl font-bold underline text-gray-700">
                Winning Bidder
              </h2>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="firstName">First Name: </Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="lastName">Last Name: </Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="streetAddress">Street Address: </Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="streetNumber">Street Number: </Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="postalCode">Province: </Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="postalCode">Country: </Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="postalCode">Postal Code: </Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="city">Total Cost: $</Label>
                </div>
              </div>
              {/* Right Column */}
              <div className="flex flex-col space-y-4">
              <h2 className="text-2xl font-bold text-gray-700">Credit Card</h2>
                <div className="flex flex-col space-y-1.5">
                  <Input id="cardNumber" placeholder="" />
                  <Label htmlFor="cardNumber">Card Number</Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Input id="nameCard" placeholder="" />
                  <Label htmlFor="nameCard">Name on Card</Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Input id="expiryDate" placeholder="" />
                  <Label htmlFor="expiryDate">Expiry Date</Label>
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Input id="securityCode" placeholder="" />
                  <Label htmlFor="securityCode">Security Code</Label>
                </div>
              </div>
            </div>
          </form>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Link href="/">
            <Button variant="outline">Go Back</Button>
          </Link>
          <Button>Submit</Button>
        </CardFooter>
      </Card>
    </div>
  );
}
