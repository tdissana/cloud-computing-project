import IconButton from "@mui/material/IconButton";
import Visibility from "@mui/icons-material/Visibility";
import VisibilityOff from "@mui/icons-material/VisibilityOff";
import { useState } from "react";

export function FloatingInput({
  id,
  label,
  type = "text",
  value,
  onChange,
  error,
  maxLength,
  isPassword,
  showPassword,
  togglePassword,
}: {
  id: string;
  label: string;
  type?: string;
  value: string;
  onChange: (v: string) => void;
  error?: string;
  maxLength?: number;
  isPassword?: boolean;
  showPassword?: boolean;
  togglePassword?: () => void;
}) {
  const [focused, setFocused] = useState(false);
  const lifted = focused || value.length > 0;

  return (
    <div style={{ marginBottom: error ? "6px" : "20px" }}>
      <div style={{ position: "relative" }}>
        <label
          htmlFor={id}
          style={{
            position: "absolute",
            left: "14px",
            top: lifted ? "7px" : "50%",
            transform: lifted ? "none" : "translateY(-50%)",
            fontSize: lifted ? "10px" : "13.5px",
            fontWeight: lifted ? "600" : "400",
            fontFamily: "'Plus Jakarta Sans', sans-serif",
            color: error ? "#e05252" : lifted ? "#6ea8fe" : "#6b7a99",
            letterSpacing: lifted ? "0.09em" : "0.01em",
            textTransform: lifted ? "uppercase" : "none",
            transition: "all 0.18s ease",
            pointerEvents: "none",
            zIndex: 2,
          }}
        >
          {label}
        </label>
        <input
          id={id}
          type={isPassword ? (showPassword ? "text" : "password") : type}
          value={value}
          maxLength={maxLength}
          onChange={(e) => onChange(e.target.value)}
          onFocus={() => setFocused(true)}
          onBlur={() => setFocused(false)}
          style={{
            width: "100%",
            boxSizing: "border-box",
            padding: "22px 40px 8px 14px", // leave space for eye button
            background: focused
              ? "rgba(110,168,254,0.04)"
              : "rgba(255,255,255,0.025)",
            border: `1.5px solid ${error ? "#e05252" : focused ? "#6ea8fe" : "rgba(255,255,255,0.09)"}`,
            borderRadius: "10px",
            color: "#e8edf5",
            fontSize: "14px",
            fontFamily: "'Plus Jakarta Sans', sans-serif",
            outline: "none",
            transition: "all 0.18s ease",
            boxShadow: focused
              ? `0 0 0 3px ${error ? "rgba(224,82,82,0.12)" : "rgba(110,168,254,0.1)"}`
              : "none",
          }}
        />
        {isPassword && togglePassword && (
          <IconButton
            onClick={togglePassword}
            style={{
              position: "absolute",
              right: "8px",
              top: "50%",
              transform: "translateY(-50%)",
              color: "#6ea8fe",
              padding: "2px",
            }}
            size="small"
          >
            {showPassword ? (
              <Visibility fontSize="small" />
            ) : (
              <VisibilityOff fontSize="small" />
            )}
          </IconButton>
        )}
      </div>
      {error && (
        <p
          style={{
            margin: "5px 0 14px 2px",
            fontSize: "11px",
            color: "#e05252",
            fontFamily: "'Plus Jakarta Sans', sans-serif",
            letterSpacing: "0.02em",
          }}
        >
          {error}
        </p>
      )}
    </div>
  );
}
