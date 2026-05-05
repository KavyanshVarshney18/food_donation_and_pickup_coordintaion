import { useState } from "react";

interface Props {
  open: boolean;
  onClose: () => void;
  onSubmit: (otp: string) => void;
}

export default function OTPModal({ open, onClose, onSubmit }: Props) {
  const [otp, setOtp] = useState("");
  if (!open) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/30">
      <div className="w-80 rounded-lg bg-white p-4">
        <h3 className="mb-2 font-semibold">Enter Pickup OTP</h3>
        <input
          className="w-full rounded border p-2"
          placeholder="4-digit OTP"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
        />
        <div className="mt-3 flex justify-end gap-2">
          <button className="rounded bg-slate-200 px-3 py-1" onClick={onClose}>Cancel</button>
          <button className="rounded bg-blue-600 px-3 py-1 text-white" onClick={() => onSubmit(otp)}>Verify</button>
        </div>
      </div>
    </div>
  );
}
