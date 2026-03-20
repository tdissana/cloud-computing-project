import { SalarySearchFilters } from "@/types/search";

/**
 * Format salary values in LKR currency
 */
export function formatSalary(value: number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "LKR",
    maximumFractionDigits: 0,
    notation: "compact",
  }).format(value);
}

/**
 * Format number with commas
 */
export function formatNumber(value: number) {
  return new Intl.NumberFormat("en-US").format(value);
}

/**
 * Get active/non-empty filters from the filter object
 */
export function getActiveFilters(filters: SalarySearchFilters) {
  return Object.entries(filters).filter(([, v]) => {
    if (v === undefined || v === null) return false;
    if (typeof v === "string" && v.trim() === "") return false;
    if (typeof v === "number" && (v < 0)) return false;
    return true;
  }) as [keyof SalarySearchFilters, string | number][];
}

/**
 * Convert filter display value to readable label
 */
export function getFilterLabel(key: keyof SalarySearchFilters, value: string | number | undefined) {
  if (typeof value === "number") return value.toString();
  return String(value);
}

/**
 * Format date time to readable format
 */
export function formatDateTime(dateString: string) {
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  } catch {
    return "—";
  }
}

/**
 * Calculate pagination info
 */
export function getPaginationInfo(
  currentPage: number,
  pageSize: number,
  totalElements: number
) {
  const startItem = currentPage * pageSize + 1;
  const endItem = Math.min((currentPage + 1) * pageSize, totalElements);
  return { startItem, endItem, totalElements };
}
