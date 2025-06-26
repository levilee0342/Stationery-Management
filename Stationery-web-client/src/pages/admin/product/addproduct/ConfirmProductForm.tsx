import React from 'react'
import { ProductDetailForm, ProductForm } from '~/types/product'

interface ConfirmProductFormProps {
  product: ProductForm
  listCategories: { value: string; label: string }[]
  sizes: { value: string; label: string }[]
  colors: { value: string; label: string; color: string }[]
  productDetails: ProductDetailForm[]
  onConfirm: () => void
  onBack: () => void
  onCancel: () => void
}

const ConfirmProductForm: React.FC<ConfirmProductFormProps> = ({
  product,
  listCategories,
  sizes,
  colors,
  productDetails,
  onConfirm,
  onBack,
  onCancel
}) => {
  return (
    <div className='mb-8 p-8 rounded-3xl shadow-xl border border-blue-200 transition-all'>
      <h2 className='text-3xl font-bold text-blue-800 mb-6 text-center'>Confirm Product Details</h2>

      {/* Product Information */}
      <div className='mb-8'>
        <h3 className='text-lg font-semibold text-gray-700 mb-4'>Product Information</h3>
        <div className='grid grid-cols-1 lg:grid-cols-2 gap-8'>
          <div>
            <img
              src={productDetails[0]?.images?.[0]?.url || ''}
              alt={product.name}
              className='w-full h-64 object-contain rounded-lg shadow-md'
            />
          </div>
          <div className='space-y-4'>
            <p>
              <strong>Title:</strong> {product.name}
            </p>
            <p>
              <strong>Slug:</strong> {product.slug}
            </p>
            <p>
              <strong>Category:</strong> {listCategories.find((c) => c.value == product.categoryId)?.label}
            </p>
            <p>
              <strong>Description:</strong> {product.description}
            </p>
          </div>
        </div>
      </div>

      {/* Product Details */}
      <div className='mb-8'>
        <h3 className='text-lg font-semibold text-gray-700 mb-4'>Product Details</h3>
        {productDetails.length === 0 ? (
          <p className='text-gray-500 italic'>No details added.</p>
        ) : (
          <div className='overflow-x-auto'>
            <table className='w-full border-collapse'>
              <thead>
                <tr className='bg-blue-100 text-blue-800 text-sm uppercase'>
                  <th className='px-4 py-3 text-left'>Color</th>
                  <th className='px-4 py-3 text-left'>Size</th>
                  <th className='px-4 py-3 text-left'>Quantity</th>
                  <th className='px-4 py-3 text-left'>Price</th>
                  <th className='px-4 py-3 text-left'>Images</th>
                </tr>
              </thead>
              <tbody className='divide-y divide-gray-200'>
                {productDetails.map((detail) => (
                  <tr key={detail.slug} className='hover:bg-gray-50 transition-colors'>
                    <td className='px-4 py-3 text-sm'>
                      <div className='flex items-center gap-2'>
                        {colors.find((c) => c.value == detail.colorId)?.label}
                      </div>
                    </td>
                    <td className='px-4 py-3 text-sm'>{sizes.find((s) => s.value == detail.sizeId)?.label}</td>
                    <td className='px-4 py-3 text-sm'>{detail.stockQuantity.toLocaleString('vi-VN')}</td>
                    <td className='px-4 py-3 text-sm'>{detail.discountPrice.toLocaleString('vi-VN')} VND</td>
                    <td className='px-4 py-3 text-sm'>
                      <div className='flex gap-2'>
                        {detail.images
                          ?.slice(0, 3)
                          .map((img, idx) => (
                            <img
                              key={idx}
                              src={img.url}
                              alt={`Detail-${idx}`}
                              className='w-8 h-8 rounded object-cover'
                            />
                          ))}
                        {detail.images && detail.images.length > 3 && (
                          <span className='text-gray-500'>+{detail.images.length - 3}</span>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Action Buttons */}
      <div className='flex justify-end gap-4 mt-10'>
        <button
          onClick={onCancel}
          className='px-6 py-2.5 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition-colors text-sm font-medium'
        >
          Cancel
        </button>
        <button
          onClick={onBack}
          className='px-6 py-2.5 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-colors text-sm font-medium'
        >
          Back
        </button>
        <button
          onClick={onConfirm}
          className='px-6 py-2.5 bg-gradient-to-r from-blue-600 to-blue-500 text-white rounded-lg hover:from-blue-700 hover:to-blue-600 transition text-sm font-semibold shadow-md'
        >
          Confirm
        </button>
      </div>
    </div>
  )
}

export default ConfirmProductForm
