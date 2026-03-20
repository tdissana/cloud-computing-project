import { ReactNode } from "react";

interface CustomTooltipProps {
  content: string;
  children: ReactNode;
  position?: "top" | "bottom" | "left" | "right";
}

export function CustomTooltip({
  content,
  children,
  position = "top",
}: CustomTooltipProps) {
  const positionClasses = {
    top: "bottom-full mb-2",
    bottom: "top-full mt-2",
    left: "right-full mr-2",
    right: "left-full ml-2",
  };

  return (
    <div className="relative inline-block group">
      {children}
      <div
        className={`absolute ${positionClasses[position]} left-1/2 -translate-x-1/2 px-2 py-1 bg-[#0f1524] text-[#e8edf5] text-xs rounded whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none border border-white/[0.07] z-10`}
      >
        {content}
      </div>
    </div>
  );
}
