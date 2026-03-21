export interface SalarySubmissionRequest {
  company: string;
  role: string;
  experienceLevel: string;
  country: string;
  baseSalary: number;
  totalCompensation: number;
  currency: string;
  anonymize: boolean;
}

export interface SalarySubmissionResponse {
  message: string;
  submissionId: string;
  status: "PENDING";
}
