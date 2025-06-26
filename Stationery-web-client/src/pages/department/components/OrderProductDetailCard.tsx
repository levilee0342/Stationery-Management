import { FiChevronLeft, FiChevronRight, FiShoppingBag } from 'react-icons/fi'
import { useState } from 'react'
import { formatNumber } from '~/utils/helper'
import { ProductDetail } from '~/types/product'

interface Props {
  detail: ProductDetail
  quantity: number
}

const OrderProductDetailCard = ({ detail, quantity }: Props) => {
  const [currentImageIndex, setCurrentImageIndex] = useState(0)
  const imageCount = detail.images?.length || 0
  const itemTotal = detail.discountPrice * quantity

  const handlePrevImage = () => {
    setCurrentImageIndex((prev) => (prev - 1 + imageCount) % imageCount)
  }

  const handleNextImage = () => {
    setCurrentImageIndex((prev) => (prev + 1) % imageCount)
  }

  return (
    <div key={detail.productDetailId} className='bg-white p-6 rounded-xl shadow-sm mb-4 border border-gray-100'>
      <div className='flex flex-col md:flex-row gap-6'>
        {/* Carousel ảnh */}
        <div className='w-full md:w-1/3 relative'>
          {imageCount > 0 ? (
            <div className='relative'>
              <img
                src={detail.images![currentImageIndex].url}
                alt={detail.name || 'Sản phẩm'}
                className='w-full h-48 object-cover rounded-lg'
              />
              {imageCount > 1 && (
                <>
                  <button
                    onClick={handlePrevImage}
                    className='absolute left-2 top-1/2 transform -translate-y-1/2 bg-gray-800 bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-all'
                  >
                    <FiChevronLeft size={20} />
                  </button>
                  <button
                    onClick={handleNextImage}
                    className='absolute right-2 top-1/2 transform -translate-y-1/2 bg-gray-800 bg-opacity-50 text-white p-2 rounded-full hover:bg-opacity-75 transition-all'
                  >
                    <FiChevronRight size={20} />
                  </button>
                  <div className='absolute bottom-2 left-1/2 transform -translate-x-1/2 flex space-x-2'>
                    {detail.images!.map((_, index) => (
                      <span
                        key={index}
                        className={`w-2 h-2 rounded-full ${
                          index === currentImageIndex ? 'bg-blue-600' : 'bg-gray-300'
                        }`}
                      ></span>
                    ))}
                  </div>
                </>
              )}
            </div>
          ) : (
            <div className='w-full h-48 bg-gray-200 rounded-lg flex items-center justify-center'>
              <FiShoppingBag className='text-gray-400 text-3xl' />
            </div>
          )}
        </div>

        {/* Thông tin sản phẩm */}
        <div className='flex-1'>
          <p className='text-lg font-medium text-gray-800'>{detail.name || 'Không có tên'}</p>
          <div className='grid grid-cols-2 gap-4 mt-4 text-sm'>
            <div>
              <p className='text-gray-500'>Giá gốc</p>
              <p className='font-medium text-gray-800'>{formatNumber(detail.originalPrice)}₫</p>
            </div>
            <div>
              <p className='text-gray-500'>Giá khuyến mãi</p>
              <p className='font-medium text-gray-800'>{formatNumber(detail.discountPrice)}₫</p>
            </div>
            {detail.size && (
              <div>
                <p className='text-gray-500'>Kích thước</p>
                <p className='font-medium text-gray-800'>{detail.size.name}</p>
              </div>
            )}
            {detail.color && (
              <div>
                <p className='text-gray-500 flex items-center'>
                  Màu sắc
                  <span
                    className='ml-2 w-4 h-4 rounded-full border border-gray-300'
                    style={{ backgroundColor: detail.color.hex }}
                  ></span>
                </p>
                <p className='font-medium text-gray-800'>{detail.color.name}</p>
              </div>
            )}
          </div>
          <div className='mt-4 text-right'>
            {detail.originalPrice !== detail.discountPrice && (
              <>
                <p className='text-sm text-gray-600'>
                  {quantity} x {formatNumber(detail.discountPrice)}₫ = {formatNumber(itemTotal)}₫
                </p>
                <p className='text-sm text-green-600'>
                  Tiết kiệm {formatNumber((detail.originalPrice - detail.discountPrice) * quantity)}₫
                </p>
              </>
            )}
            <p className='text-blue-600 font-semibold'>{formatNumber(itemTotal)}₫</p>
          </div>
        </div>
      </div>
    </div>
  )
}

export default OrderProductDetailCard
