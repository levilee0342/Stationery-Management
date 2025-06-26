import { useEffect, useState, useRef } from 'react'
import { ShoppingCart, Menu, X, Camera } from 'lucide-react'
import { useNavigate, NavLink, Link, createSearchParams } from 'react-router-dom'
import { publicPaths } from '~/constance/paths'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { userActions } from '~/store/slices/user'
import { showToastError, showToastSuccess } from '~/utils/alert'
import { dropDownProfile } from '~/constance/dropdown'
import { apiLogout } from '~/api/authenticate'
import { fetchCategories } from '~/store/actions/category'
import { fetchMyVocher } from '~/store/actions/promotion'
import { fetchCurrentUser } from '~/store/actions/user'
import { fetchMyCart } from '~/store/actions/cart'
import { cartActions } from '~/store/slices/cart'
import SearchWithSuggestions from '../search/SearchWithSuggestions'
import Cart from '~/pages/public/cart/Cart'

function Header() {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const { myCart, isCartOpen } = useAppSelector((state) => state.cart) // Lấy isCartOpen từ Redux
  const [theme, setTheme] = useState<string>(localStorage.getItem('theme') || 'light')
  const [isMenuOpen, setIsMenuOpen] = useState(false)
  const { isLoggedIn, userData, accessToken } = useAppSelector((state) => state.user)
  const fileInputRef = useRef<HTMLInputElement>(null)

  // Lọc danh sách dropdown dựa trên vai trò người dùng
  const filteredDropDownProfile = dropDownProfile.filter((item) => {
    if (item.name === 'Admin' && userData?.role.roleId == '112') {
      return false
    }
    return true
  })

  const handleCartToggle = () => {
    dispatch(cartActions.toggleCart()) // Sử dụng action toggleCart
  }

  useEffect(() => {
    if (theme) {
      localStorage.setItem('theme', theme)
      document.documentElement.setAttribute('data-theme', theme)
    }
  }, [theme])

  const toggleTheme = () => {
    setTheme((prevTheme) => (prevTheme === 'light' ? 'dark' : 'light'))
  }

  const handleLogout = async () => {
    try {
      const response = await apiLogout({ token: accessToken })
      if (response.code == 200) {
        dispatch(userActions.logout())
        showToastSuccess('Logout successfully')
        navigate('/auth?mode=login')
      } else {
        showToastError(response.message || 'Logout failed')
      }
    } catch (error) {
      console.error('Logout error:', error)
      showToastError('Logout failed. Please try again.')
    }
  }

  const handleImageSearch = async (file: File) => {
    const formData = new FormData()
    formData.append('image', file)

    try {
      const res = await fetch('http://127.0.0.1:5000/predict', {
        method: 'POST',
        body: formData
      })
      if (!res.ok) throw new Error(`Status ${res.status}`)
      const data = await res.json()
      if (data.error) throw new Error(data.error)

      const newParams = { search: data.label }
      navigate({
        pathname: publicPaths.PRODUCT,
        search: createSearchParams(newParams).toString()
      })
    } catch (err: any) {
      console.error('Image search error:', err)
      showToastError(`Tìm kiếm bằng ảnh thất bại: ${err.message}`)
    }
  }

  const handleCameraClick = () => {
    fileInputRef.current?.click()
  }

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      handleImageSearch(file)
    }
  }

  useEffect(() => {
    if (isLoggedIn && accessToken && userData?.userId) {
      dispatch(fetchCategories())
      dispatch(fetchCurrentUser({ token: accessToken }))
      dispatch(fetchMyVocher({ accessToken }))
      dispatch(fetchMyCart({ accessToken }))
    }
  }, [isLoggedIn, accessToken])

  return (
    <nav className='fixed top-0 left-0 right-0 z-50 shadow-md bg-white px-6 py-3 flex items-center justify-between md:px-10'>
      <Link to='/'>
        <div className='text-2xl font-bold text-blue-600 cursor-pointer'>Stationery's P</div>
      </Link>

      <div className='hidden md:flex space-x-6 text-gray-700 '>
        <NavLink
          to={publicPaths.PUBLIC}
          className={({ isActive }) =>
            isActive ? 'text-blue-600 font-bold ' : 'text-gray-700 hover:text-blue-500 transition '
          }
        >
          Home
        </NavLink>
        <NavLink
          to={publicPaths.ABOUT}
          className={({ isActive }) =>
            isActive ? 'text-blue-600 font-bold ' : 'text-gray-700 hover:text-blue-500 transition'
          }
        >
          About
        </NavLink>
        <NavLink
          to={publicPaths.PRODUCT}
          className={({ isActive }) =>
            isActive ? 'text-blue-600 font-bold ' : 'text-gray-700 hover:text-blue-500 transition '
          }
        >
          Product
        </NavLink>
        <NavLink
          to={publicPaths.SERVICE}
          className={({ isActive }) =>
            isActive ? 'text-blue-600 font-bold' : 'text-gray-700 hover:text-blue-500 transition '
          }
        >
          Service
        </NavLink>
        <NavLink
          to={publicPaths.CONTACT}
          className={({ isActive }) =>
            isActive ? 'text-blue-600 font-bold' : 'text-gray-700 hover:text-blue-500 transition '
          }
        >
          Contact
        </NavLink>
      </div>

      <div className='hidden md:flex items-center space-x-2'>
        <SearchWithSuggestions />
        <input type='file' accept='image/*' ref={fileInputRef} className='hidden' onChange={handleFileChange} />
        <button
          type='button'
          onClick={handleCameraClick}
          className='p-2 text-gray-500 hover:text-blue-500  '
          aria-label='Search by image'
        >
          <Camera size={20} />
        </button>
      </div>

      <div className='flex items-center space-x-4'>
        <div className='relative cursor-pointer' onClick={handleCartToggle}>
          <ShoppingCart size={24} className='text-gray-700 hover:text-blue-500 transition  ' />
          <span className='absolute -top-2 -right-2 bg-red-500 text-white text-xs px-2 py-0.5 rounded-full'>
            {myCart.length}
          </span>
        </div>

        <button
          onClick={toggleTheme}
          className='p-2 rounded-full bg-gray-200 hover:bg-gray-300 transition '
          aria-label='Toggle dark mode'
        >
          {theme === 'light' ? '🌞' : '🌙'}
        </button>

        {isLoggedIn ? (
          <div className='d-dropdown d-dropdown-hover d-dropdown-end'>
            <div tabIndex={0} className='w-10 h-10 rounded-full overflow-hidden'>
              <img src={userData?.avatar} alt={userData?.lastName} className='w-full h-full object-cover' />
            </div>
            <ul tabIndex={0} className='d-dropdown-content d-menu bg-base-100 rounded-md z-10 w-52 p-2 shadow-md'>
              {filteredDropDownProfile.map((item) => {
                let Comp: React.ElementType = 'button'
                if (item.to) {
                  Comp = Link
                }
                return (
                  <li key={item.id} className={`${item.styleParent ? item.styleParent : ''}`}>
                    <Comp
                      {...(item.to ? { to: item.to } : {})}
                      onClick={item?.onClick ? handleLogout : undefined}
                      className={`flex items-center w-full px-4 py-2 ${
                        item.style ? item.style : 'text-gray-700 hover:bg-gray-100 transition  '
                      }`}
                    >
                      {item.icon}
                      {item.name}
                    </Comp>
                  </li>
                )
              })}
            </ul>
          </div>
        ) : (
          <div className='hidden md:flex space-x-3'>
            <button
              onClick={() => navigate('/auth?mode=login')}
              className='px-4 py-2 text-blue-600 border border-blue-600 rounded-lg hover:bg-blue-600 hover:text-white transition  '
            >
              Login
            </button>
            <button
              onClick={() => navigate('/auth?mode=register')}
              className='px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition  '
            >
              Register
            </button>
          </div>
        )}

        <button className='md:hidden' onClick={() => setIsMenuOpen(!isMenuOpen)} aria-label='Toggle menu'>
          {isMenuOpen ? <X size={28} /> : <Menu size={28} />}
        </button>
      </div>

      {isMenuOpen && (
        <div className='absolute top-16 left-0 w-full bg-white shadow-md md:hidden z-[999999] 0'>
          <div className='flex flex-col space-y-4 p-4 text-gray-700 '>
            <NavLink
              to={publicPaths.PUBLIC}
              className={({ isActive }) =>
                isActive ? 'text-blue-600 font-bold' : 'text-gray-700 hover:text-blue-500 transition '
              }
              onClick={() => setIsMenuOpen(false)}
            >
              Home
            </NavLink>
            <NavLink
              to={publicPaths.ABOUT}
              className={({ isActive }) =>
                isActive ? 'text-blue-600 font-bold' : 'text-gray-700 hover:text-blue-500 transition '
              }
              onClick={() => setIsMenuOpen(false)}
            >
              About
            </NavLink>
            <NavLink
              to={publicPaths.PRODUCT}
              className={({ isActive }) =>
                isActive ? 'text-blue-600 font-bold' : 'text-gray-700 hover:text-blue-500 transition '
              }
              onClick={() => setIsMenuOpen(false)}
            >
              Product
            </NavLink>
            <NavLink
              to={publicPaths.SERVICE}
              className={({ isActive }) =>
                isActive ? 'text-blue-600 font-bold' : 'text-gray-700 hover:text-blue-500 transition '
              }
              onClick={() => setIsMenuOpen(false)}
            >
              Service
            </NavLink>
            <NavLink
              to={publicPaths.CONTACT}
              className={({ isActive }) =>
                isActive ? 'text-blue-600 font-bold' : 'text-gray-700 hover:text-blue-500 transition '
              }
              onClick={() => setIsMenuOpen(false)}
            >
              Contact
            </NavLink>
            <div className='flex items-center space-x-4 border-t pt-4 '>
              <div className='relative cursor-pointer' onClick={handleCartToggle}>
                <ShoppingCart size={24} className='text-gray-700 ' />
                <span className='absolute -top-2 -right-2 bg-red-500 text-white text-xs px-2 py-0.5 rounded-full'>
                  {myCart.length}
                </span>
              </div>
              <button onClick={toggleTheme} className='p-2 rounded-full bg-gray-200'>
                {theme === 'light' ? '🌞' : '🌙'}
              </button>
            </div>
            {!isLoggedIn && (
              <>
                <button
                  onClick={() => {
                    navigate('/auth?mode=login')
                    setIsMenuOpen(false)
                  }}
                  className='px-4 py-2 text-blue-600 border border-blue-600 rounded-lg hover:bg-blue-600 hover:text-white transition  '
                >
                  Login
                </button>
                <button
                  onClick={() => {
                    navigate('/auth?mode=register')
                    setIsMenuOpen(false)
                  }}
                  className='px-4 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition  '
                >
                  Register
                </button>
              </>
            )}
          </div>
        </div>
      )}
      <Cart isOpen={isCartOpen} onClose={handleCartToggle} cartItems={myCart} accessToken={accessToken || ''} />
    </nav>
  )
}

export default Header
