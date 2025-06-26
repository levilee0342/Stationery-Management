import { createAsyncThunk } from '@reduxjs/toolkit'
import { apiGetCategories } from '~/api/category'
import { Category } from '~/types/category'

export const fetchCategories = createAsyncThunk<Category[]>('category/fetchAll', async (_, { rejectWithValue }) => {
  try {
    const categories = await apiGetCategories()
    return categories
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
  } catch (error) {
    return rejectWithValue('Lỗi khi lấy danh sách danh mục')
  }
})
