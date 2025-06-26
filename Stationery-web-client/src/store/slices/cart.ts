import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import { CartItem } from '~/types/cart'
import { fetchMyCart } from '../actions/cart'

interface CartState {
  myCart: CartItem[]
  isLoading: boolean
  isError: boolean
  isCartOpen: boolean
}

const initialState: CartState = {
  myCart: [],
  isLoading: false,
  isError: false,
  isCartOpen: false
}

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    toggleCart(state) {
      state.isCartOpen = !state.isCartOpen
    },
    setCartOpen(state, action: PayloadAction<boolean>) {
      state.isCartOpen = action.payload
    }
  },
  extraReducers: (builder) => {
    builder.addCase(fetchMyCart.pending, (state) => {
      state.isLoading = true
    })
    builder.addCase(fetchMyCart.fulfilled, (state, action: PayloadAction<CartItem[]>) => {
      state.isLoading = false
      state.isError = false
      state.myCart = action.payload
    })
    builder.addCase(fetchMyCart.rejected, (state) => {
      state.isLoading = false
      state.isError = true
      state.myCart = []
    })
  }
})

export const cartReducer = cartSlice.reducer
export const cartActions = cartSlice.actions
