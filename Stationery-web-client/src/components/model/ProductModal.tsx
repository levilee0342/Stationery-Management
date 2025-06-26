import React, { useCallback, useState } from 'react'
import { FaTimes } from 'react-icons/fa'
import QuantitySelector from '../product_attributes/QuantitySelector'
import Button from '../button/Button'
import { Link } from 'react-router-dom'
import { Color } from '~/types/product'

interface ProductModalProps {
  isOpen: boolean
  onClose: () => void
  productName?: string
  productPrice?: number
  productDescription?: string
  selectedColor?: string
  productImage?: string
  colors: Color[]
  productId: string
}

const ProductModal: React.FC<ProductModalProps> = React.memo(
  ({
    isOpen,
    onClose,
    productName,
    productPrice,
    productDescription,
    selectedColor,
    productImage,
    colors,
    productId
  }) => {
    if (!isOpen) return null

    const handleModalClick = useCallback((e: React.MouseEvent) => {
      e.stopPropagation()
    }, [])

    const handleBackdropClick = useCallback(
      (e: React.MouseEvent) => {
        if (e.target === e.currentTarget) {
          onClose()
        }
      },
      [onClose]
    )

    const [quantity, setQuantity] = useState(1)
    const [currentColor, setCurrentColor] = useState(selectedColor) // New state for selected color
    const [currentImage, setCurrentImage] = useState(productImage) // New state for product image

    const handleColorClick = (color: { name: string; hex: string; image: string }) => {
      setCurrentColor(color.name)
      setCurrentImage(color.image) // Change product image based on selected color
    }

    return (
      <div
        className='fixed inset-0 bg-black bg-opacity-60 flex justify-center items-center z-50'
        onClick={handleBackdropClick}
      >
        <div className='bg-white p-6 rounded-lg shadow-lg max-w-4xl w-full relative' onClick={handleModalClick}>
          <div className='absolute top-2 right-2'>
            <button onClick={onClose} className='text-gray-500 hover:text-gray-700'>
              <FaTimes size={24} />
            </button>
          </div>

          <div className='flex flex-col md:flex-row items-center space-x-4'>
            <img
              src={currentImage} // Use currentImage state for the product image
              alt={productName}
              className='w-80 h-80 object-cover rounded-lg shadow-md mb-4 md:mb-0'
              loading='lazy'
            />

            <div className='flex-1'>
              <h2 className='text-3xl font-semibold'>{productName}</h2>

              <div className='mt-4 flex items-center text-blue-500 text-xl font-semibold'>
                <span className='mr-2'>${productPrice?.toFixed(2)}</span>
              </div>

              <div className='mt-4'>
                <p className='text-lg text-gray-700'>{productDescription}</p>
              </div>

              <div className='mt-4'>
                <h4 className='font-semibold'>Selected Color: {currentColor || 'None'}</h4>
              </div>

              <div className='mt-4'>
                <h4 className='font-semibold'>Available Colors:</h4>
                <div className='flex gap-3 mt-2'>
                  {colors.map((color) => (
                    <div
                      key={color.name}
                      className={`w-10 h-10 rounded-full border-2 cursor-pointer ${
                        currentColor === color.name ? 'border-black' : 'border-transparent'
                      }`}
                      style={{ backgroundColor: color.hex }}
                      onClick={() => handleColorClick(color)} // Set color and image on click
                    ></div>
                  ))}
                </div>
              </div>

              {/* Quantity Selector */}
              <div className='mt-4'>
                <h4 className='font-semibold'>Quantity:</h4>
                <QuantitySelector quantity={quantity} onQuantityChange={setQuantity} />
              </div>
            </div>
          </div>

          <div className='mt-6 flex justify-center gap-4'>
            <Button className='bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg w-1/2'>
              <Link to={`/products/payment/${productId}`}>Buy Now</Link>
            </Button>
          </div>
        </div>
      </div>
    )
  }
)

export default ProductModal
