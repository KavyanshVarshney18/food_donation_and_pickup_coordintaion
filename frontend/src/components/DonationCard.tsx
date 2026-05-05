import type { Donation } from "../types";
import CountdownTimer from "./CountdownTimer";
import { statusChipClass, urgencyClass } from "../utils/status";

interface Props {
  donation: Donation;
  onClaim?: () => void;
  onGenerateOtp?: () => void;
  onVerifyOtp?: () => void;
  onComplete?: () => void;
  showActions?: boolean;
}

export default function DonationCard({ donation, onClaim, onGenerateOtp, onVerifyOtp, onComplete, showActions }: Props) {
  const mapUrl = `https://www.google.com/maps?q=${donation.pickupLocation.lat},${donation.pickupLocation.lng}`;

  return (
    <div className={`rounded-lg border-2 bg-white p-4 shadow ${urgencyClass(donation.bestBeforeTime)}`}>
      <div className="mb-2 flex items-center justify-between">
        <h3 className="font-semibold">{donation.foodType}</h3>
        <span className={`rounded-full px-2 py-1 text-xs ${statusChipClass[donation.status]}`}>{donation.status}</span>
      </div>
      <p className="text-sm text-gray-700">{donation.description}</p>
      <p className="mt-2 text-sm">Quantity: {donation.quantity} kg</p>
      <CountdownTimer target={donation.bestBeforeTime} />

      <div className="mt-3 flex flex-wrap gap-2">
        <a className="rounded bg-slate-200 px-3 py-1 text-sm" href={mapUrl} target="_blank" rel="noreferrer">
          Navigate
        </a>
        {showActions && donation.status === "AVAILABLE" && onClaim && (
          <button className="rounded bg-green-600 px-3 py-1 text-sm text-white" onClick={onClaim}>Claim</button>
        )}
        {showActions && donation.status === "CLAIMED" && onGenerateOtp && (
          <button className="rounded bg-orange-500 px-3 py-1 text-sm text-white" onClick={onGenerateOtp}>Generate OTP</button>
        )}
        {showActions && donation.status === "CLAIMED" && onVerifyOtp && (
          <button className="rounded bg-blue-600 px-3 py-1 text-sm text-white" onClick={onVerifyOtp}>Verify OTP</button>
        )}
        {showActions && donation.status === "PICKED_UP" && onComplete && (
          <button className="rounded bg-purple-600 px-3 py-1 text-sm text-white" onClick={onComplete}>Mark Distributed</button>
        )}
      </div>
    </div>
  );
}
