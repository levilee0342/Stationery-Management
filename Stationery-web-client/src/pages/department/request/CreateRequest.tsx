import React, { useEffect, useState, useCallback, useMemo } from 'react'
import { FaPlusCircle, FaEdit } from 'react-icons/fa'
import { MdCancel } from 'react-icons/md'
import RequestForm from '../components/RequestForm'
import OrderTracking from './OrderTracking'
import {
  apiGetAllProductsWithDefaultPD,
  apiFetchColorSizeProductDetail,
  apiGetProductDetailsByProductId
} from '~/api/product'
import {
  apiGetUserOrders,
  apiGetProductDetailsByOrder,
  apiCreateOrderWithPayment,
  apiCancelOrder,
  apiEditPurchaseOrder
} from '~/api/orders'
import { useAppSelector } from '~/hooks/redux'
import { FetchColor, Size, ProductDetail, ProductDetailResponse, ListProductDetail } from '~/types/product'
import { PurchaseOrderResponse } from '~/types/order'
import { showToastSuccess } from '~/utils/alert'
import { debounce } from 'lodash'
import CancelOrderModal from '../components/CancelOrderModal'

interface ExtendedOrderDetail {
  productDetailId: string
  quantity: number
  color: string
  size: string
  name: string
  productId: string
}

export interface ExtendedPurchaseOrderResponse extends PurchaseOrderResponse {
  orderDetails: ExtendedOrderDetail[]
  addressId: string
}

interface RequestFormData {
  productId: string
  color: string
  size: string
  quantity: number
  notes: string
  deliveryDate: string
  addressId: string
  voucherId: string
}

interface Voucher {
  voucherId: string
  code: string
  discount: number
}

const STATUS = {
  PENDING: 'PENDING',
  PROCESSING: 'PROCESSING',
  SHIPPING: 'SHIPPING',
  COMPLETED: 'COMPLETED',
  CANCELED: 'CANCELED'
} as const

const CreateRequest: React.FC = () => {
  const { accessToken } = useAppSelector((state) => state.user)
  const [showForm, setShowForm] = useState(false)
  const [isEditMode, setIsEditMode] = useState(false)
  const [isModalOpen, setModalOpen] = useState(false)
  const [editRequestId, setEditRequestId] = useState<string | null>(null)
  const [cancelOrderId, setCancelOrderId] = useState<string | null>(null) // State for order to cancel
  const [formData, setFormData] = useState<RequestFormData>({
    productId: '',
    color: '',
    size: '',
    quantity: 1,
    notes: '',
    deliveryDate: '',
    addressId: '',
    voucherId: ''
  })
  const [productSearch, setProductSearch] = useState('')
  const [requests, setRequests] = useState<ExtendedPurchaseOrderResponse[]>([])
  const [activeTab, setActiveTab] = useState<'processing' | 'shipping' | 'completed' | 'canceled'>('completed')
  const [products, setProducts] = useState<ListProductDetail[]>([])
  const [colors, setColors] = useState<FetchColor[]>([])
  const [sizes, setSizes] = useState<Size[]>([])
  const [vouchers, setVouchers] = useState<Voucher[]>([])
  const [loading, setLoading] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [productDetailsMap, setProductDetailsMap] = useState<{
    [orderId: string]: ExtendedOrderDetail[]
  }>({})

  const memoizedColors = useMemo(() => colors, [colors])
  const memoizedSizes = useMemo(() => sizes, [sizes])

  useEffect(() => {
    setVouchers([
      { voucherId: 'voucher1', code: 'DISCOUNT10', discount: 10 },
      { voucherId: 'voucher2', code: 'DISCOUNT20', discount: 20 }
    ])
  }, [])

  const debouncedSetProductSearch = useCallback(
    debounce((value: string) => setProductSearch(value), 300),
    []
  )

  useEffect(() => {
    if (!accessToken) {
      setError('Vui lòng đăng nhập để xem đơn hàng.')
      return
    }

    const fetchData = async () => {
      setLoading(true)
      try {
        const productResponse = await apiGetAllProductsWithDefaultPD({
          page: '1',
          limit: '1000',
          sortBy: 'soldQuantity'
        })

        if (productResponse.code !== 200 || !productResponse.result?.content) {
          throw new Error('Không thể tải danh sách sản phẩm')
        }

        const productsWithDetails = await Promise.all(
          productResponse.result.content.map(async (item: any) => {
            const detailsResponse = await apiGetProductDetailsByProductId({ productId: item.productId })
            const productDetails = detailsResponse.code === 200 ? detailsResponse.result : []

            return {
              productId: item.productId,
              name: item.name,
              slug: item.slug || '',
              category: item.category || { categoryId: '', categoryName: '' },
              minPrice: item.minPrice || 0,
              soldQuantity: item.soldQuantity || 0,
              quantity: item.quantity || 0,
              totalRating: item.totalRating || 0,
              productDetails: productDetails.map((detail: any) => ({
                productDetailId: detail.productDetailId,
                name: detail.name || item.name,
                slug: detail.slug || item.slug,
                originalPrice: detail.originalPrice || 0,
                stockQuantity: detail.stockQuantity || 0,
                soldQuantity: detail.soldQuantity || 0,
                discountPrice: detail.discountPrice || 0,
                size: detail.size || null,
                color: detail.color || null,
                totalRating: detail.totalRating || 0,
                description: detail.description || '',
                productPromotions: detail.productPromotions || [],
                images: detail.images || [],
                productId: detail.productId || item.productId,
                createdAt: detail.createdAt || item.createdAt || ''
              })),
              fetchColor: item.fetchColor || [],
              img: item.img,
              createdAt: item.createdAt || ''
            }
          })
        )

        setProducts(productsWithDetails)

        const orderResponse = await apiGetUserOrders({ accessToken, status: 'ALL' })
        if (orderResponse.code !== 200 || !orderResponse.result) {
          throw new Error('Không thể tải danh sách đơn hàng')
        }

        const fetchedOrders: ExtendedPurchaseOrderResponse[] = orderResponse.result.map((order: any) => ({
          purchaseOrderId: order.purchaseOrderId,
          createdAt: order.createdAt || null,
          pdfUrl: order.pdfUrl || null,
          productPromotionId: order.productPromotionId || null,
          userPromotionId: order.userPromotionId || null,
          status: order.status || STATUS.PENDING,
          amount: order.amount || 0,
          note: order.note || null,
          cancelReason: order.cancelReason || null,
          expiredTime: order.expiredTime || null,
          addressId: order.addressId || '',
          orderDetails:
            order.orderDetails?.map((detail: any) => ({
              productDetailId: detail.productDetailId,
              quantity: detail.quantity || 1,
              color: '_',
              size: '_',
              name: '_',
              productId: detail.productId || ''
            })) || []
        }))

        setRequests(fetchedOrders)

        const detailsMap: { [orderId: string]: ExtendedOrderDetail[] } = {}
        await Promise.all(
          fetchedOrders.map(async (order) => {
            try {
              const detailRes: ProductDetailResponse = await apiGetProductDetailsByOrder({
                purchaseOrderId: order.purchaseOrderId,
                accessToken
              })
              if (detailRes.code === 200) {
                detailsMap[order.purchaseOrderId] = (detailRes.result as ProductDetail[]).map((p) => ({
                  productDetailId: p.productDetailId || '_',
                  name: p.name || '_',
                  color: p.color?.colorId || '_',
                  size: p.size?.sizeId || '_',
                  quantity: order.orderDetails.find((d) => d.productDetailId === p.productDetailId)?.quantity || 1,
                  productId: p.productId || ''
                }))
              } else {
                detailsMap[order.purchaseOrderId] = order.orderDetails.map((d) => ({
                  productDetailId: d.productDetailId,
                  name: '_',
                  color: '_',
                  size: '_',
                  quantity: d.quantity,
                  productId: d.productId || ''
                }))
              }
            } catch (err) {
              console.error(`Lỗi khi lấy chi tiết cho đơn hàng ${order.purchaseOrderId}:`, err)
              detailsMap[order.purchaseOrderId] = order.orderDetails.map((d) => ({
                productDetailId: d.productDetailId,
                name: '_',
                color: '_',
                size: '_',
                quantity: d.quantity,
                productId: d.productId || ''
              }))
            }
          })
        )
        setProductDetailsMap(detailsMap)
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Lỗi khi tải dữ liệu')
      } finally {
        setLoading(false)
      }
    }

    fetchData()
  }, [accessToken])

  useEffect(() => {
    if (!formData.productId || isEditMode) {
      return
    }

    const fetchColorSize = async () => {
      setLoading(true)
      try {
        const selectedProduct = products.find((p) => p.productId === formData.productId)
        if (!selectedProduct) {
          throw new Error('Không tìm thấy sản phẩm')
        }

        if (!selectedProduct.productDetails.length) {
          throw new Error('Sản phẩm không có chi tiết sản phẩm')
        }

        const selectedProductDetail = selectedProduct.productDetails[0]
        if (!selectedProductDetail?.slug) {
          throw new Error('Không tìm thấy slug của chi tiết sản phẩm')
        }

        const response = await apiFetchColorSizeProductDetail(selectedProductDetail.slug)
        if (response.code !== 200 || !response.result) {
          throw new Error('Không thể tải danh sách màu sắc và kích cỡ')
        }

        const colors = response.result
          .filter((item: any) => item && item.colorId && item.hex)
          .map((item: any) => ({
            colorId: item.colorId,
            hex: item.hex,
            slug: item.slug || '',
            name: item.name || item.colorId
          }))
          .reduce((unique: FetchColor[], color: FetchColor) => {
            return unique.some((c) => c.colorId === color.colorId) ? unique : [...unique, color]
          }, [])

        const sizes = response.result
          .flatMap((item: any) =>
            item && item.sizes && Array.isArray(item.sizes)
              ? item.sizes
                  .filter((size: any) => size && typeof size === 'object' && size.sizeId)
                  .map((size: any) => ({
                    sizeId: size.sizeId,
                    name: size.name || size.sizeId,
                    priority: size.priority || 0
                  }))
              : []
          )
          .reduce((unique: Size[], size: any) => {
            return unique.some((s) => s.sizeId === size.sizeId) ? unique : [...unique, size]
          }, [])

        setColors(colors)
        setSizes(sizes)
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Lỗi khi tải danh sách màu sắc và kích cỡ')
        setColors([])
        setSizes([])
      } finally {
        setLoading(false)
      }
    }

    fetchColorSize()
  }, [formData.productId, isEditMode, products])

  const handleEditRequest = async (request: ExtendedPurchaseOrderResponse) => {
    if (request.status !== STATUS.PENDING) {
      setError('Chỉ có thể chỉnh sửa các yêu cầu đang chờ xử lý.')
      return
    }

    const orderDetail = productDetailsMap[request.purchaseOrderId]?.[0]
    if (!orderDetail || !orderDetail.productDetailId) {
      setError('Không tìm thấy chi tiết đơn hàng hoặc ID chi tiết sản phẩm không hợp lệ.')
      return
    }

    const selectedProduct = products.find((p) =>
      p.productDetails.some((detail) => detail.productDetailId === orderDetail.productDetailId)
    )
    if (!selectedProduct) {
      setError('Không tìm thấy sản phẩm được chọn.')
      return
    }

    const selectedProductDetail = selectedProduct.productDetails.find(
      (detail) => detail.productDetailId === orderDetail.productDetailId
    )
    if (!selectedProductDetail) {
      setError('Không tìm thấy chi tiết sản phẩm được chọn.')
      return
    }

    try {
      setLoading(true)
      if (!selectedProductDetail.slug) {
        throw new Error('Không tìm thấy slug của chi tiết sản phẩm')
      }

      const response = await apiFetchColorSizeProductDetail(selectedProductDetail.slug)
      if (response.code !== 200 || !response.result) {
        throw new Error('Không thể tải danh sách màu sắc và kích cỡ')
      }

      const colors = response.result
        .filter((item: any) => item && item.colorId && item.hex)
        .map((item: any) => ({
          colorId: item.colorId,
          hex: item.hex,
          slug: item.slug || '',
          name: item.name || item.colorId
        }))
        .reduce((unique: FetchColor[], color: FetchColor) => {
          return unique.some((c) => c.colorId === color.colorId) ? unique : [...unique, color]
        }, [])

      const sizes = response.result
        .flatMap((item: any) =>
          item && item.sizes && Array.isArray(item.sizes)
            ? item.sizes
                .filter((size: any) => size && typeof size === 'object' && size.sizeId)
                .map((size: any) => ({
                  sizeId: size.sizeId,
                  name: size.name || size.sizeId,
                  priority: size.priority || 0
                }))
            : []
        )
        .reduce((unique: Size[], size: any) => {
          return unique.some((s) => s.sizeId === size.sizeId) ? unique : [...unique, size]
        }, [])

      setColors(colors)
      setSizes(sizes)

      setFormData({
        productId: selectedProduct.productId,
        color: orderDetail.color !== '_' ? orderDetail.color : selectedProductDetail.color?.colorId || '',
        size: orderDetail.size !== '_' ? orderDetail.size : selectedProductDetail.size?.sizeId || '',
        quantity: orderDetail.quantity,
        notes: request.note || '',
        deliveryDate: request.expiredTime ? new Date(request.expiredTime).toISOString().split('T')[0] : '',
        addressId: request.addressId || '',
        voucherId: request.userPromotionId || ''
      })
      setEditRequestId(request.purchaseOrderId)
      setIsEditMode(true)
      setShowForm(true)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Lỗi khi tải danh sách màu sắc và kích cỡ')
      setColors([])
      setSizes([])
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!accessToken) {
      setError('Vui lòng đăng nhập để tiếp tục')
      return
    }

    if (!formData.productId || formData.quantity < 1) {
      setError('Vui lòng chọn sản phẩm và số lượng hợp lệ')
      return
    }

    if (!formData.deliveryDate || !formData.addressId) {
      setError('Vui lòng chọn ngày giao hàng và địa chỉ giao hàng')
      return
    }

    try {
      const selectedProduct = products.find((p) => p.productId === formData.productId)
      if (!selectedProduct) throw new Error('Sản phẩm không hợp lệ')

      const deliveryDate = new Date(formData.deliveryDate)
      if (isNaN(deliveryDate.getTime())) {
        throw new Error('Ngày giao hàng không hợp lệ')
      }

      const now = new Date()
      if (deliveryDate < now) {
        throw new Error('Ngày giao hàng không thể là ngày trong quá khứ')
      }

      const response = await apiGetProductDetailsByProductId({ productId: formData.productId })
      const productDetails: ProductDetail[] = response.result
      const selectedProductDetail = productDetails.find((detail) => {
        const colorMatch = !formData.color ? detail.color === null : detail.color?.colorId === formData.color
        const sizeMatch = !formData.size ? detail.size === null : detail.size?.sizeId === formData.size
        return colorMatch && sizeMatch
      })

      if (!selectedProductDetail) {
        setError('Không tìm thấy sản phẩm với màu sắc và kích cỡ đã chọn')
        return
      }

      if (selectedProductDetail.stockQuantity && formData.quantity > selectedProductDetail.stockQuantity) {
        setError(`Số lượng vượt quá tồn kho: Chỉ có ${selectedProductDetail.stockQuantity} sản phẩm còn lại`)
        return
      }

      let amount = selectedProductDetail.discountPrice * formData.quantity
      let productPromotionId: string | null = null
      if (formData.voucherId) {
        const selectedVoucher = vouchers.find((v) => v.voucherId === formData.voucherId)
        if (selectedVoucher) {
          const discountAmount = (selectedVoucher.discount / 100) * amount
          amount = Math.max(0, amount - discountAmount)
          productPromotionId = selectedProductDetail.productPromotions?.[0]?.productPromotionId || formData.voucherId
        }
      }

      const orderDetails = [
        {
          productDetailId: selectedProductDetail.productDetailId,
          quantity: formData.quantity,
          productPromotionId
        }
      ]

      const notes = formData.notes
        ? `${formData.notes} | Ngày giao hàng mong muốn: ${formData.deliveryDate}`
        : `Ngày giao hàng mong muốn: ${formData.deliveryDate}`

      const localDateTime = `${formData.deliveryDate}T00:00:00`

      setIsSubmitting(true)
      let responseOrder
      if (isEditMode && editRequestId) {
        responseOrder = await apiEditPurchaseOrder({
          purchaseOrderId: editRequestId,
          orderDetails,
          userPromotionId: formData.voucherId || null,
          addressId: formData.addressId,
          amount,
          note: notes,
          expiredTime: localDateTime,
          accessToken
        })
      } else {
        responseOrder = await apiCreateOrderWithPayment({
          orderDetails,
          userPromotionId: formData.voucherId || null,
          addressId: formData.addressId,
          amount,
          note: notes,
          expiredTime: localDateTime,
          accessToken
        })
      }

      if (responseOrder.code === 200) {
        showToastSuccess(isEditMode ? 'Yêu cầu đã được cập nhật thành công' : 'Yêu cầu đã được gửi thành công')

        const newRequest: ExtendedPurchaseOrderResponse = {
          purchaseOrderId: isEditMode && editRequestId ? editRequestId : responseOrder.result.purchaseOrderId,
          createdAt: isEditMode
            ? requests.find((r) => r.purchaseOrderId === editRequestId)?.createdAt || new Date().toISOString()
            : new Date().toISOString(),
          pdfUrl: null,
          productPromotionId: null,
          userPromotionId: formData.voucherId || null,
          status: STATUS.PENDING,
          amount,
          note: notes,
          cancelReason: null,
          expiredTime: deliveryDate.toISOString(),
          addressId: formData.addressId,
          orderDetails: [
            {
              productDetailId: selectedProductDetail.productDetailId,
              quantity: formData.quantity,
              color: formData.color,
              size: formData.size,
              name: selectedProduct.name,
              productId: formData.productId
            }
          ]
        }

        setRequests((prev) =>
          isEditMode && editRequestId
            ? prev.map((request) => (request.purchaseOrderId === editRequestId ? newRequest : request))
            : [...prev, newRequest]
        )
        setProductDetailsMap((prev) => ({
          ...prev,
          [newRequest.purchaseOrderId]: newRequest.orderDetails
        }))
        resetForm()
      } else {
        console.error('API Error:', responseOrder) // Log the full response
        setError(
          responseOrder.message ||
            (isEditMode ? 'Không thể cập nhật yêu cầu. Vui lòng thử lại.' : 'Không thể gửi yêu cầu. Vùi lòng thử lại.')
        )
      }
    } catch (err: any) {
      console.error('Submit Error:', err) // Log the error
      setError(err.message || (isEditMode ? 'Lỗi khi cập nhật yêu cầu' : 'Lỗi khi gửi yêu cầu'))
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleCancelOrder = async (cancelReason: string) => {
    if (!cancelOrderId) {
      setError('Không tìm thấy ID đơn hàng để hủy')
      return
    }

    try {
      setLoading(true)
      const res = await apiCancelOrder({ purchaseOrderId: cancelOrderId, accessToken, cancelReason })
      if (res.code === 200) {
        showToastSuccess('Đơn hàng đã được hủy')
        setRequests((prev) =>
          prev.map((request) =>
            request.purchaseOrderId === cancelOrderId ? { ...request, status: STATUS.CANCELED, cancelReason } : request
          )
        )
      } else {
        setError(res.message || 'Lỗi khi hủy đơn')
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Lỗi khi hủy đơn')
    } finally {
      setModalOpen(false)
      setCancelOrderId(null)
      setLoading(false)
    }
  }

  const handleOpenCancelModal = (purchaseOrderId: string) => {
    setCancelOrderId(purchaseOrderId)
    setModalOpen(true)
  }

  const resetForm = () => {
    setFormData({
      productId: '',
      color: '',
      size: '',
      quantity: 1,
      notes: '',
      deliveryDate: '',
      addressId: '',
      voucherId: ''
    })
    setProductSearch('')
    setColors([])
    setSizes([])
    setShowForm(false)
    setIsEditMode(false)
    setEditRequestId(null)
    setError(null)
  }

  if (loading) {
    return (
      <div className='min-h-screen bg-gradient-to-br from-blue-50 to-gray-100 flex justify-center items-center'>
        <div className='animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-600' />
      </div>
    )
  }

  if (error) {
    return (
      <div className='min-h-screen bg-gradient-to-br from-blue-50 to-gray-100 p-10'>
        <p className='text-red-500'>{error}</p>
        <button
          onClick={() => setError(null)}
          className='mt-4 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700'
        >
          Thử lại
        </button>
      </div>
    )
  }

  return (
    <div className='min-h-screen bg-gradient-to-br from-blue-50 to-gray-100 p-10'>
      <div className='max-w-7xl mx-auto'>
        <div className='flex items-center justify-between mb-8'>
          <h1 className='text-3xl font-bold text-blue-800 ml-2'>Tạo yêu cầu</h1>
          <button
            onClick={() => {
              setShowForm(true)
              setIsEditMode(false)
              setFormData({
                productId: '',
                color: '',
                size: '',
                quantity: 1,
                notes: '',
                deliveryDate: '',
                addressId: '',
                voucherId: ''
              })
              setProductSearch('')
              setColors([])
              setSizes([])
              setError(null)
            }}
            className='flex items-center gap-2 px-5 py-2.5 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 shadow-md'
          >
            <FaPlusCircle size={20} />
            Tạo yêu cầu mới
          </button>
        </div>

        {showForm && (
          <RequestForm
            products={products}
            vouchers={vouchers}
            formData={formData}
            productSearch={productSearch}
            setFormData={setFormData}
            setProductSearch={debouncedSetProductSearch}
            onClose={resetForm}
            isEditMode={isEditMode}
            colors={memoizedColors}
            sizes={memoizedSizes}
            onSubmit={handleSubmit}
          />
        )}

        <div className='bg-white rounded-xl shadow-lg p-8 mt-8'>
          <h2 className='text-2xl font-semibold text-gray-800 mb-6'>Yêu cầu đang chờ xử lý</h2>
          <div className='overflow-x-auto'>
            <table className='w-full text-left'>
              <thead>
                <tr className='bg-gradient-to-r from-blue-50 to-blue-100 text-blue-800'>
                  <th className='p-4 text-sm font-semibold'>Sản phẩm</th>
                  <th className='p-4 text-sm font-semibold'>Màu sắc</th>
                  <th className='p-4 text-sm font-semibold'>Kích cỡ</th>
                  <th className='p-4 text-sm font-semibold'>Số lượng</th>
                  <th className='p-4 text-sm font-semibold'>Trạng thái</th>
                  <th className='p-4 text-sm font-semibold'>Ngày tạo</th>
                  <th className='p-4 text-sm font-semibold'>Ngày giao hàng</th>
                  <th className='p-4 text-sm font-semibold'>Hành động</th>
                </tr>
              </thead>
              <tbody>
                {requests
                  .filter((request) => request.status === STATUS.PENDING)
                  .map((request) => {
                    const details = productDetailsMap[request.purchaseOrderId] || [
                      {
                        productDetailId: '_',
                        name: '_',
                        color: '_',
                        size: '_',
                        quantity: 1,
                        productId: ''
                      }
                    ]
                    return (
                      <tr key={request.purchaseOrderId} className='border-b hover:bg-blue-50/50'>
                        <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.name).join(', ')}</td>
                        <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.color).join(', ')}</td>
                        <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.size).join(', ')}</td>
                        <td className='p-4 text-sm text-gray-700'>{details.map((d) => d.quantity).join(', ')}</td>
                        <td className='p-4 text-sm'>
                          <span className='px-3 py-1 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800'>
                            {request.status}
                          </span>
                        </td>
                        <td className='p-4 text-sm text-gray-700'>
                          {request.createdAt
                            ? new Date(request.createdAt).toLocaleDateString('vi-VN') +
                              ' ' +
                              new Date(request.createdAt).toLocaleTimeString('vi-VN', {
                                hour: '2-digit',
                                minute: '2-digit',
                                hour12: false
                              })
                            : '_'}
                        </td>
                        <td className='p-4 text-sm text-gray-700'>
                          {request.expiredTime
                            ? new Date(request.expiredTime).toLocaleDateString('vi-VN') +
                              ' ' +
                              new Date(request.expiredTime).toLocaleTimeString('vi-VN', {
                                hour: '2-digit',
                                minute: '2-digit',
                                hour12: false
                              })
                            : '_'}
                        </td>
                        <td className='p-4 text-sm flex gap-2'>
                          <button
                            onClick={() => handleEditRequest(request)}
                            className='bg-yellow-500 text-white p-2 rounded-lg hover:bg-yellow-600'
                            aria-label='Chỉnh sửa yêu cầu'
                            disabled={request.status !== STATUS.PENDING}
                          >
                            <FaEdit size={16} />
                          </button>
                          <button
                            onClick={() => handleOpenCancelModal(request.purchaseOrderId)}
                            className='bg-red-500 text-white p-2 rounded-lg hover:bg-red-600'
                            aria-label='Hủy yêu cầu'
                            disabled={request.status !== STATUS.PENDING} // Only allow cancel for PENDING orders
                          >
                            <MdCancel size={16} />
                          </button>
                        </td>
                      </tr>
                    )
                  })}
              </tbody>
            </table>
            {requests.filter((request) => request.status === STATUS.PENDING).length === 0 && (
              <p className='text-center text-gray-500 mt-4'>Không có yêu cầu đang chờ xử lý.</p>
            )}
          </div>
        </div>

        <OrderTracking
          requests={requests}
          productDetailsMap={productDetailsMap}
          activeTab={activeTab}
          setActiveTab={setActiveTab}
        />

        <CancelOrderModal
          isOpen={isModalOpen}
          onClose={() => {
            setModalOpen(false)
            setCancelOrderId(null)
          }}
          onConfirm={handleCancelOrder}
        />
      </div>
    </div>
  )
}

export default CreateRequest
