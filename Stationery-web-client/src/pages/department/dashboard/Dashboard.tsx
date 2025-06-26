import React, { useEffect, useState } from 'react'
import { FaHandshake, FaHeadset, FaPlusCircle } from 'react-icons/fa'
import { Link, useNavigate } from 'react-router-dom'
import { useAppSelector } from '~/hooks/redux'
import OrderOverview from './OrderOverview'
import BudgetLimit from './BudgetLimit'
import RequestHistory from './RequestHistory'
import PopularProducts from './PopularProducts'
import { departmentPath } from '~/constance/paths'
import { Product } from '~/types/product'
import RequestForm from '../components/RequestForm'

interface RequestForm {
  productId: string
  color: string
  size: string
  quantity: number
  notes: string
  deliveryDate: string
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate()
  const { accessToken, userData } = useAppSelector((state) => state.user)
  const [isRequestFormOpen, setIsRequestFormOpen] = useState(false)
  const [productSearch, setProductSearch] = useState('')
  const [formData, setFormData] = useState<RequestForm>({
    productId: '',
    color: '',
    size: '',
    quantity: 1,
    notes: '',
    deliveryDate: ''
  })
  const [products, setProducts] = useState<Product[]>([])

  useEffect(() => {
    if (!accessToken) {
      navigate('/auth?mode=login')
    }
  }, [accessToken, navigate])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      console.log('Gửi yêu cầu:', formData)
      setIsRequestFormOpen(false)
      setFormData({
        productId: '',
        color: '',
        size: '',
        quantity: 1,
        notes: '',
        deliveryDate: ''
      })
      setProductSearch('')
    } catch (error) {
      console.error('Lỗi khi gửi yêu cầu:', error)
    }
  }

  const handleClose = () => {
    setIsRequestFormOpen(false)
    setFormData({
      productId: '',
      color: '',
      size: '',
      quantity: 1,
      notes: '',
      deliveryDate: ''
    })
    setProductSearch('')
  }

  return (
    <div className='min-h-screen bg-base-100 p-10 ml-16'>
      {/* Partner Information */}
      <div className='mb-6 bg-gradient-to-r from-blue-50 to-white shadow-xl rounded-xl p-6 flex flex-col sm:flex-row items-center justify-between transform hover:scale-[1.01] transition-all duration-300'>
        <div className='flex items-center mb-4 sm:mb-0'>
          <FaHandshake className='text-4xl text-blue-600 mr-4' />
          <div>
            <h2 className='text-2xl font-bold text-gray-900 tracking-tight'>
              {userData?.firstName} {userData?.lastName}
            </h2>
            <p className='text-sm text-gray-500 mt-1'>
              Mã đối tác: <span className='font-medium'>{userData?.userId}</span>
            </p>
            <p className='text-sm text-gray-500'>
              Email: <span className='font-medium'>{userData?.email}</span>
            </p>
          </div>
        </div>
        <button
          onClick={() => setIsRequestFormOpen(true)}
          className='btn bg-blue-600 hover:bg-blue-700 text-white shadow-lg rounded-lg px-6 py-3 flex items-center group transition-all duration-200 hover:shadow-xl'
        >
          <FaPlusCircle className='mr-2 text-lg group-hover:animate-pulse' />
          <span className='font-semibold'>Tạo yêu cầu mới</span>
        </button>
      </div>

      {isRequestFormOpen && (
        <RequestForm
          products={products}
          colors={[]}
          sizes={[]}
          formData={formData}
          productSearch={productSearch}
          setFormData={setFormData}
          setProductSearch={setProductSearch}
          onSubmit={handleSubmit}
          onClose={handleClose}
          isEditMode={false}
        />
      )}

      <h1 className='text-3xl font-bold text-blue-800 mb-6 ml-1'>Dashboard</h1>

      <div className='grid grid-cols-1 md:grid-cols-3 gap-6'>
        <div className='md:col-span-2'>
          <OrderOverview />
        </div>
        <div>
          <BudgetLimit />
        </div>
      </div>

      <div className='grid grid-cols-1 md:grid-cols-3 gap-6 mt-6'>
        <div className='md:col-span-2'>
          <RequestHistory />
        </div>
        <div>
          <PopularProducts setProducts={setProducts} />
          <div className='mt-6 bg-white shadow-lg rounded-lg p-6'>
            <h2 className='text-xl font-semibold text-gray-800 mb-4'>Hỗ trợ</h2>
            <p className='text-sm text-gray-600 mb-2'>Liên hệ chúng tôi nếu bạn cần hỗ trợ:</p>
            <p className='text-sm text-gray-600'>Email: support@stationeryp.com</p>
            <p className='text-sm text-gray-600'>Hotline: 0123 456 789</p>
            <Link to={departmentPath.SUPPORT} className='btn btn-outline btn-primary mt-4 flex items-center'>
              <FaHeadset className='mr-2' />
              Trang hỗ trợ
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Dashboard
