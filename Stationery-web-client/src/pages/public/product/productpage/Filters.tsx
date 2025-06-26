import React from 'react'
import { NavigateOptions, URLSearchParamsInit } from 'react-router-dom'
import CategoryFilter from '~/components/filter/CategoryFilter'
import PriceRange from '~/components/filter/PriceRange'
import SortByPrice from '~/components/filter/SortByPrice'
import TagFilter from '~/components/filter/TagFilter'
import ListVouchers from '~/components/voucher/ListVouchers'
import Voucher from '~/components/voucher/ListVouchers'
import { ProductSearchParams } from '~/types/filter'

type FiltersProps = {
  coupons: { id: number; discount: number; minOrder: number; code: string; expiry: string }[]
  appliedCoupon: string | null
  onApplyDiscount: (code: string) => void
  currentParams: ProductSearchParams
}
const Filters: React.FC<FiltersProps> = ({ coupons, appliedCoupon, onApplyDiscount, currentParams }) => {
  return (
    <aside className='w-1/5 bg-gray-100 p-4 rounded-lg shadow'>
      <h2 className='text-xl text-blue-700 font-semibold mb-4'>Product Filters</h2>

      <CategoryFilter />
      <SortByPrice currentParams={currentParams} />
      <PriceRange currentParams={currentParams} />
      <TagFilter currentParams={currentParams} />

      <ListVouchers coupons={coupons} onApplyDiscount={onApplyDiscount} />
      {appliedCoupon && (
        <div className='mt-4 p-3 bg-green-100 border border-green-400 text-green-700 rounded-lg'>
          Applied Coupon: <strong>{appliedCoupon}</strong>
        </div>
      )}
    </aside>
  )
}

export default Filters
