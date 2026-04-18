"use client";

import { usePathname, useRouter } from "next/navigation";
import { Send, Search, BarChart2 } from "lucide-react";

const tabs = [
  { href: "/submit", label: "Submit Salary", icon: <Send    className="w-4 h-4" /> },
  { href: "/search", label: "Search",        icon: <Search  className="w-4 h-4" /> },
  { href: "/stats",  label: "Statistics",    icon: <BarChart2 className="w-4 h-4" /> },
] as const;

export function Navbar() {
  const pathname = usePathname();
  const router   = useRouter();

  return (
    <>
      <style>{`
        button, [role="button"], a, select,
        input[type="checkbox"], input[type="radio"],
        input[type="submit"], input[type="button"] {
          cursor: pointer !important;
        }
        @media (max-width: 480px) {
          .nav-tab-label { display: none !important; }
          .nav-tabs { gap: 2px !important; }
          .nav-tab { padding: 7px 10px !important; }
        }
      `}</style>

      <nav
        className="relative z-20 border-b border-white/[0.07] bg-[#0b0f1a]/90 backdrop-blur-xl sticky top-0"
        style={{ fontFamily: "'Plus Jakarta Sans', sans-serif" }}
      >
        <div className="max-w-7xl mx-auto px-4 sm:px-6 flex items-center justify-between h-14">

          {/* ── Logo → home ── */}
          <button
            onClick={() => router.push("/")}
            style={{
              display: "flex", alignItems: "center", gap: "10px",
              background: "none", border: "none", cursor: "pointer",
              padding: 0, flexShrink: 0,
            }}
          >
            <div style={{
              width: "30px", height: "30px", borderRadius: "7px",
              background: "linear-gradient(135deg, #3b7ff5, #5b5bd6)",
              display: "flex", alignItems: "center", justifyContent: "center",
              fontSize: "15px", flexShrink: 0,
            }}>
              💼
            </div>
            <span style={{
              fontFamily: "'Plus Jakarta Sans', sans-serif",
              fontWeight: 700, fontSize: "16px",
              color: "#e8edf5", letterSpacing: "-0.01em",
            }}>
              watupa<span style={{ color: "#6ea8fe" }}>.lk</span>
            </span>
          </button>

          {/* ── Nav tabs ── */}
          <div className="nav-tabs" style={{ display: "flex", alignItems: "center", gap: "4px" }}>
            {tabs.map((t) => {
              const active = pathname === t.href || pathname.startsWith(t.href + "/");
              return (
                <button
                  key={t.href}
                  className="nav-tab"
                  onClick={() => router.push(t.href)}
                  style={{
                    display: "flex", alignItems: "center", gap: "8px",
                    padding: "7px 14px", borderRadius: "8px",
                    fontSize: "13px",
                    fontWeight: active ? 600 : 400,
                    color:      active ? "#6ea8fe"               : "#6b7a99",
                    background: active ? "rgba(110,168,254,0.1)" : "transparent",
                    border:     active ? "1px solid rgba(110,168,254,0.2)" : "1px solid transparent",
                    cursor: "pointer",
                    transition: "all 0.18s ease",
                    fontFamily: "'Plus Jakarta Sans', sans-serif",
                    whiteSpace: "nowrap",
                  }}
                  onMouseEnter={(e) => {
                    if (!active) {
                      const el = e.currentTarget as HTMLButtonElement;
                      el.style.color      = "#a5c8fe";
                      el.style.background = "rgba(255,255,255,0.04)";
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (!active) {
                      const el = e.currentTarget as HTMLButtonElement;
                      el.style.color      = "#6b7a99";
                      el.style.background = "transparent";
                    }
                  }}
                >
                  {t.icon}
                  <span className="nav-tab-label">{t.label}</span>
                </button>
              );
            })}
          </div>

        </div>
      </nav>
    </>
  );
}
