import { APIResponse } from "@/types/common";
import { SignupRequest, SignupResponse } from "@/types/identity";

const BFF_URL = process.env.NEXT_PUBLIC_BFF_URL ?? "/bff";

export async function apiSignup(data: SignupRequest): Promise<SignupResponse> {
  const res = await fetch(`${BFF_URL}/api/auth/signup`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  const json: APIResponse<SignupResponse> = await res.json();

  if (!res.ok || !json.success) {
    throw new Error(json.error ?? "Signup failed");
  }

  return json.data!;
}