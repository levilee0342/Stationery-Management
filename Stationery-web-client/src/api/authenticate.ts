import { http } from '~/utils/http'
import { AxiosError } from 'axios'
import { ForgotPasswordRequest, ResetPasswordRequest } from '~/types/auth'

declare global {
  interface Window {
    gapi: any
  }
}

interface ApiResponse<T> {
  message: string
  result: T
}

const apiLogin = async ({ email, password }: { email: string; password: string }) => {
  try {
    const response = await http.post('/auth/login', { email, password })
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Trả về dữ liệu lỗi từ server (nếu có)
    }
    return (error as Error).message // Tránh lỗi undefined
  }
}

const apiLogout = async ({ token }: { token: string | null }) => {
  try {
    const response = await http.post('/auth/logout', null, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiForgotPassword = async (data: ForgotPasswordRequest) => {
  try {
    const response = await http.post<ApiResponse<string>>('/auth/forgot-password', data)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiResetPassword = async (otpData: Omit<ResetPasswordRequest, 'newPassword'>, newPassword: string) => {
  try {
    const response = await http.post<ApiResponse<any>>(
      `/auth/reset-password?newPassword=${encodeURIComponent(newPassword)}`,
      {
        email: otpData.email,
        otp: otpData.otp
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

export { apiLogin, apiLogout, apiForgotPassword, apiResetPassword }
