import { http } from '~/utils/http'
import { AxiosError } from 'axios'
import { Category } from '~/types/category'

interface ApiResponse<T> {
  message: string
  result: T
  code: number
}

export const apiGetCategories = async (): Promise<Category[]> => {
  try {
    const response = await http.get<ApiResponse<Category[]>>('/categories')
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error fetching categories:', axiosError.message)
    throw axiosError
  }
}

export const getCategoryById = async (categoryId: string): Promise<Category> => {
  try {
    const response = await http.get<ApiResponse<Category>>(`/categories/${categoryId}`)
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    console.error(`Error fetching category ${categoryId}:`, axiosError.message)
    throw axiosError
  }
}

export const createCategory = async (category: Omit<Category, 'category_id'>): Promise<Category> => {
  try {
    const response = await http.post<ApiResponse<Category>>('/categories', category)
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error creating category:', axiosError.message)
    throw axiosError
  }
}
export const apiGetAllCategories = async ({
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
    const response = await http.get('/categories/admin/get-categories', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
export const apiUpdateCategory = async (category: Category): Promise<ApiResponse<Category>> => {
  try {
    const response = await http.put<ApiResponse<Category>>(`/categories/${category.categoryId}`, category)
    return response.data
  } catch (error) {
    const axiosError = error as AxiosError
    console.error('Error creating category:', axiosError.message)
    throw axiosError
  }
}
export const apiDeleteCategory = async (category: Category): Promise<ApiResponse<Category> | any> => {
  try {
    const response = await http.delete<ApiResponse<Category>>(`/categories/${category.categoryId}`)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
