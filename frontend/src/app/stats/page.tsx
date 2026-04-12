"use client";

import { useState, useCallback, useEffect } from "react";
import { Search, TrendingUp, Users, BarChart2, Award, X, Filter } from "lucide-react";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { SearchableSelect } from "@/components/ui/SearchableSelect";

import { fetchStats } from "@/lib/stats/stats";
import { SENIORITY_LEVELS, EMPLOYMENT_TYPES } from "@/lib/common/constants";
import { getActiveFilters, formatSalary } from "@/lib/common/helpers";
import { fetchFilterOptions } from "@/lib/search/search";
import { StatCard } from "@/components/stats/StatCard";
import { SalaryStatsResponse, StatsFilters } from "@/types/stats";
import { FilterOptionsResponse } from "@/types/search";

/* ─── Main Page ──────────────────────────────────────────────────── */

export default function StatsPage() {
  const [filters, setFilters] = useState<StatsFilters>({});
  const [draft, setDraft] = useState<StatsFilters>({});
  const [stats, setStats] = useState<SalaryStatsResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [searched, setSearched] = useState(false);
  const [filterOptions, setFilterOptions] = useState<FilterOptionsResponse | null>(null);
  const [loadingOptions, setLoadingOptions] = useState(true);

  // Fetch filter options on mount
  useEffect(() => {
    const loadOptions = async () => {
      const result = await fetchFilterOptions();
      if (result.success && result.data) {
        setFilterOptions(result.data);
      }
      setLoadingOptions(false);
    };
    loadOptions();
  }, []);

  // Re-fetch filter options when tab regains focus
  useEffect(() => {
    const onFocus = async () => {
      const result = await fetchFilterOptions();
      if (result.success && result.data) {
        setFilterOptions(result.data);
      }
    };
    window.addEventListener("focus", onFocus);
    return () => window.removeEventListener("focus", onFocus);
  }, []);

  const handleSearch = useCallback(async () => {
    setLoading(true);
    setError(null);
    setFilters(draft);
    setSearched(true);

    const result = await fetchStats(draft);

    if (result.success && result.data) {
      setStats(result.data);
    } else {
      setStats(null);
      setError(result.error ?? "Failed to load statistics.");
    }
    setLoading(false);
  }, [draft]);

  const removeFilter = (key: keyof StatsFilters) => {
    const next = { ...draft, [key]: undefined };
    setDraft(next);
  };

  const clearAll = () => {
    setDraft({});
    setFilters({});
    setStats(null);
    setSearched(false);
    setError(null);
  };


  const tableRows = stats
    ? [
        { metric: "Average Salary",       value: stats.average, note: "Mean across all submissions" },
        { metric: "Median Salary",         value: stats.median,  note: "Middle value (less skewed)" },
        { metric: "90th Percentile (P90)", value: stats.p90,     note: "Top 10% earn above this" },
      ]
    : [];

  const active = getActiveFilters(draft);

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap');

        @keyframes spin {
          to { transform: rotate(360deg); }
        }
        .stats-spinner {
          display: inline-block;
          width: 14px;
          height: 14px;
          border: 2px solid rgba(255,255,255,0.3);
          border-top-color: #fff;
          border-radius: 50%;
          animation: spin 0.75s linear infinite;
          vertical-align: middle;
        }

        input:-webkit-autofill,
        input:-webkit-autofill:focus {
          -webkit-box-shadow: 0 0 0 1000px #0f1524 inset !important;
          -webkit-text-fill-color: #e8edf5 !important;
          transition: background-color 5000s;
        }
      `}</style>

      <main className="min-h-screen bg-[#0b0f1a] text-[#e8edf5]" style={{ fontFamily: "'Plus Jakarta Sans', sans-serif" }}>

        {/* ── Radial backdrop ── */}
        <div
          className="fixed inset-0 z-0 pointer-events-none"
          style={{ background: "radial-gradient(ellipse 80% 60% at 50% 0%, rgba(30,60,120,0.35) 0%, transparent 70%)" }}
        />
        <div
          className="fixed z-0 pointer-events-none"
          style={{ bottom: "-15%", right: "-5%", width: "50vw", height: "50vw", borderRadius: "50%", background: "radial-gradient(circle, rgba(91,91,214,0.08) 0%, transparent 65%)" }}
        />

        {/* ── Header ── */}
        <div className="relative z-10 border-b border-white/[0.07] bg-[#0b0f1a]/80 backdrop-blur sticky top-0">
          <div className="max-w-6xl mx-auto px-6 py-4 flex items-center gap-3">
            <BarChart2 className="text-[#6ea8fe] w-5 h-5" />
            <span className="font-bold text-[#e8edf5] tracking-tight" style={{ fontFamily: "'Plus Jakarta Sans', sans-serif" }}>
              Salary Statistics
            </span>
          </div>
        </div>

        <div className="relative z-10 max-w-6xl mx-auto px-6 py-10 space-y-10">

          {/* ── Hero ── */}
          <div className="space-y-2">
            <h1
              className="text-4xl font-normal tracking-tight text-[#e8edf5]"
              style={{ fontFamily: "'Instrument Serif', serif", lineHeight: 1.18 }}
            >
              Explore Tech Salaries
            </h1>
            <p className="text-[#4a5572] max-w-xl" style={{ fontSize: "14px", lineHeight: 1.75, fontWeight: 300 }}>
              Community-sourced, anonymised salary data for Sri Lanka&apos;s tech
              industry. Filter by job title, company, seniority level, country,
              employment type or currency to see aggregated insights.
            </p>
          </div>

          {/* ── Filter Bar ── */}
          <Card className="bg-white/[0.025] border-white/[0.07] backdrop-blur-xl shadow-[0_20px_60px_rgba(0,0,0,0.45)] overflow-visible">
            <CardHeader className="pb-4">
              <CardTitle
                className="text-base font-bold text-[#e8edf5] flex items-center gap-2 uppercase tracking-widest"
                style={{ fontSize: "11px", letterSpacing: "0.18em" }}
              >
                <Filter className="w-4 h-4 text-[#6ea8fe]" />
                Filter Salaries
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4 overflow-visible pb-6">
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">

                {/* Job Title */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Job Title
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <SearchableSelect
                      value={draft.jobTitle ?? ""}
                      onChange={(value: string) =>
                        setDraft((p) => ({ ...p, jobTitle: value || undefined }))
                      }
                      options={filterOptions.jobTitles || []}
                      placeholder="Search or type job title..."
                    />
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>

                {/* Company */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Company
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <Select
                      value={draft.company ?? ""}
                      onValueChange={(v) =>
                        setDraft((p) => ({ ...p, company: v || undefined }))
                      }
                    >
                      <SelectTrigger className="bg-[#0f1524] border-white/[0.08] text-[#e8edf5] focus:ring-[#6ea8fe]">
                        <SelectValue placeholder="Any company" />
                      </SelectTrigger>
                      <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                        <SelectItem value="" className="text-[#3a4560]">Any company</SelectItem>
                        {filterOptions.companies.map((c) => (
                          <SelectItem key={c} value={c} className="text-[#e8edf5] focus:bg-white/[0.05]">
                            {c}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>

                {/* Country */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Country
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <SearchableSelect
                      value={draft.country ?? ""}
                      onChange={(value: string) =>
                        setDraft((p) => ({ ...p, country: value || undefined }))
                      }
                      options={filterOptions.countries || []}
                      placeholder="Search or type country..."
                    />
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>

                {/* Seniority Level */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Seniority Level
                  </label>
                  <Select
                    value={draft.seniorityLevel ?? ""}
                    onValueChange={(v) =>
                      setDraft((p) => ({ ...p, seniorityLevel: v || undefined }))
                    }
                  >
                    <SelectTrigger className="bg-[#0f1524] border-white/[0.08] text-[#e8edf5] focus:ring-[#6ea8fe]">
                      <SelectValue placeholder="Any level" />
                    </SelectTrigger>
                    <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                      <SelectItem value="" className="text-[#3a4560]">Any level</SelectItem>
                      {SENIORITY_LEVELS.map((l) => (
                        <SelectItem key={l} value={l} className="text-[#e8edf5] focus:bg-white/[0.05]">
                          {l}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Employment Type */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Employment Type
                  </label>
                  <Select
                    value={draft.employmentType ?? ""}
                    onValueChange={(v) =>
                      setDraft((p) => ({ ...p, employmentType: v || undefined }))
                    }
                  >
                    <SelectTrigger className="bg-[#0f1524] border-white/[0.08] text-[#e8edf5] focus:ring-[#6ea8fe]">
                      <SelectValue placeholder="Any type" />
                    </SelectTrigger>
                    <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                      <SelectItem value="" className="text-[#3a4560]">Any type</SelectItem>
                      {EMPLOYMENT_TYPES.map((t) => (
                        <SelectItem key={t} value={t} className="text-[#e8edf5] focus:bg-white/[0.05]">
                          {t}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                {/* Currency */}
                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Currency
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <SearchableSelect
                      value={draft.currency ?? ""}
                      onChange={(value: string) =>
                        setDraft((p) => ({ ...p, currency: value || undefined }))
                      }
                      options={filterOptions.currencies || []}
                      placeholder="Search or type currency..."
                    />
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>
              </div>

              {/* Active filter chips */}
              {active.length > 0 && (
                <div className="flex flex-wrap gap-2 pt-1">
                  {active.map(([key, value]) => (
                    <Badge
                      key={key}
                      className="bg-[#6ea8fe]/10 text-[#6ea8fe] border border-[#6ea8fe]/25 hover:bg-[#6ea8fe]/20 cursor-pointer gap-1"
                      onClick={() => removeFilter(key)}
                    >
                      <span className="capitalize">{key}</span>: {value}
                      <X className="w-3 h-3 ml-0.5" />
                    </Badge>
                  ))}
                  <button
                    onClick={clearAll}
                    className="text-xs text-[#3a4560] hover:text-[#6ea8fe] underline underline-offset-2 transition-colors"
                  >
                    Clear all
                  </button>
                </div>
              )}

              {/* Search button */}
              <div className="flex justify-end pt-1">
                <Button
                  onClick={handleSearch}
                  disabled={loading || active.length === 0}
                  className="bg-gradient-to-br from-[#3b7ff5] to-[#5b5bd6] hover:from-[#5294f7] hover:to-[#6e6edc] text-white font-bold px-6 uppercase tracking-widest text-[13px] transition-all hover:-translate-y-0.5 hover:shadow-[0_10px_28px_rgba(59,127,245,0.35)] disabled:opacity-50 border-0"
                >
                  {loading ? (
                    <span className="flex items-center gap-2">
                      <span className="stats-spinner" />
                      Loading…
                    </span>
                  ) : (
                    <span className="flex items-center gap-2">
                      <Search className="w-4 h-4" />
                      Get Statistics
                    </span>
                  )}
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* ── Error ── */}
          {error && (
            <div
              className="flex items-start gap-3 px-4 py-3 rounded-xl text-sm"
              style={{ background: "rgba(224,82,82,0.08)", border: "1px solid rgba(224,82,82,0.25)", color: "#e05252" }}
            >
              <span className="flex-shrink-0 mt-0.5">✕</span>
              <span>{error}</span>
            </div>
          )}

          {/* ── Empty state (post-search) ── */}
          {searched && !loading && stats?.count === 0 && !error && (
            <p className="text-[#4a5572] text-sm">No results for given filter(s)</p>
          )}

          {/* ── Results ── */}
          {stats && stats.count > 0 && (
            <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">

              {/* Context banner */}
              <div className="flex flex-wrap items-center gap-2 text-sm text-[#4a5572]">
                <span>Showing results for</span>
                {filters.jobTitle       && <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08]">{filters.jobTitle}</Badge>}
                {filters.company        && <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08]">{filters.company}</Badge>}
                {filters.seniorityLevel && <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08]">{filters.seniorityLevel}</Badge>}
                {filters.country        && <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08]">{filters.country}</Badge>}
                {filters.employmentType && <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08]">{filters.employmentType}</Badge>}
                {filters.currency       && <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08]">{filters.currency}</Badge>}
                {!filters.jobTitle && !filters.company && !filters.seniorityLevel && !filters.country && !filters.employmentType && !filters.currency && (
                  <Badge className="bg-white/[0.05] text-[#e8edf5] border-white/[0.08]">All Submissions</Badge>
                )}
              </div>

              {/* ── Summary Cards ── */}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
                <StatCard
                  icon={<Users className="w-4 h-4" />}
                  label="Submissions"
                  value={stats.count.toLocaleString()}
                  accent="text-[#e8edf5]"
                />
                <StatCard
                  icon={<TrendingUp className="w-4 h-4" />}
                  label="Average"
                  value={formatSalary(stats.average)}
                  accent="text-[#6ea8fe]"
                />
                <StatCard
                  icon={<BarChart2 className="w-4 h-4" />}
                  label="Median"
                  value={formatSalary(stats.median)}
                  accent="text-[#a5c8fe]"
                />
                <StatCard
                  icon={<Award className="w-4 h-4" />}
                  label="90th Percentile"
                  value={formatSalary(stats.p90)}
                  accent="text-[#5b5bd6]"
                />
              </div>


              {/* ── Table ── */}
              <Card className="bg-white/[0.025] border-white/[0.07] backdrop-blur-xl shadow-[0_20px_60px_rgba(0,0,0,0.45)]">
                <CardHeader>
                  <CardTitle
                    className="font-bold text-[#e8edf5] uppercase tracking-widest"
                    style={{ fontSize: "11px", letterSpacing: "0.18em", fontFamily: "'Plus Jakarta Sans', sans-serif" }}
                  >
                    Detailed Breakdown
                  </CardTitle>
                </CardHeader>
                <CardContent className="p-0">
                  <Table>
                    <TableHeader>
                      <TableRow className="border-white/[0.07] hover:bg-transparent">
                        <TableHead className="text-[#3a4560] font-semibold uppercase text-xs tracking-wider pl-6">
                          Metric
                        </TableHead>
                        <TableHead className="text-[#3a4560] font-semibold uppercase text-xs tracking-wider">
                          Value (LKR)
                        </TableHead>
                        <TableHead className="text-[#3a4560] font-semibold uppercase text-xs tracking-wider pr-6">
                          Note
                        </TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {tableRows.map((row, i) => (
                        <TableRow
                          key={i}
                          className="border-white/[0.05] hover:bg-white/[0.03] transition-colors"
                        >
                          <TableCell className="font-medium text-[#e8edf5] pl-6">
                            {row.metric}
                          </TableCell>
                          <TableCell className="font-mono font-semibold text-[#6ea8fe]">
                            {new Intl.NumberFormat("en-US").format(row.value)}
                          </TableCell>
                          <TableCell className="text-[#3a4560] text-sm pr-6">
                            {row.note}
                          </TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                </CardContent>
              </Card>

              {/* ── Disclaimer ── */}
              <p className="text-center pb-4" style={{ fontSize: "11px", color: "#24293a", letterSpacing: "0.03em" }}>
                Data is community-sourced and anonymised. Only approved submissions
                are included in calculations.
              </p>
            </div>
          )}

          {/* ── Initial empty state ── */}
          {!searched && (
            <div className="text-center py-24 space-y-3">
              <div
                className="w-16 h-16 rounded-2xl flex items-center justify-center mx-auto"
                style={{ background: "rgba(255,255,255,0.025)", border: "1px solid rgba(255,255,255,0.07)" }}
              >
                <BarChart2 className="w-7 h-7 text-[#6ea8fe]" />
              </div>
              <p className="font-semibold text-[#4a5572]">
                Apply filters and click &ldquo;Get Statistics&rdquo;
              </p>
              <p style={{ fontSize: "13px", color: "#3a4560", maxWidth: "260px", margin: "0 auto" }}>
                Leave all filters empty to see aggregate statistics across all
                submissions.
              </p>
            </div>
          )}
        </div>
      </main>
    </>
  );
}