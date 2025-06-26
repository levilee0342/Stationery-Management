import React from 'react'

type QuantitySelectorProps = {
  quantity: number
  onQuantityChange: (quantity: number) => void
  maxQuantity: number // Thêm prop maxQuantity
}

const QuantitySelector: React.FC<QuantitySelectorProps> = ({ quantity, onQuantityChange, maxQuantity }) => {
  return (
    <div className='mt-4 flex items-center gap-2'>
      <button
        onClick={() => onQuantityChange(Math.max(1, quantity - 1))}
        className='px-3 py-2 bg-gray-300 rounded-lg disabled:opacity-50'
        disabled={quantity <= 1}
      >
        -
      </button>
      <span className='px-4 py-2 bg-gray-100'>{quantity}</span>
      <button
        onClick={() => onQuantityChange(Math.min(maxQuantity, quantity + 1))}
        className='px-3 py-2 bg-gray-300 rounded-lg disabled:opacity-50'
        disabled={quantity >= maxQuantity}
      >
        +
      </button>
    </div>
  )
}

export default QuantitySelector
