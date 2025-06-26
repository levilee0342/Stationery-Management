import React from 'react'
import { FaWallet } from 'react-icons/fa' // Thêm biểu tượng ví tiền

const BudgetLimit: React.FC = () => {
  const budget = {
    total: 5000000,
    used: 3200000
  }
  const percentage = (budget.used / budget.total) * 100

  return (
    <div className='d-card d-bg-base-100 d-shadow-2xl p-6 d-transition-all hover:d-shadow-3xl'>
      <div className='flex items-center justify-between mb-4'>
        <h2 className='text-xl font-semibold text-base-content'>Số dư hạn mức</h2>
        <FaWallet className='text-2xl d-text-primary' />
      </div>
      <div className='text-center'>
        {/* Số tiền đã sử dụng */}
        <p className='text-sm text-base-content/70 font-medium'>Đã sử dụng</p>
        <p className='text-3xl font-bold d-text-error tracking-tight mt-1'>
          {budget.used.toLocaleString()} <span className='text-lg'>VND</span>
        </p>

        {/* Tổng hạn mức */}
        <p className='text-sm text-base-content/50 mt-2'>/ {budget.total.toLocaleString()} VND</p>

        {/* Thanh tiến trình */}
        <div className='mt-6'>
          <div className='relative'>
            <progress
              className='d-progress d-progress-primary w-full h-3 d-rounded-full'
              value={percentage}
              max='100'
            ></progress>
            {/* Hiệu ứng animation cho thanh tiến trình */}
            <div
              className='absolute top-0 left-0 h-3 d-bg-primary/20 d-rounded-full animate-pulse'
              style={{ width: `${percentage}%` }}
            ></div>
          </div>
          <p className='text-sm text-base-content/70 mt-2 font-medium'>{percentage.toFixed(1)}% đã sử dụng</p>
        </div>

        {/* Thông tin bổ sung */}
        <div className='mt-4 d-bg-base-200 d-rounded-lg p-3'>
          <p className='text-sm text-base-content/80'>
            Còn lại:{' '}
            <span className='font-semibold d-text-success'>{(budget.total - budget.used).toLocaleString()} VND</span>
          </p>
        </div>
      </div>
    </div>
  )
}

export default BudgetLimit
