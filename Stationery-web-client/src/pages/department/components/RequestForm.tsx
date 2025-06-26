import React, { useState, useEffect, useMemo } from 'react'
import { FaTimes } from 'react-icons/fa'
import { Product, FetchColor, Size } from '~/types/product'
import { Address } from '~/types/address'
import { ShippingAddress } from '~/pages/user/paymentconfirmation/component/ShippingAddress'
import { useAppSelector } from '~/hooks/redux'
import { showToastError } from '~/utils/alert'
import { apiGetProductDetailsByProductId } from '~/api/product'

interface RequestFormProps {
  products: Product[]
  vouchers: Voucher[]
  formData: RequestForm
  productSearch: string
  setFormData: React.Dispatch<React.SetStateAction<RequestForm>>
  setProductSearch: React.Dispatch<React.SetStateAction<string>>
  onClose: () => void
  isEditMode: boolean
  colors: FetchColor[]
  sizes: Size[]
  onSubmit: (e: React.FormEvent) => void
}

interface RequestForm {
  productId: string
  color: string
  size: string
  quantity: number
  notes: string
  deliveryDate: string
  addressId: string
  voucherId: string
}

interface Voucher {
  voucherId: string
  code: string
  discount: number
}

interface ProductDetail {
  productDetailId: string
  color: { colorId: string; name: string; hex: string; slug: string } | null
  size: { sizeId: string; name: string; priority: number } | null
  discountPrice: number
  stockQuantity: number
  productPromotions?: { productPromotionId: string }[]
}

const RequestForm: React.FC<RequestFormProps> = ({
  products,
  vouchers,
  formData,
  productSearch,
  setFormData,
  setProductSearch,
  onClose,
  isEditMode,
  colors,
  sizes,
  onSubmit
}) => {
  const { userData } = useAppSelector((state) => state.user)
  const [selectedShippingInfo, setSelectedShippingInfo] = useState<Address | null>(null)
  const [availableColors, setAvailableColors] = useState<FetchColor[]>(colors)
  const [availableSizes, setAvailableSizes] = useState<Size[]>(sizes)
  const [selectedProductDetail, setSelectedProductDetail] = useState<ProductDetail | null>(null)

  const selectedProduct = useMemo(
    () => products.find((p) => p.productId === formData.productId),
    [products, formData.productId]
  )

  // Sync addressId with selectedShippingInfo
  useEffect(() => {
    if (selectedShippingInfo && selectedShippingInfo.addressId !== formData.addressId) {
      setFormData((prev) => ({ ...prev, addressId: selectedShippingInfo.addressId }))
    }
  }, [selectedShippingInfo, formData.addressId, setFormData])

  // Load colors and sizes only in create mode or when productId changes
  useEffect(() => {
    if (isEditMode && colors.length > 0 && sizes.length > 0) {
      setAvailableColors(colors)
      setAvailableSizes(sizes)
      return
    }

    if (!formData.productId) {
      setAvailableColors([])
      setAvailableSizes([])
      setSelectedProductDetail(null)
      return
    }

    const fetchProductDetails = async () => {
      try {
        const response = await apiGetProductDetailsByProductId({ productId: formData.productId })
        const productDetails: ProductDetail[] = response.result

        const fetchedColors = Array.from(
          new Map(
            productDetails
              .filter((detail) => detail.color)
              .map((detail) => [
                detail.color!.colorId,
                {
                  colorId: detail.color!.colorId,
                  name: detail.color!.name,
                  hex: detail.color!.hex,
                  slug: detail.color!.slug
                }
              ])
          ).values()
        )

        const fetchedSizes = Array.from(
          new Map(
            productDetails
              .filter((detail) => detail.size)
              .map((detail) => [
                detail.size!.sizeId,
                {
                  sizeId: detail.size!.sizeId,
                  name: detail.size!.name,
                  priority: detail.size!.priority
                }
              ])
          ).values()
        )

        setAvailableColors(fetchedColors)
        setAvailableSizes(fetchedSizes)

        // Find the product detail matching the selected color and size
        const selectedDetail = productDetails.find((detail) => {
          const colorMatch = !formData.color ? detail.color === null : detail.color?.colorId === formData.color
          const sizeMatch = !formData.size ? detail.size === null : detail.size?.sizeId === formData.size
          return colorMatch && sizeMatch
        })

        setSelectedProductDetail(selectedDetail || null)

        // Reset color and size if they are not available
        if (!fetchedColors.some((color) => color.colorId === formData.color)) {
          setFormData((prev) => ({ ...prev, color: '' }))
        }
        if (!fetchedSizes.some((size) => size.sizeId === formData.size)) {
          setFormData((prev) => ({ ...prev, size: '' }))
        }
      } catch (error) {
        console.error('Error fetching product details:', error)
        showToastError('Failed to load product details')
        setAvailableColors([])
        setAvailableSizes([])
        setSelectedProductDetail(null)
      }
    }

    fetchProductDetails()
  }, [formData.productId, isEditMode, colors, sizes, setFormData])

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
    if (name === 'productId') {
      setFormData((prev) => ({ ...prev, color: '', size: '', quantity: 1 }))
      setSelectedProductDetail(null)
    }
  }

  const filteredProducts = products.filter((product) =>
    product.name.toLowerCase().includes(productSearch.toLowerCase())
  )

  const selectedColor = useMemo(
    () => availableColors.find((color) => color.colorId === formData.color),
    [availableColors, formData.color]
  )

  return (
    <div className='fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4'>
      <div className='bg-white rounded-xl shadow-xl p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto'>
        <div className='flex justify-between items-center mb-4'>
          <h2 className='text-xl font-semibold text-blue-800'>
            {isEditMode ? 'Update Request' : 'Create New Request'}
          </h2>
          <button onClick={onClose} className='text-gray-500 hover:text-red-500 transition p-1 rounded-full'>
            <FaTimes size={20} />
          </button>
        </div>

        {selectedProduct && (
          <div className='mb-4'>
            <h3 className='text-sm font-semibold text-gray-800'>Selected Product: {selectedProduct.name}</h3>
          </div>
        )}

        {userData && (
          <ShippingAddress addresses={userData.addresses || []} setSelectedShippingInfo={setSelectedShippingInfo} />
        )}

        <form onSubmit={onSubmit} className='space-y-4 mt-4'>
          <div className='bg-gray-100 p-4 rounded-xl border border-gray-200'>
            <h3 className='text-sm font-semibold text-gray-800 mb-3'>Product Details</h3>
            <div className='grid grid-cols-1 md:grid-cols-2 gap-3'>
              <div className='space-y-1'>
                <label className='block text-sm font-medium text-gray-700'>Search Product</label>
                <input
                  type='text'
                  placeholder='Search for a product...'
                  value={productSearch}
                  onChange={(e) => setProductSearch(e.target.value)}
                  className='w-full p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                />
                <select
                  name='productId'
                  value={formData.productId}
                  onChange={handleInputChange}
                  className='w-full p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                  required
                >
                  <option value=''>Select a product</option>
                  {filteredProducts.map((product) => (
                    <option key={product.productId} value={product.productId}>
                      {product.name}
                    </option>
                  ))}
                </select>
              </div>

              <div className='grid grid-cols-2 gap-2'>
                <div className='space-y-1'>
                  <label className='block text-sm font-medium text-gray-700'>Color</label>
                  <div className='flex items-center gap-2'>
                    <select
                      name='color'
                      value={formData.color}
                      onChange={handleInputChange}
                      className='w-3/4 p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                      disabled={!formData.productId || availableColors.length === 0}
                    >
                      <option value=''>Select a color</option>
                      {availableColors.length > 0 ? (
                        availableColors.map((color) => (
                          <option key={color.colorId} value={color.colorId}>
                            {color.colorId || color.colorId}
                          </option>
                        ))
                      ) : (
                        <option value='' disabled>
                          Not Available
                        </option>
                      )}
                    </select>
                    {selectedColor && (
                      <div
                        style={{
                          backgroundColor: selectedColor.hex,
                          width: 20,
                          height: 20,
                          borderRadius: '50%',
                          border: '1px solid #ccc'
                        }}
                        title={selectedColor.colorId || selectedColor.colorId}
                      ></div>
                    )}
                  </div>
                </div>
                <div className='space-y-1'>
                  <label className='block text-sm font-medium text-gray-700'>Size</label>
                  <select
                    name='size'
                    value={formData.size}
                    onChange={handleInputChange}
                    className='w-full p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                    disabled={!formData.productId || availableSizes.length === 0}
                  >
                    <option value=''>Select a size</option>
                    {availableSizes.length > 0 ? (
                      availableSizes.map((size) => (
                        <option key={size.sizeId} value={size.sizeId}>
                          {size.name}
                        </option>
                      ))
                    ) : (
                      <option value='' disabled>
                        Not Available
                      </option>
                    )}
                  </select>
                </div>
              </div>
            </div>
          </div>

          <div className='bg-gray-100 p-4 rounded-xl border border-gray-200'>
            <h3 className='text-sm font-semibold text-gray-800 mb-3'>Order Details</h3>
            <div className='grid grid-cols-1 md:grid-cols-2 gap-4'>
              <div className='space-y-1'>
                <label className='block text-sm font-medium text-gray-700'>Quantity</label>
                <input
                  type='number'
                  name='quantity'
                  value={formData.quantity}
                  onChange={handleInputChange}
                  min='1'
                  max={selectedProductDetail?.stockQuantity || 100}
                  className='w-full p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                  required
                />
              </div>
              <div className='space-y-1'>
                <label className='block text-sm font-medium text-gray-700'>Delivery Date</label>
                <input
                  type='date'
                  name='deliveryDate'
                  value={formData.deliveryDate}
                  onChange={handleInputChange}
                  className='w-full p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                  required
                />
              </div>
            </div>
          </div>

          <div className='bg-gray-100 p-4 rounded-xl border border-gray-200'>
            <h3 className='text-sm font-semibold text-gray-800 mb-3'>Voucher</h3>
            <div className='space-y-1'>
              <label className='block text-sm font-medium text-gray-700'>Select Voucher</label>
              <select
                name='voucherId'
                value={formData.voucherId}
                onChange={handleInputChange}
                className='w-full p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
              >
                <option value=''>Select a voucher</option>
                {vouchers?.map((voucher) => (
                  <option key={voucher.voucherId} value={voucher.voucherId}>
                    {voucher.code} (-{voucher.discount}%)
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className='bg-gray-100 p-4 rounded-xl border border-gray-200'>
            <h3 className='text-sm font-semibold text-gray-800 mb-3'>Additional Notes</h3>
            <div className='space-y-1'>
              <label className='block text-sm font-medium text-gray-700'>Notes</label>
              <textarea
                name='notes'
                value={formData.notes}
                onChange={handleInputChange}
                className='w-full p-2 border rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500'
                rows={3}
                placeholder='Additional notes for the order (if any)...'
              />
            </div>
          </div>

          <div className='flex justify-end gap-3 pt-4'>
            <button
              type='button'
              onClick={onClose}
              className='px-4 py-2 bg-gray-200 text-gray-700 rounded hover:bg-gray-300 text-sm'
            >
              Cancel
            </button>
            <button
              type='submit'
              className='px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 text-sm disabled:bg-blue-300'
            >
              {isEditMode ? 'Update Request' : 'Submit Request'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default RequestForm
