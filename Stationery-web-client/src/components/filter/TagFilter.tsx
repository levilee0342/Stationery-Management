import React from 'react'
import { useSearchParams } from 'react-router-dom'

type Props = {
  currentParams: { sortBy: keyof typeof filters }
}
const filters = {
  bestSeller: 'soldQuantity',
  isNew: 'createdAt'
}
const reverseFilters = {
  soldQuantity: 'bestSeller',
  createdAt: 'isNew'
}
const TagFilter: React.FC<Props> = ({ currentParams }) => {
  const [, setSearchParams] = useSearchParams()
  return (
    <div className='mb-4'>
      <label className='block font-medium mb-2'>Tags</label>
      {(['bestSeller', 'isNew'] as (keyof typeof filters)[]).map((filter) => (
        <button
          key={filter}
          className={`w-full mb-2 p-2 rounded-lg text-center text-sm font-medium transition
            ${filter == reverseFilters[currentParams.sortBy as keyof typeof reverseFilters] ? 'bg-blue-500 text-white' : 'bg-gray-200 hover:bg-gray-300'}`}
          onClick={() => setSearchParams({ ...currentParams, sortBy: filters[filter] })}
        >
          {filter === 'bestSeller' ? 'Best Seller' : filter === 'isNew' ? 'New Product' : 'Discount'}
        </button>
      ))}
    </div>
  )
}

export default TagFilter
