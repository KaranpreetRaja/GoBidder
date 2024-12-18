import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: '**',
        pathname: "**",
        port: '',
        search: '',
      },
      {
        protocol: 'http',
        hostname: '**',
        pathname: "**",
        port: '',
        search: '',
      }
    ]
  },
};

export default nextConfig;
