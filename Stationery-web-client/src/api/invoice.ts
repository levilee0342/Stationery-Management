import { AxiosError } from 'axios'
import { http } from '~/utils/http'

interface InvoiceResponse {
  purchaseOrderId: string
  amount: number
  createdAt: string
  pdfUrl: string
}

interface MonthlyInvoiceSummaryResponse {
  userId: string
  month: number
  year: number
  totalAmount: number
  orderCount: number
}

interface MomoResponse {
  payUrl: string
  requestId: string
  // Add other fields as needed
}

interface ApiResponse<T> {
  code: number
  message: string
  result: T
}

// Fetch all invoices
export const getAllInvoices = async (accessToken: string): Promise<InvoiceResponse[]> => {
  try {
    const response = await http.get<ApiResponse<InvoiceResponse[]>>('/department-invoices/get-all-invoices', {
      headers: { Authorization: `Bearer ${accessToken}` }
    })
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    throw new Error(axiosError.response?.data?.message || 'Failed to fetch invoices')
  }
}

// Fetch current month invoice summary
export const getCurrentMonthInvoiceSummary = async (accessToken: string): Promise<MonthlyInvoiceSummaryResponse> => {
  try {
    const response = await http.get<ApiResponse<MonthlyInvoiceSummaryResponse>>(
      '/department-invoices/current-month-summary',
      {
        headers: { Authorization: `Bearer ${accessToken}` }
      }
    )
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    throw new Error(axiosError.response?.data?.message || 'Failed to fetch invoice summary')
  }
}

// Initiate MoMo payment for current invoice
export const payCurrentInvoice = async (accessToken: string): Promise<MomoResponse> => {
  try {
    const response = await http.post<ApiResponse<MomoResponse>>(
      '/department-invoices/pay-current-invoice',
      {},
      {
        headers: { Authorization: `Bearer ${accessToken}` }
      }
    )
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    throw new Error(axiosError.response?.data?.message || 'Failed to initiate payment')
  }
}

// Generate current invoice PDF
export const generateCurrentInvoice = async (accessToken: string): Promise<string> => {
  try {
    const response = await http.post<ApiResponse<string>>(
      '/department-invoices/generate-current-invoice',
      {},
      {
        headers: { Authorization: `Bearer ${accessToken}` }
      }
    )
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    throw new Error(axiosError.response?.data?.message || 'Failed to generate invoice')
  }
}

// Check overdue invoices
export const checkOverdueInvoices = async (accessToken: string): Promise<string[]> => {
  try {
    const response = await http.get<ApiResponse<string[]>>('/department-invoices/check-overdue', {
      headers: { Authorization: `Bearer ${accessToken}` }
    })
    return response.data.result
  } catch (error) {
    const axiosError = error as AxiosError
    throw new Error(axiosError.response?.data?.message || 'Failed to check overdue invoices')
  }
}
