export function StrengthBar({ password }: { password: string }) {
  const score = (() => {
    if (!password) return 0;
    let s = 0;
    if (password.length >= 5) s++;
    if (password.length >= 8) s++;
    if (/[A-Z]/.test(password)) s++;
    if (/[0-9]/.test(password)) s++;
    if (/[^a-zA-Z0-9]/.test(password)) s++;
    return Math.min(s, 4);
  })();

  const meta = [
    { label: "Too Weak", color: "#666" },
    { label: "Weak", color: "#e05252" },
    { label: "Fair", color: "#d4933a" },
    { label: "Good", color: "#4aa8c0" },
    { label: "Strong", color: "#4caf82" },
  ];

  if (!password) return null;
  const m = meta[score];

  return (
    <div style={{ marginBottom: "18px" }}>
      <div style={{ display: "flex", gap: "5px", marginBottom: "5px" }}>
        {[1, 2, 3, 4].map((i) => (
          <div
            key={i}
            style={{
              flex: 1,
              height: "3px",
              borderRadius: "3px",
              background: i <= score ? m.color : "rgba(255,255,255,0.07)",
              transition: "background 0.25s ease",
            }}
          />
        ))}
      </div>
      <span
        style={{
          fontSize: "10.5px",
          color: m.color,
          fontFamily: "'Plus Jakarta Sans', sans-serif",
          fontWeight: 600,
          letterSpacing: "0.08em",
          textTransform: "uppercase",
        }}
      >
        {m.label}
      </span>
    </div>
  );
}