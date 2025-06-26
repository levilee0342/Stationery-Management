// cái này xài mấy cái biến global khác
import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { fetchCurrentUser } from '../actions/user'
import { User } from '~/types/user'

interface UserState {
  userData: User | null
  accessToken: string | null
  isLoggedIn: boolean
  isLoading: boolean
  isError: boolean
}
const initialState: UserState = {
  userData: null,
  accessToken: null,
  isLoggedIn: false,
  isLoading: false,
  isError: false
}
const userSlice = createSlice({
  name: 'user',
  initialState,
  reducers: {
    login(state, action: PayloadAction<{ accessToken: string; userData: User }>) {
      state.accessToken = action.payload.accessToken
      state.userData = action.payload.userData
      state.isLoggedIn = true
    },
    logout(state) {
      state.accessToken = null
      state.userData = null
      state.isLoggedIn = false
    }
  },
  extraReducers: (builder) => {
    builder.addCase(fetchCurrentUser.pending, (state) => {
      state.isLoading = true
    })
    builder.addCase(fetchCurrentUser.fulfilled, (state, action: PayloadAction<User>) => {
      console.log('action.payload', action.payload)
      state.isLoading = false
      state.isLoggedIn = true
      state.userData = action.payload
      return state
    })
    builder.addCase(fetchCurrentUser.rejected, (state) => {
      state.isLoading = false
      state.isError = true
      state.isLoggedIn = false
      state.userData = null
      state.accessToken = null
    })
  }
})
const userReducer = userSlice.reducer
const userActions = userSlice.actions
export { userReducer, userActions }
