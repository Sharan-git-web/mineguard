import type { Config } from "tailwindcss";

const config: Config = {
  darkMode: ["class"],
  content: [
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        border: "#e2e8f0",
        input: "#e2e8f0",
        ring: "#0f172a",
        background: "#f8fafc",
        foreground: "#0f172a",
        primary: {
          DEFAULT: "#0f172a",
          foreground: "#ffffff",
        },
        secondary: {
          DEFAULT: "#64748b",
          foreground: "#0f172a",
        },
        muted: {
          DEFAULT: "#f1f5f9",
          foreground: "#64748b",
        },
        accent: {
          DEFAULT: "#f1f5f9",
          foreground: "#0f172a",
        },
        card: {
          DEFAULT: "#ffffff",
          foreground: "#0f172a",
        },
        danger: {
          DEFAULT: "#ef4444",
          light: "#fef2f2",
        },
        warning: {
          DEFAULT: "#f59e0b",
          light: "#fffbeb",
        },
        info: {
          DEFAULT: "#3b82f6",
          light: "#eff6ff",
        },
        success: {
          DEFAULT: "#10b981",
          light: "#ecfdf5",
        }
      },
      fontFamily: {
        sans: ["Inter", "-apple-system", "BlinkMacSystemFont", "Segoe UI", "Roboto", "sans-serif"],
      }
    },
  },
  plugins: [],
};
export default config;
