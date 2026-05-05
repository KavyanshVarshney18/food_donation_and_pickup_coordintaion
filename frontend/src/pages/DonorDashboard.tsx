import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DonationCard from "../components/DonationCard";
import OTPModal from "../components/OTPModal";
import { donationApi } from "../services/api";
import { authStore } from "../utils/auth";
import type { Donation } from "../types";

export default function DonorDashboard() {
  const user = authStore.getUser();
  const [donations, setDonations] = useState<Donation[]>([]);
  const [otpModalFor, setOtpModalFor] = useState<string | null>(null);
  const [form, setForm] = useState({
    foodType: "",
    quantity: 1,
    description: "",
    bestBeforeTime: "",
    lat: user?.latitude ?? 0,
    lng: user?.longitude ?? 0,
    imageUrl: ""
  });

  const loadMine = async () => {
    if (!user) return;
    const response = await donationApi.mine(user.id);
    setDonations(response.data);
  };

  useEffect(() => { loadMine(); }, []);

  if (!user) return null;

  return (
    <div className="mx-auto max-w-6xl p-4">
      <Navbar title={`Donor Dashboard - Meals Saved: ${user.totalMealsDonated}`} />
      <div className="mb-6 rounded-lg bg-white p-4 shadow">
        <h3 className="mb-3 font-semibold">Create Donation</h3>
        <div className="grid gap-2 md:grid-cols-2">
          <input className="rounded border p-2" placeholder="Food Type" onChange={(e) => setForm({ ...form, foodType: e.target.value })} />
          <input className="rounded border p-2" placeholder="Quantity (kg)" type="number" onChange={(e) => setForm({ ...form, quantity: Number(e.target.value) })} />
          <input className="rounded border p-2" placeholder="Best Before (ISO datetime)" onChange={(e) => setForm({ ...form, bestBeforeTime: e.target.value })} />
          <input className="rounded border p-2" placeholder="Image URL (optional)" onChange={(e) => setForm({ ...form, imageUrl: e.target.value })} />
          <textarea className="rounded border p-2 md:col-span-2" placeholder="Description" onChange={(e) => setForm({ ...form, description: e.target.value })} />
        </div>
        <button className="mt-3 rounded bg-green-600 px-4 py-2 text-white" onClick={async () => { await donationApi.create(user.id, form); await loadMine(); }}>
          Post Donation
        </button>
      </div>
      <div className="grid gap-3 md:grid-cols-2">
        {donations.map((donation) => (
          <DonationCard
            key={donation.id}
            donation={donation}
            showActions
            onGenerateOtp={async () => {
              const otp = await donationApi.generateOtp(donation.id);
              alert(`Share this OTP with volunteer at pickup: ${otp.data.otp}`);
            }}
            onVerifyOtp={() => setOtpModalFor(donation.id)}
          />
        ))}
      </div>
      <OTPModal
        open={Boolean(otpModalFor)}
        onClose={() => setOtpModalFor(null)}
        onSubmit={async (otp) => {
          if (!otpModalFor) return;
          await donationApi.verifyOtp(otpModalFor, otp);
          setOtpModalFor(null);
          await loadMine();
        }}
      />
    </div>
  );
}
