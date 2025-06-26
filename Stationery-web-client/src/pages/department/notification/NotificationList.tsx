import React from 'react'
import NotificationItem from './NotificationItem'

export interface Notification {
  id: string
  title: string
  message: string
  createdAt: string
  isRead: boolean
}

interface Props {
  notifications: Notification[]
  onMarkAsRead: (id: string) => void
}

const NotificationList: React.FC<Props> = ({ notifications, onMarkAsRead }) => {
  if (notifications.length === 0) {
    return <div className='text-center py-10 text-gray-500'>Không có thông báo nào cho ngày này.</div>
  }
  return (
    <div className='bg-white rounded-xl shadow overflow-hidden'>
      {notifications.map((n) => (
        <NotificationItem key={n.id} notification={n} onMarkAsRead={onMarkAsRead} />
      ))}
    </div>
  )
}

export default NotificationList
