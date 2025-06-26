/** @type {import('tailwindcss').Config} */
const COLORS = {
  primary: 'var(--primary)',
  baseBackground: 'var(--baseBackground)',
  baseText: 'var(--baseText)',
  baseBorder: 'var(--baseBorder)',
  btnPrimary: 'var(--btnPrimary)',
  btnTextColor: 'var(--btnTextColor)',
}

export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        ...COLORS,
        brand: {
          500: '#465fff',
          50: '#ecf3ff'
        }
      },
      flex: {
        '2': '2 2 0%',
        '3': '3 3 0%',
        '4': '4 4 0%',
        '5': '5 5 0%',
        '6': '6 6 0%',
        '7': '7 7 0%',
        '8': '8 8 0%',
        '9': '9 9 0%',
        '10': '10 10 0%'
      },
      animation: {
        'modal-enter': 'modalEnter 300ms ease-out forwards',
      },
      maxWidth: {
        'main': '1200px',
      },
      keyframes: {
        modalEnter: {
          '0%': {
            opacity: '0',
            transform: 'scale(0.95)',
          },
          '100%': {
            opacity: '1',
            transform: 'scale(1)',
          },
        },
      },

    },
  },
  daisyui: {
    themes: ['light'], // false: only light + dark | true: all themes | array: specific themes like this ["light", "dark", "cupcake"]
    // darkTheme: "dark", // name of one of the included themes for dark mode
    base: true, // applies background color and foreground color for root element by default
    styled: true, // include daisyUI colors and design decisions for all components
    utils: true, // adds responsive and modifier utility classes
    prefix: "d-", // prefix for daisyUI classnames (components, modifiers and responsive class names. Not colors)
    logs: false, // Shows info about daisyUI version and used config in the console when building your CSS
    themeRoot: ":root", // The element that receives theme color CSS variables
  },
  plugins: [require("daisyui")],
}