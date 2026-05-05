import axios from "axios";
import type { AuthResponse, Donation, User, UserRole } from "../types";

const api = axios.create({
  baseURL: "http://localhost:8080"
});

export const authApi = {
  register(payload: {
    name: string;
    phone: string;
    password: string;
    role: UserRole;
    latitude: number;
    longitude: number;
  }) {
    return api.post<AuthResponse>("/auth/register", payload);
  },
  login(payload: { phone: string; password: string }) {
    return api.post<AuthResponse>("/auth/login", payload);
  }
};

export const donationApi = {
  create(userId: string, payload: object) {
    return api.post<Donation>("/donations/create", payload, { headers: { "X-User-Id": userId } });
  },
  nearby(lat: number, lng: number) {
    return api.get<Donation[]>("/donations/nearby", { params: { lat, lng } });
  },
  mine(userId: string) {
    return api.get<Donation[]>("/donations/my", { headers: { "X-User-Id": userId } });
  },
  claim(id: string, userId: string) {
    return api.post<Donation>(`/donations/${id}/claim`, {}, { headers: { "X-User-Id": userId } });
  },
  generateOtp(id: string) {
    return api.post<{ otp: string }>(`/donations/${id}/generate-otp`);
  },
  verifyOtp(id: string, otp: string) {
    return api.post<Donation>(`/donations/${id}/verify-otp`, { otp });
  },
  complete(id: string, distributionProofUrl: string) {
    return api.post<Donation>(`/donations/${id}/complete`, { distributionProofUrl });
  }
};

export const adminApi = {
  leaderboard() {
    return api.get<User[]>("/admin/leaderboard");
  }
};
