import { AxiosError } from 'axios'
import { UpdatePromotion, VoucherFormData } from '~/types/promotion'
import { http } from '~/utils/http'

const apiGetMyVoucher = async ({ accessToken }: { accessToken: string }) => {
  try {
    const configs = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.get('/user-promotions', configs)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}

const apiGetAllProductPromotionPagination = async ({
  page,
  limit,
  search,
  accessToken
}: {
  page: string
  limit: string
  search?: string
  accessToken: string
}) => {
  try {
    const params = {
      page,
      limit,
      search
    }
    const configs = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      params
    }
    const response = await http.get('/promotions/admin/get-product-promotions', configs)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}

const apiGetAllUsertPromotionPagination = async ({
  page,
  limit,
  search,
  accessToken
}: {
  page: string
  limit: string
  search?: string
  accessToken: string
}) => {
  try {
    const params = {
      page,
      limit,
      search
    }
    const configs = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      params
    }
    const response = await http.get('/user-promotions/admin/get-user-promotions', configs)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
const apiGetAllPromotionPagination = async ({
  page,
  limit,
  search,
  accessToken
}: {
  page: string
  limit: string
  search?: string
  accessToken: string
}) => {
  try {
    const params = {
      page,
      limit,
      search
    }
    const configs = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      },
      params
    }
    const response = await http.get('/promotions/all-promotions', configs)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}

const apiCreatePromotion = async (data: VoucherFormData) => {
  try {
    const response = await http.post('/promotions', data)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
const apiUpdatePromotion = async (data: UpdatePromotion) => {
  try {
    const response = await http.put('/promotions/update', data)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
export {
  apiGetMyVoucher,
  apiGetAllUsertPromotionPagination,
  apiGetAllProductPromotionPagination,
  apiGetAllPromotionPagination,
  apiCreatePromotion,
  apiUpdatePromotion
}
