import { formatSalary, getActiveFilters } from "@/lib/common/helpers";
import { StatsFilters } from "@/types/stats";

// Re-export common helpers with stats-specific names for backward compatibility
export const fmt = (value: number) => formatSalary(value);

export const activeFilters = (filters: StatsFilters) =>
  getActiveFilters(filters);