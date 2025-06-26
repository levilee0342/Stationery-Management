import React from 'react'
import { MdNotificationsActive, MdDone, MdMarkunread } from 'react-icons/md'

export interface Notification {
  id: string
  title: string
  message: string
  createdAt: string
  isRead: boolean
}

interface Props {
  notification: Notification
  onMarkAsRead: (id: string) => void
}

const NotificationItem: React.FC<Props> = ({ notification, onMarkAsRead }) => {
  return (
    <div
      className={`flex items-start gap-4 p-4 border-b hover:bg-gray-50 transition ${
        !notification.isRead ? 'bg-blue-50' : 'bg-white'
      }`}
    >
      <div className='text-2xl text-blue-600 mt-1'>
        <MdNotificationsActive />
      </div>
      <div className='flex-1'>
        <div className='flex justify-between items-center'>
          <h3 className='text-lg font-semibold text-gray-800'>{notification.title}</h3>
          <span
            className={`text-sm flex items-center gap-1 ${notification.isRead ? 'text-green-600' : 'text-blue-500'}`}
          >
            {notification.isRead ? <MdDone /> : <MdMarkunread />}
            {notification.isRead ? 'Đã đọc' : 'Chưa đọc'}
          </span>
        </div>
        <p className='text-gray-600 mt-1'>{notification.message}</p>
        <div className='text-sm text-gray-400 mt-2 flex justify-between items-center'>
          <span>{new Date(notification.createdAt).toLocaleString()}</span>
          {!notification.isRead && (
            <button onClick={() => onMarkAsRead(notification.id)} className='text-sm text-blue-600 hover:underline'>
              Đánh dấu đã đọc
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

export default NotificationItem
