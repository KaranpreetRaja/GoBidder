"use client";

import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label"
import { useRouter } from "next/navigation";
import Link from "next/link";

export default function Home() {
  const router = useRouter();
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <div className="grid grid-cols-2 gap-8">
        {/* Left Column */}
        <div className="flex flex-col space-y-4">
          <Label htmlFor="welcome">Welcome to GoBidder</Label>
        </div>
        {/* Right Column */}
        <div className="flex flex-col space-y-4">
          <Link href="/login">
            <Button>Sign-In</Button>
          </Link>
          <Link href="/signup">
            <Button>Sign-Up</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
