import { SalaryResultResponse } from "@/types/search";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { formatSalary, formatDateTime } from "@/lib/search/helpers";
import { TrendingUp, MapPin, Briefcase, Code, Lock } from "lucide-react";

interface SalaryCardProps {
  salary: SalaryResultResponse;
}

export function SalaryCard({ salary }: SalaryCardProps) {
  return (
    <Card className="bg-white/[0.025] border-white/[0.07] backdrop-blur-xl hover:border-white/[0.14] transition-colors hover:bg-white/[0.035]">
      <CardContent className="pt-6 space-y-4">
        {/* Header: Company & Title */}
        <div>
          <div className="flex items-start justify-between gap-2 mb-2">
            <div>
              <p className="text-[#6ea8fe] font-semibold text-sm">
                {salary.companyName}
              </p>
              <h3 className="text-[#e8edf5] font-bold text-base mt-0.5">
                {salary.jobTitle}
              </h3>
            </div>
            {salary.anonymized && (
              <Badge
                variant="outline"
                className="bg-[#a5c8fe]/10 text-[#a5c8fe] border-[#a5c8fe]/25 whitespace-nowrap flex items-center gap-1"
              >
                <Lock className="w-3 h-3" />
                <span className="text-xs">Anonymized</span>
              </Badge>
            )}
          </div>
        </div>

        {/* Badges: Level, Type, etc. */}
        <div className="flex flex-wrap gap-2">
          <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08] text-xs">
            {salary.seniorityLevel}
          </Badge>
          <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08] text-xs">
            {salary.employmentType}
          </Badge>
          {salary.yearsOfExperience !== undefined && (
            <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08] text-xs">
              {salary.yearsOfExperience} yrs exp
            </Badge>
          )}
        </div>

        {/* Salary Info */}
        <div className="border-t border-white/[0.07] pt-3 space-y-2">
          <div className="flex items-baseline justify-between">
            <p className="text-[#4a5572] text-sm font-medium uppercase tracking-widest">
              Gross Monthly
            </p>
            <p className="text-[#6ea8fe] font-bold text-lg">
              {formatSalary(salary.grossMonthlySalary)}
            </p>
          </div>
          {salary.additionalCompensation !== undefined &&
            salary.additionalCompensation > 0 && (
              <div className="flex items-baseline justify-between">
                <p className="text-[#4a5572] text-sm font-medium uppercase tracking-widest">
                  Additional Comp
                </p>
                <p className="text-[#a5c8fe] font-bold text-sm">
                  {formatSalary(salary.additionalCompensation)}
                </p>
              </div>
            )}
          <p className="text-[#3a4560] text-xs">Currency: {salary.currency}</p>
        </div>

        {/* Location & Tech Stack */}
        <div className="border-t border-white/[0.07] pt-3 space-y-2 text-sm">
          {salary.city && (
            <div className="flex items-center gap-2 text-[#4a5572]">
              <MapPin className="w-4 h-4 text-[#6ea8fe]" />
              <span>
                {salary.city}, {salary.country}
              </span>
            </div>
          )}
          {!salary.city && (
            <div className="flex items-center gap-2 text-[#4a5572]">
              <MapPin className="w-4 h-4 text-[#6ea8fe]" />
              <span>{salary.country}</span>
            </div>
          )}
          {salary.techStack && (
            <div className="flex items-start gap-2">
              <Code className="w-4 h-4 text-[#6ea8fe] mt-0.5 flex-shrink-0" />
              <span className="text-[#4a5572]">{salary.techStack}</span>
            </div>
          )}
        </div>

        {/* Votes & Date */}
        <div className="border-t border-white/[0.07] pt-3 flex items-center justify-between text-xs text-[#3a4560]">
          <div className="flex items-center gap-3">
            <div className="flex items-center gap-1">
              <TrendingUp className="w-3 h-3" />
              <span>{salary.upvotes || 0} upvotes</span>
            </div>
          </div>
          <span>{formatDateTime(salary.approvedAt)}</span>
        </div>
      </CardContent>
    </Card>
  );
}
