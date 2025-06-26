// Card.tsx
import React, { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { FaPlus, FaShoppingCart } from 'react-icons/fa'
import Button from '../button/Button'
import { Link } from 'react-router-dom'
import { FetchColor, Product, ProductDetail } from '~/types/product'
import NumberToStart from '~/components/numberToStar/NumberToStart'
import { calculatePercent, formatNumber, priceInPromotion } from '~/utils/helper'
import { DefaultProduct } from '~/assets/images'
import { apiAddItemToCart } from '~/api/cart'
import { useAppSelector } from '~/hooks/redux'
import { showToastError, showToastSuccess } from '~/utils/alert'

interface ProductCardProps {
  product: Product
  onViewDetails: (id: string) => void
  onAddToCart?: () => Promise<void>
}

const Card: React.FC<ProductCardProps> = ({ product }) => {
  const { accessToken, userData } = useAppSelector((state) => state.user)
  const [selectedColor, setSelectedColor] = useState<string>(product.productDetail.color?.colorId ?? '')
  const [selectedSize, setSelectedSize] = useState<string>(product.productDetail.size?.sizeId ?? '')
  const [productDetail, setProductDetail] = useState<ProductDetail | null>(null)
  const [, setColors] = useState<FetchColor[]>([])

  useEffect(() => {
    setColors(product.fetchColor)
    setProductDetail(product.productDetail)
    setSelectedColor(product.productDetail.color?.colorId ?? '')
    setSelectedSize(product.productDetail.size?.sizeId ?? '')
  }, [product])

  const handleAddToCart = async () => {
    if (!selectedColor || !selectedSize) {
      return
    }

    try {
      const response = await apiAddItemToCart({
        productDetailId: product.productDetail.productDetailId,
        quantity: 1,
        accessToken: accessToken ?? ''
      })

      console.log(product.productDetail.productDetailId)

      if (typeof response === 'string') {
        showToastError(response)
      } else {
        showToastSuccess('Added to cart successfully!')
      }
    } catch {
      showToastError('Added to cart failed!')
    }
  }

  const handleAddRequest = () => {
    // Xử lý thêm yêu cầu mới tại đây
    showToastSuccess('Thêm yêu cầu mới thành công!')
  }

  return (
    <motion.div className='bg-white text-gray-900 rounded-2xl shadow-lg p-5 flex flex-col items-center space-y-4 transition-all duration-300 hover:shadow-2xl'>
      <Link to={`/products/${productDetail?.slug}`} className='w-full flex justify-center'>
        <motion.img
          src={product?.img ? product.img : DefaultProduct}
          alt={product?.name}
          className='w-52 h-52 object-cover rounded-xl shadow-md'
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ duration: 0.5 }}
        />
      </Link>
      <div className='w-full min-h-[60px] max-h-[60px] overflow-hidden'>
        <h3 className='text-lg font-bold text-center line-clamp-2'>{product?.name}</h3>
      </div>
      <div className='flex items-center justify-between w-full px-4'>
        <div>
          <p className='text-[19px] max-sm:text-xs font-semibold text-blue-500'>
            {formatNumber(priceInPromotion(productDetail))} ₫
          </p>
          <div className='flex items-center'>
            {productDetail?.originalPrice !== 0 && (
              <>
                <p className='line-through text-[#6b7280] text-sm'>
                  {formatNumber(productDetail?.originalPrice ?? 0)}₫
                </p>
                <p className={`ml-1 text-red-400 text-sm font-bold`}>
                  - {calculatePercent(productDetail?.originalPrice ?? 0, priceInPromotion(productDetail))}%
                </p>
              </>
            )}
          </div>
        </div>
        <div className='flex items-center text-sm'>
          <NumberToStart number={product.totalRating} />
        </div>
      </div>

      <div className='flex justify-between w-full text-gray-500 text-sm px-4'>
        <span>Stock: {product.quantity}</span>
        <span>Sold: {product.soldQuantity}</span>
      </div>

      {/* <ColorSelector
        colors={colors}
        selectedColor={selectedColor}
        onColorSelect={(colorId) => setSelectedColor(colorId)}
      /> */}

      {/* Thêm Size Selector nếu cần */}
      {/* <SizeSelector
        sizes={product.fetchSize}
        selectedSize={selectedSize}
        onSizeSelect={(sizeId) => setSelectedSize(sizeId)}
      /> */}
      <div className='flex space-x-4 w-full mt-2'>
        <Link
          to={`${userData?.role?.roleId === '113' ? '/department' : ''}/products/${productDetail?.slug}`}
          className='w-full'
        >
          <Button className='bg-blue-500 text-white px-4 py-2 w-full rounded-lg shadow-md transition hover:bg-blue-600'>
            View Details
          </Button>
        </Link>

        <button
          className={`p-3 rounded-lg shadow-md transition ${
            userData?.role?.roleId === '113'
              ? 'bg-green-500 text-white hover:bg-green-600'
              : 'bg-yellow-400 text-black hover:bg-yellow-500'
          }`}
          onClick={userData?.role?.roleId === '113' ? handleAddRequest : handleAddToCart}
          title={userData?.role?.roleId === '113' ? 'Thêm yêu cầu mới' : 'Thêm vào giỏ hàng'}
        >
          {userData?.role?.roleId === '113' ? <FaPlus size={20} /> : <FaShoppingCart size={20} />}
        </button>
      </div>
    </motion.div>
  )
}

export default Card
