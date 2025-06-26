// cái này xài mấy cái biến global khác
import { createSlice, PayloadAction } from '@reduxjs/toolkit'

interface SideBarState {
  isExpanded: boolean
  isMobileOpen: boolean
  isHovered: boolean
  activeItem: string | null
  openSubmenu: string | null
}
const initialState: SideBarState = {
  isExpanded: false,
  isMobileOpen: false,
  isHovered: false,
  activeItem: null,
  openSubmenu: null
}
const sideBarSlice = createSlice({
  name: 'sideBar',
  initialState,
  reducers: {
    toggleSidebar(state) {
      state.isExpanded = !state.isExpanded
    },
    toggleMobileSidebar(state) {
      state.isMobileOpen = !state.isMobileOpen
    },
    setIsHovered(state, action: PayloadAction<boolean>) {
      state.isHovered = action.payload
    },
    setActiveItem(state, action: PayloadAction<string | null>) {
      state.activeItem = action.payload
    },
    toggleSubmenu(state, action: PayloadAction<string | null>) {
      state.openSubmenu = state.openSubmenu === action.payload ? null : action.payload
    }
  }
})
const sideBarReducer = sideBarSlice.reducer
const sideBarActions = sideBarSlice.actions
export { sideBarReducer, sideBarActions }
