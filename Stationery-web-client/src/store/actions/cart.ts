import { createAsyncThunk } from '@reduxjs/toolkit'
import { apiGetCartItems } from '~/api/cart'
import { CartItem } from '~/types/cart'

interface CartApiResponse {
  code: number
  message: string
  result: CartItem[]
}

export const fetchMyCart = createAsyncThunk<CartItem[], { accessToken: string }>(
  'cart/fetchMyCart',
  async ({ accessToken }, { rejectWithValue }) => {
    const res: CartApiResponse | string = await apiGetCartItems(accessToken)
    if (typeof res === 'string') return rejectWithValue(res)
    if (res.code !== 200) return rejectWithValue(res.message)
    return res.result
  }
)
