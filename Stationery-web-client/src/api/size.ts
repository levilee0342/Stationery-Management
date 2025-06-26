import { http } from '~/utils/http'
import { AxiosError } from 'axios'
import { Size } from '~/types/product'

const apiGetAllSizes = async () => {
  try {
    const response = await http.get('/sizes')
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return (error as Error).message // Avoid undefined error
  }
}

export const apiGetAllSizeAdmin = async ({
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
    const config = {
      params,
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.get('/sizes/admin/get-sizes', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
// Removed duplicate export of apiDeleteColor to resolve declaration conflict
export const apiUpdateSize = async (color: Size) => {
  try {
    const response = await http.put(`/sizes/${color.sizeId}`, color)
    return response.data
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error creating category:', axiosError.message)
    throw axiosError
  }
}
export const apiCreateSize = async (color: Size) => {
  try {
    const response = await http.post('/sizes', color)
    return response.data
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error creating category:', axiosError.message)
    throw axiosError
  }
}

export const apiDeleteSize = async (sizeId: string) => {
  try {
    const response = await http.delete(`/sizes/${sizeId}`)
    return response.data
  } catch (error) {
    const axiosError = error as AxiosError
    return axiosError.response?.data
  }
}
export { apiGetAllSizes }
