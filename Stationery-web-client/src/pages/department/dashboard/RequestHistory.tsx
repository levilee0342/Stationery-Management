import React, { useEffect, useState } from 'react'
import { FaInfoCircle, FaCheckCircle, FaHourglassHalf, FaTruck, FaTimesCircle } from 'react-icons/fa'
import { apiGetProductDetailsByOrder, apiGetUserOrders } from '~/api/orders'
import { useAppSelector } from '~/hooks/redux'
import { PurchaseOrderResponse } from '~/types/order'
import { ProductDetail } from '~/types/product'
import OrderProductDetailCard from '../components/OrderProductDetailCard'

const RequestHistory: React.FC = () => {
  const { accessToken } = useAppSelector((state) => state.user)
  const [orders, setOrders] = useState<PurchaseOrderResponse[]>([])
  const [selectedOrderId, setSelectedOrderId] = useState<string | null>(null)
  const [selectedDetails, setSelectedDetails] = useState<ProductDetail[]>([])
  const [showModal, setShowModal] = useState<boolean>(false)
  const [productDetailsMap, setProductDetailsMap] = useState<{
    [orderId: string]: string[]
  }>({})
  const [loading, setLoading] = useState<boolean>(true)
  const [error, setError] = useState<string>('')

  const getStatusClass = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return 'bg-green-100 text-green-700'
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-700'
      case 'SHIPPING':
        return 'bg-blue-100 text-blue-700'
      case 'CANCELED':
        return 'bg-red-100 text-red-700'
      default:
        return 'bg-gray-100 text-gray-700'
    }
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'COMPLETED':
        return <FaCheckCircle className='mr-1' />
      case 'PENDING':
        return <FaHourglassHalf className='mr-1' />
      case 'SHIPPING':
        return <FaTruck className='mr-1 text-blue-600' />
      case 'CANCELED':
        return <FaTimesCircle className='mr-1 text-red-600' />
      default:
        return null
    }
  }

  const handleOpenDetail = async (orderId: string) => {
    try {
      const res = await apiGetProductDetailsByOrder({ purchaseOrderId: orderId, accessToken })
      setSelectedDetails(res.result)
      setSelectedOrderId(orderId)
      setShowModal(true)
    } catch (err) {
      alert('Không thể tải chi tiết đơn hàng.')
    }
  }

  const handleCloseModal = () => {
    setShowModal(false)
    setSelectedDetails([])
    setSelectedOrderId(null)
  }

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        const response = await apiGetUserOrders({ accessToken, status: 'ALL' })
        const fetchedOrders = response.result || []
        // Sort orders by createdAt in descending order and take the first 7
        const sortedOrders = fetchedOrders
          .sort(
            (a: PurchaseOrderResponse, b: PurchaseOrderResponse) =>
              new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
          )
          .slice(0, 7)
        setOrders(sortedOrders)

        const detailsMap: { [orderId: string]: string[] } = {}
        await Promise.all(
          sortedOrders.map(async (order) => {
            try {
              const detailRes = await apiGetProductDetailsByOrder({
                purchaseOrderId: order.purchaseOrderId,
                accessToken
              })
              console.log('Thông tin sản phẩm từ order: ', detailRes)
              detailsMap[order.purchaseOrderId] = detailRes.result?.map((p: any) => p.name) || []
            } catch {
              detailsMap[order.purchaseOrderId] = ['Không lấy được sản phẩm']
            }
          })
        )
        setProductDetailsMap(detailsMap)
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
  }, [accessToken])

  return (
    <div className='bg-white shadow-lg rounded-xl p-6'>
      <h2 className='text-2xl font-bold text-gray-900 mb-4'>Lịch sử yêu cầu</h2>

      {loading && <p>Đang tải dữ liệu...</p>}
      {error && <p className='text-red-500 font-medium'>{error}</p>}

      {!loading && !error && (
        <>
          {/* Desktop Table */}
          <div className='hidden md:block overflow-x-auto'>
            <table className='w-full'>
              <thead className='bg-gradient-to-r from-blue-50 to-blue-100 text-gray-900'>
                <tr>
                  <th className='p-4 text-left font-bold text-sm'>Mã yêu cầu</th>
                  <th className='p-4 text-left font-bold text-sm'>Ngày tạo</th>
                  <th className='p-4 text-left font-bold text-sm'>Sản phẩm</th>
                  <th className='p-4 text-left font-bold text-sm'>Chi tiết</th>
                  <th className='p-4 text-left font-bold text-sm'>Trạng thái</th>
                </tr>
              </thead>
              <tbody>
                {orders.map((order, index) => (
                  <tr
                    key={order.purchaseOrderId}
                    className={`${
                      index % 2 === 0 ? 'bg-white' : 'bg-gray-50'
                    } hover:bg-blue-50 transition-all duration-200 animate-fade-in`}
                  >
                    <td className='p-4 text-sm font-medium text-gray-800'>
                      {`${order.purchaseOrderId.slice(0, 4)}${order.purchaseOrderId.slice(-4)}`}
                    </td>
                    <td className='p-4 text-sm font-medium text-gray-800'>
                      {new Date(order.createdAt).toLocaleDateString()}
                    </td>
                    <td className='p-4 text-sm text-gray-600'>
                      {productDetailsMap[order.purchaseOrderId]?.join(', ') || '—'}
                    </td>
                    <td className='p-4'>
                      <button
                        onClick={() => handleOpenDetail(order.purchaseOrderId)}
                        className='bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors'
                      >
                        <FaInfoCircle size={16} />
                      </button>
                    </td>
                    <td className='p-4'>
                      <span
                        className={`inline-flex items-center px-3 py-1 rounded-full text-sm font-semibold ${getStatusClass(
                          order.status
                        )} shadow-sm`}
                      >
                        {getStatusIcon(order.status)}
                        {order.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {/* Modal for Order Details */}
          {showModal && (
            <div className='fixed inset-0 bg-gray-800 bg-opacity-50 flex items-center justify-center z-50'>
              <div className='bg-white rounded-xl p-6 max-w-3xl w-full max-h-[80vh] overflow-y-auto'>
                <h3 className='text-xl font-bold text-gray-900 mb-4'>
                  Chi tiết đơn hàng #{selectedOrderId?.slice(0, 4)}
                  {selectedOrderId?.slice(-4)}
                </h3>

                {selectedDetails.length > 0 ? (
                  selectedDetails.map((detail) => {
                    const orderDetail = orders
                      .find((o) => o.purchaseOrderId === selectedOrderId)
                      ?.orderDetails?.find((od) => od.productDetailId === detail.productDetailId)

                    return (
                      <OrderProductDetailCard
                        key={detail.productDetailId}
                        detail={detail}
                        quantity={orderDetail?.quantity || 1}
                      />
                    )
                  })
                ) : (
                  <p className='text-gray-600'>Không có sản phẩm trong đơn hàng này.</p>
                )}

                <button
                  onClick={handleCloseModal}
                  className='mt-4 bg-gray-500 text-white px-4 py-2 rounded-lg hover:bg-gray-600 transition-colors'
                >
                  Đóng
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  )
}

export default RequestHistory
