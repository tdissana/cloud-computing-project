import type { Metadata } from "next";
import "./globals.css"

export const metadata: Metadata = {
  title: "watupa.lk - Sri Lanka's Salary Transparency Website",
  description: "Anonymous salary data for Sri Lanka",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <head>
        <link
          href="https://fonts.googleapis.com/css2?family=Syne:wght@400;600;700;800&family=DM+Sans:opsz,wght@9..40,300;9..40,400;9..40,500&display=swap"
          rel="stylesheet"
        />
      </head>
      <body style={{ margin: 0, background: "#090c0a", color: "#f0f0f0" }}>
        {children}
      </body>
    </html>
  );
}