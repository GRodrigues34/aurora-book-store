/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        paper: {
          DEFAULT: '#FAF8F5',
          subtle: '#F3EFE9',
          border: '#E6DFD5',
        },
        ink: {
          DEFAULT: '#18141F',
          muted: '#5E5768',
          subtle: '#8B8396',
        },
        aurora: {
          50:  '#FAF5FF',
          100: '#F3E8FF',
          200: '#E9D5FF',
          300: '#D8B4FE',
          400: '#C084FC',
          500: '#A855F7',
          600: '#7E22CE',
          700: '#6B21A8',
          800: '#581C87',
          900: '#3B0764',
          950: '#240440',
        },
      },
      fontFamily: {
        display: ['Newsreader', 'Georgia', 'serif'],
        sans: ['Epilogue', 'system-ui', '-apple-system', 'sans-serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
      boxShadow: {
        'aurora-sm': '0 2px 6px -1px rgba(24, 20, 31, 0.05), 0 1px 2px -1px rgba(24, 20, 31, 0.03)',
        'aurora-md': '0 8px 20px -4px rgba(24, 20, 31, 0.08), 0 2px 6px -2px rgba(107, 33, 168, 0.05)',
        'aurora-lg': '0 16px 36px -8px rgba(24, 20, 31, 0.12), 0 4px 12px -4px rgba(107, 33, 168, 0.08)',
      },
      borderRadius: {
        'aurora': '1rem',
      },
      animation: {
        'fade-in': 'fadeIn 0.35s cubic-bezier(0.16, 1, 0.3, 1)',
        'slide-up': 'slideUp 0.4s cubic-bezier(0.16, 1, 0.3, 1)',
        'pulse-soft': 'pulseSoft 2.4s ease-in-out infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(14px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        pulseSoft: {
          '0%, 100%': { boxShadow: '0 0 0 0 rgba(107, 33, 168, 0.35)' },
          '50%': { boxShadow: '0 0 0 10px rgba(107, 33, 168, 0)' },
        },
      },
    },
  },
  plugins: [],
}
