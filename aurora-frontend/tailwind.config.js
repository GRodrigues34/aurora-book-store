/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        obsidian: {
          DEFAULT: '#111111',
          deep: '#070707',
        },
        'deep-purple': {
          DEFAULT: '#2A1B3D',
          plum: '#5A2A5F',
        },
        paper: {
          DEFAULT: '#F7F4EF',
          subtle: '#EFECE6',
          border: '#E2DDD5',
        },
        ink: {
          DEFAULT: '#18141F',
          muted: '#5E5768',
          subtle: '#8B8396',
        },
        gold: {
          DEFAULT: '#D4AF37',
          soft: 'rgba(212, 175, 55, 0.16)',
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
        sans: ['General Sans', 'system-ui', '-apple-system', 'sans-serif'],
        serif: ['Instrument Serif', 'Georgia', 'serif'],
        mono: ['JetBrains Mono', 'monospace'],
      },
      boxShadow: {
        'aurora-sm': '0 2px 6px -1px rgba(17, 17, 17, 0.06), 0 1px 2px -1px rgba(17, 17, 17, 0.04)',
        'aurora-md': '0 10px 24px -6px rgba(17, 17, 17, 0.10), 0 2px 6px -2px rgba(107, 33, 168, 0.06)',
        'aurora-lg': '0 20px 44px -10px rgba(17, 17, 17, 0.18), 0 4px 12px -4px rgba(107, 33, 168, 0.10)',
      },
    },
  },
  plugins: [],
}
