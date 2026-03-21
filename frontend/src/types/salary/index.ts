export interface SalarySubmissionRequest {
<<<<<<< HEAD
  company: string;
  role: string;
  experienceLevel: string;
  country: string;
  baseSalary: number;
  totalCompensation: number;
  currency: string;
  anonymize: boolean;
=======
  role: string;
  company: string;
  level: string;
  country: string;
  salary: number;
  currency: string;
  yearsOfExperience: number;
  anonymize: boolean;
  additionalInfo?: string;
>>>>>>> development
}

export interface SalarySubmissionResponse {
  message: string;
  submissionId: string;
  status: "PENDING";
}
