"use client";

import { useState, useRef, useEffect } from "react";
import { Send, Lock, CheckCircle2, AlertCircle, ChevronDown } from "lucide-react";

import { submitSalary } from "@/lib/salary/submit";
import { LEVELS, COUNTRIES, CURRENCIES } from "@/lib/salary/constants";
import { SalarySubmissionRequest } from "@/types/salary";

// ─── Validation ───────────────────────────────────────────────────────────────

type FormErrors = Partial<Record<keyof SalarySubmissionRequest, string>>;

function validateForm(f: SalarySubmissionRequest): FormErrors {
  const errs: FormErrors = {};

  if (!f.role || f.role.trim().length < 2)
    errs.role = "Please enter a job role (min 2 characters)";
  if (!f.company || f.company.trim().length < 2)
    errs.company = "Please enter a company name (min 2 characters)";
  if (!f.experienceLevel)
    errs.experienceLevel = "Please select an experience level";
  if (!f.country)
    errs.country = "Please select a country";
  if (!f.baseSalary || f.baseSalary <= 0)
    errs.baseSalary = "Please enter a valid base salary";
  if (!f.totalCompensation || f.totalCompensation <= 0)
    errs.totalCompensation = "Please enter a valid total compensation";
  if (!f.currency)
    errs.currency = "Please select a currency";

  return errs;
}

// ─── Floating Input ───────────────────────────────────────────────────────────

interface FloatingFieldProps {
  id: string;
  label: string;
  value: string;
  onChange: (v: string) => void;
  error?: string;
  type?: string;
  maxLength?: number;
  placeholder?: string;
}

function FloatingField({
  id, label, value, onChange, error, type = "text", maxLength, placeholder,
}: FloatingFieldProps) {
  const [focused, setFocused] = useState(false);
  const raised = focused || value.length > 0;

  return (
    <div style={{ position: "relative", marginBottom: "20px" }}>
      <label
        htmlFor={id}
        style={{
          position: "absolute",
          top: raised ? "6px" : "50%",
          left: "14px",
          transform: raised ? "none" : "translateY(-50%)",
          fontSize: raised ? "10px" : "13px",
          fontWeight: raised ? 600 : 400,
          color: error ? "#e05252" : raised ? "#6ea8fe" : "#4a5572",
          letterSpacing: raised ? "0.08em" : "0",
          textTransform: raised ? "uppercase" : "none",
          transition: "all 0.18s cubic-bezier(0.22,1,0.36,1)",
          pointerEvents: "none",
          zIndex: 1,
        }}
      >
        {label}
      </label>
      <input
        id={id}
        type={type}
        value={value}
        maxLength={maxLength}
        placeholder={focused ? (placeholder ?? "") : ""}
        onFocus={() => setFocused(true)}
        onBlur={() => setFocused(false)}
        onChange={(e) => onChange(e.target.value)}
        style={{
          width: "100%",
          padding: raised ? "22px 14px 8px" : "14px 14px",
          background: "#0f1524",
          border: `1px solid ${error ? "rgba(224,82,82,0.5)" : focused ? "rgba(110,168,254,0.5)" : "rgba(255,255,255,0.08)"}`,
          borderRadius: "10px",
          color: "#e8edf5",
          fontSize: "14px",
          fontFamily: "'Plus Jakarta Sans', sans-serif",
          outline: "none",
          transition: "border-color 0.18s ease",
          boxSizing: "border-box",
          boxShadow: focused ? "0 0 0 3px rgba(110,168,254,0.08)" : "none",
        }}
      />
      {error && (
        <p style={{ marginTop: "5px", fontSize: "11.5px", color: "#e05252", display: "flex", alignItems: "center", gap: "4px" }}>
          <AlertCircle style={{ width: "11px", height: "11px", flexShrink: 0 }} />
          {error}
        </p>
      )}
    </div>
  );
}

// ─── Select Field ─────────────────────────────────────────────────────────────

interface SelectFieldProps {
  id: string;
  label: string;
  value: string;
  onChange: (v: string) => void;
  options: { value: string; label: string }[];
  error?: string;
}

function SelectField({ id, label, value, onChange, options, error }: SelectFieldProps) {
  const [focused, setFocused] = useState(false);
  const raised = value.length > 0;

  return (
    <div style={{ position: "relative", marginBottom: "20px" }}>
      <label
        htmlFor={id}
        style={{
          position: "absolute",
          top: raised ? "6px" : "50%",
          left: "14px",
          transform: raised ? "none" : "translateY(-50%)",
          fontSize: raised ? "10px" : "13px",
          fontWeight: raised ? 600 : 400,
          color: error ? "#e05252" : raised ? "#6ea8fe" : "#4a5572",
          letterSpacing: raised ? "0.08em" : "0",
          textTransform: raised ? "uppercase" : "none",
          transition: "all 0.18s cubic-bezier(0.22,1,0.36,1)",
          pointerEvents: "none",
          zIndex: 1,
        }}
      >
        {label}
      </label>
      <div style={{ position: "relative" }}>
        <select
          id={id}
          value={value}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          onChange={(e) => onChange(e.target.value)}
          style={{
            width: "100%",
            padding: raised ? "22px 36px 8px 14px" : "14px 36px 14px 14px",
            background: "#0f1524",
            border: `1px solid ${error ? "rgba(224,82,82,0.5)" : focused ? "rgba(110,168,254,0.5)" : "rgba(255,255,255,0.08)"}`,
            borderRadius: "10px",
            color: value ? "#e8edf5" : "#3a4560",
            fontSize: "14px",
            fontFamily: "'Plus Jakarta Sans', sans-serif",
            outline: "none",
            appearance: "none",
            cursor: "pointer",
            transition: "border-color 0.18s ease",
            boxSizing: "border-box",
            boxShadow: focused ? "0 0 0 3px rgba(110,168,254,0.08)" : "none",
          }}
        >
          <option value="" disabled hidden />
          {options.map((o) => (
            <option key={o.value} value={o.value} style={{ background: "#0f1524", color: "#e8edf5" }}>
              {o.label}
            </option>
          ))}
        </select>
        <ChevronDown
          style={{
            position: "absolute",
            right: "12px",
            top: "50%",
            transform: "translateY(-50%)",
            width: "15px",
            height: "15px",
            color: "#3a4560",
            pointerEvents: "none",
          }}
        />
      </div>
      {error && (
        <p style={{ marginTop: "5px", fontSize: "11.5px", color: "#e05252", display: "flex", alignItems: "center", gap: "4px" }}>
          <AlertCircle style={{ width: "11px", height: "11px", flexShrink: 0 }} />
          {error}
        </p>
      )}
    </div>
  );
}

// ─── Step indicator ───────────────────────────────────────────────────────────

function StepDot({ active, done, n }: { active: boolean; done: boolean; n: number }) {
  return (
    <div
      style={{
        width: "28px",
        height: "28px",
        borderRadius: "50%",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        fontSize: "12px",
        fontWeight: 700,
        flexShrink: 0,
        background: done
          ? "rgba(76,175,130,0.15)"
          : active
          ? "rgba(59,127,245,0.18)"
          : "rgba(255,255,255,0.04)",
        border: `1px solid ${done ? "rgba(76,175,130,0.4)" : active ? "rgba(110,168,254,0.4)" : "rgba(255,255,255,0.08)"}`,
        color: done ? "#4caf82" : active ? "#6ea8fe" : "#3a4560",
        transition: "all 0.25s ease",
      }}
    >
      {done ? <CheckCircle2 style={{ width: "13px", height: "13px" }} /> : n}
    </div>
  );
}

// ─── Main Page ────────────────────────────────────────────────────────────────

const EMPTY_FORM: SalarySubmissionRequest = {
  company: "",
  role: "",
  experienceLevel: "",
  country: "",
  baseSalary: 0,
  totalCompensation: 0,
  currency: "LKR",
  anonymize: true,
};

export default function SubmitPage() {
  const [step, setStep] = useState<1 | 2>(1);
  const [form, setForm] = useState<SalarySubmissionRequest>(EMPTY_FORM);
  const [errors, setErrors] = useState<FormErrors>({});
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<{ type: "success" | "error"; text: string } | null>(null);
  const [submitted, setSubmitted] = useState(false);
  const canvasRef = useRef<HTMLCanvasElement>(null);

  // Salary display values (strings for controlled inputs)
  const [baseSalaryStr, setBaseSalaryStr] = useState("");
  const [totalCompStr, setTotalCompStr] = useState("");

  // ── Particle canvas ──────────────────────────────────────────────────────────
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;
    let raf: number;

    const resize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };
    resize();
    window.addEventListener("resize", resize);

    const particles: { x: number; y: number; vx: number; vy: number; size: number; alpha: number }[] = [];
    for (let i = 0; i < 80; i++) {
      particles.push({
        x: Math.random() * window.innerWidth,
        y: Math.random() * window.innerHeight,
        vx: (Math.random() - 0.5) * 0.35,
        vy: (Math.random() - 0.5) * 0.35,
        size: Math.random() * 2.5 + 0.8,
        alpha: Math.random() * 0.4 + 0.2,
      });
    }

    const draw = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      particles.forEach((p) => {
        p.x += p.vx;
        p.y += p.vy;
        if (p.x < 0 || p.x > canvas.width) p.vx *= -1;
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
          if (dist < 110) {
            ctx.beginPath();
            ctx.moveTo(particles[i].x, particles[i].y);
            ctx.lineTo(particles[j].x, particles[j].y);
            ctx.strokeStyle = `rgba(110,168,254,${0.07 * (1 - dist / 110)})`;
            ctx.lineWidth = 0.6;
            ctx.stroke();
          }
        }
      }
      raf = requestAnimationFrame(draw);
    };
    draw();
    return () => {
      cancelAnimationFrame(raf);
      window.removeEventListener("resize", resize);
    };
  }, []);

  const set = <K extends keyof SalarySubmissionRequest>(key: K, value: SalarySubmissionRequest[K]) => {
    setForm((f) => ({ ...f, [key]: value }));
    setErrors((e) => ({ ...e, [key]: undefined }));
  };

  const handleNext = () => {
    // Validate step 1 fields only
    const step1Errors: FormErrors = {};
    if (!form.role || form.role.trim().length < 2) step1Errors.role = "Please enter a job role (min 2 characters)";
    if (!form.company || form.company.trim().length < 2) step1Errors.company = "Please enter a company name (min 2 characters)";
    if (!form.experienceLevel) step1Errors.experienceLevel = "Please select an experience level";
    if (!form.country) step1Errors.country = "Please select a country";

    if (Object.keys(step1Errors).length > 0) {
      setErrors(step1Errors);
      return;
    }
    setStep(2);
  };

  const handleSubmit = async () => {
    const allErrors = validateForm(form);
    if (Object.keys(allErrors).length > 0) {
      setErrors(allErrors);
      return;
    }

    setLoading(true);
    setResult(null);
    try {
      const res = await submitSalary(form);
      setResult({ type: "success", text: res.message ?? "Salary submitted successfully! It will appear after community review." });
      setSubmitted(true);
    } catch (e) {
      setResult({ type: "error", text: (e as Error).message });
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setForm(EMPTY_FORM);
    setBaseSalaryStr("");
    setTotalCompStr("");
    setErrors({});
    setResult(null);
    setSubmitted(false);
    setStep(1);
  };

  const step1Done = !!(form.role && form.company && form.experienceLevel && form.country);
  const step2Done = !!(form.baseSalary > 0 && form.totalCompensation > 0 && form.currency);

  return (
    <>
      <style>{`
        @import url('https://fonts.googleapis.com/css2?family=Instrument+Serif:ital@0;1&family=Plus+Jakarta+Sans:wght@300;400;500;600;700&display=swap');

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        html, body {
          background: #0b0f1a;
          font-family: 'Plus Jakarta Sans', sans-serif;
          color: #e8edf5;
          min-height: 100vh;
        }

        @keyframes fadeUp {
          from { opacity: 0; transform: translateY(18px); }
          to   { opacity: 1; transform: translateY(0); }
        }

        @keyframes spin {
          to { transform: rotate(360deg); }
        }

        @keyframes successPop {
          0%   { transform: scale(0.85); opacity: 0; }
          60%  { transform: scale(1.04); }
          100% { transform: scale(1); opacity: 1; }
        }

        .fade-up { animation: fadeUp 0.35s cubic-bezier(0.22,1,0.36,1) forwards; }

        .submit-btn {
          width: 100%;
          padding: 13px 20px;
          background: linear-gradient(135deg, #3b7ff5 0%, #5b5bd6 100%);
          border: none;
          border-radius: 10px;
          color: #fff;
          font-family: 'Plus Jakarta Sans', sans-serif;
          font-size: 13.5px;
          font-weight: 700;
          letter-spacing: 0.06em;
          text-transform: uppercase;
          cursor: pointer;
          transition: transform 0.15s ease, box-shadow 0.15s ease, opacity 0.15s ease;
          margin-top: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          gap: 8px;
        }
        .submit-btn:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 10px 28px rgba(59,127,245,0.35);
        }
        .submit-btn:active:not(:disabled) {
          transform: translateY(0);
        }
        .submit-btn:disabled {
          opacity: 0.55;
          cursor: not-allowed;
        }

        .secondary-btn {
          width: 100%;
          padding: 13px 20px;
          background: rgba(255,255,255,0.04);
          border: 1px solid rgba(255,255,255,0.09);
          border-radius: 10px;
          color: #6ea8fe;
          font-family: 'Plus Jakarta Sans', sans-serif;
          font-size: 13.5px;
          font-weight: 700;
          letter-spacing: 0.06em;
          text-transform: uppercase;
          cursor: pointer;
          transition: background 0.15s ease, border-color 0.15s ease;
          margin-top: 6px;
        }
        .secondary-btn:hover {
          background: rgba(110,168,254,0.07);
          border-color: rgba(110,168,254,0.25);
        }

        .spinner {
          display: inline-block;
          width: 14px;
          height: 14px;
          border: 2px solid rgba(255,255,255,0.3);
          border-top-color: #fff;
          border-radius: 50%;
          animation: spin 0.75s linear infinite;
        }

        select option { background: #0f1524; color: #e8edf5; }

        input[type=number]::-webkit-inner-spin-button,
        input[type=number]::-webkit-outer-spin-button { -webkit-appearance: none; }
        input[type=number] { -moz-appearance: textfield; }

        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-track { background: transparent; }
        ::-webkit-scrollbar-thumb { background: rgba(110,168,254,0.2); border-radius: 3px; }
      `}</style>

      {/* Particle canvas */}
      <canvas
        ref={canvasRef}
        style={{ position: "fixed", inset: 0, zIndex: 0, pointerEvents: "none" }}
      />

      {/* Radial backdrops */}
      <div style={{ position: "fixed", inset: 0, zIndex: 0, pointerEvents: "none", background: "radial-gradient(ellipse 80% 60% at 50% 0%, rgba(30,60,120,0.35) 0%, transparent 70%)" }} />
      <div style={{ position: "fixed", bottom: "-15%", right: "-5%", zIndex: 0, pointerEvents: "none", width: "50vw", height: "50vw", borderRadius: "50%", background: "radial-gradient(circle, rgba(91,91,214,0.08) 0%, transparent 65%)" }} />

      {/* Layout */}
      <div style={{ minHeight: "100vh", display: "flex", position: "relative", zIndex: 1 }}>

        {/* ── Left panel ── */}
        <div
          className="left-panel"
          style={{ flex: "0 0 42%", display: "flex", flexDirection: "column", justifyContent: "space-between", padding: "48px 52px", borderRight: "1px solid rgba(255,255,255,0.05)" }}
        >
          {/* Logo */}
          <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
            <div style={{ width: "32px", height: "32px", borderRadius: "8px", background: "linear-gradient(135deg, #3b7ff5, #5b5bd6)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "16px" }}>
              💼
            </div>
            <span style={{ fontFamily: "'Plus Jakarta Sans', sans-serif", fontWeight: 700, fontSize: "17px", color: "#e8edf5", letterSpacing: "-0.01em" }}>
              watupa<span style={{ color: "#6ea8fe" }}>.lk</span>
            </span>
          </div>

          {/* Hero text */}
          <div>
            <p style={{ fontFamily: "'Plus Jakarta Sans', sans-serif", fontSize: "11px", fontWeight: 600, letterSpacing: "0.18em", textTransform: "uppercase", color: "#6ea8fe", marginBottom: "18px" }}>
              Salary Submission
            </p>
            <h1 style={{ fontFamily: "'Instrument Serif', serif", fontSize: "clamp(28px, 3.2vw, 42px)", fontWeight: 400, lineHeight: 1.2, color: "#e8edf5", letterSpacing: "-0.01em", marginBottom: "22px" }}>
              Share your salary.
              <br />
              <em style={{ color: "#6ea8fe" }}>Help others know their worth.</em>
            </h1>
            <p style={{ fontSize: "14px", color: "#5a6888", lineHeight: 1.75, maxWidth: "340px", fontWeight: 300 }}>
              Your submission is always anonymous. No account required. Data is stored without any link to your identity.
            </p>

            {/* Privacy promise cards */}
            <div style={{ marginTop: "36px", display: "flex", flexDirection: "column", gap: "12px" }}>
              {[
                { icon: "🔒", title: "No Login Required", desc: "Anyone can submit salary data — no sign-up needed." },
                { icon: "👤", title: "Fully Anonymous", desc: "Submissions are never linked to your email or account." },
                { icon: "✅", title: "Community Verified", desc: "Submissions go through community upvote review before appearing in stats." },
              ].map(({ icon, title, desc }) => (
                <div
                  key={title}
                  style={{ display: "flex", gap: "12px", padding: "12px 14px", background: "rgba(255,255,255,0.025)", border: "1px solid rgba(255,255,255,0.06)", borderRadius: "10px" }}
                >
                  <span style={{ fontSize: "18px", flexShrink: 0 }}>{icon}</span>
                  <div>
                    <div style={{ fontSize: "13px", fontWeight: 600, color: "#e8edf5", marginBottom: "2px" }}>{title}</div>
                    <div style={{ fontSize: "12px", color: "#4a5572", lineHeight: 1.55 }}>{desc}</div>
                  </div>
                </div>
              ))}
            </div>

            {/* Step progress on left panel */}
            <div style={{ marginTop: "36px", paddingTop: "28px", borderTop: "1px solid rgba(255,255,255,0.06)", display: "flex", gap: "8px", alignItems: "center" }}>
              {[
                { n: 1, label: "Job Details" },
                { n: 2, label: "Compensation" },
              ].map(({ n, label }, i) => (
                <div key={n} style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                  {i > 0 && <div style={{ width: "24px", height: "1px", background: "rgba(255,255,255,0.08)" }} />}
                  <div style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                    <StepDot active={step === n} done={n === 1 ? step > 1 : submitted} n={n} />
                    <span style={{ fontSize: "12px", color: step === n ? "#6ea8fe" : "#3a4560", fontWeight: step === n ? 600 : 400 }}>
                      {label}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Footer */}
          <p style={{ fontSize: "11px", color: "white", letterSpacing: "0.03em" }}>
            © {new Date().getFullYear()} watupa.lk · Privacy-first · Open data
          </p>
        </div>

        {/* ── Right panel — form ── */}
        <div style={{ flex: 1, display: "flex", alignItems: "center", justifyContent: "center", padding: "40px 24px" }}>
          <div style={{ width: "100%", maxWidth: "440px" }}>

            {/* Mobile logo */}
            <div style={{ marginBottom: "28px", display: "none" }} className="mobile-logo">
              <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
                <div style={{ width: "28px", height: "28px", borderRadius: "7px", background: "linear-gradient(135deg, #3b7ff5, #5b5bd6)", display: "flex", alignItems: "center", justifyContent: "center", fontSize: "14px" }}>
                  💼
                </div>
                <span style={{ fontWeight: 700, fontSize: "16px", letterSpacing: "-0.01em" }}>
                  watupa<span style={{ color: "#6ea8fe" }}>.lk</span>
                </span>
              </div>
            </div>

            {/* Success state */}
            {submitted && result?.type === "success" ? (
              <div className="fade-up" style={{ textAlign: "center" }}>
                <div
                  style={{
                    width: "72px", height: "72px", borderRadius: "50%", margin: "0 auto 24px",
                    background: "rgba(76,175,130,0.12)", border: "1px solid rgba(76,175,130,0.35)",
                    display: "flex", alignItems: "center", justifyContent: "center",
                    animation: "successPop 0.4s cubic-bezier(0.22,1,0.36,1) forwards",
                  }}
                >
                  <CheckCircle2 style={{ width: "36px", height: "36px", color: "#4caf82" }} />
                </div>
                <h2 style={{ fontFamily: "'Instrument Serif', serif", fontSize: "28px", fontWeight: 400, color: "#e8edf5", marginBottom: "10px" }}>
                  Submission received!
                </h2>
                <p style={{ fontSize: "14px", color: "#4a5572", lineHeight: 1.7, marginBottom: "32px", maxWidth: "320px", margin: "0 auto 32px" }}>
                  {result.text}
                </p>
                <div
                  style={{
                    padding: "16px 20px", background: "rgba(59,127,245,0.06)",
                    border: "1px solid rgba(110,168,254,0.14)", borderRadius: "12px",
                    fontSize: "12.5px", color: "#4a6088", lineHeight: 1.65, marginBottom: "28px", textAlign: "left",
                  }}
                >
                  <strong style={{ color: "#6ea8fe", fontWeight: 600 }}>What happens next?</strong>
                  <br />
                  Your entry is stored as <code style={{ background: "rgba(255,255,255,0.06)", padding: "1px 5px", borderRadius: "4px", fontSize: "11px" }}>PENDING</code> and
                  will be reviewed by the community. Once it reaches the approval threshold via upvotes, it will appear in search and statistics.
                </div>
                <button className="secondary-btn" onClick={handleReset}>
                  Submit Another Salary
                </button>
              </div>
            ) : (
              <>
                {/* Heading */}
                <div style={{ marginBottom: "24px" }}>
                  <h2 style={{ fontFamily: "'Instrument Serif', serif", fontSize: "28px", fontWeight: 400, color: "#e8edf5", marginBottom: "6px", letterSpacing: "-0.01em" }}>
                    {step === 1 ? "Job details" : "Compensation"}
                  </h2>
                  <p style={{ fontSize: "13.5px", color: "#4a5572", fontWeight: 400, lineHeight: 1.5 }}>
                    {step === 1
                      ? "Tell us about your role. This is 100% anonymous."
                      : "Enter your salary information. Currency and amount are required."}
                  </p>
                </div>

                {/* Card */}
                <div
                  style={{
                    background: "rgba(255,255,255,0.025)", border: "1px solid rgba(255,255,255,0.07)",
                    borderRadius: "16px", padding: "28px", backdropFilter: "blur(20px)",
                    boxShadow: "0 20px 60px rgba(0,0,0,0.45), 0 1px 0 rgba(255,255,255,0.05) inset",
                  }}
                >
                  {/* Mobile step indicator */}
                  <div style={{ display: "flex", gap: "6px", alignItems: "center", marginBottom: "24px" }}>
                    {[1, 2].map((n, i) => (
                      <div key={n} style={{ display: "flex", alignItems: "center", gap: "6px" }}>
                        {i > 0 && <div style={{ flex: 1, height: "1px", background: "rgba(255,255,255,0.07)", width: "20px" }} />}
                        <StepDot active={step === n} done={n === 1 && step > 1} n={n} />
                      </div>
                    ))}
                    <span style={{ marginLeft: "8px", fontSize: "11px", color: "#3a4560", fontWeight: 500, letterSpacing: "0.05em", textTransform: "uppercase" }}>
                      Step {step} of 2
                    </span>
                  </div>

                  {/* ── Step 1: Job Details ── */}
                  {step === 1 && (
                    <div className="fade-up">
                      <FloatingField
                        id="role"
                        label="Job Role / Title"
                        value={form.role}
                        onChange={(v) => set("role", v)}
                        error={errors.role}
                        placeholder="e.g. Software Engineer"
                        maxLength={80}
                      />
                      <FloatingField
                        id="company"
                        label="Company Name"
                        value={form.company}
                        onChange={(v) => set("company", v)}
                        error={errors.company}
                        placeholder="e.g. WSO2"
                        maxLength={80}
                      />
                      <SelectField
                        id="experienceLevel"
                        label="Experience Level"
                        value={form.experienceLevel}
                        onChange={(v) => set("experienceLevel", v)}
                        error={errors.experienceLevel}
                        options={LEVELS.map((l) => ({ value: l, label: l }))}
                      />
                      <SelectField
                        id="country"
                        label="Country"
                        value={form.country}
                        onChange={(v) => set("country", v)}
                        error={errors.country}
                        options={COUNTRIES.map((c) => ({ value: c, label: c }))}
                      />

                      <button className="submit-btn" onClick={handleNext}>
                        Continue to Compensation →
                      </button>
                    </div>
                  )}

                  {/* ── Step 2: Compensation ── */}
                  {step === 2 && (
                    <div className="fade-up">
                      {/* Currency row */}
                      <SelectField
                        id="currency"
                        label="Currency"
                        value={form.currency}
                        onChange={(v) => set("currency", v)}
                        error={errors.currency}
                        options={CURRENCIES.map((c) => ({ value: c.code, label: c.code }))}
                      />

                      {/* Base Salary */}
                      <FloatingField
                        id="baseSalary"
                        label="Base Salary (Annual)"
                        value={baseSalaryStr}
                        onChange={(v) => {
                          setBaseSalaryStr(v);
                          const n = parseFloat(v.replace(/,/g, ""));
                          set("baseSalary", isNaN(n) ? 0 : n);
                        }}
                        error={errors.baseSalary}
                        type="number"
                        placeholder="e.g. 1200000"
                      />

                      {/* Total Compensation */}
                      <FloatingField
                        id="totalCompensation"
                        label="Total Compensation (Annual)"
                        value={totalCompStr}
                        onChange={(v) => {
                          setTotalCompStr(v);
                          const n = parseFloat(v.replace(/,/g, ""));
                          set("totalCompensation", isNaN(n) ? 0 : n);
                        }}
                        error={errors.totalCompensation}
                        type="number"
                        placeholder="e.g. 1500000"
                      />

                      {/* Helper note */}
                      <div style={{ marginBottom: "20px", fontSize: "11.5px", color: "#3a4560", lineHeight: 1.6 }}>
                        💡 <em>Total compensation includes base salary + bonuses, allowances, and any other benefits.</em>
                      </div>

                      {/* Anonymize toggle */}
                      <div
                        style={{
                          display: "flex", alignItems: "center", justifyContent: "space-between",
                          padding: "14px 16px", background: "rgba(255,255,255,0.02)",
                          border: "1px solid rgba(255,255,255,0.07)", borderRadius: "10px",
                          marginBottom: "20px", cursor: "pointer",
                        }}
                        onClick={() => set("anonymize", !form.anonymize)}
                      >
                        <div style={{ display: "flex", gap: "10px", alignItems: "flex-start" }}>
                          <Lock style={{ width: "16px", height: "16px", color: "#6ea8fe", flexShrink: 0, marginTop: "1px" }} />
                          <div>
                            <div style={{ fontSize: "13px", fontWeight: 600, color: "#e8edf5" }}>Anonymize submission</div>
                            <div style={{ fontSize: "11.5px", color: "#4a5572", marginTop: "2px", lineHeight: 1.5 }}>
                              Hide or generalize identifying details in public records
                            </div>
                          </div>
                        </div>
                        {/* Toggle switch */}
                        <div
                          style={{
                            width: "38px", height: "22px", borderRadius: "11px", flexShrink: 0,
                            background: form.anonymize ? "rgba(59,127,245,0.6)" : "rgba(255,255,255,0.08)",
                            border: form.anonymize ? "1px solid rgba(110,168,254,0.5)" : "1px solid rgba(255,255,255,0.1)",
                            position: "relative", transition: "background 0.2s ease, border-color 0.2s ease",
                          }}
                        >
                          <div
                            style={{
                              position: "absolute", top: "3px",
                              left: form.anonymize ? "18px" : "3px",
                              width: "14px", height: "14px", borderRadius: "50%",
                              background: form.anonymize ? "#6ea8fe" : "#3a4560",
                              transition: "left 0.2s cubic-bezier(0.22,1,0.36,1), background 0.2s ease",
                            }}
                          />
                        </div>
                      </div>

                      {/* Privacy note */}
                      <div
                        style={{
                          display: "flex", gap: "10px", alignItems: "flex-start",
                          padding: "12px 14px", background: "rgba(59,127,245,0.06)",
                          border: "1px solid rgba(110,168,254,0.14)", borderRadius: "9px",
                          marginBottom: "20px",
                        }}
                      >
                        <span style={{ fontSize: "15px", flexShrink: 0, marginTop: "1px" }}>🔒</span>
                        <p style={{ fontSize: "11.5px", color: "#4a6088", lineHeight: 1.65, fontWeight: 400 }}>
                          Your submission is{" "}
                          <strong style={{ color: "#6ea8fe", fontWeight: 600 }}>never linked</strong>{" "}
                          to your email or identity. No login required.
                        </p>
                      </div>

                      {/* Error */}
                      {result?.type === "error" && (
                        <div
                          style={{
                            display: "flex", alignItems: "flex-start", gap: "9px",
                            padding: "11px 13px", borderRadius: "8px", marginBottom: "16px",
                            fontSize: "12.5px", background: "rgba(224,82,82,0.08)",
                            border: "1px solid rgba(224,82,82,0.25)", color: "#e05252",
                          }}
                        >
                          <span style={{ flexShrink: 0 }}>✕</span>
                          <span>{result.text}</span>
                        </div>
                      )}

                      {/* Buttons */}
                      <button className="submit-btn" onClick={handleSubmit} disabled={loading}>
                        {loading ? (
                          <><span className="spinner" /> Submitting…</>
                        ) : (
                          <><Send style={{ width: "14px", height: "14px" }} /> Submit Salary</>
                        )}
                      </button>
                      <button className="secondary-btn" onClick={() => setStep(1)} style={{ marginTop: "10px" }}>
                        ← Back to Job Details
                      </button>
                    </div>
                  )}
                </div>

                {/* Footnote */}
                <p style={{ textAlign: "center", marginTop: "20px", fontSize: "11px", color: "#24293a", lineHeight: 1.7, letterSpacing: "0.02em" }}>
                  Salary submissions are always anonymous
                  <br />
                  No account required to browse or submit
                </p>
              </>
            )}
          </div>
        </div>
      </div>

      <style>{`
        @media (max-width: 820px) {
          .left-panel { display: none !important; }
          .mobile-logo { display: flex !important; }
        }
      `}</style>
    </>
  );
}
