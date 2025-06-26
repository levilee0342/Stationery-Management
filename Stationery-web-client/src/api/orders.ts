import { AxiosError } from 'axios'
import { ApiResponse, CreateOrderParams } from '~/types/order'
import { ProductDetailResponse } from '~/types/product'
import { http } from '~/utils/http'

const apiCreateOrderWithPayment = async ({
  orderDetails,
  userPromotionId,
  addressId,
  amount,
  note,
  expiredTime,
  accessToken
}: CreateOrderParams) => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const body = {
      orderDetails,
      userPromotionId,
      addressId,
      amount,
      note,
      expiredTime
    }
    const response = await http.post('/purchase-orders/payment-momo', body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}
const apiCreateOrderWithNoPayment = async ({
  orderDetails,
  userPromotionId,
  addressId,
  amount,
  note,
  expiredTime,
  accessToken
}: CreateOrderParams) => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const body = {
      orderDetails,
      userPromotionId,
      addressId,
      amount,
      note,
      expiredTime
    }
    const response = await http.post('/purchase-orders/payment', body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiCheckTransactionStatus = async ({
  orderId,
  accessToken,
  status
}: {
  orderId: string
  accessToken: string
  status?: number
}) => {
  try {
    const config = {
      params: { status },
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.get(`/purchase-orders/payment-momo/transaction-status/${orderId}`, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiGetUserOrders = async ({
  accessToken,
  status
}: {
  accessToken: string
  status: string
}): Promise<ApiResponse> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      params: { status }
    }
    const response = await http.get('/purchase-orders/user/orders', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

const apiGetProductDetailsByOrder = async ({
  purchaseOrderId,
  accessToken
}: {
  purchaseOrderId: string
  accessToken: string
}): Promise<ProductDetailResponse> => {
  try {
    const response = await http.get(`/purchase-orders/${purchaseOrderId}/product-details`, {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

const apiGetOrderStatusStatistics = async ({ accessToken }: { accessToken: string }): Promise<ApiResponse> => {
  try {
    const response = await http.get(`/purchase-orders/user/status-statistics`, {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

const apiCancelOrder = async ({
  purchaseOrderId,
  accessToken,
  cancelReason
}: {
  purchaseOrderId: string
  accessToken: string
  cancelReason: string
}) => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const body = {
      cancelReason
    }
    const response = await http.post(`/purchase-orders/cancel/${purchaseOrderId}`, body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiEditPurchaseOrder = async ({
  purchaseOrderId,
  orderDetails,
  userPromotionId,
  addressId,
  note,
  expiredTime,
  accessToken
}: {
  purchaseOrderId: string
} & CreateOrderParams) => {
  try {
    const response = await http.put(
      `/purchase-orders/${purchaseOrderId}`,
      {
        orderDetails,
        userPromotionId,
        addressId,
        note,
        expiredTime
      },
      {
        headers: {
          Authorization: `Bearer ${accessToken}`,
          'Content-Type': 'application/json'
        }
      }
    )
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

// Admin-specific API calls
const apiGetPendingOrders = async ({
  accessToken,
  roleName,
  page = 0,
  size = 10
}: {
  accessToken: string
  roleName?: string
  page?: number
  size?: number
}): Promise<ApiResponse> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      params: {
        roleName: roleName === 'All' ? undefined : roleName,
        page,
        size
      }
    }
    const response = await http.get('/admin/pending', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

const apiGetNonPendingOrders = async ({
  accessToken,
  roleName,
  status,
  page = 0,
  size = 10
}: {
  accessToken: string
  roleName?: string
  status?: string[]
  page?: number
  size?: number
}): Promise<ApiResponse> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      params: {
        roleName: roleName === 'All' ? undefined : roleName,
        status: status && status.length > 0 ? status : undefined,
        page,
        size
      },
      paramsSerializer: (params: Record<string, any>) => {
        const searchParams = new URLSearchParams()
        for (const key in params) {
          const value = params[key]
          if (Array.isArray(value)) {
            value.forEach((v: string) => searchParams.append('status', v))
          } else if (value !== undefined && value !== null) {
            searchParams.append(key, value.toString())
          }
        }
        return searchParams.toString()
      }
    }
    const response = await http.get('/admin/non-pending', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

const apiConfirmOrder = async ({
  purchaseOrderId,
  accessToken
}: {
  purchaseOrderId: string
  accessToken: string
}): Promise<ApiResponse> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.post(`/admin/confirm/${purchaseOrderId}`, {}, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

const apiUpdateOrderStatus = async ({
  purchaseOrderId,
  status,
  cancelReason,
  accessToken
}: {
  purchaseOrderId: string
  status: string
  cancelReason?: string
  accessToken: string
}): Promise<ApiResponse> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const body = {
      status,
      cancelReason
    }
    const response = await http.put(`/admin/update-status/${purchaseOrderId}`, body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    throw new Error((error as Error).message)
  }
}

export {
  apiCreateOrderWithPayment,
  apiCheckTransactionStatus,
  apiGetUserOrders,
  apiGetProductDetailsByOrder,
  apiGetOrderStatusStatistics,
  apiCancelOrder,
  apiEditPurchaseOrder,
  apiGetPendingOrders,
  apiGetNonPendingOrders,
  apiConfirmOrder,
  apiUpdateOrderStatus,
  apiCreateOrderWithNoPayment
}
