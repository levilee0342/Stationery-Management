import { AxiosError } from 'axios'
import { http } from '~/utils/http'

export interface SupportEmailPayload {
  fullName: string
  emailAddress: string
  subject: string
  message: string
}

export const sendSupportEmail = async (payload: SupportEmailPayload): Promise<string> => {
  try {
    const response = await http.post<string>('/email/support', payload)
    return response.data
  } catch (error) {
    const err = error as AxiosError
    throw err.response?.data || 'An error occurred while sending the support email.'
  }
}
