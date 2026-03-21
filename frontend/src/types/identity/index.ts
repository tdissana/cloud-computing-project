export interface SignupRequest {
  username: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface SignupResponse {
  message: string;
}

export interface LoginResponse {
  message: string;
  token: string;
}