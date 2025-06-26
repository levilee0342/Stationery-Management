import { useEffect, useState } from 'react'
import { Themes } from '~/constance/themes'

const ThemeToggle: React.FC = () => {
  // Lấy theme từ localStorage hoặc mặc định là "light"
  const [theme, setTheme] = useState<string>(localStorage.getItem(Themes.NAME) || Themes.LIGHT)

  // Cập nhật theme khi user thay đổi
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme)
    localStorage.setItem('theme', theme)
  }, [theme])

  return (
    <button
      className='d-btn d-btn-primary text-2xl'
      onClick={() => setTheme(theme === Themes.LIGHT ? Themes.DARK : Themes.LIGHT)}
    >
      {theme === 'light' ? '🌙' : '☀️'}
    </button>
  )
}

export default ThemeToggle
