/**
 * Get active/non-empty filters from any filter object.
 * Works with both SalarySearchFilters and StatsFilters.
 */
export function getActiveFilters<T extends object>(filters: T) {
  return Object.entries(filters).filter(([, v]) => {
    if (v === undefined || v === null) return false;
    if (typeof v === "string" && v.trim() === "") return false;
    if (typeof v === "number" && v < 0) return false;
    return true;
  }) as [keyof T & string, string | number][];
}

/**
 * Format salary values in compact currency notation.
 */
export function formatSalary(value: number, currency = "LKR") {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency,
    maximumFractionDigits: 0,
    notation: "compact",
  }).format(value);
}
