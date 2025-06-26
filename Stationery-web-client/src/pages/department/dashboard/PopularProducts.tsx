import React, { useEffect, useState } from 'react'
import { FaPlusCircle } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { departmentPath } from '~/constance/paths'
import { apiGetAllProductsWithDefaultPD, apiFetchColorSizeProductDetail } from '~/api/product'
import { Product, FetchColor, Size } from '~/types/product'
import RequestForm from '../components/RequestForm'

interface RequestForm {
  productId: string
  color: string
  size: string
  quantity: number
  notes: string
  deliveryDate: string
}

interface PopularProductsProps {
  setProducts: React.Dispatch<React.SetStateAction<Product[]>>
}

const PopularProducts: React.FC<PopularProductsProps> = ({ setProducts }) => {
  const [localProducts, setLocalProducts] = useState<Product[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [isRequestFormOpen, setIsRequestFormOpen] = useState(false)
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null)
  const [formData, setFormData] = useState<RequestForm>({
    productId: '',
    color: '',
    size: '',
    quantity: 1,
    notes: '',
    deliveryDate: ''
  })
  const [productSearch, setProductSearch] = useState('')
  const [colors, setColors] = useState<FetchColor[]>([])
  const [sizes, setSizes] = useState<Size[]>([])

  useEffect(() => {
    const fetchPopularProducts = async () => {
      try {
        setLoading(true)
        const response = await apiGetAllProductsWithDefaultPD({
          page: '1',
          limit: '1000',
          sortBy: 'soldQuantity'
        })
        console.log('Popular products response:', response)
        if (response.code === 200 && response.result?.content) {
          const fetchedProducts: Product[] = response.result.content.map((item: any) => ({
            productId: item.productId,
            description: item.description || '',
            slug: item.slug || '',
            name: item.name,
            category: item.category || { categoryId: '', categoryName: '' },
            minPrice: item.minPrice || 0,
            soldQuantity: item.soldQuantity || 0,
            quantity: item.quantity || 0,
            totalRating: item.totalRating || 0,
            productDetail: item.productDetail || null,
            fetchColor: item.fetchColor || [],
            img: item.img,
            createdAt: item.createdAt || ''
          }))
          setLocalProducts(fetchedProducts)
          setProducts(fetchedProducts)
        } else {
          setError('Không thể tải sản phẩm phổ biến')
        }
      } catch (err) {
        console.error('Error fetching popular products:', err)
        setError('Lỗi khi tải sản phẩm phổ biến')
      } finally {
        setLoading(false)
      }
    }

    fetchPopularProducts()
  }, [setProducts])

  const handleAddProduct = async (product: Product) => {
    try {
      setLoading(true)
      if (!product.slug) {
        setError('Sản phẩm không có slug')
        return
      }
      const response = await apiFetchColorSizeProductDetail(product.slug)
      console.log('ColorSize response:', response)
      if (response.code === 200 && response.result) {
        const fetchedColors = response.result || []
        const fetchedSizes = response.result.sizes || []
        console.log('Fetched colors:', fetchedColors)
        console.log('Fetched sizes:', fetchedSizes)
        setColors(fetchedColors)
        setSizes(fetchedSizes)
        setSelectedProduct(product)
        setFormData((prev) => ({ ...prev, productId: product.productId }))
        setIsRequestFormOpen(true)
      } else {
        setError('Không thể tải màu và kích thước')
      }
    } catch (err) {
      console.error('Error fetching color/size:', err)
      setError('Lỗi khi tải màu và kích thước')
    } finally {
      setLoading(false)
    }
  }

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
    } catch (err) {
      console.error('Lỗi khi gửi yêu cầu:', err)
      setError('Lỗi khi gửi yêu cầu')
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
    setColors([])
    setSizes([])
    setSelectedProduct(null)
  }

  if (loading) {
    return (
      <div className='bg-white shadow-lg rounded-lg p-6'>
        <h2 className='text-xl font-semibold text-gray-800 mb-4'>Sản phẩm phổ biến</h2>
        <div className='flex justify-center'>
          <div className='animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-blue-600'></div>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className='bg-white shadow-lg rounded-lg p-6'>
        <h2 className='text-xl font-semibold text-gray-800 mb-4'>Sản phẩm phổ biến</h2>
        <p className='text-red-500'>{error}</p>
      </div>
    )
  }

  return (
    <div className='bg-white shadow-lg rounded-lg p-6'>
      <div className='flex justify-between items-center mb-4'>
        <h2 className='text-xl font-semibold text-gray-800'>Sản phẩm phổ biến</h2>
        <Link to={departmentPath.PRODUCT} className='text-blue-600 hover:underline text-sm font-medium'>
          Xem tất cả
        </Link>
      </div>
      <div className='grid grid-cols-1 gap-4'>
        {localProducts.slice(0, 3).map((product) => (
          <div
            key={product.productId}
            className='flex items-center p-3 border border-gray-200 rounded-lg hover:bg-gray-50 transition'
          >
            <img src={product.img} alt={product.name} className='w-16 h-16 object-cover rounded-md mr-4' />
            <div className='flex-1'>
              <h3 className='text-sm font-semibold text-gray-800'>{product.name}</h3>
              <p className='text-sm text-red-600'>{product.productDetail.discountPrice.toLocaleString()} VND</p>
            </div>
            <button onClick={() => handleAddProduct(product)} className='btn btn-sm btn-primary flex items-center'>
              <FaPlusCircle className='mr-1 hover:text-blue-600' />
              Thêm
            </button>
          </div>
        ))}
      </div>
      {isRequestFormOpen && selectedProduct && (
        <RequestForm
          products={localProducts}
          colors={colors}
          sizes={sizes}
          formData={formData}
          productSearch={productSearch}
          setFormData={setFormData}
          setProductSearch={setProductSearch}
          onSubmit={handleSubmit}
          onClose={handleClose}
          isEditMode={false}
        />
      )}
    </div>
  )
}

export default PopularProducts
