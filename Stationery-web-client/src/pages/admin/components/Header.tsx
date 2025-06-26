import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { sideBarActions } from '~/store/slices/sideBar'
import { FiMenu } from 'react-icons/fi'
import { IoClose } from 'react-icons/io5'
const Header = () => {
  const { isMobileOpen } = useAppSelector((state) => state.sideBar)
  const dispatch = useAppDispatch()
  const handleToggle = () => {
    if (window.innerWidth >= 991) {
      dispatch(sideBarActions.toggleSidebar())
    } else {
      dispatch(sideBarActions.toggleMobileSidebar())
    }
  }

  return (
    <header className='flex w-full bg-white border-gray-200 z-99999 lg:hidden'>
      <div className='flex flex-col items-center justify-between grow lg:flex-row lg:px-6'>
        <div className='flex items-center justify-between w-full gap-2 px-3 py-3 border-b border-gray-200sm:gap-4 lg:justify-normal lg:border-b-0 lg:px-0 lg:py-4'>
          <button
            className='flex items-center justify-center w-10 h-10 text-gray-500 border-gray-200 rounded-lg z-99999lg:flex lg:h-11 lg:w-11 lg:border'
            onClick={handleToggle}
            aria-label='Toggle Sidebar'
          >
            <span className='block text-base text-black'>{isMobileOpen ? <IoClose /> : <FiMenu />}</span>
          </button>
        </div>
      </div>
    </header>
  )
}

export default Header
