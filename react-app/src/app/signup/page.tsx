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

export default function Home() {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <Card className="w-full max-w-4xl p-6">
        <CardHeader>
          <CardTitle>Sign Up</CardTitle>
          <CardDescription>Deploy your new project in one-click.</CardDescription>
        </CardHeader>
        <CardContent>
          <form>
            {/* Two-column layout */}
            <div className="grid grid-cols-2 gap-8">
              {/* Left Column */}
              <div className="flex flex-col space-y-4">
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="firstName">First Name</Label>
                  <Input id="firstName" placeholder="" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="lastName">Last Name</Label>
                  <Input id="lastName" placeholder="" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="streetAddress">Street Address</Label>
                  <Input id="streetAddress" placeholder="" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="streetNumber">Street Number</Label>
                  <Input id="streetNumber" placeholder="" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="postalCode">Postal Code</Label>
                  <Input id="postalCode" placeholder="" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="city">City</Label>
                  <Input id="city" placeholder="" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="country">Country</Label>
                  <Input id="country" placeholder="" />
                </div>
              </div>
              {/* Right Column */}
              <div className="flex flex-col space-y-4">
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="username">Username</Label>
                  <Input id="username" placeholder="" />
                </div>
                <div className="flex flex-col space-y-1.5">
                  <Label htmlFor="password">Password</Label>
                  <Input id="password" type="password" placeholder="" />
                </div>
              </div>
            </div>
          </form>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Button variant="outline">Go Back</Button>
          <Button>Submit</Button>
        </CardFooter>
      </Card>
    </div>
  );
}
