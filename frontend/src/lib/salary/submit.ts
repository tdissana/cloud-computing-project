import { APIResponse } from "@/types/common";
import { SalarySubmissionRequest, SalarySubmissionResponse } from "@/types/salary";

const BFF_URL = process.env.NEXT_PUBLIC_BFF_URL ?? "/bff";

export async function submitSalary(
  data: SalarySubmissionRequest
): Promise<SalarySubmissionResponse> {
  const res = await fetch(`${BFF_URL}/api/submissions/submit`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  const json: APIResponse<SalarySubmissionResponse> = await res.json();

  if (!res.ok || !json.success) {
    throw new Error(json.error ?? "Submission failed");
  }

  return json.data!;
}
