import React from 'react'
import { useSearchParams } from 'react-router-dom'
import { Size } from '~/types/product'

type SizeSelectorProps = {
  sizes: Size[]
  currentParams: { sizeId?: string }
}

const SizeSelector: React.FC<SizeSelectorProps> = ({ sizes, currentParams }) => {
  const [, setSearchParams] = useSearchParams()
  const handleChangeSize = (sizeId: string) => {
    setSearchParams({ ...currentParams, sizeId: sizeId })
  }
  return (
    <div className='mt-4'>
      <label className='block text-gray-700 font-semibold'>Choose size:</label>
      <div className='flex gap-2 mt-2'>
        {sizes.map((size) => (
          <button
            key={size.sizeId}
            onClick={() => handleChangeSize(size.sizeId)}
            className={`px-4 py-2 border rounded-lg ${currentParams.sizeId === size.sizeId ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
          >
            {size.name}
          </button>
        ))}
      </div>
    </div>
  )
}

export default SizeSelector
