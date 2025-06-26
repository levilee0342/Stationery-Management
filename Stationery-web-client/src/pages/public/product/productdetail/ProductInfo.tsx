import React, { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import NumberToStart from '~/components/numberToStar/NumberToStart'
import QuantitySelector from '~/components/product_attributes/QuantitySelector'
import { ColorSize, SizeSlug } from '~/types/color'
import { ProductDetail } from '~/types/product'
import { showAlertError, showToastError, showToastSuccess } from '~/utils/alert'
import { calculatePercent, formatNumber, priceInPromotion } from '~/utils/helper'
import Voucher from '~/components/voucher/Voucher'
import { apiAddItemToCart } from '~/api/cart'
import { useAppDispatch } from '~/hooks/redux'
import { fetchMyCart } from '~/store/actions/cart'

type ProductInfoProps = {
  colorSize: ColorSize[] | []
  productDetail?: ProductDetail
  name?: string
  totalRating?: number
  accessToken: string
  productDetailId?: string
}

export const ProductInfo: React.FC<ProductInfoProps> = ({
  colorSize,
  name,
  productDetail,
  totalRating,
  accessToken,
  productDetailId
}) => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const [selectedColor, setSelectedColor] = useState<string>(productDetail?.color?.colorId ?? '')
  const [selectedSize, setSelectedSize] = useState<string>(productDetail?.size?.sizeId ?? '')
  const [sizes, setSizes] = useState<SizeSlug[]>([])
  const [quantity, setQuantity] = useState(1)

  useEffect(() => {
    setSelectedColor(productDetail?.color?.colorId ?? '')
    setSelectedSize(productDetail?.size?.sizeId ?? '')
    colorSize?.forEach((item) => {
      if (item.colorId === productDetail?.color?.colorId) {
        setSizes(item.sizes)
      }
    })
  }, [productDetail, colorSize])

  const handleAddToCart = async () => {
    if (!productDetailId) {
      showToastError('Missing product information')
      return
    }
    if (quantity > (productDetail?.stockQuantity ?? 0)) {
      showToastError(`Cannot add to cart: Only ${productDetail?.stockQuantity} items in stock`)
      return
    }

    try {
      const response = await apiAddItemToCart({
        productDetailId: productDetailId,
        quantity,
        accessToken
      })

      if (typeof response === 'string') {
        showToastError(response)
        return
      }
      dispatch(fetchMyCart({ accessToken }))
      showToastSuccess('Added to cart successfully!')
    } catch {
      showToastError('Added to cart failed!')
    }
  }

  const handleBuyNow = ({ quantity }: { quantity: number }) => {
    if (!productDetailId || !productDetail) {
      showAlertError('Missing product information')
      return
    }
    if (quantity > (productDetail?.stockQuantity ?? 0)) {
      showAlertError(`Cannot proceed: Only ${productDetail?.stockQuantity} items in stock`)
      return
    }

    const discountPrice = priceInPromotion(productDetail)
    const order = {
      items: [
        {
          productDetailId: productDetailId,
          quantity,
          colorId: productDetail.color?.colorId,
          colorName: productDetail.color?.name,
          sizeId: productDetail.size?.sizeId,
          sizeName: productDetail.size?.name,
          imageUrl: productDetail.images?.[0]?.url ?? '',
          productName: productDetail.name,
          originalPrice: productDetail.originalPrice,
          discountPrice: discountPrice,
          discountValue: productDetail.discountPrice - discountPrice,
          productId: productDetail.productId,
          productPromotion: productDetail.productPromotions.length > 0 ? productDetail.productPromotions : null
        }
      ],
      totalAmount: discountPrice * quantity
    }

    navigate(`/products/payment-confirmation`, { state: { order } })
  }

  return (
    <div className='w-full md:w-1/2'>
      <h1 className='text-2xl font-bold text-blue-700'>{name}</h1>
      <div className='flex'>
        <div className='flex-1 flex gap-3'>
          <p className='text-[19px] max-sm:text-xs font-semibold text-blue-500'>
            {formatNumber(priceInPromotion(productDetail))} ₫
          </p>
          <div className='lex-1 flex items-center'>
            {productDetail?.originalPrice !== 0 && (
              <>
                <p className='line-through text-[#6b7280] text-sm'>
                  {formatNumber(productDetail?.originalPrice ?? 0)}₫
                </p>
                <p className={`ml-1 text-red-400 text-sm font-bold`}>
                  - {calculatePercent(productDetail?.originalPrice, priceInPromotion(productDetail))}%
                </p>
              </>
            )}
          </div>
        </div>
        <div className='flex items-center text-sm'>
          <NumberToStart number={totalRating ?? 0} />
        </div>
      </div>
      <p className='mt-2'>Stock: {productDetail?.stockQuantity}</p>
      <div className='mt-4'>
        <label className='block text-gray-700 font-semibold'>Choose size:</label>
        <div className='flex gap-2 mt-2'>
          {sizes.map((size) =>
            size.size ? (
              <button
                key={size.size}
                onClick={() => {
                  setSelectedSize(size.size)
                  navigate(`/products/${size.slug}`)
                }}
                className={`px-3 py-1 border rounded-lg text-sm ${
                  selectedSize === size.size ? 'bg-blue-500 text-white' : 'bg-gray-200'
                }`}
              >
                {size.size}
              </button>
            ) : null
          )}
        </div>
      </div>
      <div className='flex text-gray-500 text-sm gap-2 mt-2'>
        {colorSize?.map((item) => (
          <div key={item.colorId} className='flex gap-3 mt-2'>
            <button
              onClick={() => {
                setSelectedColor(item.colorId)
                navigate(`/products/${item.sizes[0].slug}`)
              }}
              className={`w-5 h-5 rounded-full border-2 transition-all ${
                selectedColor === item.colorId
                  ? 'border-black scale-110 ring-1 ring-offset-1 ring-black'
                  : item.hex.toLowerCase() === '#ffffff' || item.hex.toLowerCase() === '#fff'
                    ? 'border-black'
                    : 'border-transparent'
              }`}
              style={{ backgroundColor: item.hex }}
            ></button>
          </div>
        ))}
      </div>

      <QuantitySelector
        quantity={quantity}
        onQuantityChange={setQuantity}
        maxQuantity={productDetail?.stockQuantity ?? 0} // Truyền stockQuantity
      />

      <div className='mt-4 flex gap-4'>
        <button
          className='bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg w-1/2'
          onClick={() => handleBuyNow({ quantity })}
        >
          Buy Now
        </button>
        <button
          className='bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded-lg w-1/2'
          onClick={handleAddToCart}
        >
          Add to Cart
        </button>
      </div>
      <div className='mt-4'>
        <Voucher promotion={productDetail?.productPromotions[0]?.promotion} isActive />
      </div>
    </div>
  )
}

export default ProductInfo
