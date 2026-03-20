// next.config.ts
import type { NextConfig } from "next";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";

const nextConfig: NextConfig = {
  output: "standalone",   // required for Docker
  async rewrites() {
    return [
      {
        // Proxy /bff/* → Spring Boot BFF during local dev
        source: "/bff/:path*",
        destination: `${API_URL}/bff/:path*`,
      },
    ];
  },
};

export default nextConfig;