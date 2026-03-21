export interface SalarySearchFilters {
  country?: string;
  company?: string;
  jobTitle?: string;
  seniorityLevel?: string;
  employmentType?: string;
  currency?: string;
  minExperience?: number;
  maxExperience?: number;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
}

export interface SalaryResultResponse {
  id: string;
  companyName: string;      // masked to "Anonymous" if anonymized=true
  jobTitle: string;
  seniorityLevel: string;
  employmentType: string;
  country: string;
  city?: string;             // masked to null if anonymized=true
  grossMonthlySalary: number;
  currency: string;
  additionalCompensation?: number;
  yearsOfExperience: number;
  techStack: string;
  anonymized: boolean;
  approvedAt: string;
  upvotes: number;
  downvotes: number;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface FilterOptionsResponse {
  countries: string[];
  companies: string[];
  jobTitles: string[];
  seniorityLevels: string[];
  employmentTypes: string[];
  currencies: string[];
}

export interface SearchPageState {
  filters: SalarySearchFilters;
  results: PagedResponse<SalaryResultResponse> | null;
  filterOptions: FilterOptionsResponse | null;
  loading: boolean;
  error: string | null;
  searched: boolean;
}
