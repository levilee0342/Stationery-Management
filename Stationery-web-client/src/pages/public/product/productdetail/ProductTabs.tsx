// components/product/ProductTabs.tsx
import React, { useState } from 'react'
import CommentContainer from '~/components/comment/CommentContainer'
import { Review } from '~/types/comment'

type ProductTabsProps = {
  pId?: string
  reviews?: Review[]
  desc?: string
  totalRating?: number
  setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>
}

export const ProductTabs: React.FC<ProductTabsProps> = ({ pId, reviews, desc, totalRating, setFetchAgain }) => {
  const [activeTab, setActiveTab] = useState('description')
  return (
    <div className='mt-8'>
      <div className='flex border-b'>
        <button
          className={`px-4 py-2 font-semibold ${
            activeTab === 'description' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'
          }`}
          onClick={() => setActiveTab('description')}
        >
          Product Description
        </button>
        <button
          className={`px-4 py-2 font-semibold ${
            activeTab === 'reviews' ? 'border-b-2 border-blue-600 text-blue-600' : 'text-gray-500'
          }`}
          onClick={() => setActiveTab('reviews')}
        >
          Customer Reviews
        </button>
      </div>
      <div className='p-6'>
        {activeTab === 'description' && <div className='text-gray-700 text-base'>{desc}</div>}
        <CommentContainer pId={pId} comments={reviews} totalRating={totalRating} setFetchAgain={setFetchAgain} />
      </div>
    </div>
  )
}
