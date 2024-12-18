"use client"

import { useAuth } from "@/lib/auth_context";

import { Landmark } from "lucide-react";

import Link from "next/link";

import { Button } from "@/components/ui/button";

export default function Navbar() {
  const { isAuthenticated, logout } = useAuth();

  const handleLogout = () => {
    console.log("Logging out...");
    logout();
    window.location.href = '/login';
  }

  return (
    <nav className="grid grid-cols-3 bg-white p-1">
      <div className="flex justify-begin align-center">
        <Landmark size={32} />
      </div>
      <div className="flex justify-center align-center gap-4">
        {isAuthenticated
          ?
          (
            <div className="flex gap-2">
              <Link className="font-bold hover:underline" href="/auctions">Auctions</Link>
              <Link className="font-bold hover:underline" href="/me">Profile</Link>
            </div>
          )
          : (
            <h1 className="flex justify-center align-center text-xl font-bold text-center m-auto">
              GoBidder
            </h1>
          )
        }
      </div>
      <div className="flex justify-end align-center">
        {isAuthenticated
          ? (
            <>
              <Button onClick={handleLogout}>Log Out</Button>
            </>
          )
          : (
            <>
              <Link href="signup">
                <Button>Sign Up</Button>
              </Link>
              <Link className="ml-4" href="login">
                <Button>Log In</Button>
              </Link>
            </>
          )
        }
      </div>
    </nav>
  );
}
