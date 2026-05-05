import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { authApi } from "../services/api";
import { authStore } from "../utils/auth";
import type { UserRole } from "../types";

export default function LoginPage() {
  const [isSignup, setIsSignup] = useState(false);
  const [form, setForm] = useState({
    name: "",
    phone: "",
    password: "",
    role: "DONOR" as UserRole,
    latitude: 12.9716,
    longitude: 77.5946
  });
  const navigate = useNavigate();

  const submit = async () => {
    const response = isSignup
      ? await authApi.register(form)
      : await authApi.login({ phone: form.phone, password: form.password });
    authStore.setUser(response.data.user);
    navigate("/dashboard");
  };

  return (
    <div className="mx-auto mt-16 max-w-md rounded-lg bg-white p-6 shadow">
      <h2 className="mb-4 text-xl font-semibold">Food Donation System</h2>
      {isSignup && <input className="mb-2 w-full rounded border p-2" placeholder="Name" onChange={(e) => setForm({ ...form, name: e.target.value })} />}
      <input className="mb-2 w-full rounded border p-2" placeholder="Phone" onChange={(e) => setForm({ ...form, phone: e.target.value })} />
      <input className="mb-2 w-full rounded border p-2" type="password" placeholder="Password" onChange={(e) => setForm({ ...form, password: e.target.value })} />
      {isSignup && (
        <select className="mb-4 w-full rounded border p-2" onChange={(e) => setForm({ ...form, role: e.target.value as UserRole })}>
          <option value="DONOR">DONOR</option>
          <option value="VOLUNTEER">VOLUNTEER / NGO</option>
          <option value="ADMIN">ADMIN</option>
        </select>
      )}
      <button className="w-full rounded bg-green-600 py-2 text-white" onClick={submit}>
        {isSignup ? "Sign Up" : "Login"}
      </button>
      <button className="mt-3 text-sm text-blue-600" onClick={() => setIsSignup(!isSignup)}>
        {isSignup ? "Already have an account? Login" : "New user? Create account"}
      </button>
    </div>
  );
}
