import React, { useEffect, useState } from 'react'
import { FaPaperPlane, FaHourglassHalf, FaCheckCircle } from 'react-icons/fa'
import { FiX } from 'react-icons/fi'
import { apiGetOrderStatusStatistics } from '~/api/orders'
import { useAppSelector } from '~/hooks/redux'

interface OrderStats {
  PROCESSING: number
  CANCELED: number
  SHIPPING: number
  PENDING: number
  COMPLETED: number
}

const OrderOverview: React.FC = () => {
  const { userData, accessToken } = useAppSelector((state) => state.user)
  const [orderStats, setOrderStats] = useState<OrderStats | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!userData?.userId || !accessToken || orderStats) return

    const fetchData = async () => {
      try {
        const res = await apiGetOrderStatusStatistics({
          userId: userData.userId,
          accessToken
        })
        if (res.code === 200) {
          setOrderStats(res.result)
        }
      } catch (error) {
        console.error('Lỗi khi lấy thống kê đơn hàng:', error)
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [userData, accessToken, orderStats])

  if (loading || !orderStats) {
    return <div>Đang tải thống kê đơn hàng...</div>
  }

  const totalRequests =
    orderStats.PENDING + orderStats.PROCESSING + orderStats.SHIPPING + orderStats.COMPLETED + orderStats.CANCELED

  const stats = [
    {
      title: 'Yêu cầu đã gửi',
      value: totalRequests,
      icon: <FaPaperPlane />,
      color: 'bg-blue-500',
      isMain: true
    },
    {
      title: 'Đang xử lý',
      value: orderStats.PENDING,
      icon: <FaHourglassHalf />,
      color: 'bg-gray-400'
    },
    {
      title: 'Đang giao',
      value: orderStats.SHIPPING,
      icon: <FaHourglassHalf />,
      color: 'bg-yellow-500'
    },
    {
      title: 'Đã nhận',
      value: orderStats.COMPLETED,
      icon: <FaCheckCircle />,
      color: 'bg-emerald-400'
    },
    {
      title: 'Đã hủy',
      value: orderStats.CANCELED,
      icon: <FiX />,
      color: 'bg-red-400'
    }
  ]

  return (
    <div className='d-card d-bg-base-100 d-shadow-xl p-6'>
      <h2 className='text-xl font-semibold text-base-content mb-4'>Tổng quan đơn hàng</h2>
      <div className='grid grid-cols-1 sm:grid-cols-4 gap-4'>
        {stats.map((stat, index) =>
          stat.isMain ? (
            <div
              key={index}
              className={`${stat.color} col-span-1 sm:col-span-4 text-white p-6 rounded-xl flex items-center justify-between shadow-lg ring-2 ring-blue-700`}
            >
              <div className='flex items-center gap-4'>
                <div className='text-4xl'>{stat.icon}</div>
                <div>
                  <p className='text-base font-semibold'>{stat.title}</p>
                  <p className='text-xs opacity-80'>Tổng số yêu cầu</p>
                </div>
              </div>
              <p className='text-4xl font-extrabold'>{stat.value}</p>
            </div>
          ) : (
            <div
              key={index}
              className={`${stat.color} text-white p-4 rounded-lg flex items-center justify-between transition-transform hover:scale-105`}
            >
              <div>
                <p className='text-sm'>{stat.title}</p>
                <p className='text-2xl font-bold'>{stat.value}</p>
              </div>
              <div className='text-3xl'>{stat.icon}</div>
            </div>
          )
        )}
      </div>
    </div>
  )
}

export default OrderOverview
