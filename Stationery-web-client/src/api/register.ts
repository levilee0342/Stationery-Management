import { http } from '~/utils/http'
import { AxiosError } from 'axios'

interface RegisterData {
  firstName: string
  lastName: string
  email: string
  password: string
  confirmPassword: string
}

interface OtpData {
  email: string
  otp: number
}

interface ResendOtpData {
  email: string
}

interface ApiResponse<T> {
  success: boolean
  data?: T
  error?: string
}

const apiRegister = async (data: RegisterData): Promise<ApiResponse<string>> => {
  try {
    const response = await http.post('/users/register', data)
    return { success: true, data: response.data }
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return { success: false, error: error.response.data.message || 'Registration failed' }
    }
    return { success: false, error: (error as Error).message || 'Unknown error' }
  }
}

const apiVerifyOtp = async (data: OtpData): Promise<ApiResponse<any>> => {
  try {
    const response = await http.post('/users/verify-otp', data)
    return { success: true, data: response.data }
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return { success: false, error: error.response.data.message || 'OTP verification failed' }
    }
    return { success: false, error: (error as Error).message || 'Unknown error' }
  }
}

const apiResendOtp = async (data: ResendOtpData): Promise<ApiResponse<string>> => {
  try {
    const response = await http.post('/users/resend-otp', data)
    return { success: true, data: response.data }
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return { success: false, error: error.response.data.message || 'Failed to resend OTP' }
    }
    return { success: false, error: (error as Error).message || 'Unknown error' }
  }
}

export { apiRegister, apiVerifyOtp, apiResendOtp }
