import { LoginRequest, SignupRequest } from "@/types/identity";

export function validateSignup(request: SignupRequest, confirm: string) {
  const errs: Partial<SignupRequest> = {};
  if (!request.username || request.username.length < 3 || request.username.length > 10)
    errs.username = "Must be 3–10 characters";
  
  if (!request.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(request.email))
    errs.email = "Enter a valid email address";
  
  if (request.email.length > 50) 
    errs.email = "Must be less than 50 characters";
 
  if (!request.password || request.password.length < 5 || request.password.length > 12)
    errs.password = "Must be 5–12 characters";

  if (request.password !== confirm) {
    errs.password = "Passwords do not match";
  }

  return errs;
}

export function validateLogin(request: LoginRequest) {
  const errs: Partial<LoginRequest> = {};
  if (!request.username || request.username.length < 3)
    errs.username = "Must be 3–10 characters";

  if (!request.password || request.password.length < 5)
    errs.password = "Must be 5–12 characters";

  return errs;
}