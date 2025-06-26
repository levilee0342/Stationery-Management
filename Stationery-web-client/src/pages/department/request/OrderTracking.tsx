import React, { useState } from 'react'
import { FaInfoCircle, FaCheckCircle, FaTruck, FaBan, FaCogs } from 'react-icons/fa'
import { ExtendedPurchaseOrderResponse } from './CreateRequest'
import { MdCancel } from 'react-icons/md'

interface ExtendedOrderDetail {
  productDetailId: string
  quantity: number
  color: string
  size: string
  name: string
}

interface OrderTrackingProps {
  requests: ExtendedPurchaseOrderResponse[]
  productDetailsMap: { [orderId: string]: ExtendedOrderDetail[] }
  activeTab: 'processing' | 'shipping' | 'completed' | 'canceled'
  setActiveTab: React.Dispatch<React.SetStateAction<'processing' | 'shipping' | 'completed' | 'canceled'>>
}

const STATUS = {
  PENDING: 'PENDING',
  PROCESSING: 'PROCESSING',
  SHIPPING: 'SHIPPING',
  COMPLETED: 'COMPLETED',
  CANCELED: 'CANCELED'
} as const

const OrderTracking: React.FC<OrderTrackingProps> = ({ requests, productDetailsMap, activeTab, setActiveTab }) => {
  const [showCancelModal, setShowCancelModal] = useState(false)
  const [selectedRequest, setSelectedRequest] = useState<ExtendedPurchaseOrderResponse | null>(null)

  // Filter requests for tabs
  const processingRequests = requests.filter((request) => request.status === STATUS.PROCESSING)
  const shippingRequests = requests.filter((request) => request.status === STATUS.SHIPPING)
  const completedRequests = requests.filter((request) => request.status === STATUS.COMPLETED)
  const canceledRequests = requests.filter((request) => request.status === STATUS.CANCELED)

  console.log('Xem lý do hủy: ', selectedRequest)
  // Handle view cancellation details
  const handleViewCancelDetails = (request: ExtendedPurchaseOrderResponse) => {
    setSelectedRequest(request)
    setShowCancelModal(true)
  }

  // Close cancellation details modal
  const handleCloseModal = () => {
    setShowCancelModal(false)
    setSelectedRequest(null)
  }

  // Status badge configuration
  const getStatusBadge = (status: string) => {
    switch (status) {
      case STATUS.PROCESSING:
        return {
          icon: <FaCogs size={14} className='mr-1' />,
          className: 'bg-orange-100 text-orange-800'
        }
      case STATUS.SHIPPING:
        return {
          icon: <FaTruck size={14} className='mr-1' />,
          className: 'bg-blue-100 text-blue-800'
        }
      case STATUS.COMPLETED:
        return {
          icon: <FaCheckCircle size={14} className='mr-1' />,
          className: 'bg-green-100 text-green-800'
        }
      case STATUS.CANCELED:
        return {
          icon: <MdCancel size={14} className='mr-1' />,
          className: 'bg-red-100 text-red-800'
        }
      default:
        return {
          icon: null,
          className: 'bg-gray-100 text-gray-800'
        }
    }
  }

  // Tab configuration with icons
  const getTabConfig = (tab: string) => {
    switch (tab) {
      case 'processing':
        return {
          icon: <FaCogs size={16} className='mr-2' />,
          label: 'PROCESSING',
          activeBorder: 'border-orange-600',
          activeText: 'text-orange-600'
        }
      case 'shipping':
        return {
          icon: <FaTruck size={16} className='mr-2' />,
          label: 'SHIPPING',
          activeBorder: 'border-blue-600',
          activeText: 'text-blue-600'
        }
      case 'completed':
        return {
          icon: <FaCheckCircle size={16} className='mr-2' />,
          label: 'COMPLETED',
          activeBorder: 'border-green-600',
          activeText: 'text-green-600'
        }
      case 'canceled':
        return {
          icon: <MdCancel size={16} className='mr-2' />,
          label: 'CANCELED',
          activeBorder: 'border-red-600',
          activeText: 'text-red-600'
        }
      default:
        return {
          icon: null,
          label: tab.toUpperCase(),
          activeBorder: 'border-blue-600',
          activeText: 'text-blue-600'
        }
    }
  }

  return (
    <div className='bg-white rounded-xl shadow-lg p-8 mt-8'>
      <h2 className='text-2xl font-semibold text-gray-800 mb-6'>Order Tracking</h2>
      <div className='flex border-b border-gray-200 mb-6'>
        {['processing', 'shipping', 'completed', 'canceled'].map((tab) => {
          const { icon, label, activeBorder, activeText } = getTabConfig(tab)
          return (
            <button
              key={tab}
              onClick={() => setActiveTab(tab as any)}
              className={`flex items-center px-6 py-3 text-sm font-medium transition-all duration-300 ${
                activeTab === tab ? `border-b-2 ${activeBorder} ${activeText}` : 'text-gray-600 hover:text-gray-800'
              }`}
            >
              {icon}
              {label}
            </button>
          )
        })}
      </div>
      <div className='overflow-x-auto'>
        <table className='w-full text-left'>
          <thead>
            <tr className='bg-gradient-to-r from-blue-50 to-blue-100 text-blue-800'>
              <th className='p-4 text-sm font-semibold'>Order ID</th>
              <th className='p-4 text-sm font-semibold'>Products</th>
              <th className='p-4 text-sm font-semibold'>Colors</th>
              <th className='p-4 text-sm font-semibold'>Sizes</th>
              <th className='p-4 text-sm font-semibold'>Quantities</th>
              <th className='p-4 text-sm font-semibold'>Status</th>
              <th className='p-4 text-sm font-semibold'>Created Date</th>
              {activeTab === 'canceled' && <th className='p-4 text-sm font-semibold'>Cancel Details</th>}
            </tr>
          </thead>
          <tbody>
            {(activeTab === 'processing'
              ? processingRequests
              : activeTab === 'shipping'
                ? shippingRequests
                : activeTab === 'completed'
                  ? completedRequests
                  : canceledRequests
            ).map((request) => {
              const { icon, className } = getStatusBadge(request.status)
              const details = productDetailsMap[request.purchaseOrderId] || [
                {
                  productDetailId: 'N/A',
                  name: 'N/A',
                  color: 'N/A',
                  size: 'N/A',
                  quantity: 1
                }
              ]
              return (
                <tr key={request.purchaseOrderId} className='border-b hover:bg-blue-50/50 transition'>
                  <td className='p-4 text-sm text-gray-700'>
                    {request.purchaseOrderId.slice(0, 4)}
                    {request.purchaseOrderId.slice(-4)}
                  </td>
                  <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.name).join(', ')}</td>
                  <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.color).join(', ')}</td>
                  <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.size).join(', ')}</td>
                  <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.quantity).join(', ')}</td>
                  <td className='p-4 text-sm'>
                    <span
                      className={`inline-flex items-center px-3 py-1 rounded-full text-xs font-medium ${className}`}
                    >
                      {icon}
                      {request.status}
                    </span>
                  </td>
                  <td className='p-4 text-sm text-gray-700'>{request.createdAt?.split('T')[0] || 'N/A'}</td>
                  {activeTab === 'canceled' && (
                    <td className='p-4 text-sm'>
                      <button
                        onClick={() => handleViewCancelDetails(request)}
                        className='bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors'
                      >
                        <FaInfoCircle size={16} />
                      </button>
                    </td>
                  )}
                </tr>
              )
            })}
          </tbody>
        </table>
        {(activeTab === 'processing'
          ? processingRequests
          : activeTab === 'shipping'
            ? shippingRequests
            : activeTab === 'completed'
              ? completedRequests
              : canceledRequests
        ).length === 0 && <p className='text-center text-gray-500 mt-4'>No orders {activeTab}.</p>}
      </div>

      {showCancelModal && selectedRequest && (
        <div className='fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4'>
          <div className='bg-white rounded-2xl shadow-2xl p-6 w-full max-w-md transform transition-all duration-300'>
            <div className='flex justify-between items-center mb-6'>
              <h2 className='text-2xl font-bold text-blue-700'>Order Cancellation Details</h2>
              <button
                onClick={handleCloseModal}
                className='text-gray-400 hover:text-red-500 transition'
                aria-label='Close modal'
              >
                <svg className='w-6 h-6' fill='none' stroke='currentColor' viewBox='0 0 24 24'>
                  <path strokeLinecap='round' strokeLinejoin='round' strokeWidth='2' d='M6 18L18 6M6 6l12 12' />
                </svg>
              </button>
            </div>
            <div className='space-y-5'>
              <div>
                <label className='block text-sm font-semibold text-gray-700'>
                  <b>Order ID</b>
                </label>
                <p className='text-base text-gray-900'>{selectedRequest.purchaseOrderId}</p>
              </div>
              <div>
                <label className='block text-sm font-semibold text-gray-700'>
                  <b>Products</b>
                </label>
                <p className='text-base text-gray-900'>
                  {productDetailsMap[selectedRequest.purchaseOrderId]?.map((detail) => detail.name).join(', ') || 'N/A'}
                </p>
              </div>
              <div>
                <label className='block text-sm font-semibold text-gray-700'>
                  <b>Cancel Reason</b>
                </label>
                <p className='text-base text-gray-900'>{selectedRequest.cancelReason || 'No reason provided.'}</p>
              </div>
            </div>
            <div className='flex justify-end mt-8'>
              <button
                onClick={handleCloseModal}
                className='px-5 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition text-sm font-medium shadow'
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default OrderTracking
