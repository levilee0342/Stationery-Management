// cái này xài mấy cái biến global khác
import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface SideBarState {
  childrenModal: React.ReactNode
  isOpenModal: boolean
}
const initialState: SideBarState = {
  childrenModal: null,
  isOpenModal: false
}
const sideBarSlice = createSlice({
  name: 'modal',
  initialState,
  reducers: {
    toggleModal(state, action: PayloadAction<{ childrenModal: React.ReactNode; isOpenModal: boolean }>) {
      state.childrenModal = action.payload.childrenModal
      state.isOpenModal = action.payload.isOpenModal
    }
  }
})
const modalReducer = sideBarSlice.reducer
const modalActions = sideBarSlice.actions
export { modalReducer, modalActions }
