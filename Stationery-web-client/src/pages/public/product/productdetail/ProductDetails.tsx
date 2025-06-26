// pages/ProductDetail.tsx
import { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import { ProductImages } from './ProductImages'
import { ProductInfo } from './ProductInfo'
import { ProductTabs } from './ProductTabs'
import { SimilarProducts } from './SimilarProducts'
import { apiFetchColorSizeProductDetail, apiGetDetailProduct } from '~/api/product'
import { useAppSelector } from '~/hooks/redux'
import { showToastError, showToastSuccess } from '~/utils/alert'
import { Product } from '~/types/product'
import AxiosError from 'axios'
import { apiGetReviewOfProduct } from '~/api/review'
import { Review } from '~/types/comment'
import { ColorSize } from '~/types/color'
import { apiAddItemToCart } from '~/api/cart'

export default function ProductDetail() {
  const { slug } = useParams()
  const { accessToken } = useAppSelector((state) => state.user)
  const [product, setProduct] = useState<Product | null>(null)
  const [fechAgain, setFetchAgain] = useState(false)
  const [reviews, setReviews] = useState<Review[]>([])
  const [colorSize, setColorSize] = useState<ColorSize[]>([])

  const handleViewDetails = (id: number) => {
    console.log(`Xem chi tiết sản phẩm ${id}`)
  }

  const handleAddToCart = async (productDetailId: string): Promise<void> => {
    try {
      const response = await apiAddItemToCart({
        productDetailId: productDetailId,
        quantity: 1,
        accessToken: accessToken ?? ''
      })

      if (typeof response === 'string') {
        showToastError(response)
      } else {
        showToastSuccess('Added to cart successfully!')
      }
    } catch {
      showToastError('Added to cart failed!')
    }
  }

  const getProductDetail = async () => {
    try {
      const response = await apiGetDetailProduct(slug)
      if (response.code == 200) {
        setProduct(response.result)
      } else {
        showToastError(response.message || response.error)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        // showToastError(error.message)
      }
    }
  }
  const fetchColorSize = async () => {
    try {
      const response = await apiFetchColorSizeProductDetail(slug)
      if (response.code == 200) {
        setColorSize(response.result)
      } else {
        showToastError(response.message || response.error)
      }
    } catch (error) {
      if (error instanceof Error) {
        showToastError(error.message)
      } else {
        showToastError(error as string)
      }
    }
  }
  const getReviewOfProduct = async () => {
    try {
      const response = await apiGetReviewOfProduct({ slug })
      if (response.code == 200) {
        setReviews(response.result)
      } else {
        showToastError(response.message || response.error)
      }
    } catch (error) {
      if (error instanceof Error) {
        showToastError(error.message)
      } else {
        showToastError(error as string)
      }
    }
  }
  useEffect(() => {
    getReviewOfProduct()
  }, [fechAgain])
  useEffect(() => {
    fetchColorSize()
  }, [product?.productId])
  useEffect(() => {
    getProductDetail()
    setTimeout(() => {
      window.scrollTo(0, 0)
    }, 400)
  }, [slug])
  return (
    <div className='max-w-7xl mx-auto p-8 bg-white rounded-lg shadow-lg mt-16'>
      <div className='flex flex-col md:flex-row gap-6'>
        <ProductImages images={product?.productDetail?.images || []} />
        <ProductInfo
          colorSize={colorSize}
          productDetail={product?.productDetail}
          name={product?.name}
          totalRating={product?.totalRating}
          productDetailId={product?.productDetail?.productDetailId}
          accessToken={accessToken || ''}
        />
      </div>
      <ProductTabs
        pId={product?.productId}
        totalRating={product?.totalRating}
        desc={product?.description}
        reviews={reviews}
        setFetchAgain={setFetchAgain}
      />
      <SimilarProducts pId={product?.productId} onAddToCart={handleAddToCart} onViewDetails={handleViewDetails} />
    </div>
  )
}
