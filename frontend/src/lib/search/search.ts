import { APIResponse } from "@/types/common";
import {
  SalarySearchFilters,
  PagedResponse,
  SalaryResultResponse,
  FilterOptionsResponse,
} from "@/types/search";

function resolveBffBase(): string {
  const rawBase = process.env.NEXT_PUBLIC_BFF_URL?.trim();
  if (!rawBase) return "/bff";
  if (rawBase.endsWith("/bff")) return rawBase;
  return `${rawBase}/bff`;
}

const BFF_BASE = resolveBffBase();

function getErrorMessage(payload: unknown, fallback: string): string {
  if (payload && typeof payload === "object" && "error" in payload) {
    const message = (payload as { error?: unknown }).error;
    if (typeof message === "string" && message.trim().length > 0) {
      return message;
    }
  }
  return fallback;
}

/**
 * Search salaries using POST endpoint with structured request body
 */
export async function searchSalaries(
  filters: SalarySearchFilters
): Promise<APIResponse<PagedResponse<SalaryResultResponse>>> {
  // Build request body with only non-empty filters
  const body: Record<string, unknown> = {};

  if (filters.country) body.country = filters.country;
  if (filters.company) body.company = filters.company;
  if (filters.jobTitle) body.jobTitle = filters.jobTitle;
  if (filters.seniorityLevel) body.seniorityLevel = filters.seniorityLevel;
  if (filters.employmentType) body.employmentType = filters.employmentType;
  if (filters.currency) body.currency = filters.currency;
  if (filters.minExperience !== undefined) body.minExperience = filters.minExperience;
  if (filters.maxExperience !== undefined) body.maxExperience = filters.maxExperience;

  // Pagination
  body.page = filters.page ?? 0;
  body.size = filters.size ?? 20;

  // Sorting
  body.sortBy = filters.sortBy ?? "approvedAt";
  body.sortDir = filters.sortDir ?? "desc";

  try {
    const res = await fetch(`${BFF_BASE}/api/search/salaries`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(body),
    });

    const payload: APIResponse<PagedResponse<SalaryResultResponse>> = await res
      .json()
      .catch(() => ({ success: false, error: `Server error: ${res.status}` }));

    if (!res.ok || !payload.success) {
      return {
        success: false,
        error: getErrorMessage(payload, `Server error: ${res.status}`),
      };
    }

    return { success: true, data: payload.data };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : "Unknown error",
    };
  }
}

/**
 * Fetch available filter options
 */
export async function fetchFilterOptions(): Promise<
  APIResponse<FilterOptionsResponse>
> {
  try {
    const res = await fetch(`${BFF_BASE}/api/search/filters`);
    const payload: APIResponse<FilterOptionsResponse> = await res
      .json()
      .catch(() => ({ success: false, error: `Server error: ${res.status}` }));

    if (!res.ok || !payload.success) {
      return {
        success: false,
        error: getErrorMessage(payload, `Server error: ${res.status}`),
      };
    }

    return { success: true, data: payload.data };
  } catch (error) {
    return {
      success: false,
      error: error instanceof Error ? error.message : "Unknown error",
    };
  }
}
