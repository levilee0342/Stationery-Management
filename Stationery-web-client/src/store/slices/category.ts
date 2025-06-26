// cái này xài mấy cái biến global khác
import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { Category } from '~/types/category'
import { fetchCategories } from '../actions/category'

const initialState: { categories: Category[]; isLoading: boolean; isError: boolean } = {
  isLoading: false,
  isError: false,
  categories: []
}
const categorySlice = createSlice({
  name: 'user',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder.addCase(fetchCategories.pending, (state) => {
      state.isLoading = false
    })
    builder.addCase(fetchCategories.fulfilled, (state, action: PayloadAction<Category[]>) => {
      state.isLoading = false
      state.categories = action.payload
    })
    builder.addCase(fetchCategories.rejected, (state) => {
      state.isLoading = false
      state.isError = true
      state.categories = []
    })
  }
})
const categoryReducer = categorySlice.reducer
const categoryActions = categorySlice.actions
export { categoryReducer, categoryActions }
