import React, { useState } from 'react'
import {
  FiPackage,
  FiCheckCircle,
  FiTruck,
  FiX,
  FiEye,
  FiMapPin,
  FiCalendar,
  FiShoppingBag,
  FiXCircle,
  FiChevronLeft,
  FiChevronRight
} from 'react-icons/fi'
import { FaRegSmile, FaRegFrown, FaBoxOpen } from 'react-icons/fa'
import { PurchaseOrderResponse } from '~/types/order'
import { apiGetProductDetailsByOrder } from '~/api/orders'
import { useAppSelector } from '~/hooks/redux'
import { ProductDetail } from '~/types/product'

interface OrderItemProps {
  order: PurchaseOrderResponse
}

// Function to format numbers for VND currency
const formatNumber = (number: number): string => {
  return number.toLocaleString('vi-VN')
}

const statusMap: Record<PurchaseOrderResponse['status'], string> = {
  PENDING: 'Đang chuẩn bị hàng',
  PROCESSING: 'Đã xác nhận đơn hàng',
  SHIPPING: 'Đang Giao',
  COMPLETED: 'Hoàn thành',
  CANCELED: 'Đã hủy'
}

const OrderItem: React.FC<OrderItemProps> = ({ order }) => {
  const { purchaseOrderId, createdAt, status, amount, orderDetails } = order
  const displayStatus = statusMap[status]
  const [showModal, setShowModal] = useState(false)
  const [productDetails, setProductDetails] = useState<ProductDetail[]>([])
  const [loadingDetails, setLoadingDetails] = useState(false)
  const [errorDetails, setErrorDetails] = useState<string | null>(null)
  const { accessToken } = useAppSelector((state) => state.user)
  const [currentImageIndices, setCurrentImageIndices] = useState<{ [key: string]: number }>({})

  const statusStyles: Record<
    string,
    {
      bg: string
      text: string
      icon: React.ReactNode
      border: string
      gradient: string
    }
  > = {
    'Đang chuẩn bị hàng': {
      bg: 'bg-amber-100',
      text: 'text-amber-800',
      icon: <FiPackage className='mr-1' />,
      border: 'border-amber-200',
      gradient: 'from-amber-400 to-amber-500'
    },
    'Đã xác nhận đơn hàng': {
      bg: 'bg-blue-100',
      text: 'text-blue-800',
      icon: <FiCheckCircle className='mr-1' />,
      border: 'border-blue-200',
      gradient: 'from-blue-400 to-blue-500'
    },
    'Đang Giao': {
      bg: 'bg-green-100',
      text: 'text-green-800',
      icon: <FiTruck className='mr-1' />,
      border: 'border-green-200',
      gradient: 'from-green-400 to-green-500'
    },
    'Hoàn thành': {
      bg: 'bg-purple-100',
      text: 'text-purple-800',
      icon: <FiCheckCircle className='mr-1' />,
      border: 'border-purple-200',
      gradient: 'from-purple-400 to-purple-500'
    },
    'Đã hủy': {
      bg: 'bg-red-100',
      text: 'text-red-800',
      icon: <FiX className='mr-1' />,
      border: 'border-red-200',
      gradient: 'from-red-400 to-red-500'
    }
  }

  const formatDate = (dateString: string | null) => {
    if (!dateString) return 'Không có ngày'
    const options: Intl.DateTimeFormatOptions = {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }
    return new Date(dateString).toLocaleDateString('vi-VN', options)
  }

  const totalItems = orderDetails.reduce((sum, item) => sum + item.quantity, 0)

  const handleViewDetails = async () => {
    setShowModal(true)
    setLoadingDetails(true)
    setErrorDetails(null)
    try {
      const response = await apiGetProductDetailsByOrder({ purchaseOrderId, accessToken })
      if (response.code === 200) {
        setProductDetails(response.result)
      }
      setCurrentImageIndices(
        response.result.reduce(
          (acc, detail) => {
            acc[detail.productDetailId] = 0
            return acc
          },
          {} as { [key: string]: number }
        )
      )
    } catch {
      setErrorDetails('Không thể tải chi tiết sản phẩm. Vui lòng thử lại.')
    } finally {
      setLoadingDetails(false)
    }
  }

  const handlePrevImage = (productDetailId: string, imageCount: number) => {
    setCurrentImageIndices((prev) => ({
      ...prev,
      [productDetailId]: (prev[productDetailId] - 1 + imageCount) % imageCount
    }))
  }

  const handleNextImage = (productDetailId: string, imageCount: number) => {
    setCurrentImageIndices((prev) => ({
      ...prev,
      [productDetailId]: (prev[productDetailId] + 1) % imageCount
    }))
  }

  return (
    <>
      <div
        className={`group flex flex-col p-5 border-2 ${statusStyles[displayStatus].border} rounded-xl bg-white hover:shadow-lg transition-all`}
      >
        <div className='flex justify-between items-start mb-3'>
          <div>
            <div className='flex items-center'>
              <span
                className={`bg-gradient-to-r ${statusStyles[displayStatus].gradient} text-white p-2 rounded-lg mr-3 shadow-sm`}
              >
                {displayStatus === 'Đã hủy' ? <FaRegFrown size={18} /> : <FaRegSmile size={18} />}
              </span>
              <div>
                <h3 className='font-bold text-gray-800 text-lg'>Đơn hàng #{purchaseOrderId.slice(0, 8)}</h3>
              </div>
            </div>
            <div className='flex items-center text-sm text-gray-500 mt-2 ml-1'>
              <FiCalendar className='mr-1' />
              {formatDate(createdAt)}
            </div>
          </div>
          <span
            className={`flex items-center px-3 py-1 rounded-full text-sm font-medium ${statusStyles[displayStatus].bg} ${statusStyles[displayStatus].text}`}
          >
            {statusStyles[displayStatus].icon}
            {displayStatus}
          </span>
        </div>

        <div className='flex justify-between items-center pt-3 border-t border-gray-100 mt-2'>
          <div className='flex items-center text-sm text-gray-600'>
            <FiShoppingBag className='mr-1' />
            {totalItems} sản phẩm
          </div>
          <div className='font-bold text-lg bg-clip-text text-transparent bg-gradient-to-r from-purple-500 to-pink-500'>
            {formatNumber(amount)}₫
          </div>
        </div>

        <div className='mt-5 flex space-x-3'>
          <button
            onClick={handleViewDetails}
            className='flex items-center text-sm py-2 px-4 border border-gray-200 rounded-lg text-gray-600 hover:bg-gray-50 hover:border-gray-300 transition-colors'
          >
            <FiEye className='mr-2' />
            Xem chi tiết
          </button>
          <button
            className={`flex items-center text-sm py-2 px-4 rounded-lg text-white font-medium bg-gradient-to-r ${statusStyles[displayStatus].gradient} shadow-md hover:shadow-lg transition-all`}
          >
            <FiMapPin className='mr-2' />
            {displayStatus === 'Đã hủy' ? 'Xem lý do' : 'Theo dõi đơn'}
          </button>
        </div>
      </div>

      {showModal && (
        <div className='fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50'>
          <div className='bg-white p-8 rounded-2xl max-w-2xl w-full h-auto max-h-[90vh] shadow-2xl border border-gray-100'>
            <div className='flex justify-between items-center mb-6'>
              <h3 className='text-2xl font-bold text-gray-800 bg-clip-text text-transparent bg-gradient-to-r from-blue-600 to-indigo-600'>
                Chi tiết đơn hàng #{purchaseOrderId.slice(0, 8)}
              </h3>
              <button
                onClick={() => setShowModal(false)}
                className='text-gray-600 hover:text-gray-800 transition-colors'
              >
                <FiXCircle size={28} />
              </button>
            </div>

            {/* Rest of the modal content remains unchanged */}
            {loadingDetails ? (
              <div className='text-center py-12'>
                <div className='animate-spin rounded-full h-10 w-10 border-t-2 border-b-2 border-blue-600 mx-auto'></div>
                <p className='text-gray-600 mt-4'>Đang tải chi tiết...</p>
              </div>
            ) : errorDetails ? (
              <div className='text-center py-12'>
                <FaBoxOpen className='mx-auto text-gray-300 text-5xl mb-4' />
                <p className='text-gray-600 text-lg'>{errorDetails}</p>
              </div>
            ) : productDetails.length > 0 ? (
              <div className='space-y-8'>
                {/* Thông tin đơn hàng */}
                <div className='bg-gradient-to-r from-gray-50 to-gray-100 p-6 rounded-xl shadow-sm'>
                  <h4 className='text-lg font-semibold text-gray-700 mb-4'>Thông tin đơn hàng</h4>
                  <div className='grid grid-cols-2 gap-4 text-sm'>
                    <div>
                      <p className='text-gray-500'>Mã đơn hàng</p>
                      <p className='font-medium text-gray-800'>{purchaseOrderId.slice(0, 8)}</p>
                    </div>
                    <div>
                      <p className='text-gray-500'>Ngày đặt hàng</p>
                      <p className='font-medium text-gray-800'>{formatDate(createdAt)}</p>
                    </div>
                    <div>
                      <p className='text-gray-500'>Trạng thái</p>
                      <p
                        className={`font-medium ${statusStyles[displayStatus].text} bg-gradient-to-r ${statusStyles[displayStatus].gradient} bg-clip-text text-transparent`}
                      >
                        {displayStatus}
                      </p>
                    </div>
                    <div>
                      <p className='text-gray-500'>Tổng số lượng</p>
                      <p className='font-medium text-gray-800'>{totalItems} sản phẩm</p>
                    </div>
                  </div>
                </div>

                {/* Chi tiết sản phẩm */}
                <div>
                  <h4 className='text-lg font-semibold text-gray-700 mb-4'>Sản phẩm</h4>
                  {productDetails.map((detail) => {
                    const orderDetail = orderDetails.find((od) => od.productDetailId === detail.productDetailId)
                    const currentImageIndex = currentImageIndices[detail.productDetailId] || 0
                    const imageCount = detail.images?.length || 0
                    const itemTotal = orderDetail ? detail.discountPrice * orderDetail.quantity : 0

                    return (
                      <div
                        key={detail.productDetailId}
                        className='bg-white p-6 rounded-xl shadow-sm mb-4 border border-gray-100'
                      >
                        <div className='flex flex-col md:flex-row gap-6'>
                          {/* Carousel ảnh */}
                          <div className='w-full md:w-1/3 relative'>
                            {detail.images && detail.images.length > 0 ? (
                              <div className='relative'>
                                <img
                                  src={detail.images[currentImageIndex].url}
                                  alt={detail.name || 'Sản phẩm'}
                                  className='w-full h-48 object-cover rounded-lg'
                                />
                                {imageCount > 1 && (
                                  <>
                                    <button
                                      onClick={() => handlePrevImage(detail.productDetailId, imageCount)}
                                      className='absolute left-2 top-1/2 transform -translate-y-1/2 bg-gray-800 bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-all'
                                    >
                                      <FiChevronLeft size={20} />
                                    </button>
                                    <button
                                      onClick={() => handleNextImage(detail.productDetailId, imageCount)}
                                      className='absolute right-2 top-1/2 transform -translate-y-1/2 bg-gray-800 bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-all'
                                    >
                                      <FiChevronRight size={20} />
                                    </button>
                                    <div className='absolute bottom-2 left-1/2 transform -translate-x-1/2 flex space-x-2'>
                                      {detail.images.map((_, index) => (
                                        <span
                                          key={index}
                                          className={`w-2 h-2 rounded-full ${
                                            index === currentImageIndex ? 'bg-blue-600' : 'bg-gray-300'
                                          }`}
                                        ></span>
                                      ))}
                                    </div>
                                  </>
                                )}
                              </div>
                            ) : (
                              <div className='w-full h-48 bg-gray-200 rounded-lg flex items-center justify-center'>
                                <FiShoppingBag className='text-gray-400 text-3xl' />
                              </div>
                            )}
                          </div>

                          {/* Thông tin sản phẩm */}
                          <div className='flex-1'>
                            <p className='text-lg font-medium text-gray-800'>{detail.name || 'Không có tên'}</p>
                            <div className='grid grid-cols-2 gap-4 mt-4 text-sm'>
                              <div>
                                <p className='text-gray-500'>Giá gốc</p>
                                <p className='font-medium text-gray-800'>{formatNumber(detail.originalPrice)}₫</p>
                              </div>
                              <div>
                                <p className='text-gray-500'>Giá khuyến mãi</p>
                                <p className='font-medium text-gray-800'>{formatNumber(detail.discountPrice)}₫</p>
                              </div>
                              {detail.size && (
                                <div>
                                  <p className='text-gray-500'>Kích thước</p>
                                  <p className='font-medium text-gray-800'>{detail.size.name}</p>
                                </div>
                              )}
                              {detail.color && (
                                <div>
                                  <p className='text-gray-500 flex items-center'>
                                    Màu sắc
                                    <span
                                      className='ml-2 w-4 h-4 rounded-full'
                                      style={{ backgroundColor: detail.color.hex }}
                                    ></span>
                                  </p>
                                  <p className='font-medium text-gray-800'>{detail.color.name}</p>
                                </div>
                              )}
                            </div>
                            <div className='mt-4 text-right'>
                              {detail.originalPrice !== detail.discountPrice && (
                                <>
                                  <p className='text-sm text-gray-600'>
                                    {orderDetail?.quantity || 1} x {formatNumber(detail.discountPrice)}₫ ={' '}
                                    {formatNumber(itemTotal)}₫
                                  </p>
                                  <p className='text-sm text-green-600'>
                                    Tiết kiệm{' '}
                                    {formatNumber(
                                      (detail.originalPrice - detail.discountPrice) * (orderDetail?.quantity || 1)
                                    )}
                                    ₫
                                  </p>
                                </>
                              )}
                              <p className='text-blue-600 font-semibold'>{formatNumber(itemTotal)}₫</p>
                            </div>
                          </div>
                        </div>
                      </div>
                    )
                  })}
                </div>
              </div>
            ) : (
              <div className='text-center py-12'>
                <FaBoxOpen className='mx-auto text-gray-300 text-5xl mb-4' />
                <p className='text-gray-600 text-lg'>Không có sản phẩm trong đơn hàng này</p>
              </div>
            )}
          </div>
        </div>
      )}
    </>
  )
}

export default OrderItem
