export interface SalarySubmissionRequest {
  role: string;
  company: string;
  level: string;
  country: string;
  salary: number;
  currency: string;
  yearsOfExperience: number;
  anonymize: boolean;
  additionalInfo?: string;
}

export interface SalarySubmissionResponse {
  message: string;
  submissionId: string;
  status: "PENDING";
}
