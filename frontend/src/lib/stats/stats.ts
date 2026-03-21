import { APIResponse } from "@/types/common";
import { SalaryStatsResponse, StatsFilters } from "@/types/stats";

const BFF_BASE = process.env.NEXT_PUBLIC_BFF_URL ?? "";

export async function fetchStats(
  filters: StatsFilters
): Promise<APIResponse<SalaryStatsResponse>> {
  const params = new URLSearchParams();
  if (filters.role)    params.set("role",    filters.role);
  if (filters.company) params.set("company", filters.company);
  if (filters.level)   params.set("level",   filters.level);
  if (filters.country) params.set("country", filters.country);

  try {
    const res = await fetch(`${BFF_BASE}/api/stats?${params.toString()}`);
    if (!res.ok) throw new Error(`Server error: ${res.status}`);
    const data: SalaryStatsResponse = await res.json();
    return { success: true, data };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : "Unknown error",
    };
  }
}