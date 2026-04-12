export interface StatsFilters {
  jobTitle?: string;
  company?: string;
  seniorityLevel?: string;
  country?: string;
  employmentType?: string;
  currency?: string;
}

export interface SalaryStatsResponse {
  jobTitle: string | null;
  company: string | null;
  seniorityLevel: string | null;
  country: string | null;
  employmentType: string | null;
  currency: string | null;
  count: number;
  average: number;
  median: number;
  p90: number;
}