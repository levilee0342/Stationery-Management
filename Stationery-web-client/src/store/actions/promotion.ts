import { createAsyncThunk } from '@reduxjs/toolkit'
import { apiGetMyVoucher } from '~/api/promotion'
import { UserPromotion } from '~/types/promotion'

export const fetchMyVocher = createAsyncThunk<UserPromotion[], { accessToken: string }>(
  'my-voucher/fetchAll',
  async ({ accessToken }, { rejectWithValue }) => {
    try {
      const promotions = await apiGetMyVoucher({ accessToken })
      return promotions.result
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      return rejectWithValue('Error fetching promotions')
    }
  }
)
