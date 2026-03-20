// next.config.ts
import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",   // required for Docker
  async rewrites() {
    return [
      {
        // Proxy /bff/* → Spring Boot BFF during local dev
        source: "/bff/:path*",
        destination: `${process.env.NEXT_PUBLIC_BFF_URL}/bff/:path*`,
      },
    ];
  },
};

export default nextConfig;