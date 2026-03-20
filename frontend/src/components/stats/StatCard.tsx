import { Card, CardContent } from "@/components/ui/card";

interface StatCardProps {
  icon: React.ReactNode;
  label: string;
  value: string;
  accent: string;
}

export function StatCard({ icon, label, value, accent }: StatCardProps) {
  return (
    <Card className="bg-white/[0.025] border-white/[0.07] backdrop-blur-xl shadow-[0_20px_60px_rgba(0,0,0,0.45)] hover:border-white/[0.14] transition-colors">
      <CardContent className="pt-6">
        <div className="flex items-start justify-between">
          <div>
            <p className="text-[#4a5572] text-sm font-medium uppercase tracking-widest mb-1">
              {label}
            </p>
            <p className={`text-3xl font-bold tracking-tight ${accent}`}>
              {value}
            </p>
          </div>
          <div className={`p-2 rounded-lg bg-white/[0.05] ${accent}`}>{icon}</div>
        </div>
      </CardContent>
    </Card>
  );
}