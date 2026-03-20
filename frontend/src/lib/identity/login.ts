import { APIResponse, LoginRequest, LoginResponse } from "@/types/identity";

const BFF_URL = process.env.NEXT_PUBLIC_BFF_URL ?? "/bff";

export async function apiLogin(data: LoginRequest): Promise<LoginResponse> {
  const res = await fetch(`${BFF_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });

  const json: APIResponse<LoginResponse> = await res.json();

  if (!res.ok || !json.success) {
    throw new Error(json.error ?? "Login failed");
  }

  return json.data!;
}