export type UserRole = "DONOR" | "VOLUNTEER" | "ADMIN";
export type DonationStatus = "AVAILABLE" | "CLAIMED" | "PICKED_UP" | "DISTRIBUTED" | "EXPIRED";

export interface User {
  id: string;
  name: string;
  phone: string;
  role: UserRole;
  latitude: number;
  longitude: number;
  totalMealsDonated: number;
}

export interface Donation {
  id: string;
  foodType: string;
  quantity: number;
  description: string;
  bestBeforeTime: string;
  createdAt: string;
  pickupLocation: { lat: number; lng: number };
  status: DonationStatus;
  donorId: string;
  volunteerId?: string;
  imageUrl?: string;
  distributionProofUrl?: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}
