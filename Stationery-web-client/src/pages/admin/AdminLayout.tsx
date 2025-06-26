import SideBar from './components/SideBar'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { Outlet, useNavigate } from 'react-router-dom'
import Header from './components/Header'
import { useEffect } from 'react'
import { fetchCategories } from '~/store/actions/category'

const AdminLayout = () => {
  const { isExpanded, isHovered, isMobileOpen } = useAppSelector((state) => state.sideBar)
  const { userData } = useAppSelector((state) => state.user)
  const dispatch = useAppDispatch()
  const navigate = useNavigate()

  useEffect(() => {
    if (Number(userData?.role.roleId) !== 111) {
      navigate('/')
    }
    dispatch(fetchCategories())
  }, [])
  return (
    <div className='min-h-screen xl:flex'>
      <div>
        <SideBar />
      </div>
      <div
        className={`flex-1 transition-all duration-300 ease-in-out ${
          isExpanded || isHovered ? 'lg:ml-[290px]' : 'lg:ml-[90px]'
        } ${isMobileOpen ? 'ml-0' : ''}`}
      >
        <Header />
        <div className='p-4 mx-auto max-w-(--breakpoint-2xl) md:p-6'>
          <Outlet />
        </div>
      </div>
    </div>
  )
}

export default AdminLayout
