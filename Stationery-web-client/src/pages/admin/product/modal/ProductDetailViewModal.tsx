import React, { useState } from 'react'
import moment from 'moment'
import { FetchColor, ListProductDetail, ProductDetail } from '~/types/product'
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import NumberToStart from '~/components/numberToStar/NumberToStart'

interface ProductDetailViewModalProps {
  isOpen: boolean
  product: ListProductDetail
  details: ProductDetail[]
  fetchColors: FetchColor[]
  onClose: () => void
}

const ProductDetailViewModal: React.FC<ProductDetailViewModalProps> = ({
  isOpen,
  product,
  details,
  fetchColors,
  onClose
}) => {
  const [selectedImage, setSelectedImage] = useState<string | null>(null)

  if (!isOpen) return null

  // Kiểm tra xem có màu hay không
  const hasColors = fetchColors.length > 0 && details.some((detail) => detail.color?.colorId)
  // Kiểm tra xem có size hay không
  const hasSizes = details.some((detail) => detail.size?.name)

  // Nhóm ProductDetail
  let groupedDetails: { color?: FetchColor; details: ProductDetail[] }[] = []

  if (hasColors) {
    // Trường hợp có màu: Nhóm theo màu
    groupedDetails = fetchColors
      .map((color) => ({
        color,
        details: details.filter((detail) => detail.color?.colorId === color.colorId)
      }))
      .filter((group) => group.details.length > 0)
  } else {
    // Trường hợp không có màu: Nhóm theo size hoặc không nhóm
    if (hasSizes) {
      // Nhóm theo size
      const uniqueSizes = Array.from(new Set(details.map((detail) => detail.size?.name || 'No size')))
      groupedDetails = uniqueSizes.map((size) => ({
        details: details.filter((detail) => (detail.size?.name || 'No size') === size)
      }))
    } else {
      // Không màu, không size: Hiển thị tất cả details trong một nhóm
      groupedDetails = [{ details }]
    }
  }

  // Xử lý cuộn carousel
  const scrollImages = (direction: 'left' | 'right', containerId: string) => {
    const container = document.getElementById(containerId)
    if (container) {
      const scrollAmount = direction === 'left' ? -200 : 200
      container.scrollBy({ left: scrollAmount, behavior: 'smooth' })
    }
  }

  return (
    <>
      {/* Modal chính */}
      <div className='fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4'>
        <div className='bg-white rounded-2xl max-w-5xl w-full max-h-[90vh] overflow-y-auto shadow-2xl'>
          {/* Header */}
          <div className='sticky top-0 bg-gradient-to-r from-blue-700 to-blue-600 text-white p-6 rounded-t-2xl flex justify-between items-center'>
            <h2 className='text-3xl font-bold'>Product Details</h2>
            <button
              onClick={onClose}
              className='bg-white text-blue-700 px-4 py-2 rounded-lg font-semibold hover:bg-gray-100 transition-colors'
            >
              ✕
            </button>
          </div>

          {/* Product Information */}
          <div className='p-8 border-b border-gray-200'>
            <h3 className='text-2xl font-semibold text-gray-800 mb-6'>Product Information</h3>
            <div className='grid grid-cols-1 md:grid-cols-2 gap-8'>
              {/* Product Image */}
              <div className='flex justify-center'>
                <img
                  src={product.img || details[0]?.images?.[0]?.url || 'https://via.placeholder.com/300'}
                  alt={product.name}
                  className='w-80 h-80 object-contain rounded-xl shadow-lg hover:scale-105 transition-transform duration-300 cursor-pointer'
                  onClick={() => setSelectedImage(product.img || details[0]?.images?.[0]?.url || '')}
                />
              </div>
              {/* Product Details */}
              <div className='space-y-4 text-lg'>
                <p>
                  <span className='font-semibold text-gray-700'>Name:</span> {product.name}
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Slug:</span> {product.slug}
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Category:</span> {product.category.categoryName}
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Price:</span> {product.minPrice.toLocaleString('vi-VN')}{' '}
                  VND
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Quantity:</span>{' '}
                  {product.quantity.toLocaleString('vi-VN')}
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Sold:</span>{' '}
                  {product.soldQuantity.toLocaleString('vi-VN')}
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Rating:</span>{' '}
                  <NumberToStart number={product.totalRating} />
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Created:</span>{' '}
                  {moment(product.createdAt).format('DD/MM/YYYY HH:mm')}
                </p>
                <p>
                  <span className='font-semibold text-gray-700'>Description:</span>{' '}
                  {product.description || 'No description available.'}
                </p>
              </div>
            </div>
          </div>

          {/* Product Variants */}
          <div className='p-8'>
            <h3 className='text-2xl font-semibold text-gray-800 mb-6'>Product Variants</h3>
            {details.length === 0 ? (
              <p className='text-gray-500 italic text-lg'>No variants available.</p>
            ) : (
              <div className='space-y-8'>
                {groupedDetails.map((group, groupIdx) => (
                  <div
                    key={group.color?.colorId || `group-${groupIdx}`}
                    className='bg-gray-50 rounded-xl p-6 shadow-sm'
                  >
                    {/* Tiêu đề nhóm */}
                    <div className='flex items-center gap-4 mb-6'>
                      {group.color ? (
                        <>
                          <span
                            className='w-8 h-8 rounded-full shadow-inner border border-gray-200'
                            style={{ backgroundColor: group.color.hex }}
                          />
                          <h4 className='text-xl font-semibold text-gray-800'>
                            {group.color.slug.replace('color-', '').replace(/-/g, ' ')} ({group.color.hex})
                          </h4>
                        </>
                      ) : (
                        <h4 className='text-xl font-semibold text-gray-800'>
                          {hasSizes ? `Size: ${group.details[0].size?.name || 'No size'}` : 'Variants'}
                        </h4>
                      )}
                    </div>
                    <div className='space-y-6'>
                      {group.details.map((detail) => (
                        <div
                          key={detail.productDetailId}
                          className='bg-white p-6 rounded-xl shadow-md hover:shadow-xl transition-shadow duration-300'
                        >
                          <div className='grid grid-cols-1 md:grid-cols-3 gap-6'>
                            {/* Thông tin chi tiết */}
                            <div className='md:col-span-1 space-y-3'>
                              {hasSizes && (
                                <p className='text-base'>
                                  <span className='font-medium text-gray-600'>Size:</span> {detail.size?.name || 'N/A'}
                                </p>
                              )}
                              {!hasColors && (
                                <p className='text-base'>
                                  <span className='font-medium text-gray-600'>Color:</span>{' '}
                                  {detail.color?.name || 'No color'}
                                </p>
                              )}
                              <p className='text-base'>
                                <span className='font-medium text-gray-600'>Quantity:</span>{' '}
                                {detail.stockQuantity.toLocaleString('vi-VN')}
                              </p>
                              <p className='text-base'>
                                <span className='font-medium text-gray-600'>Sold:</span>{' '}
                                {detail.soldQuantity.toLocaleString('vi-VN')}
                              </p>
                              <p className='text-base'>
                                <span className='font-medium text-gray-600'>Price:</span>{' '}
                                {detail.discountPrice.toLocaleString('vi-VN')} VND
                              </p>
                              <p className='text-base'>
                                <span className='font-medium text-gray-600'>Rating:</span>
                              </p>
                            </div>
                            {/* Carousel ảnh */}
                            <div className='md:col-span-2 relative'>
                              <div
                                id={`image-carousel-${detail.productDetailId}`}
                                className='flex gap-3 overflow-x-auto scrollbar-thin scrollbar-thumb-gray-300 scrollbar-track-gray-100 pb-2'
                              >
                                {detail.images?.length ? (
                                  detail.images.map((img, idx) => (
                                    <div
                                      key={idx}
                                      className='relative w-28 h-28 border border-gray-200 rounded-lg shadow-sm bg-white p-1 hover:shadow-md hover:scale-105 transition-all duration-200 cursor-pointer'
                                      onClick={() => setSelectedImage(img.url)}
                                    >
                                      <img
                                        src={img.url}
                                        alt={`Detail-${idx}`}
                                        className='w-full h-full object-cover rounded-md'
                                      />
                                    </div>
                                  ))
                                ) : (
                                  <span className='text-gray-400 text-base'>No images available</span>
                                )}
                              </div>
                              {detail.images && detail.images.length > 3 && (
                                <>
                                  <button
                                    onClick={() => scrollImages('left', `image-carousel-${detail.productDetailId}`)}
                                    className='absolute left-0 top-1/2 transform -translate-y-1/2 bg-blue-600 text-white p-2 rounded-full shadow-md hover:bg-blue-700 transition-colors'
                                  >
                                    <FaArrowLeft size={16} />
                                  </button>
                                  <button
                                    onClick={() => scrollImages('right', `image-carousel-${detail.productDetailId}`)}
                                    className='absolute right-0 top-1/2 transform -translate-y-1/2 bg-blue-600 text-white p-2 rounded-full shadow-md hover:bg-blue-700 transition-colors'
                                  >
                                    <FaArrowRight size={16} />
                                  </button>
                                </>
                              )}
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Modal phóng to ảnh */}
      {selectedImage && (
        <div className='fixed inset-0 bg-black bg-opacity-80 flex items-center justify-center z-60 p-4'>
          <div className='relative max-w-3xl w-full'>
            <img src={selectedImage} alt='Zoomed' className='w-full h-auto max-h-[80vh] object-contain rounded-lg' />
            <button
              onClick={() => setSelectedImage(null)}
              className='absolute top-4 right-4 bg-white text-gray-800 p-2 rounded-full shadow-md hover:bg-gray-100 transition-colors'
            >
              ✕
            </button>
          </div>
        </div>
      )}
    </>
  )
}

export default ProductDetailViewModal
