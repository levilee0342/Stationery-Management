import React, { useState } from 'react'
import Slider from 'rc-slider'
import 'rc-slider/assets/index.css'
import { useSearchParams } from 'react-router-dom'

type Props = {
  currentParams: { minPrice?: number; maxPrice?: number }
}
const PriceRange: React.FC<Props> = ({ currentParams }) => {
  const [, setSearchParams] = useSearchParams()
  const [priceRange, setPriceRange] = useState<{ minPrice: number; maxPrice: number }>({
    minPrice: currentParams.minPrice || 0,
    maxPrice: currentParams.maxPrice || 200000
  })
  const handleMinPriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newMin = Math.max(Number(e.target.value.replace(/^0+/, '')) || 0, 0)
    setSearchParams({ ...currentParams, minPrice: newMin.toString() })
    setPriceRange((prev) => ({ ...prev, minPrice: newMin }))
  }

  const handleMaxPriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newMax = Math.min(Number(e.target.value.replace(/^0+/, '')) || 0, 200000)
    setSearchParams({ ...currentParams, minPrice: newMax.toString() })
    setPriceRange((prev) => ({ ...prev, maxPrice: newMax }))
  }
  return (
    <div className='mb-4'>
      <label className='block font-medium mb-2'>Price Range:</label>
      <p className='mb-4 text-blue-500'>
        {priceRange.minPrice} - {priceRange.maxPrice} VNĐ
      </p>
      <div className='flex gap-2 mb-4'>
        <input
          type='text'
          className='w-1/2 p-2 border rounded-lg'
          value={priceRange.minPrice}
          onChange={handleMinPriceChange}
        />
        <input
          type='text'
          className='w-1/2 p-2 border rounded-lg'
          value={priceRange.maxPrice}
          onChange={handleMaxPriceChange}
        />
      </div>
      <Slider
        range
        min={0}
        max={200000}
        step={5000}
        value={[priceRange.minPrice, priceRange.maxPrice]}
        onChange={(val: number | number[]) => {
          if (Array.isArray(val)) {
            setSearchParams({
              ...currentParams,
              minPrice: val[0].toString(),
              maxPrice: val[1].toString()
            })
            setPriceRange({ minPrice: val[0], maxPrice: val[1] })
          }
        }}
      />
    </div>
  )
}

export default PriceRange
