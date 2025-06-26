// src/components/Sidebar.tsx
import React from 'react'
import { FaUser, FaLock, FaBox, FaSignOutAlt } from 'react-icons/fa'
import { MdDiscount } from 'react-icons/md'

// Định nghĩa interface cho props của Sidebar
interface SidebarProps {
  activeSection: string
  setActiveSection: (section: string) => void
  onLogout: () => void
}

const Sidebar: React.FC<SidebarProps> = ({ activeSection, setActiveSection, onLogout }) => {
  const menuItems = [
    { id: 'profile', label: 'Profile', icon: <FaUser className='mr-2' /> },
    { id: 'password', label: 'Đổi mật khẩu', icon: <FaLock className='mr-2' /> },
    { id: 'orders', label: 'Quản lý đơn hàng', icon: <FaBox className='mr-2' /> },
    { id: 'myVoucher', label: 'Quản lý voucher', icon: <MdDiscount className='mr-2' /> }
  ]

  return (
    <div className='w-64 bg-white h-screen p-6 shadow-md'>
      <h1 className='text-2xl font-bold mb-8'>User Profile</h1>
      <ul className='space-y-4'>
        {menuItems.map((item) => (
          <li key={item.id}>
            <button
              onClick={() => setActiveSection(item.id)}
              className={`w-full text-left px-4 py-2 rounded-md flex items-center ${
                activeSection === item.id ? 'bg-blue-500 text-white' : 'text-gray-700 hover:bg-gray-100'
              }`}
            >
              {item.icon}
              {item.label}
            </button>
          </li>
        ))}
        <li>
          <button
            onClick={onLogout}
            className='w-full text-left px-4 py-2 rounded-md flex items-center text-red-500 hover:bg-red-100'
          >
            <FaSignOutAlt className='mr-2' />
            Đăng xuất
          </button>
        </li>
      </ul>
    </div>
  )
}

export default Sidebar
