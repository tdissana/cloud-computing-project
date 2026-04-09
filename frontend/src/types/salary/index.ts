export interface SalarySubmissionRequest {
  companyName: string;
  jobTitle: string;
  experienceLevel: string;
  seniority: number | null;
  country: string;
  currency: string;
  totalCompensation: number;
  baseSalary: number;
  skills: string;
  anonymize: boolean;
}

export interface SalarySubmissionResponse {
  message: string;
  submissionId: number;
  status: "PENDING";
}
