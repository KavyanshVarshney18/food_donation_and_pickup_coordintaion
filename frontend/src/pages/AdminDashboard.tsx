import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import { adminApi } from "../services/api";
import type { User } from "../types";

export default function AdminDashboard() {
  const [leaders, setLeaders] = useState<User[]>([]);
  useEffect(() => {
    adminApi.leaderboard().then((res) => setLeaders(res.data));
  }, []);

  return (
    <div className="mx-auto max-w-4xl p-4">
      <Navbar title="Admin Dashboard" />
      <div className="rounded-lg bg-white p-4 shadow">
        <h3 className="mb-3 font-semibold">Top Donors (Meals Saved)</h3>
        {leaders.map((leader) => (
          <div key={leader.id} className="mb-2 flex justify-between rounded bg-slate-50 p-2">
            <span>{leader.name}</span>
            <span>{leader.totalMealsDonated} meals</span>
          </div>
        ))}
      </div>
    </div>
  );
}
