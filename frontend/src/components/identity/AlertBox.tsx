export function AlertBox({ type, text }: { type: "success" | "error"; text: string }) {
  const isSuccess = type === "success";
  return (
    <div
      style={{
        display: "flex",
        alignItems: "flex-start",
        gap: "9px",
        padding: "11px 13px",
        borderRadius: "8px",
        marginBottom: "16px",
        fontSize: "12.5px",
        fontFamily: "'Plus Jakarta Sans', sans-serif",
        background: isSuccess
          ? "rgba(76,175,130,0.08)"
          : "rgba(224,82,82,0.08)",
        border: `1px solid ${isSuccess ? "rgba(76,175,130,0.25)" : "rgba(224,82,82,0.25)"}`,
        color: isSuccess ? "#4caf82" : "#e05252",
        lineHeight: 1.5,
      }}
    >
      <span style={{ flexShrink: 0, marginTop: "1px" }}>
        {isSuccess ? "✓" : "✕"}
      </span>
      <span>{text}</span>
    </div>
  );
}