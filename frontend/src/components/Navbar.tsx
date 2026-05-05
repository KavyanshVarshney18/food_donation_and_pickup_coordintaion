import { useNavigate } from "react-router-dom";
import { authStore } from "../utils/auth";

export default function Navbar({ title }: { title: string }) {
  const navigate = useNavigate();
  return (
    <div className="mb-6 flex items-center justify-between rounded-lg bg-white p-4 shadow">
      <h1 className="text-lg font-semibold">{title}</h1>
      <button
        className="rounded bg-slate-900 px-3 py-2 text-sm text-white"
        onClick={() => {
          authStore.clear();
          navigate("/");
        }}
      >
        Logout
      </button>
    </div>
  );
}
