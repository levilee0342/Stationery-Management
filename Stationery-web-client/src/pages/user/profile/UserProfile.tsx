// src/pages/UserProfile.tsx
import React, { useEffect, useState } from 'react'
import ProfileHeader from './component/ProfileHeader'
import PasswordChange from './PasswordChange'
import OrderTabs from './order/OrderTab'
import Sidebar from './component/Sidebar'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { useNavigate } from 'react-router-dom'
import { fetchCurrentUser } from '~/store/actions/user'
import { userActions } from '~/store/slices/user'
import MyVoucher from './voucher/MyVoucher'

interface Order {
  id: string
  status: 'Đang chuẩn bị hàng' | 'Đã xác nhận đơn hàng' | 'Đang Giao' | 'Đã hủy'
  date: string
}

const UserProfile: React.FC = () => {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()
  const [activeSection, setActiveSection] = useState<string>('profile')
  const { accessToken, isLoggedIn } = useAppSelector((state) => state.user)
  const orders: Order[] = [
    { id: '001', status: 'Đang chuẩn bị hàng', date: '2025-03-06' },
    { id: '002', status: 'Đã xác nhận đơn hàng', date: '2025-03-05' },
    { id: '003', status: 'Đang Giao', date: '2025-03-04' },
    { id: '004', status: 'Đã hủy', date: '2025-03-03' }
  ]
  useEffect(() => {
    if (!isLoggedIn || !accessToken) {
      navigate('/')
      return
    }
    dispatch(fetchCurrentUser({ token: accessToken }))
  }, [accessToken, isLoggedIn, dispatch, navigate])

  // src/pages/UserProfile.tsx
  const renderContent = () => {
    switch (activeSection) {
      case 'profile':
        return <ProfileHeader />
      case 'password':
        return <PasswordChange />
      case 'orders':
        return <OrderTabs orders={orders} />
      case 'myVoucher':
        return <MyVoucher />
      default:
        return null
    }
  }
  const handleLogout = () => {
    dispatch(userActions.logout())
  }
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [])
  return (
    <div className='min-h-screen bg-gray-100 flex mt-16'>
      <Sidebar activeSection={activeSection} setActiveSection={setActiveSection} onLogout={handleLogout} />
      <div className='flex-1 p-10'>{renderContent()}</div>
    </div>
  )
}

export default UserProfile
