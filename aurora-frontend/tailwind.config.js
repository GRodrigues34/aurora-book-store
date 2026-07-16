/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        aurora: {
          50:  '#faf5ff',
          100: '#f3e8ff',
          200: '#e9d5ff',
          300: '#d8b4fe',
          400: '#c084fc',
          500: '#a855f7',
          600: '#9333ea',
          700: '#7c3aed',
          800: '#6b21a8',
          900: '#4a1a6b',
          950: '#2e1065',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
      },
      boxShadow: {
        'aurora-sm': '0 1px 3px 0 rgba(147, 51, 234, 0.04), 0 1px 2px -1px rgba(147, 51, 234, 0.04)',
        'aurora-md': '0 4px 6px -1px rgba(147, 51, 234, 0.06), 0 2px 4px -2px rgba(147, 51, 234, 0.04)',
        'aurora-lg': '0 10px 15px -3px rgba(147, 51, 234, 0.08), 0 4px 6px -4px rgba(147, 51, 234, 0.04)',
      },
      borderRadius: {
        'aurora': '1rem',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out',
        'slide-up': 'slideUp 0.5s ease-out',
        'pulse-soft': 'pulseSoft 2s ease-in-out infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        pulseSoft: {
          '0%, 100%': { boxShadow: '0 0 0 0 rgba(147, 51, 234, 0.4)' },
          '50%': { boxShadow: '0 0 0 12px rgba(147, 51, 234, 0)' },
        },
      },
    },
  },
  plugins: [],
}
