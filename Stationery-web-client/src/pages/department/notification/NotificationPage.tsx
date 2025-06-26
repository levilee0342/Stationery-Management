import React, { useState } from 'react'
import NotificationList from './NotificationList'
import FilterBar from './FilterBar'

export interface Notification {
  id: string
  title: string
  message: string
  createdAt: string
  isRead: boolean
}

const initialData: Notification[] = [
  {
    id: '1',
    title: 'Yêu cầu bổ sung vật tư',
    message: 'Bạn cần cung cấp thêm 5 hộp bút bi màu xanh.',
    createdAt: '2025-05-11T10:00:00',
    isRead: false
  },
  {
    id: '2',
    title: 'Thông báo hệ thống',
    message: 'Hệ thống sẽ bảo trì vào 12/5 lúc 21h.',
    createdAt: '2025-05-11T15:30:00',
    isRead: true
  },
  {
    id: '3',
    title: 'Yêu cầu giấy in',
    message: 'Cần 10 ram giấy in A4 trước 15/05.',
    createdAt: '2025-05-10T09:00:00',
    isRead: false
  }
]

const NotificationPage: React.FC = () => {
  const [notifications, setNotifications] = useState<Notification[]>(initialData)
  const [selectedDate, setSelectedDate] = useState<Date>(new Date())
  const [isLoading, setIsLoading] = useState<boolean>(false)

  const markAsRead = (id: string) => {
    setNotifications((prev) => prev.map((n) => (n.id === id ? { ...n, isRead: true } : n)))
  }

  const markAllAsRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })))
  }

  const filteredNotifications = notifications.filter((n) => {
    const notificationDate = new Date(n.createdAt)
    return (
      notificationDate.getDate() === selectedDate.getDate() &&
      notificationDate.getMonth() === selectedDate.getMonth() &&
      notificationDate.getFullYear() === selectedDate.getFullYear()
    )
  })

  const unreadCount = notifications.filter((n) => !n.isRead).length

  return (
    <div className='min-h-screen bg-gray-100 p-10 ml-16'>
      <div className='max-w-6xlxl'>
        <h1 className='text-3xl font-bold mb-6 text-blue-700'>Thông báo</h1>
        <FilterBar
          selectedDate={selectedDate}
          setSelectedDate={setSelectedDate}
          onMarkAllAsRead={markAllAsRead}
          unreadCount={unreadCount}
        />
        {isLoading ? (
          <div className='text-center py-10'>
            <div className='animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mx-auto'></div>
          </div>
        ) : (
          <NotificationList notifications={filteredNotifications} onMarkAsRead={markAsRead} />
        )}
      </div>
    </div>
  )
}

export default NotificationPage
