import type { Metadata } from "next";
import "./globals.css"
import { Geist } from "next/font/google";
import { Plus_Jakarta_Sans, Instrument_Serif } from "next/font/google";
import { cn } from "@/lib/utils";

const geist = Geist({ subsets: ['latin'], variable: '--font-sans' });
const plusJakarta = Plus_Jakarta_Sans({ subsets: ['latin'], variable: '--font-plus-jakarta' });
const instrumentSerif = Instrument_Serif({ weight: ['400'], subsets: ['latin'], variable: '--font-instrument-serif' });

export const metadata: Metadata = {
  title: "watupa.lk - Sri Lanka's Salary Transparency Website",
  description: "Anonymous salary data for Sri Lanka",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={cn("font-sans", geist.variable, plusJakarta.variable, instrumentSerif.variable)}>
      <body style={{ margin: 0, background: "#090c0a", color: "#f0f0f0" }}>
        {children}
      </body>
    </html>
  );
}