"use client";

import { useState, useEffect, useRef } from "react";
import { useRouter } from "next/navigation";
import { SignupRequest, LoginRequest } from "@/types/identity";
import { apiLogin } from "@/lib/identity/login";
import { apiSignup } from "@/lib/identity/signup";
import { FloatingInput } from "@/components/identity/FloatingInput";
import { StrengthBar } from "@/components/identity/StrengthBar";
import { validateLogin, validateSignup } from "@/lib/identity/validations";
import { AlertBox } from "@/components/identity/AlertBox";

export default function AuthPage() {
  const router = useRouter();
  const [mode, setMode] = useState<"login" | "signup">("login");
  const [animating, setAnimating] = useState(false);
  const canvasRef = useRef<HTMLCanvasElement>(null);

  const [loginForm, setLoginForm] = useState<LoginRequest>({
    username: "",
    password: "",
  });
  const [loginErrors, setLoginErrors] = useState<Partial<LoginRequest>>({});
  const [loginLoading, setLoginLoading] = useState(false);
  const [loginMsg, setLoginMsg] = useState<{
    type: "success" | "error";
    text: string;
  } | null>(null);

  const [signupForm, setSignupForm] = useState<SignupRequest>({
    username: "",
    email: "",
    password: "",
  });
  const [signupErrors, setSignupErrors] = useState<Partial<SignupRequest>>({});
  const [signupLoading, setSignupLoading] = useState(false);
  const [signupMsg, setSignupMsg] = useState<{
    type: "success" | "error";
    text: string;
  } | null>(null);

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmationPassword, setShowConfirmationPassword] = useState(false);
  const [confirmPassword, setConfirmPassword] = useState("");

  // ── Particle network canvas ─────────────────────────────────────────────────
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

    const particles: {
      x: number;
      y: number;
      vx: number;
      vy: number;
      size: number;
      alpha: number;
    }[] = [];

    for (let i = 0; i < 100; i++) {
      particles.push({
        x: Math.random() * window.innerWidth,
        y: Math.random() * window.innerHeight,
        vx: (Math.random() - 0.5) * 0.4,
        vy: (Math.random() - 0.5) * 0.4,
        size: Math.random() * 3 + 1,
        alpha: Math.random() * 0.5 + 0.3,
      });
    }

    const draw = () => {
      ctx.clearRect(0, 0, canvas.width, canvas.height);

      // Move & draw particles
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

      // Connecting lines between nearby particles
      for (let i = 0; i < particles.length; i++) {
        for (let j = i + 1; j < particles.length; j++) {
          const dx = particles[i].x - particles[j].x;
          const dy = particles[i].y - particles[j].y;
          const dist = Math.sqrt(dx * dx + dy * dy);
          if (dist < 120) {
            ctx.beginPath();
            ctx.moveTo(particles[i].x, particles[i].y);
            ctx.lineTo(particles[j].x, particles[j].y);
            ctx.strokeStyle = `rgba(110,168,254,${0.08 * (1 - dist / 120)})`;
            ctx.lineWidth = 0.7;
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

  const switchMode = (next: "login" | "signup") => {
    if (next === mode || animating) return;
    setAnimating(true);
    setTimeout(() => {
      setMode(next);
      setAnimating(false);
      setLoginMsg(null);
      setSignupMsg(null);
    }, 220);
  };

  const handleLogin = async () => {
    const errs = validateLogin(loginForm);
    setLoginErrors(errs);
    if (Object.keys(errs).length > 0) return;
    setLoginLoading(true);
    setLoginMsg(null);
    try {
      const res = await apiLogin(loginForm);
      localStorage.setItem("auth_token", res.token);
      setLoginMsg({ type: "success", text: "Welcome back! Redirecting…" });
      const redirectPath = sessionStorage.getItem("redirect_after_login");
      sessionStorage.removeItem("redirect_after_login");
      setTimeout(() => router.push(redirectPath || "/"), 1200);
    } catch (e) {
      setLoginMsg({ type: "error", text: (e as Error).message });
    } finally {
      setLoginLoading(false);
    }
  };

  const handleSignup = async () => {
    const errs = validateSignup(signupForm, confirmPassword);
    setSignupErrors(errs);
    if (Object.keys(errs).length > 0) return;
    setSignupLoading(true);
    setSignupMsg(null);
    try {
      const res = await apiSignup(signupForm);
      setSignupMsg({
        type: "success",
        text: res.message + " You can now log in.",
      });
      setTimeout(() => switchMode("login"), 2000);
    } catch (e) {
      setSignupMsg({ type: "error", text: (e as Error).message });
    } finally {
      setSignupLoading(false);
    }
  };

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
          from { opacity: 0; transform: translateY(20px); }
          to   { opacity: 1; transform: translateY(0); }
        }

        @keyframes fadeDown {
          from { opacity: 1; transform: translateY(0); }
          to   { opacity: 0; transform: translateY(-14px); }
        }

        @keyframes spin {
          to { transform: rotate(360deg); }
        }

        @keyframes pulse-ring {
          0%   { box-shadow: 0 0 0 0 rgba(110,168,254,0.25); }
          70%  { box-shadow: 0 0 0 10px rgba(110,168,254,0); }
          100% { box-shadow: 0 0 0 0 rgba(110,168,254,0); }
        }

        .form-enter { animation: fadeUp 0.32s cubic-bezier(0.22,1,0.36,1) forwards; }
        .form-exit  { animation: fadeDown 0.22s ease forwards; }

        .btn-primary {
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
          position: relative;
          overflow: hidden;
        }
        .btn-primary::after {
          content: '';
          position: absolute;
          inset: 0;
          background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, transparent 60%);
          border-radius: inherit;
        }
        .btn-primary:hover:not(:disabled) {
          transform: translateY(-2px);
          box-shadow: 0 10px 28px rgba(59,127,245,0.35);
        }
        .btn-primary:active:not(:disabled) {
          transform: translateY(0);
          box-shadow: 0 4px 12px rgba(59,127,245,0.3);
        }
        .btn-primary:disabled {
          opacity: 0.55;
          cursor: not-allowed;
        }

        .tab-btn {
          flex: 1;
          padding: 9px 12px;
          background: none;
          border: none;
          cursor: pointer;
          font-family: 'Plus Jakarta Sans', sans-serif;
          font-size: 12.5px;
          font-weight: 700;
          letter-spacing: 0.1em;
          text-transform: uppercase;
          transition: color 0.2s ease;
          position: relative;
          z-index: 1;
        }

        .text-link {
          background: none;
          border: none;
          color: #6ea8fe;
          font-family: 'Plus Jakarta Sans', sans-serif;
          font-size: 13px;
          font-weight: 500;
          cursor: pointer;
          padding: 0;
          text-decoration: none;
          border-bottom: 1px solid rgba(110,168,254,0.35);
          transition: color 0.15s, border-color 0.15s;
        }
        .text-link:hover {
          color: #a5c8fe;
          border-color: rgba(165,200,254,0.5);
        }

        .spinner {
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

        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-track { background: transparent; }
        ::-webkit-scrollbar-thumb { background: rgba(110,168,254,0.2); border-radius: 3px; }
      `}</style>

      {/* Canvas background */}
      <canvas
        ref={canvasRef}
        style={{
          position: "fixed",
          inset: 0,
          zIndex: 0,
          pointerEvents: "none",
        }}
      />

      {/* Deep blue radial backdrop */}
      <div
        style={{
          position: "fixed",
          inset: 0,
          zIndex: 0,
          pointerEvents: "none",
          background:
            "radial-gradient(ellipse 80% 60% at 50% 0%, rgba(30,60,120,0.35) 0%, transparent 70%)",
        }}
      />
      <div
        style={{
          position: "fixed",
          bottom: "-15%",
          right: "-5%",
          zIndex: 0,
          pointerEvents: "none",
          width: "50vw",
          height: "50vw",
          borderRadius: "50%",
          background:
            "radial-gradient(circle, rgba(91,91,214,0.08) 0%, transparent 65%)",
        }}
      />

      {/* Layout */}
      <div
        style={{
          minHeight: "100vh",
          display: "flex",
          position: "relative",
          zIndex: 1,
        }}
      >
        {/* Left panel — branding (hidden on small screens via media query workaround) */}
        <div
          style={{
            flex: "0 0 42%",
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-between",
            padding: "48px 52px",
            borderRight: "1px solid rgba(255,255,255,0.05)",
          }}
          className="left-panel"
        >
          {/* Logo */}
          <div style={{ display: "flex", alignItems: "center", gap: "10px" }}>
            <div
              style={{
                width: "32px",
                height: "32px",
                borderRadius: "8px",
                background: "linear-gradient(135deg, #3b7ff5, #5b5bd6)",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                fontSize: "16px",
              }}
            >
              💼
            </div>
            <span
              style={{
                fontFamily: "'Plus Jakarta Sans', sans-serif",
                fontWeight: 700,
                fontSize: "17px",
                color: "#e8edf5",
                letterSpacing: "-0.01em",
              }}
            >
              watupa<span style={{ color: "#6ea8fe" }}>.lk</span>
            </span>
          </div>

          {/* Hero text */}
          <div>
            <p
              style={{
                fontFamily: "'Plus Jakarta Sans', sans-serif",
                fontSize: "11px",
                fontWeight: 600,
                letterSpacing: "0.18em",
                textTransform: "uppercase",
                color: "#6ea8fe",
                marginBottom: "18px",
              }}
            >
              Salary Transparency Platform
            </p>
            <h1
              style={{
                fontFamily: "'Instrument Serif', serif",
                fontSize: "clamp(32px, 3.5vw, 46px)",
                fontWeight: 400,
                lineHeight: 1.18,
                color: "#e8edf5",
                letterSpacing: "-0.01em",
                marginBottom: "22px",
              }}
            >
              Know your worth.
              <br />
              <em style={{ color: "#6ea8fe" }}>Shape the market.</em>
            </h1>
            <p
              style={{
                fontSize: "14px",
                color: "#5a6888",
                lineHeight: 1.75,
                maxWidth: "340px",
                fontWeight: 300,
              }}
            >
              Anonymous salary data contributed by Sri Lanka&apos;s tech
              community. Submit without logging in. Vote to verify. Search to
              benchmark.
            </p>

            {/* Stats strip */}
            <div
              style={{
                display: "flex",
                gap: "32px",
                marginTop: "40px",
                paddingTop: "32px",
                borderTop: "1px solid rgba(255,255,255,0.06)",
              }}
            >
              {[
                ["Anonymous", "Submissions"],
                ["Community", "Verified"],
                ["Real-time", "Insights"],
              ].map(([a, b]) => (
                <div key={a}>
                  <div
                    style={{
                      fontFamily: "'Instrument Serif', serif",
                      fontSize: "20px",
                      color: "#6ea8fe",
                      marginBottom: "2px",
                    }}
                  >
                    {a}
                  </div>
                  <div
                    style={{
                      fontSize: "11px",
                      color: "#3a4560",
                      fontWeight: 500,
                      letterSpacing: "0.04em",
                    }}
                  >
                    {b}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Footer */}
          <p
            style={{
              fontSize: "11px",
              color: "white",
              letterSpacing: "0.03em",
            }}
          >
            © {new Date().getFullYear()} watupa.lk · Privacy-first · Open data
          </p>
        </div>

        {/* Right panel — auth form */}
        <div
          style={{
            flex: 1,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: "40px 24px",
          }}
        >
          <div style={{ width: "100%", maxWidth: "400px" }}>
            {/* Mobile logo (only shown when left panel is hidden) */}
            <div
              style={{ marginBottom: "32px", display: "none" }}
              className="mobile-logo"
            >
              <div
                style={{ display: "flex", alignItems: "center", gap: "8px" }}
              >
                <div
                  style={{
                    width: "28px",
                    height: "28px",
                    borderRadius: "7px",
                    background: "linear-gradient(135deg, #3b7ff5, #5b5bd6)",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "center",
                    fontSize: "14px",
                  }}
                >
                  💼
                </div>
                <span
                  style={{
                    fontWeight: 700,
                    fontSize: "16px",
                    letterSpacing: "-0.01em",
                  }}
                >
                  watupa<span style={{ color: "#6ea8fe" }}>.lk</span>
                </span>
              </div>
            </div>

            {/* Heading */}
            <div style={{ marginBottom: "28px" }}>
              <h2
                style={{
                  fontFamily: "'Instrument Serif', serif",
                  fontSize: "28px",
                  fontWeight: 400,
                  color: "#e8edf5",
                  marginBottom: "6px",
                  letterSpacing: "-0.01em",
                }}
              >
                {mode === "login" ? "Welcome back" : "Create an account"}
              </h2>
              <p
                style={{
                  fontSize: "13.5px",
                  color: "#4a5572",
                  fontWeight: 400,
                  lineHeight: 1.5,
                }}
              >
                {mode === "login"
                  ? "Log in to upvote, downvote and report salary entries."
                  : "Join the community. Your identity stays private."}
              </p>
            </div>

            {/* Card */}
            <div
              style={{
                background: "rgba(255,255,255,0.025)",
                border: "1px solid rgba(255,255,255,0.07)",
                borderRadius: "16px",
                padding: "28px",
                backdropFilter: "blur(20px)",
                boxShadow:
                  "0 20px 60px rgba(0,0,0,0.45), 0 1px 0 rgba(255,255,255,0.05) inset",
              }}
            >
              {/* Tab switcher */}
              <div
                style={{
                  display: "flex",
                  background: "rgba(255,255,255,0.035)",
                  borderRadius: "9px",
                  padding: "3px",
                  marginBottom: "26px",
                  position: "relative",
                }}
              >
                <div
                  style={{
                    position: "absolute",
                    top: "3px",
                    left: mode === "login" ? "3px" : "calc(50% + 0px)",
                    width: "calc(50% - 3px)",
                    height: "calc(100% - 6px)",
                    background: "rgba(59,127,245,0.18)",
                    border: "1px solid rgba(110,168,254,0.25)",
                    borderRadius: "7px",
                    transition: "left 0.28s cubic-bezier(0.22,1,0.36,1)",
                    zIndex: 0,
                  }}
                />
                <button
                  className="tab-btn"
                  onClick={() => switchMode("login")}
                  style={{ color: mode === "login" ? "#6ea8fe" : "#3a4560" }}
                >
                  Log In
                </button>
                <button
                  className="tab-btn"
                  onClick={() => switchMode("signup")}
                  style={{ color: mode === "signup" ? "#6ea8fe" : "#3a4560" }}
                >
                  Sign Up
                </button>
              </div>

              {/* Forms */}
              <div className={animating ? "form-exit" : "form-enter"}>
                {mode === "login" ? (
                  <div>
                    <FloatingInput
                      id="l-user"
                      label="Username"
                      value={loginForm.username}
                      onChange={(v) =>
                        setLoginForm((f) => ({ ...f, username: v }))
                      }
                      error={loginErrors.username}
                      maxLength={10}
                    />
                    <FloatingInput
                      id="l-pass"
                      label="Password"
                      isPassword
                      showPassword={showPassword}
                      togglePassword={() => setShowPassword((p) => !p)}
                      value={loginForm.password}
                      onChange={(v) =>
                        setLoginForm((f) => ({ ...f, password: v }))
                      }
                      error={loginErrors.password}
                      maxLength={12}
                    />

                    {loginMsg && (
                      <AlertBox type={loginMsg.type} text={loginMsg.text} />
                    )}

                    <button
                      className="btn-primary"
                      onClick={handleLogin}
                      disabled={loginLoading}
                    >
                      {loginLoading ? <span className="spinner" /> : "Log In"}
                    </button>

                    <p
                      style={{
                        textAlign: "center",
                        marginTop: "18px",
                        fontSize: "13px",
                        color: "#3a4560",
                      }}
                    >
                      Don&apos;t have an account?{" "}
                      <button
                        className="text-link"
                        onClick={() => switchMode("signup")}
                      >
                        Create one
                      </button>
                    </p>
                  </div>
                ) : (
                  <div>
                    <FloatingInput
                      id="s-user"
                      label="Username"
                      value={signupForm.username}
                      onChange={(v) =>
                        setSignupForm((f) => ({ ...f, username: v }))
                      }
                      error={signupErrors.username}
                      maxLength={10}
                    />{" "}
                    <FloatingInput
                      id="s-email"
                      label="Email address"
                      type="email"
                      value={signupForm.email}
                      onChange={(v) =>
                        setSignupForm((f) => ({ ...f, email: v }))
                      }
                      error={signupErrors.email}
                      maxLength={50}
                    />
                    <FloatingInput
                      id="s-pass"
                      label="Password"
                      isPassword
                      showPassword={showPassword}
                      togglePassword={() => setShowPassword((p) => !p)}
                      value={signupForm.password}
                      onChange={(v) =>
                        setSignupForm((f) => ({ ...f, password: v }))
                      }
                      error={signupErrors.password}
                      maxLength={12}
                    />
                    <FloatingInput
                      id="s-pass-confirm"
                      label="Confirm Password"
                      isPassword
                      showPassword={showConfirmationPassword}
                      togglePassword={() =>
                        setShowConfirmationPassword((p) => !p)
                      }
                      value={confirmPassword}
                      onChange={(v) => setConfirmPassword(v)}
                      error={signupErrors.password}
                      maxLength={12}
                    />
                    <StrengthBar password={signupForm.password} />
                    {/* Privacy notice */}
                    <div
                      style={{
                        display: "flex",
                        gap: "10px",
                        alignItems: "flex-start",
                        padding: "12px 14px",
                        background: "rgba(59,127,245,0.06)",
                        border: "1px solid rgba(110,168,254,0.14)",
                        borderRadius: "9px",
                        marginBottom: "20px",
                      }}
                    >
                      <span
                        style={{
                          fontSize: "15px",
                          flexShrink: 0,
                          marginTop: "1px",
                        }}
                      >
                        🔒
                      </span>
                      <p
                        style={{
                          fontSize: "11.5px",
                          color: "#4a6088",
                          lineHeight: 1.65,
                          fontWeight: 400,
                        }}
                      >
                        Your account is{" "}
                        <strong style={{ color: "#6ea8fe", fontWeight: 600 }}>
                          never linked
                        </strong>{" "}
                        to salary records. Login is only required for community
                        actions.
                      </p>
                    </div>
                    {signupMsg && (
                      <AlertBox type={signupMsg.type} text={signupMsg.text} />
                    )}
                    <button
                      className="btn-primary"
                      onClick={handleSignup}
                      disabled={signupLoading}
                    >
                      {signupLoading ? (
                        <span className="spinner" />
                      ) : (
                        "Create Account"
                      )}
                    </button>
                    <p
                      style={{
                        textAlign: "center",
                        marginTop: "18px",
                        fontSize: "13px",
                        color: "#3a4560",
                      }}
                    >
                      Already a member?{" "}
                      <button
                        className="text-link"
                        onClick={() => switchMode("login")}
                      >
                        Log in
                      </button>
                    </p>
                  </div>
                )}
              </div>
            </div>

            {/* Footnote */}
            <p
              style={{
                textAlign: "center",
                marginTop: "20px",
                fontSize: "11px",
                color: "#24293a",
                lineHeight: 1.7,
                letterSpacing: "0.02em",
              }}
            >
              Salary submissions are always anonymous
              <br />
              No account required to browse or submit
            </p>
          </div>
        </div>
      </div>

      {/* Responsive: hide left panel on small screens */}
      <style>{`
        @media (max-width: 820px) {
          .left-panel { display: none !important; }
          .mobile-logo { display: flex !important; }
        }
      `}</style>
    </>
  );
}