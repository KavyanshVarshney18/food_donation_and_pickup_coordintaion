import type { DonationStatus } from "../types";

export const statusChipClass: Record<DonationStatus, string> = {
  AVAILABLE: "bg-green-100 text-green-700",
  CLAIMED: "bg-orange-100 text-orange-700",
  PICKED_UP: "bg-blue-100 text-blue-700",
  DISTRIBUTED: "bg-purple-100 text-purple-700",
  EXPIRED: "bg-red-100 text-red-700"
};

export const urgencyClass = (bestBeforeTime: string) => {
  const minutesLeft = (new Date(bestBeforeTime).getTime() - Date.now()) / 60000;
  if (minutesLeft < 60) return "border-red-400";
  if (minutesLeft < 180) return "border-orange-400";
  return "border-green-400";
};
