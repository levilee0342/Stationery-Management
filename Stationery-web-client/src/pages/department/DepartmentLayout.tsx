import { Outlet } from 'react-router-dom'
import Sidebar from '~/components/header/SidebarDepartments'

const DepartmentLayout = () => {
  return (
    <div>
      <Sidebar />
      <div className=''>
        <Outlet />
      </div>
    </div>
  )
}

export default DepartmentLayout
