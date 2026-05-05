import { useEffect, useState } from "react";

export default function CountdownTimer({ target }: { target: string }) {
  const [remaining, setRemaining] = useState("");

  useEffect(() => {
    const timer = setInterval(() => {
      const diff = new Date(target).getTime() - Date.now();
      if (diff <= 0) {
        setRemaining("Expired");
        return;
      }
      const hours = Math.floor(diff / 3600000);
      const mins = Math.floor((diff % 3600000) / 60000);
      setRemaining(`${hours}h ${mins}m`);
    }, 1000);

    return () => clearInterval(timer);
  }, [target]);

  return <span className="text-sm text-gray-600">Best before: {remaining}</span>;
}
