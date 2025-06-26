import React, { useMemo } from 'react'
import { useSearchParams } from 'react-router-dom'
import { useAppSelector } from '~/hooks/redux'
import { ProductSearchParams } from '~/types/filter'

const CategoryFilter: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const currentParams = useMemo(() => Object.fromEntries([...searchParams]) as ProductSearchParams, [searchParams])
  const { categories } = useAppSelector((state) => state.category)
  return (
    <div className='mb-4'>
      <label className='block font-medium mb-2'>Product Category</label>
      <select
        className='w-full p-2 border rounded-lg'
        value={currentParams.categoryId}
        onChange={(e) => {
          setSearchParams({ ...currentParams, categoryId: e.target.value })
        }}
      >
        <option key={'none'} value={''}>
          None
        </option>
        {categories.map((category) => (
          <option key={category.categoryId} value={category.categoryId}>
            {category.categoryName}
          </option>
        ))}
      </select>
    </div>
  )
}

export default CategoryFilter
