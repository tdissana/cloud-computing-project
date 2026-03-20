import { StatsFilters } from "@/types/stats";

export function fmt(value: number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "LKR",
    maximumFractionDigits: 0,
    notation: "compact",
  }).format(value);
}

export function activeFilters(filters: StatsFilters) {
  return Object.entries(filters).filter(([, v]) => v && v.trim() !== "") as [
    keyof StatsFilters,
    string,
  ][];
}