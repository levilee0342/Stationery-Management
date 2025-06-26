import React from 'react'
import { useSearchParams } from 'react-router-dom'

type Props = {
  currentParams: { sortBy?: string }
}

const SortByPrice: React.FC<Props> = ({ currentParams }) => {
  const [, setSearchParams] = useSearchParams()
  return (
    <div className='mb-4'>
      <label className='block font-medium mb-2'>Sort by Price</label>
      <select
        className='w-full p-2 border rounded-lg'
        value={currentParams.sortBy || ''}
        onChange={(e) => {
          setSearchParams({ ...currentParams, sortBy: e.target.value })
        }}
      >
        <option value=''>Default</option>
        <option value='-minPrice'>Ascending</option>
        <option value='minPrice'>Descending</option>
      </select>
    </div>
  )
}

export default SortByPrice
