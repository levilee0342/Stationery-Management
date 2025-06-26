// cái này xài mấy cái biến global khác
import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { UserPromotion } from '~/types/promotion'
import { fetchMyVocher } from '../actions/promotion'

const initialState: { myVouchers: UserPromotion[]; isLoading: boolean; isError: boolean } = {
  isLoading: false,
  isError: false,
  myVouchers: []
}
const voucherSlice = createSlice({
  name: 'my-voucher',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(fetchMyVocher.pending, (state) => {
      state.isLoading = false
    })
    builder.addCase(fetchMyVocher.fulfilled, (state, action: PayloadAction<UserPromotion[]>) => {
      state.isLoading = false
      state.myVouchers = action.payload
    })
    builder.addCase(fetchMyVocher.rejected, (state) => {
      state.isLoading = false
      state.isError = true
      state.myVouchers = []
    })
  }
})
const voucherReducer = voucherSlice.reducer
const voucherActions = voucherSlice.actions
export { voucherReducer, voucherActions }
