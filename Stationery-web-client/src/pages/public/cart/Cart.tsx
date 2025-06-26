import React, { useEffect, useState } from 'react'
import { X, Trash2, Plus, Minus } from 'lucide-react'
import { CartItem } from '~/types/cart'
import { apiRemoveCartItem, apiUpdateCartItem } from '~/api/cart'
import { useNavigate } from 'react-router-dom'
import { fetchMyCart } from '~/store/actions/cart'
import { useAppDispatch } from '~/hooks/redux'
import { cartActions } from '~/store/slices/cart' // Import cartActions

interface CartProps {
  isOpen: boolean
  onClose: () => void
  cartItems: CartItem[]
  accessToken: string
}

const Cart: React.FC<CartProps> = ({ isOpen, onClose, cartItems, accessToken }) => {
  const navigate = useNavigate()
  const dispatch = useAppDispatch()
  const [selectedItems, setSelectedItems] = useState<string[]>([])
  const [loadingItemId, setLoadingItemId] = useState<string | null>(null)
  const [totalPrice, setTotalPrice] = useState(0)

  const handleQuantityChange = async (productDetailId: string, newQuantity: number) => {
    if (newQuantity < 1) return
    setLoadingItemId(productDetailId)

    const item = cartItems.find((i) => i.productDetailId === productDetailId)

    if (!item) {
      console.error('Item not found in cartItems')
      setLoadingItemId(null)
      return
    }

    const res = await apiUpdateCartItem({
      productDetailId: productDetailId,
      quantity: newQuantity,
      colorId: item.colorName,
      sizeId: item.sizeName,
      accessToken
    })

    setLoadingItemId(null)
    if (typeof res !== 'string') {
      dispatch(fetchMyCart({ accessToken }))
    }
  }

  const handleRemove = async (productDetailId: string) => {
    setLoadingItemId(productDetailId)
    await apiRemoveCartItem({ productDetailId: productDetailId, accessToken })
    setLoadingItemId(null)
    dispatch(fetchMyCart({ accessToken }))
  }

  const toggleSelectItem = (productDetailId: string) => {
    setSelectedItems((prev) =>
      prev.includes(productDetailId) ? prev.filter((id) => id !== productDetailId) : [...prev, productDetailId]
    )
  }

  const handleCheckout = () => {
    const orderItems = cartItems.filter((item) => selectedItems.includes(item.productDetailId))
    const order = {
      items: orderItems,
      totalAmount: totalPrice
    }

    console.log('cart__', order)
    onClose()
    navigate(`/products/payment-confirmation`, { state: { order } })
  }

  useEffect(() => {
    if (isOpen) {
      const allIds = cartItems.map((item) => item.productDetailId)
      setSelectedItems(allIds)
    }
  }, [cartItems, isOpen])

  useEffect(() => {
    const totalPrice = cartItems
      .filter((item) => selectedItems.includes(item.productDetailId))
      .reduce((total, item) => {
        return total + item.discountPrice * item.quantity
      }, 0)
    setTotalPrice(totalPrice)
  }, [selectedItems, cartItems])

  return (
    <>
      {isOpen && <div className='fixed inset-0 bg-black bg-opacity-50 z-40' onClick={onClose} />}
      <div
        className={`fixed top-0 right-0 h-full w-full sm:w-[400px] bg-white shadow-2xl transform transition-transform duration-300 ease-in-out z-50 ${
          isOpen ? 'translate-x-0' : 'translate-x-full'
        }`}
      >
        <div className='flex flex-col h-full'>
          <div className='p-4 border-b flex justify-between items-center'>
            <h2 className='text-xl font-bold'>🛒 Shopping Cart</h2>
            <button
              onClick={() => {
                dispatch(cartActions.toggleCart()) // Toggle giỏ hàng khi đóng
              }}
              className='p-1 hover:bg-gray-100 rounded-full'
            >
              <X size={24} />
            </button>
          </div>

          <div className='flex-1 overflow-y-auto p-4 space-y-4'>
            {cartItems.length > 0 && (
              <div className='flex justify-end mb-2'>
                <button
                  onClick={() => {
                    if (selectedItems.length === cartItems.length) {
                      setSelectedItems([])
                    } else {
                      setSelectedItems(cartItems.map((item) => item.productDetailId))
                    }
                  }}
                  className='text-sm text-blue-600 hover:underline'
                >
                  {selectedItems.length === cartItems.length ? 'Deselect All' : 'Select All'}
                </button>
              </div>
            )}
            {cartItems.length === 0 ? (
              <p className='text-gray-500 text-center'>Your cart is currently empty.</p>
            ) : (
              cartItems.map((item) => (
                <div
                  key={item.productDetailId}
                  className={`flex items-start gap-4 py-4 border-b ${
                    loadingItemId === item.productDetailId ? 'opacity-50 pointer-events-none' : ''
                  }`}
                >
                  <input
                    type='checkbox'
                    checked={selectedItems.includes(item.productDetailId)}
                    onChange={() => toggleSelectItem(item.productDetailId)}
                    className='mt-2 accent-blue-600'
                  />
                  <div className='w-20 h-20 border rounded overflow-hidden'>
                    <img src={item.imageUrl} alt={item.productName} className='w-full h-full object-cover' />
                  </div>
                  <div className='flex-1'>
                    <div className='flex justify-between items-start'>
                      <h3 className='text-base font-semibold'>{item.productName}</h3>
                      <button
                        className='text-gray-400 hover:text-red-500'
                        onClick={() => handleRemove(item.productDetailId)}
                        disabled={loadingItemId === item.productDetailId}
                      >
                        <Trash2 size={18} />
                      </button>
                    </div>
                    <p className='text-sm text-gray-500 mb-1'>
                      Color: {item.colorName} | Size: {item.sizeName}
                    </p>
                    <p className='text-sm text-gray-600'>
                      <span className='line-through text-red-400 mr-1'>
                        ₫{item.originalPrice.toLocaleString('vi-VN')}
                      </span>
                      <span className='text-green-600 font-medium'>₫{item.discountPrice.toLocaleString('vi-VN')}</span>
                    </p>
                    <div className='flex items-center gap-2 mt-2'>
                      <button
                        onClick={() => handleQuantityChange(item.productDetailId, item.quantity - 1)}
                        className='w-7 h-7 bg-gray-200 rounded-full flex justify-center items-center disabled:opacity-50'
                        disabled={loadingItemId === item.productDetailId || item.quantity <= 1}
                      >
                        <Minus size={16} />
                      </button>
                      <span>{item.quantity}</span>
                      <button
                        onClick={() => handleQuantityChange(item.productDetailId, item.quantity + 1)}
                        className='w-7 h-7 bg-gray-200 rounded-full flex justify-center items-center disabled:opacity-50'
                        disabled={loadingItemId === item.productDetailId}
                      >
                        <Plus size={16} />
                      </button>
                    </div>
                    <p className='font-bold text-blue-600 mt-1'>
                      Total: ₫{(item.discountPrice * item.quantity).toLocaleString('vi-VN')}
                    </p>
                    <p className='text-xs text-gray-400 mt-1'>
                      Added: {new Date(item.createdAt).toLocaleString('en-GB')}
                    </p>
                  </div>
                </div>
              ))
            )}
          </div>

          <div className='p-4 border-t'>
            <div className='flex justify-between items-center mb-4'>
              <span className='text-base font-semibold'>Total Amount:</span>
              <span className='text-lg font-bold text-red-600'>₫{totalPrice.toLocaleString('vi-VN')}</span>
            </div>
            <button
              onClick={handleCheckout}
              className='w-full bg-blue-600 text-white py-3 rounded-lg text-base font-semibold hover:bg-blue-700 transition-colors'
            >
              Checkout Now
            </button>
          </div>
        </div>
      </div>
    </>
  )
}

export default Cart
