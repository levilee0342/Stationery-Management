import { Route, Routes } from 'react-router-dom'
import { useAppSelector } from './hooks/redux'
import { adminPaths, publicPaths, userPaths } from './constance/paths'
import Login from './pages/public/auth/Login'
import Register from './pages/public/auth/Register'
import DashBoard from './pages/admin/dashBoard/DashBoard'
import AdminLayout from './pages/admin/AdminLayout'
import UserManagement from './pages/admin/user/UserManagement'
import ProductsManagement from './pages/admin/product/ProductsManagement'
import Modal from './components/model/Modal'
import AuthPage from './pages/public/auth/AuthPage'
import VoucherManagement from './pages/admin/voucher/VoucherManagement'

// import Hero from '~/sections/Hero'
import ProductPage from './pages/public/product/productpage/ProductPage'
import ProductDetail from './pages/public/product/productdetail/ProductDetails'
import PaymentConfirmation from './pages/user/paymentconfirmation/PaymentConfirmation'
import Contact from './pages/public/contact/Contact'
import PublicLayout from './pages/public/PublicLayout'
import Home from './pages/home/Home'
import UserProfile from './pages/user/profile/UserProfile'
import About from './pages/public/about/About'
import Service from './pages/public/service/Service'
import { departmentPath } from './constance/paths'
import DepartmentLayout from './pages/department/DepartmentLayout'
import Dashboard from './pages/department/dashboard/Dashboard'
import PurchaseGuidePage from './pages/public/support/PurchaseGuidePage'
import WarrantyPolicyPage from './pages/public/support/WarrantyPolicyPage'
import NotFound from './pages/public/notfound/NotFound'
import ReturnExchangePolicy from './pages/public/support/ReturnExchangePolicy'
import ShippingPolicyPage from './pages/public/support/ShippingPolicyPage'
import CategoriesProductManagement from './pages/admin/category/CategoriesProductManagement'
import ColorManagement from './pages/admin/color/ColorManagement'
import SizeManagement from './pages/admin/size/SizeManagement'
import OrderManagement from './pages/admin/order/OrderManagement'
import ProductDepartment from './pages/department/product/ProductDepartment'
import CreateRequest from './pages/department/request/CreateRequest'
import InvoiceAndReport from './pages/department/invoice/InvoiceAndReport'
import NotificationPage from './pages/department/notification/NotificationPage'
import SupportPage from './pages/department/support/SupportPage'

function App() {
  const { childrenModal, isOpenModal } = useAppSelector((state) => state.modal)

  return (
    <div className='bg-baseBackground h-screen text-baseText'>
      <Modal isOpen={isOpenModal}>{childrenModal}</Modal>
      <Routes>
        <Route path={publicPaths.PUBLIC} element={<PublicLayout />}>
          <Route path='/auth' element={<AuthPage />} />
          <Route path={publicPaths.LOGIN} element={<Login />} />
          <Route path={publicPaths.REGISTER} element={<Register />} />
          <Route index element={<Home />} />
          <Route path={publicPaths.PRODUCT} element={<ProductPage />} />
          <Route path={userPaths.PROFILE} element={<UserProfile />} />
          <Route path={publicPaths.PRODUCT_DETAIL} element={<ProductDetail />} />
          <Route path='/products/payment-confirmation' element={<PaymentConfirmation />} />
          <Route path={publicPaths.CONTACT} element={<Contact />} />
          <Route path={publicPaths.SERVICE} element={<Service />} />
          <Route path={publicPaths.ABOUT} element={<About />} />
          <Route path={publicPaths.SUPPORT_PURCHASE_GUIDE} element={<PurchaseGuidePage />} />
          <Route path={publicPaths.SUPPORT_WARRANTY_POLICY} element={<WarrantyPolicyPage />} />
          <Route path={publicPaths.RETURN_EXCHANGE_POLICY} element={<ReturnExchangePolicy />} />
          <Route path={publicPaths.SHIPPING_POLICY} element={<ShippingPolicyPage />} />
        </Route>
        <Route path={departmentPath.DASHBOARD} element={<DepartmentLayout />}>
          <Route index element={<Dashboard />} />
          <Route path={departmentPath.PRODUCT} element={<ProductDepartment />} />
          <Route path={departmentPath.CREATE_REQUEST} element={<CreateRequest />} />
          <Route path={departmentPath.INVOICE} element={<InvoiceAndReport />} />
          <Route path={departmentPath.NOTIFICATION} element={<NotificationPage />} />
          <Route path={departmentPath.SUPPORT} element={<SupportPage />} />
          <Route path={departmentPath.PRODUCT_DETAIL} element={<ProductDetail />} />
        </Route>
        <Route path={adminPaths.ADMIN} element={<AdminLayout />}>
          <Route index element={<DashBoard />} />
          <Route path={adminPaths.USER} element={<UserManagement />} />
          <Route path={adminPaths.PRODUCT_CATEGORY} element={<CategoriesProductManagement />} />
          <Route path={adminPaths.COLOR} element={<ColorManagement />} />
          <Route path={adminPaths.SIZE} element={<SizeManagement />} />
          <Route path={adminPaths.PRODUCT} element={<ProductsManagement />} />
          <Route path={adminPaths.ORDER} element={<OrderManagement />} />
          <Route path={adminPaths.VOUCHER} element={<VoucherManagement />} />
        </Route>
        <Route path='*' element={<NotFound />} />
      </Routes>
    </div>
  )
}

export default App
