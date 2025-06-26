import { http } from '~/utils/http'
import { AxiosError } from 'axios'
import { Category } from '~/types/category'

interface ApiResponse<T> {
  message: string
  result: T
  code: number
}

// Hàm gọi API lấy top 10 từ khóa hot
export const apiGetTopSearchKeywords = async (): Promise<string[]> => {
  try {
    const response = await http.get<ApiResponse<string[]>>('/search/top-keywords')
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error fetching top search keywords:', axiosError.message)
    throw axiosError
  }
}

export const apiGetTopUserCategories = async (accessToken: string): Promise<Category[]> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.get<ApiResponse<Category[]>>('/search/top-user-categories', config)
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error fetching top user categories:', axiosError.message)
    throw axiosError
  }
}

export const apiGetTopHotCategories = async (): Promise<Category[]> => {
  try {
    const response = await http.get<ApiResponse<Category[]>>('/search/hot-categories')
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error fetching top hot categories:', axiosError.message)
    throw axiosError
  }
}
