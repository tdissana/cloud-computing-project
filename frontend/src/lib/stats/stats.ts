import { APIResponse } from "@/types/common";
import { SalaryStatsResponse, StatsFilters } from "@/types/stats";

const BFF_BASE = process.env.NEXT_PUBLIC_BFF_URL ?? "/bff";

export async function fetchStats(
  filters: StatsFilters
): Promise<APIResponse<SalaryStatsResponse>> {
  const params = new URLSearchParams();
  if (filters.jobTitle)       params.set("jobTitle",       filters.jobTitle);
  if (filters.company)        params.set("company",        filters.company);
  if (filters.seniorityLevel) params.set("seniorityLevel", filters.seniorityLevel);
  if (filters.country)        params.set("country",        filters.country);
  if (filters.employmentType) params.set("employmentType", filters.employmentType);
  if (filters.currency)       params.set("currency",       filters.currency);

  try {
    const res = await fetch(`${BFF_BASE}/api/stats?${params.toString()}`);
    if (!res.ok) throw new Error(`Server error: ${res.status}`);
    const json: { success: boolean; data: SalaryStatsResponse } = await res.json();
    return { success: true, data: json.data };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : "Unknown error",
    };
  }
}