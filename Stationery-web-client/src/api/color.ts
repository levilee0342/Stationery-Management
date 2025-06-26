import { http } from '~/utils/http'
import { AxiosError } from 'axios'
import { Color } from '~/types/product'

const apiGetAllColors = async () => {
  try {
    const response = await http.get('/colors')
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return (error as Error).message // Avoid undefined error
  }
}
export const apiGetAllColorAdmin = async ({
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
    const response = await http.get('/colors/admin/get-colors', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
// Removed duplicate export of apiDeleteColor to resolve declaration conflict
export const apiUpdateColor = async (color: Color) => {
  try {
    const response = await http.put(`/colors/${color.colorId}`, color)
    return response.data
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error creating category:', axiosError.message)
    throw axiosError
  }
}
export const apiCreateColor = async (color: Color) => {
  try {
    const response = await http.post('/colors', color)
    return response.data
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error creating category:', axiosError.message)
    throw axiosError
  }
}

export const apiDeleteColor = async (colorId: string) => {
  try {
    const response = await http.delete(`/colors/${colorId}`)
    console.log('Color deleted successfully:', response.data)
    return response.data
  } catch (error) {
    const axiosError = error as AxiosError
    return axiosError.response?.data
  }
}
export { apiGetAllColors }
