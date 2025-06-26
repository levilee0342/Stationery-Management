import Banner from './Banner'
import AxiosError from 'axios'
import ProductCategories from './ProductCategories'
import Faq from './Faq'
import { publicPaths } from '~/constance/paths'
import FloatingButtons from '~/components/contactnow/FloatingButtons'
import NewProduct from './component/NewProduct'
import BestSellingProduct from './component/BestSellingProduct' // Thêm import
import { useAppSelector } from '~/hooks/redux'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { useEffect, useMemo } from 'react'
import { showAlertError, showAlertSucess, showToastError } from '~/utils/alert'
import { apiCheckTransactionStatus } from '~/api/orders'

// Định nghĩa kiểu dữ liệu cho banner
interface BannerData {
  id: number
  image: string
  title: string
  content: string
  buttonText: string
  buttonLink: string
}

// Mảng dữ liệu banner
const banners: BannerData[] = [
  {
    id: 1,
    image: 'public/images/img_banner_1.jpg',
    title: 'New Collection Arrived',
    content: 'Discover unique and creative stationery items!',
    buttonText: 'Shop Now',
    buttonLink: publicPaths.PRODUCT
  },
  {
    id: 2,
    image: 'public/images/img_banner_5.jpg',
    title: 'Big April Sale',
    content: 'Up to 50% off on hundreds of stationery products!',
    buttonText: 'Get Deals',
    buttonLink: publicPaths.PRODUCT
  },
  {
    id: 3,
    image: 'public/images/img_banner_2.jpg',
    title: 'Free Shipping',
    content: 'Enjoy free shipping on orders over $12!',
    buttonText: 'Learn More',
    buttonLink: publicPaths.SHIPPING_POLICY
  }
]

const Home: React.FC = () => {
  const { userData, accessToken } = useAppSelector((state) => state.user)
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { orderId } = useMemo(() => Object.fromEntries([...searchParams]), [searchParams])

  useEffect(() => {
    if (userData?.role.roleId == '113') {
      navigate('/department', { replace: true })
    }
  }, [userData, navigate])

  const checkTransactionStatus = async (orderId: string) => {
    try {
      // số 1 là đang gọi check status từ home nếu order đó có resultCode != 0 thì đó là hủy dơn hàng
      const res = await apiCheckTransactionStatus({ orderId, accessToken: accessToken || '' })
      if (res.result.resultCode === 0) {
        showAlertSucess('Order successfully created')
      } else {
        showAlertError(res.message)
      }
      navigate('/', { replace: true })
    } catch (error) {
      if (error instanceof AxiosError) {
        showToastError('Error: ' + error)
      } else {
        showToastError('Error: ' + (error as Error).message)
      }
      navigate('/', { replace: true })
    }
  }
  useEffect(() => {
    if (orderId) {
      checkTransactionStatus(orderId)
    }
  }, [orderId])

  return (
    <div className='flex flex-col min-h-screen bg-gray-100 p-6'>
      <Banner banners={banners} />
      <ProductCategories />
      <NewProduct />
      <BestSellingProduct />
      <Faq />
      <FloatingButtons />
    </div>
  )
}

export default Home
