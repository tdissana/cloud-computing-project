import { useState, useRef, useEffect, useMemo } from "react";
import { Input } from "@/components/ui/input";
import { Card } from "@/components/ui/card";
import { X } from "lucide-react";

interface SearchableSelectProps {
  value: string;
  onChange: (value: string) => void;
  options: string[];
  placeholder?: string;
}

export function SearchableSelect({
  value,
  onChange,
  options,
  placeholder = "Search or type....",
}: SearchableSelectProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [inputValue, setInputValue] = useState(value);
  const containerRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Compute filtered options as a derived value (no setState in effect)
  const filteredOptions = useMemo(() => {
    const searchTerm = inputValue.toLowerCase();
    return searchTerm.trim() === "" 
      ? options 
      : options.filter((opt) => opt.toLowerCase().includes(searchTerm));
  }, [inputValue, options]);

  // Close dropdown when clicking outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        containerRef.current &&
        !containerRef.current.contains(event.target as Node)
      ) {
        setIsOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
    setIsOpen(true);
  };

  const handleSelectOption = (option: string) => {
    setInputValue(option);
    onChange(option);
    setIsOpen(false);
  };

  const handleClear = (e: React.MouseEvent) => {
    e.stopPropagation();
    setInputValue("");
    onChange("");
    setIsOpen(true);
    inputRef.current?.focus();
  };

  return (
    <div ref={containerRef} className="relative">
      <div className="relative">
        <Input
          ref={inputRef}
          placeholder={placeholder}
          value={inputValue}
          onChange={handleInputChange}
          onFocus={() => setIsOpen(true)}
          className="bg-[#0f1524] border-white/[0.08] text-[#e8edf5] placeholder:text-[#3a4560] focus-visible:ring-[#6ea8fe] focus-visible:border-[#6ea8fe]/50 pr-8"
        />
        {inputValue && (
          <button
            onClick={handleClear}
            className="absolute right-2 top-1/2 -translate-y-1/2 p-1 hover:bg-white/[0.1] rounded transition-colors"
            type="button"
          >
            <X className="w-4 h-4 text-[#4a5572]" />
          </button>
        )}
      </div>

      {/* Dropdown Menu */}
      {isOpen && filteredOptions.length > 0 && (
        <Card className="absolute z-50 w-full mt-1 bg-[#0f1524] border-white/[0.08] backdrop-blur-xl shadow-[0_20px_60px_rgba(0,0,0,0.45)]">
          <div className="max-h-60 overflow-y-auto">
            {filteredOptions.map((option) => (
              <button
                key={option}
                onClick={() => handleSelectOption(option)}
                className={`w-full text-left px-3 py-2.5 hover:bg-white/[0.05] transition-colors text-sm ${
                  inputValue === option
                    ? "bg-white/[0.08] text-[#6ea8fe]"
                    : "text-[#e8edf5]"
                }`}
              >
                {option}
              </button>
            ))}
          </div>
        </Card>
      )}

      {/* No results message */}
      {isOpen && inputValue && filteredOptions.length === 0 && (
        <Card className="absolute z-50 w-full mt-1 bg-[#0f1524] border-white/[0.08] backdrop-blur-xl">
          <div className="px-3 py-3 text-sm text-[#4a5572] text-center">
            No matches found. Press Enter to use &quot;{inputValue}&quot;
          </div>
        </Card>
      )}
    </div>
  );
}
