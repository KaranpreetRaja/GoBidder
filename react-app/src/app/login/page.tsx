"use client"

import { useState } from "react";
import { useRouter } from "next/navigation";
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

import { authAxios } from "@/lib/axios";
import { isAxiosError } from "axios";
import Link from "next/link";

export default function Home() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleSubmit = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Make the login request
      const response = await authAxios.post(
        '/auth/login',
        { username: email, password }
      );

      // Example: Save JWT to localStorage
      const { token } = response.data; // Adjust according to your API response structure
      localStorage.setItem('jwt', token);

      // Redirect to the home page
      router.push('/');
    } catch (error) {
      if (isAxiosError(error)) {
        // Narrowed down to AxiosError
        console.error('Login failed:', error.response?.data || error.message);
        alert(error.response?.data?.message || 'Login failed. Please check your credentials.');
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

  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-50">
      <Card className="w-[450px]">
        <CardHeader>
          <CardTitle>Sign In</CardTitle>
          <CardDescription>
            Sign into GoBidder with your email and password.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form>
            <div className="grid w-full items-center gap-4">
              <div className="flex flex-col space-y-1.5">
                <Label htmlFor="email">Username</Label>
                <Input
                  id="email"
                  name="email"
                  placeholder="Enter your email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
              <div className="flex flex-col space-y-1.5">
                <Label htmlFor="password">Password</Label>
                <Input
                  id="password"
                  name="password"
                  placeholder="Enter your password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)} // Update password state
                />
              </div>
            </div>
          </form>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Link href="/">
            <Button variant="outline">Go Back</Button>
          </Link>
          <Button onClick={handleSubmit} disabled={loading}>
            {loading ? 'Submitting...' : 'Submit'}
          </Button>
        </CardFooter>
      </Card>
    </div>
  );
}
