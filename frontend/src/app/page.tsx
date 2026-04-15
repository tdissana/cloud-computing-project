"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import { Search, Send, BarChart2, ChevronRight } from "lucide-react";
import { Navbar } from "@/components/shared/Navbar";




// ─── Feature Card ─────────────────────────────────────────────────────────────

function FeatureCard({
  icon, title, desc, cta, onClick,
}: {
  icon: React.ReactNode; title: string; desc: string; cta: string; onClick: () => void;
}) {
  const [hovered, setHovered] = useState(false);
  return (
    <button
      onClick={onClick}
      onMouseEnter={() => setHovered(true)}
      onMouseLeave={() => setHovered(false)}
      style={{
        textAlign: "left", width: "100%",
        padding: "28px 28px 24px",
        background:    hovered ? "rgba(110,168,254,0.06)" : "rgba(255,255,255,0.025)",
        border:        `1px solid ${hovered ? "rgba(110,168,254,0.25)" : "rgba(255,255,255,0.07)"}`,
        borderRadius:  "16px",
        backdropFilter: "blur(20px)",
        boxShadow:     hovered
          ? "0 24px 60px rgba(0,0,0,0.5), 0 1px 0 rgba(255,255,255,0.06) inset"
          : "0 20px 50px rgba(0,0,0,0.4), 0 1px 0 rgba(255,255,255,0.04) inset",
        cursor: "pointer",
        transition: "all 0.2s ease",
        transform: hovered ? "translateY(-3px)" : "none",
        display: "flex", flexDirection: "column", gap: "16px",
        fontFamily: "'Plus Jakarta Sans', sans-serif",
      }}
    >
      <div style={{
        width: "44px", height: "44px", borderRadius: "12px",
        background:  hovered ? "rgba(110,168,254,0.15)" : "rgba(255,255,255,0.05)",
        border:      `1px solid ${hovered ? "rgba(110,168,254,0.3)" : "rgba(255,255,255,0.07)"}`,
        display: "flex", alignItems: "center", justifyContent: "center",
        color: "#6ea8fe", transition: "all 0.2s ease", flexShrink: 0,
      }}>
        {icon}
      </div>

      <div style={{ flex: 1 }}>
        <div style={{
          fontSize: "15px", fontWeight: 700, color: "#e8edf5",
          marginBottom: "8px", letterSpacing: "-0.01em",
        }}>
          {title}
        </div>
        <div style={{ fontSize: "13.5px", color: "#8392b0", lineHeight: 1.65, fontWeight: 400 }}>
          {desc}
        </div>
      </div>

      <div style={{
        display: "flex", alignItems: "center", gap: "6px",
        fontSize: "12px", fontWeight: 600,
        color: hovered ? "#6ea8fe" : "#4a5e80",
        letterSpacing: "0.06em", textTransform: "uppercase",
        transition: "color 0.2s ease",
      }}>
        {cta}
        <ChevronRight style={{
          width: "13px", height: "13px",
          transition: "transform 0.2s ease",
          transform: hovered ? "translateX(3px)" : "none",
        }} />
      </div>
    </button>
  );
}

// ─── Home Landing ─────────────────────────────────────────────────────────────

function HomeLanding() {
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const router = useRouter();

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    let raf: number;
    const resize = () => {
      canvas.width  = window.innerWidth;
      canvas.height = window.innerHeight;
    };
    resize();
    window.addEventListener("resize", resize);
    const particles: { x: number; y: number; vx: number; vy: number; size: number; alpha: number }[] = [];
    for (let i = 0; i < 55; i++) {
      particles.push({
        x: Math.random() * window.innerWidth,
        y: Math.random() * window.innerHeight,
        vx: (Math.random() - 0.5) * 0.38,
        vy: (Math.random() - 0.5) * 0.38,
        size:  Math.random() * 1.4 + 0.4,
        alpha: Math.random() * 0.32 + 0.08,
      });
    }
    const draw = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      particles.forEach((p) => {
        p.x += p.vx; p.y += p.vy;
        if (p.x < 0 || p.x > canvas.width)  p.vx *= -1;
        if (p.y < 0 || p.y > canvas.height) p.vy *= -1;
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(110,168,254,${p.alpha})`;
        ctx.fill();
      });
      for (let i = 0; i < particles.length; i++) {
        for (let j = i + 1; j < particles.length; j++) {
          const dx = particles[i].x - particles[j].x;
          const dy = particles[i].y - particles[j].y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < 120) {
            ctx.beginPath();
            ctx.moveTo(particles[i].x, particles[i].y);
            ctx.lineTo(particles[j].x, particles[j].y);
            ctx.strokeStyle = `rgba(110,168,254,${0.07 * (1 - dist / 120)})`;
            ctx.lineWidth = 0.7;
            ctx.stroke();
          }
        }
      }
      raf = requestAnimationFrame(draw);
    };
    draw();
    return () => { cancelAnimationFrame(raf); window.removeEventListener("resize", resize); };
  }, []);

  return (
    <main
      className="min-h-screen bg-[#0b0f1a]"
      style={{ fontFamily: "'Plus Jakarta Sans', sans-serif" }}
    >
      <canvas ref={canvasRef} style={{ position: "fixed", inset: 0, zIndex: 0, pointerEvents: "none" }} />
      <div className="fixed inset-0 z-0 pointer-events-none" style={{ background: "radial-gradient(ellipse 80% 60% at 50% 0%, rgba(30,60,120,0.35) 0%, transparent 70%)" }} />
      <div className="fixed z-0 pointer-events-none" style={{ bottom: "-15%", right: "-5%", width: "50vw", height: "50vw", borderRadius: "50%", background: "radial-gradient(circle, rgba(91,91,214,0.08) 0%, transparent 65%)" }} />

      <div className="relative z-10 max-w-6xl mx-auto px-4 sm:px-6 pt-20 pb-24 space-y-20">

        {/* ── Hero ── */}
        <div style={{ textAlign: "center", maxWidth: "680px", margin: "0 auto" }} className="space-y-6">
          <div style={{
            display: "inline-flex", alignItems: "center", gap: "8px",
            padding: "6px 14px", borderRadius: "100px",
            background: "rgba(110,168,254,0.08)",
            border: "1px solid rgba(110,168,254,0.18)",
            fontSize: "11px", fontWeight: 600, color: "#6ea8fe",
            letterSpacing: "0.12em", textTransform: "uppercase",
            marginBottom: "8px",
          }}>
            <span style={{ width: "6px", height: "6px", borderRadius: "50%", background: "#6ea8fe", display: "inline-block" }} />
            Sri Lanka&apos;s Salary Transparency Community
          </div>

          <h1 style={{
            fontFamily: "'Instrument Serif', serif",
            fontSize: "clamp(38px, 6vw, 66px)",
            fontWeight: 400, lineHeight: 1.1,
            color: "#e8edf5", letterSpacing: "-0.02em",
            margin: 0,
          }}>
            Know your worth.<br />
            <em style={{ color: "#6ea8fe" }}>Shape the market.</em>
          </h1>

          <p style={{
            fontSize: "16px", color: "#8392b0",
            lineHeight: 1.75, fontWeight: 300,
            maxWidth: "500px", margin: "0 auto",
          }}>
            Anonymous salary data contributed by Sri Lanka&apos;s tech community.
            Submit without logging in, vote to verify, search to benchmark.
          </p>
        </div>

        {/* ── Stats strip ── */}
        <div className="stats-grid" style={{
          display: "grid", gridTemplateColumns: "repeat(3, 1fr)",
          gap: "1px", background: "rgba(255,255,255,0.06)",
          border: "1px solid rgba(255,255,255,0.07)",
          borderRadius: "16px", overflow: "hidden",
        }}>
          {[
            { value: "Anonymous", label: "All Submissions",   sub: "No login required to submit" },
            { value: "Community", label: "Verified Data",     sub: "Upvoted by real members" },
            { value: "Real-time", label: "Salary Insights",   sub: "Averages, medians & percentiles" },
          ].map(({ value, label, sub }) => (
            <div key={label} style={{ padding: "28px 24px", background: "rgba(255,255,255,0.018)", textAlign: "center" }}>
              <div style={{ fontFamily: "'Instrument Serif', serif", fontSize: "22px", color: "#6ea8fe", marginBottom: "4px" }}>{value}</div>
              <div style={{ fontSize: "13px", fontWeight: 600, color: "#c8d6f0", marginBottom: "4px" }}>{label}</div>
              <div style={{ fontSize: "11.5px", color: "#5a6e90" }}>{sub}</div>
            </div>
          ))}
        </div>

        {/* ── Feature cards ── */}
        <div>
          <p style={{ fontSize: "11px", fontWeight: 600, letterSpacing: "0.18em", textTransform: "uppercase", color: "#6ea8fe", marginBottom: "20px" }}>
            What you can do
          </p>
          <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))", gap: "16px" }}>
            <FeatureCard
              icon={<Send className="w-5 h-5" />}
              title="Submit a Salary"
              desc="Share your compensation anonymously. No account required. Your identity is never linked to your submission."
              cta="Submit now"
              onClick={() => router.push("/submit")}
            />
            <FeatureCard
              icon={<Search className="w-5 h-5" />}
              title="Search Salaries"
              desc="Browse verified salary records filtered by role, company, seniority level, country, and employment type."
              cta="Start searching"
              onClick={() => router.push("/search")}
            />
            <FeatureCard
              icon={<BarChart2 className="w-5 h-5" />}
              title="View Statistics"
              desc="Explore aggregated salary insights — averages, medians and 90th percentile breakdowns across the industry."
              cta="See stats"
              onClick={() => router.push("/stats")}
            />
          </div>
        </div>

        {/* ── How it works ── */}
        <div>
          <p style={{ fontSize: "11px", fontWeight: 600, letterSpacing: "0.18em", textTransform: "uppercase", color: "#6ea8fe", marginBottom: "20px" }}>
            How it works
          </p>
          <div style={{ display: "flex", flexDirection: "column", gap: "12px" }}>
            {[
              { n: "01", title: "Submit anonymously",    desc: "Anyone can submit salary data — no sign-up, no tracking, no link to your identity." },
              { n: "02", title: "Community votes",       desc: "Logged-in members upvote or downvote submissions. Once a threshold is reached, the entry is approved." },
              { n: "03", title: "Approved data appears", desc: "Approved submissions appear in search results and are included in the statistics aggregations." },
              { n: "04", title: "Benchmark your salary", desc: "Search and filter approved records to compare your compensation against the market." },
            ].map(({ n, title, desc }) => (
              <div key={n} style={{
                display: "flex", gap: "20px", alignItems: "flex-start",
                padding: "20px 24px",
                background: "rgba(255,255,255,0.02)",
                border: "1px solid rgba(255,255,255,0.06)",
                borderRadius: "12px",
              }}>
                <span style={{ fontFamily: "'Instrument Serif', serif", fontSize: "20px", color: "rgba(110,168,254,0.4)", flexShrink: 0, lineHeight: 1.3, minWidth: "28px" }}>{n}</span>
                <div>
                  <div style={{ fontSize: "14px", fontWeight: 600, color: "#ccd8f0", marginBottom: "4px" }}>{title}</div>
                  <div style={{ fontSize: "13px", color: "#7a8daa", lineHeight: 1.65 }}>{desc}</div>
                </div>
              </div>
            ))}
          </div>
        </div>

        {/* ── Privacy strip ── */}
        <div style={{ padding: "20px 28px", background: "rgba(110,168,254,0.04)", border: "1px solid rgba(110,168,254,0.12)", borderRadius: "12px", display: "flex", alignItems: "center", gap: "14px" }}>
          <span style={{ fontSize: "22px", flexShrink: 0 }}>🔒</span>
          <div>
            <div style={{ fontSize: "13.5px", fontWeight: 600, color: "#c0d0ee", marginBottom: "3px" }}>Privacy by design</div>
            <div style={{ fontSize: "12.5px", color: "#5a6e90", lineHeight: 1.6 }}>
              User identities are stored in a completely separate schema from salary data.
              Salary records contain no email, no name, and no link to any account.
              Login is only required for upvoting and downvoting.
            </div>
          </div>
        </div>

        {/* ── Footer ── */}
        <footer style={{ textAlign: "center", paddingTop: "8px", paddingBottom: "24px" }}>
          <p style={{ fontSize: "11px", color: "#2e3a50", letterSpacing: "0.04em" }}>
            © 2026 watupa.lk · Privacy-first · Open data
          </p>
        </footer>

      </div>
    </main>
  );
}

// ─── Root Page ────────────────────────────────────────────────────────────────

export default function HomePage() {
  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap');
        *, *::before, *::after { box-sizing: border-box; }
        html, body { margin: 0; background: #0b0f1a; }
        button, [role="button"], a, select { cursor: pointer !important; }
        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-thumb { background: rgba(110,168,254,0.2); border-radius: 3px; }
        @media (max-width: 640px) {
          .stats-grid { grid-template-columns: 1fr !important; }
        }
      `}</style>

      <Navbar />
      <HomeLanding />
    </>
  );
}
