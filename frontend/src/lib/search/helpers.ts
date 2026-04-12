import { getActiveFilters as _getActiveFilters, formatSalary as _formatSalary } from "@/lib/common/helpers";
import { SalarySearchFilters } from "@/types/search";

// Re-export common helpers for backward compatibility
export const formatSalary = (value: number) => _formatSalary(value);

export function formatNumber(value: number) {
  return new Intl.NumberFormat("en-US").format(value);
}

export const getActiveFilters = (filters: SalarySearchFilters) =>
  _getActiveFilters(filters);

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
