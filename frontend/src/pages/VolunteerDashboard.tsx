import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DonationCard from "../components/DonationCard";
import { donationApi } from "../services/api";
import { connectDonationsSocket } from "../services/websocket";
import { authStore } from "../utils/auth";
import type { Donation } from "../types";

export default function VolunteerDashboard() {
  const user = authStore.getUser();
  const [donations, setDonations] = useState<Donation[]>([]);

  const loadNearby = async () => {
    if (!user) return;
    const response = await donationApi.nearby(user.latitude, user.longitude);
    setDonations(response.data);
  };

  useEffect(() => {
    loadNearby();
    const disconnect = connectDonationsSocket(() => loadNearby());
    return () => disconnect();
  }, []);

  if (!user) return null;

  return (
    <div className="mx-auto max-w-6xl p-4">
      <Navbar title="Volunteer / NGO Dashboard (Live Feed)" />
      <div className="grid gap-3 md:grid-cols-2">
        {donations.map((donation) => (
          <DonationCard
            key={donation.id}
            donation={donation}
            showActions
            onClaim={async () => {
              await donationApi.claim(donation.id, user.id);
              await loadNearby();
            }}
            onComplete={async () => {
              const proof = prompt("Add distribution proof URL") ?? "";
              await donationApi.complete(donation.id, proof);
              await loadNearby();
            }}
          />
        ))}
      </div>
    </div>
  );
}
