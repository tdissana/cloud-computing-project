// Re-export shared constants from common
export { SENIORITY_LEVELS, EMPLOYMENT_TYPES, CURRENCIES, COUNTRIES } from "@/lib/common/constants";

export const SORT_OPTIONS = [
  { label: "Latest Submissions", value: "approvedAt" },
  { label: "Highest Salary", value: "grossMonthlySalary" },
  { label: "Most Upvoted", value: "upvotes" },
  { label: "Years of Experience", value: "yearsOfExperience" },
];

export const PAGE_SIZES = [10, 20, 50];
export const DEFAULT_PAGE_SIZE = 20;
