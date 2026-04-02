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
        background: '#edeff1',
        surface: '#f5f5f5',
        text: '#1E293B',
        border: '#2ecc71',
      },
      fontFamily: {
        sans: ['Inter', 'Roboto', 'Segoe UI', 'sans-serif'],
      }
    },
  },
  plugins: [],
}

