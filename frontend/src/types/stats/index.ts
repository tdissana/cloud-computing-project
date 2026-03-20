export interface StatsFilters {
  role?: string;
  company?: string;
  level?: string;
  country?: string;
}

export interface SalaryStatsResponse {
  role: string | null;
  company: string | null;
  level: string | null;
  country: string | null;
  count: number;
  average: number;
  median: number;
  p90: number;
}