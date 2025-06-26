import React, { useEffect, useState } from 'react'
import {
  FaHome,
  FaClipboardList,
  FaBox,
  FaBell,
  FaSignOutAlt,
  FaChevronLeft,
  FaChevronRight,
  FaHandshake,
  FaPlusCircle,
  FaHeadset
} from 'react-icons/fa'
import { useDispatch } from 'react-redux'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { departmentPath } from '~/constance/paths'
import { useAppSelector } from '~/hooks/redux'
import { userActions } from '~/store/slices/user'

const Sidebar: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false)
  const location = useLocation()
  const [hoverItem, setHoverItem] = useState<string | null>(null)

  const navigate = useNavigate()
  const { accessToken, userData } = useAppSelector((state) => state.user)
  useEffect(() => {
    if (!accessToken) {
      navigate('/auth?mode=login')
    }
  }, [accessToken, navigate])

  const dispatch = useDispatch()

  const handleLogout = () => {
    dispatch(userActions.logout())
    navigate('/auth?mode=login')
  }

  const navItems = [
    { icon: <FaHome size={18} />, text: 'Dashboard', path: departmentPath.DASHBOARD },
    { icon: <FaBox size={18} />, text: 'Products', path: departmentPath.PRODUCT },
    { icon: <FaPlusCircle size={18} />, text: 'Create Request', path: departmentPath.CREATE_REQUEST },
    { icon: <FaClipboardList size={18} />, text: 'Invoice', path: departmentPath.INVOICE },
    { icon: <FaBell size={18} />, text: 'Notification', path: departmentPath.NOTIFICATION, badge: 3 },
    { icon: <FaHeadset size={18} />, text: 'Support', path: departmentPath.SUPPORT }
  ]

  return (
    <div
      className={`fixed top-0 left-0 h-screen bg-gradient-to-b from-blue-800 to-blue-900 text-white transition-all duration-300 ease-in-out 
      ${isOpen ? 'w-64' : 'w-20'} flex flex-col z-50 shadow-xl`}
    >
      {/* Header */}
      <div className='flex items-center justify-between p-4 h-20 border-b border-blue-700'>
        {isOpen && (
          <div className='flex items-center space-x-3'>
            <div className='w-9 h-9 rounded-md bg-white flex items-center justify-center shadow-sm'>
              <FaHandshake className='text-blue-600' size={20} />
            </div>
            <div>
              <span className='font-bold text-white text-lg tracking-tight'>STATIONERY P</span>
              <p className='text-xs text-blue-200'>
                {userData?.firstName} {userData?.lastName}
              </p>
            </div>
          </div>
        )}
        <button
          onClick={() => setIsOpen(!isOpen)}
          className='p-1.5 rounded-full bg-blue-700 hover:bg-blue-600 transition-colors shadow-sm'
        >
          {isOpen ? (
            <FaChevronLeft className='text-blue-100' size={16} />
          ) : (
            <FaChevronRight className='text-blue-100' size={16} />
          )}
        </button>
      </div>

      {/* Navigation */}
      <nav className='flex-1 overflow-y-auto py-4 px-3'>
        <ul className='space-y-2'>
          {navItems.map((item) => (
            <li key={item.text}>
              <Link
                to={item.path}
                className={`flex items-center p-3 rounded-lg transition-all duration-200 group
                  ${isOpen ? 'justify-start' : 'justify-center'}
                  ${
                    location.pathname === item.path || location.pathname === `${item.path}/`
                      ? 'bg-blue-700 shadow-inner font-semibold'
                      : 'hover:bg-blue-700/80 font-medium'
                  }`}
                onMouseEnter={() => setHoverItem(item.text)}
                onMouseLeave={() => setHoverItem(null)}
              >
                <div className='relative flex items-center'>
                  <span
                    className={`transition-all ${
                      location.pathname.includes(item.path) ? 'text-white' : 'text-blue-100'
                    }`}
                  >
                    {item.icon}
                  </span>
                  {item.badge && (
                    <span className='absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center'>
                      {item.badge}
                    </span>
                  )}
                </div>

                {isOpen ? (
                  <span className='ml-3 text-[15px]'>{item.text}</span>
                ) : (
                  hoverItem === item.text && (
                    <div className='absolute left-full ml-4 px-3 py-2 bg-blue-700 text-white shadow-lg rounded-md whitespace-nowrap text-sm font-semibold z-50'>
                      {item.text}
                    </div>
                  )
                )}
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      {/* Footer */}
      <div className='p-4 border-t border-blue-700 bg-blue-800/50'>
        <button
          onClick={handleLogout}
          className={`w-full flex items-center p-3 rounded-lg hover:bg-blue-700 transition-colors
          ${isOpen ? 'justify-start' : 'justify-center'} font-semibold`}
        >
          <FaSignOutAlt className={`${isOpen ? 'mr-3' : ''} text-blue-200`} size={16} />
          {isOpen && <span className='text-[15px]'>SIGN OUT</span>}
        </button>
      </div>
    </div>
  )
}

export default Sidebar
