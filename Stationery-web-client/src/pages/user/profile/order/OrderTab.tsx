import React, { useState, useEffect } from 'react'
import OrderItem from './OrderItem'
import { FiPackage, FiCheckCircle, FiTruck, FiXCircle, FiInbox } from 'react-icons/fi'
import { FaBoxOpen } from 'react-icons/fa'
import { PurchaseOrderResponse } from '~/types/order'
import { useAppSelector } from '~/hooks/redux'
import { apiGetUserOrders } from '~/api/orders'

const OrderTabs: React.FC = () => {
  const [activeTab, setActiveTab] = useState('Đang chuẩn bị hàng')
  const [orders, setOrders] = useState<PurchaseOrderResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const { accessToken } = useAppSelector((state) => state.user)

  const tabs = [
    {
      name: 'Đang chuẩn bị hàng',
      apiStatus: 'PENDING',
      icon: <FiPackage className='mr-2' />,
      color: 'from-amber-400 to-amber-500'
    },
    {
      name: 'Đã xác nhận đơn hàng',
      apiStatus: 'PROCESSING',
      icon: <FiCheckCircle className='mr-2' />,
      color: 'from-blue-400 to-blue-500'
    },
    {
      name: 'Đang Giao',
      apiStatus: 'SHIPPING',
      icon: <FiTruck className='mr-2' />,
      color: 'from-green-400 to-green-500'
    },
    {
      name: 'Hoàn thành',
      apiStatus: 'COMPLETED',
      icon: <FiCheckCircle className='mr-2' />,
      color: 'from-purple-400 to-purple-500'
    },
    {
      name: 'Đã hủy',
      apiStatus: 'CANCELED',
      icon: <FiXCircle className='mr-2' />,
      color: 'from-red-400 to-red-500'
    }
  ]

  useEffect(() => {
    const fetchOrders = async () => {
      setLoading(true)
      setError(null)

      try {
        const selectedTab = tabs.find((tab) => tab.name === activeTab)
        if (!selectedTab) return

        const response = await apiGetUserOrders({ accessToken, status: selectedTab.apiStatus })
        setOrders(response.result || [])
      } catch (err) {
        setError('Không thể tải đơn hàng. Vui lòng thử lại.')
      } finally {
        setLoading(false)
      }
    }

    if (accessToken) {
      fetchOrders()
    } else {
      setError('Vui lòng đăng nhập để xem đơn hàng.')
      setLoading(false)
    }
  }, [accessToken, activeTab]) // chạy lại khi tab đổi

  return (
    <div className='bg-gradient-to-br from-gray-50 to-gray-100 p-6 rounded-2xl shadow-xl'>
      <div className='flex justify-between items-center mb-8'>
        <h2 className='text-3xl font-bold text-gray-800 flex items-center'>
          <span className='bg-gradient-to-r from-blue-400 to-blue-500 text-white p-3 rounded-xl mr-4 shadow-md'>
            <FiInbox size={24} />
          </span>
          <span className='bg-clip-text text-transparent bg-gradient-to-r from-gray-700 to-gray-900'>
            Quản lý đơn hàng
          </span>
        </h2>
        <div className='text-sm bg-white py-2 px-4 rounded-full shadow-sm'>
          Tổng cộng: <span className='font-bold text-blue-600'>{orders.length} đơn</span>
        </div>
      </div>

      <div className='flex space-x-2 mb-8 bg-white p-2 rounded-xl shadow-inner'>
        {tabs.map((tab) => (
          <button
            key={tab.name}
            onClick={() => setActiveTab(tab.name)}
            className={`flex items-center justify-center py-3 px-4 rounded-lg flex-1 transition-all ${
              activeTab === tab.name
                ? `bg-gradient-to-r ${tab.color} text-white font-bold shadow-md`
                : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-200'
            }`}
          >
            {tab.icon}
            {tab.name}
          </button>
        ))}
      </div>

      {loading ? (
        <div className='text-center py-16 bg-white rounded-2xl shadow-sm'>
          <div className='animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500 mx-auto'></div>
          <p className='text-gray-600 mt-4'>Đang tải đơn hàng...</p>
        </div>
      ) : error ? (
        <div className='text-center py-16 bg-white rounded-2xl shadow-sm'>
          <FaBoxOpen className='mx-auto text-gray-300 text-6xl mb-4' />
          <h3 className='text-xl font-medium text-gray-600 mb-2'>Lỗi</h3>
          <p className='text-gray-500 max-w-md mx-auto'>{error}</p>
        </div>
      ) : orders.length > 0 ? (
        <div className='space-y-5'>
          {orders.map((order) => (
            <OrderItem key={order.purchaseOrderId} order={order} />
          ))}
        </div>
      ) : (
        <div className='text-center py-16 bg-white rounded-2xl shadow-sm'>
          <FaBoxOpen className='mx-auto text-gray-300 text-6xl mb-4' />
          <h3 className='text-xl font-medium text-gray-600 mb-2'>Không có đơn hàng</h3>
          <p className='text-gray-500 max-w-md mx-auto'>Hiện không có đơn hàng nào trong trạng thái "{activeTab}"</p>
          <button className='mt-4 bg-gradient-to-r from-blue-400 to-blue-500 text-white py-2 px-6 rounded-full shadow-md hover:shadow-lg transition-all'>
            Tạo đơn hàng mới
          </button>
        </div>
      )}
    </div>
  )
}

export default OrderTabs
