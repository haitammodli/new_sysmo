/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        primary: '#009045',
        background: '#F8FAFB',
        surface: '#FFFFFF',
        text: '#1E293B',
        border: '#E2E8F0',
      }
    },
  },
  plugins: [],
}

