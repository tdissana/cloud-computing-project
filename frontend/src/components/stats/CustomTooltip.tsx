import { fmt } from "@/lib/stats/helpers";

interface CustomTooltipProps {
  active?: boolean;
  payload?: { value: number; name: string }[];
}

export function CustomTooltip({ active, payload }: CustomTooltipProps) {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-[#0f1524] border border-white/[0.10] rounded-lg px-4 py-2 shadow-[0_20px_60px_rgba(0,0,0,0.45)]">
      <p className="text-[#4a5572] text-sm font-medium">{payload[0].name}</p>
      <p className="text-[#e8edf5] font-bold">{fmt(payload[0].value)}</p>
    </div>
  );
}