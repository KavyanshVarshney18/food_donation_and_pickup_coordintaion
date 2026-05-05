import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import DonorDashboard from "./pages/DonorDashboard";
import VolunteerDashboard from "./pages/VolunteerDashboard";
import AdminDashboard from "./pages/AdminDashboard";
import { authStore } from "./utils/auth";

function DashboardRouter() {
  const user = authStore.getUser();
  if (!user) return <Navigate to="/" />;
  if (user.role === "DONOR") return <DonorDashboard />;
  if (user.role === "VOLUNTEER") return <VolunteerDashboard />;
  return <AdminDashboard />;
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      <Route path="/dashboard" element={<DashboardRouter />} />
    </Routes>
  );
}
