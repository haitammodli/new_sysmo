/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: 'class',
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#2ecc71',
        background: '#F8FAFC',
        surface: '#FFFFFF',
        text: '#1E293B',
        border: '#E2E8F0',
      },
      fontFamily: {
        sans: ['Inter', 'Roboto', 'Segoe UI', 'sans-serif'],
      }
    },
  },
  plugins: [],
}

