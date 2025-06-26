import React from 'react'
import CategoryFilter from '~/components/filter/CategoryFilter'
import PriceRange from '~/components/filter/PriceRange'
import SortByPrice from '~/components/filter/SortByPrice'
import TagFilter from '~/components/filter/TagFilter'
import { ProductSearchParams } from '~/types/filter'

type Coupon = {
  id: number
  discount: number
  minOrder: number
  code: string
  expiry: string
}

type Props = {
  currentParams: ProductSearchParams
  coupons: Coupon[]
  appliedCoupon: string | null
  onApplyDiscount: (code: string) => void
}

const FiltersDepartment: React.FC<Props> = ({ currentParams }) => {
  return (
    <div className='w-full p-4'>
      <div className='flex flex-row gap-16 mb-6 justify-center overflow-x-auto'>
        <div className='min-w-[400px]'>
          <CategoryFilter />
        </div>
        <div className='min-w-[200px]'>
          <PriceRange currentParams={currentParams} />
        </div>
        <div className='min-w-[200px]'>
          <SortByPrice currentParams={currentParams} />
        </div>
        <div className='min-w-[200px]'>
          <TagFilter currentParams={currentParams} />
        </div>
      </div>
    </div>
  )
}

export default FiltersDepartment
