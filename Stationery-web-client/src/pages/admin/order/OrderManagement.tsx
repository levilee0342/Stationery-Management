import { useState, useEffect, useMemo } from 'react'
import { FaTimes, FaFilter, FaEye, FaCheck, FaInfoCircle } from 'react-icons/fa'
import Select from 'react-select'
import Swal from 'sweetalert2'
import {
  apiConfirmOrder,
  apiGetNonPendingOrders,
  apiGetPendingOrders,
  apiGetProductDetailsByOrder,
  apiUpdateOrderStatus
} from '~/api/orders'
import { apiGetUserById } from '~/api/users'
import { useAppSelector } from '~/hooks/redux'
import { ApiResponse, PurchaseOrderResponse } from '~/types/order'
import { ProductDetailResponse } from '~/types/product'

const OrderManagement = () => {
  // State for orders, filters, and loading
  const [pendingOrders, setPendingOrders] = useState<PurchaseOrderResponse[]>([])
  const [confirmedOrders, setConfirmedOrders] = useState<PurchaseOrderResponse[]>([])
  const [pendingFilterRole, setPendingFilterRole] = useState<string>('All')
  const [confirmedFilterRole, setConfirmedFilterRole] = useState<string>('All') // Default: User
  const [confirmedFilterStatus, setConfirmedFilterStatus] = useState<string>('PROCESSING') // Default: Processing
  const [pendingPage, setPendingPage] = useState<number>(0)
  const [confirmedPage, setConfirmedPage] = useState<number>(0)
  const [pendingTotalPages, setPendingTotalPages] = useState<number>(1)
  const [confirmedTotalPages, setConfirmedTotalPages] = useState<number>(1)
  const [userMap, setUserMap] = useState<Record<string, { name: string; roleName: string }>>({})
  const [isPendingLoading, setIsPendingLoading] = useState<boolean>(true)
  const [isConfirmedLoading, setIsConfirmedLoading] = useState<boolean>(true)
  const { userData, accessToken } = useAppSelector((state) => state.user)

  // Check if user is admin
  useEffect(() => {
    if (!userData || userData.role?.roleId !== '111') {
      Swal.fire('Error', 'You do not have permission to access the order management page!', 'error').then(() => {
        window.location.href = '/'
      })
    }
  }, [userData])

  // Fetch user info
  const fetchUserInfo = async (userIds: string[]) => {
    try {
      const uniqueUserIds = [...new Set(userIds)].filter((id) => !userMap[id])
      if (uniqueUserIds.length === 0) return

      const userPromises = uniqueUserIds.map((userId) =>
        apiGetUserById({ token: accessToken, userId }).then((res) => ({
          userId,
          name: res.result?.name || 'User',
          roleName: res.result?.role?.roleName || 'Unknown'
        }))
      )
      const users = await Promise.all(userPromises)
      const newUserMap = users.reduce(
        (acc, user) => ({
          ...acc,
          [user.userId]: { name: user.name, roleName: user.roleName }
        }),
        {}
      )
      setUserMap((prev) => ({ ...prev, ...newUserMap }))
    } catch (error) {
      console.error('Failed to fetch user info:', error)
    }
  }

  // Fetch pending orders
  const fetchPendingOrders = async () => {
    if (!accessToken) return
    setIsPendingLoading(true)
    try {
      const roleFilter =
        pendingFilterRole === 'User'
          ? ['User', 'Admin']
          : pendingFilterRole === 'Department'
            ? ['Department']
            : undefined
      const response: ApiResponse = await apiGetPendingOrders({
        accessToken,
        roleName: roleFilter,
        page: pendingPage,
        size: 6
      })
      const orders = response.result.content || []
      setPendingOrders(orders)
      setPendingTotalPages(response.result.page?.totalPages || 1)
      const userIds = orders.map((order) => order.userId).filter((id): id is string => !!id)
      if (userIds.length > 0) {
        await fetchUserInfo(userIds)
      }
    } catch (error) {
      Swal.fire('Error', 'Failed to fetch pending orders', 'error')
    } finally {
      setIsPendingLoading(false)
    }
  }

  // Fetch non-pending orders
  const fetchNonPendingOrders = async () => {
    if (!accessToken) return
    setIsConfirmedLoading(true)
    try {
      const roleFilter =
        confirmedFilterRole === 'User'
          ? ['User', 'Admin']
          : confirmedFilterRole === 'Department'
            ? ['Department']
            : undefined
      const statusFilter = confirmedFilterStatus === 'All' ? undefined : [confirmedFilterStatus]
      const response: ApiResponse = await apiGetNonPendingOrders({
        accessToken,
        roleName: roleFilter,
        status: statusFilter,
        page: confirmedPage,
        size: 6
      })
      const orders = response.result.content || []
      setConfirmedOrders(orders)
      setConfirmedTotalPages(response.result.page?.totalPages || 1)
      const userIds = orders.map((order) => order.userId).filter((id): id is string => !!id)
      if (userIds.length > 0) {
        await fetchUserInfo(userIds)
      }
    } catch (error) {
      Swal.fire('Error', 'Failed to fetch confirmed orders', 'error')
    } finally {
      setIsConfirmedLoading(false)
    }
  }

  // Initial fetch
  useEffect(() => {
    if (userData?.role?.roleId === '111' && accessToken) {
      fetchPendingOrders()
      fetchNonPendingOrders()
    }
  }, [userData, accessToken])

  // Refetch when pending filters change
  useEffect(() => {
    if (userData?.role?.roleId === '111') {
      fetchPendingOrders()
    }
  }, [pendingFilterRole, pendingPage])

  // Refetch when confirmed filters change
  useEffect(() => {
    if (userData?.role?.roleId === '111') {
      fetchNonPendingOrders()
    }
  }, [confirmedFilterRole, confirmedFilterStatus, confirmedPage])

  // Memoize orders
  const memoizedPendingOrders = useMemo(() => pendingOrders, [pendingOrders])
  const memoizedConfirmedOrders = useMemo(() => confirmedOrders, [confirmedOrders])

  // Format date-time
  const formatDateTime = (dateTime: string | null) => {
    if (!dateTime) return 'N/A'
    const date = new Date(dateTime)
    return date.toLocaleString('en-GB', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  // Status options for react-select
  const statusOptions = [
    { value: 'PROCESSING', label: 'Processing', color: '#f59e0b' },
    { value: 'SHIPPING', label: 'Shipping', color: '#3b82f6' },
    { value: 'COMPLETED', label: 'Completed', color: '#10b981' },
    { value: 'CANCELED', label: 'Canceled', color: '#ef4444' }
  ]

  // Filter status options
  const filterStatusOptions = [...statusOptions]

  // Handle confirm order
  const handleConfirmOrder = async (id: string) => {
    Swal.fire({
      title: 'Confirm Order?',
      text: 'Are you sure you want to confirm this order?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: 'Confirm',
      cancelButtonText: 'Cancel'
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await apiConfirmOrder({ purchaseOrderId: id, accessToken })
          setPendingOrders((prev) => prev.filter((order) => order.purchaseOrderId !== id))
          fetchNonPendingOrders()
          Swal.fire('Confirmed!', 'The order has been confirmed.', 'success')
        } catch (error) {
          Swal.fire('Error', 'Failed to confirm order', 'error')
        }
      }
    })
  }

  // Handle cancel order
  const handleCancelOrder = async (id: string) => {
    Swal.fire({
      title: 'Cancel Order?',
      text: 'Please provide the reason for cancellation',
      icon: 'warning',
      input: 'text',
      inputPlaceholder: 'Enter cancellation reason',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Cancel Order',
      cancelButtonText: 'Back',
      inputValidator: (value) => {
        if (!value) {
          return 'You must provide a cancellation reason!'
        }
      }
    }).then(async (result) => {
      if (result.isConfirmed) {
        try {
          await apiUpdateOrderStatus({
            purchaseOrderId: id,
            status: 'CANCELED',
            cancelReason: result.value,
            accessToken
          })
          setPendingOrders((prev) => prev.filter((order) => order.purchaseOrderId !== id))
          fetchNonPendingOrders()
          Swal.fire('Canceled!', 'The order has been canceled.', 'success')
        } catch (error) {
          Swal.fire('Error', 'Failed to cancel order', 'error')
        }
      }
    })
  }

  // Handle update status
  const handleUpdateStatus = async (id: string, selectedOption: { value: string; label: string } | null) => {
    if (!selectedOption) return
    const newStatus = selectedOption.value as 'PROCESSING' | 'SHIPPING' | 'COMPLETED' | 'CANCELED'
    let cancelReason: string | undefined
    if (newStatus === 'CANCELED') {
      const { value, isConfirmed } = await Swal.fire({
        title: 'Cancel Order?',
        text: 'Please provide the reason for cancellation',
        icon: 'warning',
        input: 'text',
        inputPlaceholder: 'Enter cancellation reason',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Cancel Order',
        cancelButtonText: 'Back',
        inputValidator: (value) => {
          if (!value) {
            return 'You must provide a cancellation reason!'
          }
        }
      })
      if (!isConfirmed) return
      cancelReason = value
    } else {
      const result = await Swal.fire({
        title: 'Update Status?',
        text: `Do you want to update the status to "${selectedOption.label}"?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Update',
        cancelButtonText: 'Cancel'
      })
      if (!result.isConfirmed) return
    }
    try {
      await apiUpdateOrderStatus({
        purchaseOrderId: id,
        status: newStatus,
        cancelReason,
        accessToken
      })
      setConfirmedOrders((prev) =>
        prev.map((order) =>
          order.purchaseOrderId === id
            ? { ...order, status: newStatus, cancelReason: cancelReason || order.cancelReason }
            : order
        )
      )
      Swal.fire('Updated!', 'The order status has been updated.', 'success')
    } catch (error) {
      Swal.fire('Error', 'Failed to update order status', 'error')
    }
  }

  // Handle view order details
  const handleViewDetails = async (purchaseOrderId: string) => {
    try {
      const details: ProductDetailResponse = await apiGetProductDetailsByOrder({
        purchaseOrderId,
        accessToken
      })
      const detailContent = details.result
        .map(
          (detail) =>
            `<tr>
              <td class="border px-2 py-1"><img src="${detail.images || 'https://via.placeholder.com/40?text=Product'}" alt="${detail.productName}" class="w-10 h-10 object-cover" /></td>
              <td class="border px-2 py-1">${detail.name}</td>
              <td class="border px-2 py-1">${detail.soldQuantity}</td>
              <td class="border px-2 py-1">${detail.originalPrice.toLocaleString('en-GB')} GBP</td>
              <td class="border px-2 py-1">${detail.color || 'N/A'}</td>
              <td class="border px-2 py-1">${detail.size || 'N/A'}</td>
            </tr>`
        )
        .join('')
      Swal.fire({
        title: 'Order Details',
        html: `
          <table class="w-full border-collapse border text-sm">
            <thead>
              <tr class="bg-blue-600 text-white">
                <th class="border px-2 py-1">Image</th>
                <th class="border px-2 py-1">Product Name</th>
                <th class="border px-2 py-1">Quantity</th>
                <th class="border px-2 py-1">Price</th>
                <th class="border px-2 py-1">Color</th>
                <th class="border px-2 py-1">Size</th>
              </tr>
            </thead>
            <tbody>
              ${detailContent}
            </tbody>
          </table>
        `,
        showConfirmButton: true,
        confirmButtonText: 'Close',
        width: '700px'
      })
    } catch (error) {
      Swal.fire('Error', 'Failed to fetch order details', 'error')
    }
  }

  // Handle view note or cancel reason
  const handleViewText = (title: string, text: string | null) => {
    Swal.fire({
      title,
      text: text || 'No content available',
      icon: 'info',
      confirmButtonText: 'Close'
    })
  }

  // Custom styles for react-select
  const customStyles = {
    control: (provided: any) => ({
      ...provided,
      borderRadius: '0.375rem',
      borderColor: '#d1d5db',
      boxShadow: 'none',
      minHeight: '28px',
      height: '28px',
      fontSize: '0.875rem',
      '&:hover': { borderColor: '#2563eb' }
    }),
    valueContainer: (provided: any) => ({
      ...provided,
      padding: '0 6px'
    }),
    singleValue: (provided: any, state: any) => ({
      ...provided,
      color: state.data.color || '#111827',
      fontSize: '0.875rem'
    }),
    indicatorsContainer: (provided: any) => ({
      ...provided,
      height: '28px'
    }),
    option: (provided: any, state: any) => ({
      ...provided,
      backgroundColor: state.isSelected ? state.data.color : state.isFocused ? `${state.data.color}20` : 'white',
      color: state.isSelected ? 'white' : 'black',
      fontSize: '0.875rem',
      '&:hover': {
        backgroundColor: `${state.data.color}20`
      }
    }),
    menu: (provided: any) => ({
      ...provided,
      zIndex: 9999,
      marginTop: '2px',
      borderRadius: '0.375rem',
      boxShadow: '0 2px 8px rgba(0,0,0,0.1)'
    }),
    menuPortal: (provided: any) => ({
      ...provided,
      zIndex: 9999
    })
  }

  // Skeleton loading component
  const SkeletonRow = () => (
    <tr className='border-b border-gray-200'>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-20 animate-pulse'></div>
      </td>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-16 animate-pulse'></div>
      </td>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-24 animate-pulse'></div>
      </td>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-28 animate-pulse'></div>
      </td>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-16 animate-pulse'></div>
      </td>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-8 animate-pulse'></div>
      </td>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-8 animate-pulse'></div>
      </td>
      <td className='px-3 py-2'>
        <div className='h-3 bg-gray-200 rounded w-20 animate-pulse'></div>
      </td>
    </tr>
  )

  return (
    <div className='p-4 max-w-7xl mx-auto bg-gray-50 min-h-screen'>
      {/* Header */}
      <h1 className='text-2xl font-bold text-gray-900 mb-6'>Order Management</h1>

      {/* Table 1: Pending Orders */}
      <div className='mb-8 bg-white rounded-lg shadow-sm p-4'>
        <h2 className='text-lg font-semibold text-gray-800 mb-4'>Pending Orders</h2>
        {/* Filters */}
        <div className='flex gap-3 mb-4'>
          <div className='flex items-center gap-2'>
            <FaFilter className='text-gray-500 text-sm' />
            <select
              className='px-3 py-1 text-sm bg-gray-50 border border-gray-200 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500'
              value={pendingFilterRole}
              onChange={(e) => {
                setPendingFilterRole(e.target.value)
                setPendingPage(0)
              }}
            >
              <option value='All'>All</option>
              <option value='User'>User</option>
              <option value='Department'>Department</option>
            </select>
          </div>
        </div>
        <div className='overflow-x-auto'>
          <table className='w-full border-collapse bg-white text-sm'>
            <thead>
              <tr className='bg-blue-600 text-white text-left'>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Order ID</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Customer Type</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Customer Name</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Created At</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Total</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Note</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Actions</th>
              </tr>
            </thead>
            <tbody>
              {isPendingLoading
                ? Array.from({ length: 6 }).map((_, index) => <SkeletonRow key={index} />)
                : memoizedPendingOrders?.map((order) => (
                    <tr key={order.purchaseOrderId} className='border-b border-gray-200 hover:bg-gray-50 transition'>
                      <td className='px-3 py-2 font-mono text-xs'>{order.purchaseOrderId?.slice(0, 10)}</td>
                      <td className='px-3 py-2 text-xs'>
                        {['User', 'Admin'].includes(userMap[order.userId]?.roleName || '')
                          ? 'User'
                          : userMap[order.userId]?.roleName || 'N/A'}
                      </td>
                      <td className='px-3 py-2 font-medium text-xs'>{userMap[order.userId]?.name || 'N/A'}</td>
                      <td className='px-3 py-2 text-xs'>{formatDateTime(order.createdAt)}</td>
                      <td className='px-3 py-2 text-xs'>{order.amount?.toLocaleString('en-GB')} GBP</td>
                      <td className='px-3 py-2'>
                        <button
                          className='text-blue-600 hover:text-blue-800'
                          onClick={() => handleViewText('Order Note', order.note)}
                          title='View Note'
                        >
                          <FaInfoCircle size={12} />
                        </button>
                      </td>
                      <td className='px-3 py-2 flex gap-2'>
                        <button
                          className='bg-green-600 text-white p-1 rounded-full hover:bg-green-700 transition-colors'
                          onClick={() => handleConfirmOrder(order.purchaseOrderId)}
                          title='Confirm'
                        >
                          <FaCheck size={10} />
                        </button>
                        <button
                          className='bg-red-600 text-white p-1 rounded-full hover:bg-red-700 transition-colors'
                          onClick={() => handleCancelOrder(order.purchaseOrderId)}
                          title='Cancel'
                        >
                          <FaTimes size={10} />
                        </button>
                        <button
                          className='bg-blue-600 text-white p-1 rounded-full hover:bg-blue-700 transition-colors'
                          onClick={() => handleViewDetails(order.purchaseOrderId)}
                          title='View Details'
                        >
                          <FaEye size={10} />
                        </button>
                      </td>
                    </tr>
                  ))}
            </tbody>
          </table>
        </div>
        {/* Pagination */}
        <div className='flex justify-between items-center mt-4 text-sm'>
          <button
            className='px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-300 transition-colors'
            disabled={pendingPage === 0 || isPendingLoading}
            onClick={() => setPendingPage((prev) => prev - 1)}
          >
            Previous
          </button>
          <span className='text-gray-600'>
            Page {pendingPage + 1} of {pendingTotalPages}
          </span>
          <button
            className='px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-300 transition-colors'
            disabled={pendingPage >= pendingTotalPages - 1 || isPendingLoading}
            onClick={() => setPendingPage((prev) => prev + 1)}
          >
            Next
          </button>
        </div>
      </div>

      {/* Table 2: Confirmed Orders */}
      <div className='bg-white rounded-lg shadow-sm p-4'>
        <h2 className='text-lg font-semibold text-gray-800 mb-4'>Finishing Orders</h2>
        {/* Filters */}
        <div className='flex gap-3 mb-4'>
          <div className='flex items-center gap-2'>
            <FaFilter className='text-gray-500 text-sm' />
            <select
              className='px-3 py-1 text-sm bg-gray-50 border border-gray-200 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500'
              value={confirmedFilterRole}
              onChange={(e) => {
                setConfirmedFilterRole(e.target.value)
                setConfirmedPage(0)
              }}
            >
              <option value='All'>All</option>
              <option value='User'>User</option>
              <option value='Department'>Department</option>
            </select>
          </div>
          <div className='flex items-center gap-2'>
            <FaFilter className='text-gray-500 text-sm' />
            <Select
              options={filterStatusOptions}
              value={filterStatusOptions.find((option) => option.value === confirmedFilterStatus)}
              onChange={(selected) => {
                setConfirmedFilterStatus(selected?.value ?? 'All')
                setConfirmedPage(0)
              }}
              className='w-28 text-sm'
              styles={customStyles}
              menuPortalTarget={document.body}
            />
          </div>
        </div>
        <div className='overflow-x-auto'>
          <table className='w-full border-collapse bg-white text-sm'>
            <thead>
              <tr className='bg-blue-600 text-white text-left'>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Order ID</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Customer Type</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Customer Name</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Created At</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Total</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Note</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Cancel Reason</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Details</th>
                <th className='px-3 py-2 font-medium uppercase text-xs'>Actions</th>
              </tr>
            </thead>
            <tbody>
              {isConfirmedLoading
                ? Array.from({ length: 6 }).map((_, index) => <SkeletonRow key={index} />)
                : memoizedConfirmedOrders?.map((order) => (
                    <tr key={order.purchaseOrderId} className='border-b border-gray-200 hover:bg-gray-50 transition'>
                      <td className='px-3 py-2 font-mono text-xs'>{order.purchaseOrderId?.slice(0, 10)}</td>
                      <td className='px-3 py-2 text-xs'>
                        {['User', 'Admin'].includes(userMap[order.userId]?.roleName || '')
                          ? 'User'
                          : userMap[order.userId]?.roleName || 'N/A'}
                      </td>
                      <td className='px-3 py-2 font-medium text-xs'>{userMap[order.userId].name || 'N/A'}</td>
                      <td className='px-3 py-2 text-xs'>{formatDateTime(order.createdAt)}</td>
                      <td className='px-3 py-2 text-xs'>{order.amount?.toLocaleString('en-GB')} GBP</td>
                      <td className='px-3 py-2'>
                        <button
                          className='text-blue-600 hover:text-blue-800'
                          onClick={() => handleViewText('Order Note', order.note)}
                          title='View Note'
                        >
                          <FaInfoCircle size={12} />
                        </button>
                      </td>
                      <td className='px-3 py-2'>
                        {order.status === 'CANCELED' ? (
                          <button
                            className='text-blue-600 hover:text-blue-800'
                            onClick={() => handleViewText('Cancel Reason', order.cancelReason)}
                            title='View Cancel Reason'
                          >
                            <FaInfoCircle size={12} />
                          </button>
                        ) : (
                          <span className='text-gray-500 text-xs'>N/A</span>
                        )}
                      </td>
                      <td className='px-3 py-2'>
                        <button
                          className='bg-blue-600 text-white p-1 rounded-full hover:bg-blue-700 transition-colors'
                          onClick={() => handleViewDetails(order.purchaseOrderId)}
                          title='View Details'
                        >
                          <FaEye size={10} />
                        </button>
                      </td>
                      <td className='px-3 py-2'>
                        <Select
                          options={statusOptions}
                          value={statusOptions.find((option) => option.value === order.status)}
                          onChange={(selected) => handleUpdateStatus(order.purchaseOrderId, selected)}
                          className='w-28 text-sm'
                          styles={customStyles}
                          menuPortalTarget={document.body}
                        />
                      </td>
                    </tr>
                  ))}
            </tbody>
          </table>
        </div>
        {/* Pagination */}
        <div className='flex justify-between items-center mt-4 text-sm'>
          <button
            className='px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-300 transition-colors'
            disabled={confirmedPage === 0 || isConfirmedLoading}
            onClick={() => setConfirmedPage((prev) => prev - 1)}
          >
            Previous
          </button>
          <span className='text-gray-600'>
            Page {confirmedPage + 1} of {confirmedTotalPages}
          </span>
          <button
            className='px-3 py-1 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-300 transition-colors'
            disabled={confirmedPage >= confirmedTotalPages - 1 || isConfirmedLoading}
            onClick={() => setConfirmedPage((prev) => prev + 1)}
          >
            Next
          </button>
        </div>
      </div>
    </div>
  )
}

export default OrderManagement
