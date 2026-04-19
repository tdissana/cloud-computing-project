import type { NextConfig } from "next";

const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8000";

const nextConfig: NextConfig = {
  output: "standalone",
  async rewrites() {
    return [
      {
        source: "/bff/:path*",
        destination: `${API_URL}/bff/:path*`,
      },
    ];
  },
};

export default nextConfig;