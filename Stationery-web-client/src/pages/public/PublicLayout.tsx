import { Outlet } from 'react-router-dom'
import Header from '~/components/header/Header'
import Footer from '~/components/footer/Footer'

const PublicLayout: React.FC = () => {
  return (
    <div>
      <Header />
      <div className=''>
        <Outlet />
      </div>
      <Footer />
      {/* <Sidebar/> */}
    </div>
  )
}

export default PublicLayout
