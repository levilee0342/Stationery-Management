import React from 'react'
import DatePicker from 'react-datepicker'
import 'react-datepicker/dist/react-datepicker.css'

interface FilterBarProps {
  selectedDate: Date
  setSelectedDate: (date: Date) => void
  onMarkAllAsRead: () => void
  unreadCount: number
}

const FilterBar: React.FC<FilterBarProps> = ({ selectedDate, setSelectedDate, onMarkAllAsRead, unreadCount }) => {
  return (
    <div className='mb-6 flex items-center justify-between gap-4'>
      <div className='flex items-center gap-4'>
        <label className='text-gray-700 font-medium'>Chọn ngày:</label>
        <DatePicker
          selected={selectedDate}
          onChange={(date: Date) => setSelectedDate(date)}
          dateFormat='dd/MM/yyyy'
          className='p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
        />
      </div>
      <div className='flex items-center gap-4'>
        <span className='text-sm text-gray-600'>{unreadCount} thông báo chưa đọc</span>
        <button
          onClick={onMarkAllAsRead}
          className='px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition'
        >
          Đánh dấu tất cả đã đọc
        </button>
      </div>
    </div>
  )
}

export default FilterBar
