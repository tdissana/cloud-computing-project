"use client";

import { useState, useCallback, useEffect } from "react";
import {
  Search,
  ChevronLeft,
  ChevronRight,
  Filter,
  X,
  Briefcase,
} from "lucide-react";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button, buttonVariants } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { Badge } from "@/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import {
  searchSalaries,
  fetchFilterOptions,
  voteSubmission,
} from "@/lib/search/search";
import {
  SENIORITY_LEVELS,
  EMPLOYMENT_TYPES,
  SORT_OPTIONS,
  PAGE_SIZES,
  DEFAULT_PAGE_SIZE,
} from "@/lib/search/constants";
import {
  getActiveFilters,
  getPaginationInfo,
  formatNumber,
} from "@/lib/search/helpers";
import {
  SalarySearchFilters,
  PagedResponse,
  SalaryResultResponse,
  FilterOptionsResponse,
} from "@/types/search";
import { SalaryCard } from "@/components/search/SalaryCard";
import { SearchableSelect } from "@/components/search/SearchableSelect";

export type VerificationMode = "VERIFIED" | "UNVERIFIED" | "BOTH";

interface SearchPageContentProps {
  verificationStatus?: VerificationMode;
}

export function SearchPageContent({ verificationStatus: initialStatus }: SearchPageContentProps) {
  const [verificationStatus, setVerificationStatus] = useState<VerificationMode>(
    initialStatus || "BOTH"
  );
  const [filters, setFilters] = useState<SalarySearchFilters>({
    page: 0,
    size: DEFAULT_PAGE_SIZE,
    sortBy: "approvedAt",
    sortDir: "desc",
    verificationStatus,
  });
  const [draft, setDraft] = useState<SalarySearchFilters>({});
  const [results, setResults] = useState<PagedResponse<SalaryResultResponse> | null>(
    null
  );
  const [filterOptions, setFilterOptions] = useState<FilterOptionsResponse | null>(
    null
  );
  const [loading, setLoading] = useState(false);
  const [loadingOptions, setLoadingOptions] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searched, setSearched] = useState(false);
  const [votingId, setVotingId] = useState<string | null>(null);

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

  const handleSearch = useCallback(async () => {
    setLoading(true);
    setError(null);

    const searchFilters: SalarySearchFilters = {
      ...draft,
      page: 0,
      size: DEFAULT_PAGE_SIZE,
      sortBy: (draft.sortBy || "approvedAt") as string,
      sortDir: (draft.sortDir || "desc") as string,
      verificationStatus,
    };

    setFilters(searchFilters);
    setSearched(true);

    const result = await searchSalaries(searchFilters);

    if (result.success && result.data) {
      setResults(result.data);
    } else {
      setResults(null);
      setError(result.error ?? "Failed to fetch search results.");
    }
    setLoading(false);
  }, [draft, verificationStatus]);

  const handleVerificationStatusChange = useCallback(
    async (newStatus: VerificationMode) => {
      setVerificationStatus(newStatus);
      setLoading(true);
      setError(null);

      const searchFilters: SalarySearchFilters = {
        ...draft,
        page: 0,
        size: DEFAULT_PAGE_SIZE,
        sortBy: (draft.sortBy || "approvedAt") as string,
        sortDir: (draft.sortDir || "desc") as string,
        verificationStatus: newStatus,
      };

      setFilters(searchFilters);
      setSearched(true);

      const result = await searchSalaries(searchFilters);

      if (result.success && result.data) {
        setResults(result.data);
      } else {
        setResults(null);
        setError(result.error ?? "Failed to fetch search results.");
      }
      setLoading(false);
    },
    [draft]
  );

  const handlePageChange = useCallback(
    async (newPage: number) => {
      setLoading(true);
      setError(null);

      const searchFilters: SalarySearchFilters = {
        ...filters,
        page: newPage,
        sortBy: (filters.sortBy || "approvedAt") as string,
        sortDir: (filters.sortDir || "desc") as string,
        verificationStatus,
      };

      setFilters(searchFilters);

      const result = await searchSalaries(searchFilters);

      if (result.success && result.data) {
        setResults(result.data);
      } else {
        setError(result.error ?? "Failed to fetch search results.");
      }
      setLoading(false);
    },
    [filters, verificationStatus]
  );

  const handlePageSizeChange = useCallback(
    async (newSize: number) => {
      setLoading(true);
      setError(null);

      const searchFilters: SalarySearchFilters = {
        ...filters,
        page: 0,
        size: newSize,
        sortBy: (filters.sortBy || "approvedAt") as string,
        sortDir: (filters.sortDir || "desc") as string,
        verificationStatus,
      };

      setFilters(searchFilters);

      const result = await searchSalaries(searchFilters);

      if (result.success && result.data) {
        setResults(result.data);
      } else {
        setError(result.error ?? "Failed to fetch search results.");
      }
      setLoading(false);
    },
    [filters, verificationStatus]
  );

  const handleSortChange = useCallback(
    async (newSortBy: string) => {
      setLoading(true);
      setError(null);

      const searchFilters: SalarySearchFilters = {
        ...filters,
        page: 0,
        sortBy: newSortBy || "approvedAt",
        sortDir: (filters.sortDir || "desc") as string,
        verificationStatus,
      };

      setFilters(searchFilters);

      const result = await searchSalaries(searchFilters);

      if (result.success && result.data) {
        setResults(result.data);
      } else {
        setError(result.error ?? "Failed to fetch search results.");
      }
      setLoading(false);
    },
    [filters, verificationStatus]
  );

  const removeFilter = (key: keyof SalarySearchFilters) => {
    const next = { ...draft, [key]: undefined };
    setDraft(next);
  };

  const handleVote = useCallback(
    async (submissionId: string, voteType: "UP" | "DOWN") => {
      setVotingId(submissionId);
      const result = await voteSubmission(submissionId, voteType);

      if (!result.success) {
        setError(result.error ?? "Failed to submit vote.");
        setVotingId(null);
        return;
      }

      setResults((prev) => {
        if (!prev) return prev;
        return {
          ...prev,
          content: prev.content.map((salary) => {
            if (salary.id !== submissionId) return salary;
            if (voteType === "UP") {
              return { ...salary, upvotes: (salary.upvotes || 0) + 1 };
            }
            return { ...salary, downvotes: (salary.downvotes || 0) + 1 };
          }),
        };
      });
      setVotingId(null);
    },
    []
  );

  const clearAll = () => {
    setDraft({});
    setFilters({
      page: 0,
      size: DEFAULT_PAGE_SIZE,
      sortBy: "approvedAt",
      sortDir: "desc",
      verificationStatus,
    });
    setResults(null);
    setSearched(false);
    setError(null);
  };

  const active = getActiveFilters(draft);
  const paginationInfo = results
    ? getPaginationInfo(results.page, results.size, results.totalElements)
    : null;
  const selectedSortLabel =
    SORT_OPTIONS.find((opt) => opt.value === (filters.sortBy || "approvedAt"))?.label ??
    "Latest Submissions";

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap');

        @keyframes spin {
          to { transform: rotate(360deg); }
        }
        .search-spinner {
          display: inline-block;
          width: 14px;
          height: 14px;
          border: 2px solid rgba(255,255,255,0.3);
          border-top-color: #fff;
          border-radius: 50%;
          animation: spin 0.75s linear infinite;
          vertical-align: middle;
        }
      `}</style>

      <main className="min-h-screen bg-[#0b0f1a] text-[#e8edf5]" style={{ fontFamily: "'Plus Jakarta Sans', sans-serif" }}>

        <div
          className="fixed inset-0 z-0 pointer-events-none"
          style={{
            background:
              "radial-gradient(ellipse 80% 60% at 50% 0%, rgba(30,60,120,0.35) 0%, transparent 70%)",
          }}
        />
        <div
          className="fixed z-0 pointer-events-none"
          style={{
            bottom: "-15%",
            right: "-5%",
            width: "50vw",
            height: "50vw",
            borderRadius: "50%",
            background:
              "radial-gradient(circle, rgba(91,91,214,0.08) 0%, transparent 65%)",
          }}
        />

        <div className="relative z-10 border-b border-white/[0.07] bg-[#0b0f1a]/80 backdrop-blur sticky top-0">
          <div className="max-w-7xl mx-auto px-6 py-4 flex items-center gap-3">
            <Briefcase className="text-[#6ea8fe] w-5 h-5" />
            <span
              className="font-bold text-[#e8edf5] tracking-tight"
              style={{ fontFamily: "'Plus Jakarta Sans', sans-serif" }}
            >
              Salary Search
            </span>
          </div>
        </div>

        <div className="relative z-10 max-w-7xl mx-auto px-6 py-10 space-y-10">

          <div className="space-y-2">
            <h1
              className="text-4xl font-normal tracking-tight text-[#e8edf5]"
              style={{ fontFamily: "'Instrument Serif', serif", lineHeight: 1.18 }}
            >
              Discover Tech Salaries
            </h1>
            <p
              className="text-[#4a5572] max-w-xl"
              style={{ fontSize: "14px", lineHeight: 1.75, fontWeight: 300 }}
            >
              Search through detailed salary records from the tech industry. Filter
              by role, company, seniority level, and more to find comparable
              compensation data.
            </p>
          </div>



          <Card className="bg-white/[0.025] border-white/[0.07] backdrop-blur-xl shadow-[0_20px_60px_rgba(0,0,0,0.45)]">
            <CardHeader className="pb-4">
              <CardTitle
                className="text-base font-bold text-[#e8edf5] flex items-center gap-2 uppercase tracking-widest"
                style={{ fontSize: "11px", letterSpacing: "0.18em" }}
              >
                <Filter className="w-4 h-4 text-[#6ea8fe]" />
                Search Filters
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Job Title
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <SearchableSelect
                      value={draft.jobTitle ?? ""}
                      onChange={(value: string | null) =>
                        setDraft((p) => ({
                          ...p,
                          jobTitle: (value || undefined) as string | undefined,
                        }))
                      }
                      options={filterOptions.jobTitles || []}
                      placeholder="Search or type job title..."
                    />
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Company
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <Select
                      value={draft.company ?? ""}
                      onValueChange={(v: string | null) =>
                        setDraft((p) => ({ ...p, company: (v || undefined) as string | undefined }))
                      }
                    >
                      <SelectTrigger className="bg-[#0f1524] border-white/[0.08] text-[#e8edf5] focus:ring-[#6ea8fe]">
                        <SelectValue placeholder="Any company" />
                      </SelectTrigger>
                      <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                        <SelectItem value="">Any company</SelectItem>
                        {filterOptions.companies.map((c) => (
                          <SelectItem
                            key={c}
                            value={c}
                            className="text-[#e8edf5] focus:bg-white/[0.05]"
                          >
                            {c}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Country
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <SearchableSelect
                      value={draft.country ?? ""}
                      onChange={(value: string | null) =>
                        setDraft((p) => ({
                          ...p,
                          country: (value || undefined) as string | undefined,
                        }))
                      }
                      options={filterOptions.countries || []}
                      placeholder="Search or type country..."
                    />
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Seniority Level
                  </label>
                  <Select
                    value={draft.seniorityLevel ?? ""}
                    onValueChange={(v: string | null) =>
                      setDraft((p) => ({
                        ...p,
                        seniorityLevel: (v || undefined) as string | undefined,
                      }))
                    }
                  >
                    <SelectTrigger className="bg-[#0f1524] border-white/[0.08] text-[#e8edf5] focus:ring-[#6ea8fe]">
                      <SelectValue placeholder="Any level" />
                    </SelectTrigger>
                    <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                      <SelectItem value="" className="text-[#3a4560]">
                        Any level
                      </SelectItem>
                      {SENIORITY_LEVELS.map((l) => (
                        <SelectItem
                          key={l}
                          value={l}
                          className="text-[#e8edf5] focus:bg-white/[0.05]"
                        >
                          {l}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Employment Type
                  </label>
                  <Select
                    value={draft.employmentType ?? ""}
                    onValueChange={(v: string | null) =>
                      setDraft((p) => ({
                        ...p,
                        employmentType: (v || undefined) as string | undefined,
                      }))
                    }
                  >
                    <SelectTrigger className="bg-[#0f1524] border-white/[0.08] text-[#e8edf5] focus:ring-[#6ea8fe]">
                      <SelectValue placeholder="Any type" />
                    </SelectTrigger>
                    <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                      <SelectItem value="" className="text-[#3a4560]">
                        Any type
                      </SelectItem>
                      {EMPLOYMENT_TYPES.map((t) => (
                        <SelectItem
                          key={t}
                          value={t}
                          className="text-[#e8edf5] focus:bg-white/[0.05]"
                        >
                          {t}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>

                <div className="space-y-1.5">
                  <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
                    Currency
                  </label>
                  {!loadingOptions && filterOptions ? (
                    <SearchableSelect
                      value={draft.currency ?? ""}
                      onChange={(value: string | null) =>
                        setDraft((p) => ({
                          ...p,
                          currency: (value || undefined) as string | undefined,
                        }))
                      }
                      options={filterOptions.currencies || []}
                      placeholder="Search or type currency..."
                    />
                  ) : (
                    <Input disabled placeholder="Loading..." />
                  )}
                </div>
              </div>

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

              <div className="flex justify-end pt-1">
                <Button
                  onClick={handleSearch}
                  disabled={loading}
                  className="bg-gradient-to-br from-[#3b7ff5] to-[#5b5bd6] hover:from-[#5294f7] hover:to-[#6e6edc] text-white font-bold px-6 uppercase tracking-widest text-[13px] transition-all hover:-translate-y-0.5 hover:shadow-[0_10px_28px_rgba(59,127,245,0.35)] disabled:opacity-50 border-0"
                >
                  {loading ? (
                    <span className="flex items-center gap-2">
                      <span className="search-spinner" />
                      Loading…
                    </span>
                  ) : (
                    <span className="flex items-center gap-2">
                      <Search className="w-4 h-4" />
                      Search Salaries
                    </span>
                  )}
                </Button>
              </div>
            </CardContent>
          </Card>

          <div className="space-y-2">
            <label className="text-xs font-semibold text-[#4a5572] uppercase tracking-wider">
              Show Results
            </label>
            <div className="flex flex-wrap gap-2">
              <button
                onClick={() => handleVerificationStatusChange("VERIFIED")}
                disabled={loading}
                className={cn(
                  buttonVariants({ variant: "ghost", size: "default" }),
                  "h-9 px-4",
                  verificationStatus === "VERIFIED"
                    ? "bg-[#6ea8fe]/15 text-[#6ea8fe] border border-[#6ea8fe]/35 hover:bg-[#6ea8fe]/25"
                    : "bg-white/[0.04] text-[#4a5572] border border-white/[0.08] hover:bg-white/[0.08] hover:text-[#e8edf5]",
                  loading && "opacity-50 cursor-not-allowed"
                )}
              >
                Verified Only
              </button>
              <button
                onClick={() => handleVerificationStatusChange("UNVERIFIED")}
                disabled={loading}
                className={cn(
                  buttonVariants({ variant: "ghost", size: "default" }),
                  "h-9 px-4",
                  verificationStatus === "UNVERIFIED"
                    ? "bg-[#6ea8fe]/15 text-[#6ea8fe] border border-[#6ea8fe]/35 hover:bg-[#6ea8fe]/25"
                    : "bg-white/[0.04] text-[#4a5572] border border-white/[0.08] hover:bg-white/[0.08] hover:text-[#e8edf5]",
                  loading && "opacity-50 cursor-not-allowed"
                )}
              >
                Unverified Only
              </button>
              <button
                onClick={() => handleVerificationStatusChange("BOTH")}
                disabled={loading}
                className={cn(
                  buttonVariants({ variant: "ghost", size: "default" }),
                  "h-9 px-4",
                  verificationStatus === "BOTH"
                    ? "bg-[#6ea8fe]/15 text-[#6ea8fe] border border-[#6ea8fe]/35 hover:bg-[#6ea8fe]/25"
                    : "bg-white/[0.04] text-[#4a5572] border border-white/[0.08] hover:bg-white/[0.08] hover:text-[#e8edf5]",
                  loading && "opacity-50 cursor-not-allowed"
                )}
              >
                All Results
              </button>
            </div>
          </div>

          {error && (
            <div
              className="flex items-start gap-3 px-4 py-3 rounded-xl text-sm"
              style={{
                background: "rgba(224,82,82,0.08)",
                border: "1px solid rgba(224,82,82,0.25)",
                color: "#e05252",
              }}
            >
              <span className="flex-shrink-0 mt-0.5">✕</span>
              <span>{error}</span>
            </div>
          )}

          {searched && !loading && results?.content?.length === 0 && !error && (
            <p className="text-[#4a5572] text-sm">
              No results found for your search criteria. Try adjusting your filters.
            </p>
          )}

          {results && results.content.length > 0 && (
            <div className="space-y-8 animate-in fade-in slide-in-from-bottom-4 duration-500">

              <div className="flex flex-col gap-4">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <div className="text-sm text-[#4a5572]">
                    Showing{" "}
                    <span className="font-semibold text-[#6ea8fe]">
                      {paginationInfo?.startItem || 0} -{" "}
                      {paginationInfo?.endItem || 0}
                    </span>{" "}
                    of{" "}
                    <span className="font-semibold text-[#6ea8fe]">
                      {formatNumber(paginationInfo?.totalElements || 0)}
                    </span>{" "}
                    results
                  </div>

                  <Select
                    value={filters.sortBy || "approvedAt"}
                    onValueChange={(v: string | null) => {
                      handleSortChange(v || "approvedAt");
                    }}
                  >
                    <SelectTrigger className="w-auto bg-white/[0.025] border-white/[0.07] text-[#e8edf5] text-sm">
                      <SelectValue>{selectedSortLabel}</SelectValue>
                    </SelectTrigger>
                    <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                      {SORT_OPTIONS.map((opt) => (
                        <SelectItem
                          key={opt.value}
                          value={opt.value}
                          className="text-[#e8edf5] focus:bg-white/[0.05]"
                        >
                          {opt.label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {results.content.map((salary) => (
                  <SalaryCard
                    key={salary.id}
                    salary={salary}
                    onVote={handleVote}
                    voting={votingId === salary.id}
                  />
                ))}
              </div>

              {results.totalPages > 1 && (
                <Card className="bg-white/[0.025] border-white/[0.07] backdrop-blur-xl">
                  <CardContent className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-4">
                    <div className="flex items-center gap-2">
                      <span className="text-sm text-[#4a5572]">Items per page:</span>
                      <Select
                        value={String(filters.size ?? DEFAULT_PAGE_SIZE)}
                        onValueChange={(v: string | null) =>
                          handlePageSizeChange(parseInt(v || "20", 10))
                        }
                      >
                        <SelectTrigger className="w-auto bg-white/[0.025] border-white/[0.07] text-[#e8edf5] text-sm">
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent className="bg-[#0f1524] border-white/[0.08]">
                          {PAGE_SIZES.map((size) => (
                            <SelectItem
                              key={size}
                              value={String(size)}
                              className="text-[#e8edf5] focus:bg-white/[0.05]"
                            >
                              {size}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>
                    </div>

                    <div className="flex items-center gap-2">
                      <Button
                        onClick={() => handlePageChange(results.page - 1)}
                        disabled={results.page === 0 || loading}
                        className="bg-white/[0.05] hover:bg-white/[0.1] text-[#e8edf5] border-white/[0.08] h-9 w-9 p-0"
                      >
                        <ChevronLeft className="w-4 h-4" />
                      </Button>

                      <span className="text-sm text-[#4a5572] min-w-[60px] text-center">
                        Page {results.page + 1} of {results.totalPages}
                      </span>

                      <Button
                        onClick={() => handlePageChange(results.page + 1)}
                        disabled={results.last || loading}
                        className="bg-white/[0.05] hover:bg-white/[0.1] text-[#e8edf5] border-white/[0.08] h-9 w-9 p-0"
                      >
                        <ChevronRight className="w-4 h-4" />
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>
          )}

          {!searched && (
            <div className="text-center py-24 space-y-3">
              <div
                className="w-16 h-16 rounded-2xl flex items-center justify-center mx-auto"
                style={{
                  background: "rgba(255,255,255,0.025)",
                  border: "1px solid rgba(255,255,255,0.07)",
                }}
              >
                <Briefcase className="w-7 h-7 text-[#6ea8fe]" />
              </div>
              <p className="font-semibold text-[#4a5572]">
                Start searching to explore salary data
              </p>
              <p
                style={{
                  fontSize: "13px",
                  color: "#3a4560",
                  maxWidth: "260px",
                  margin: "0 auto",
                }}
              >
                Use the filters above to search for specific job titles, companies,
                and locations.
              </p>
            </div>
          )}
        </div>
      </main>
    </>
  );
}
