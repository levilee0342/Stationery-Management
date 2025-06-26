import React from 'react'

const ProductSkeleton: React.FC = () => {
  return (
    <div className='animate-pulse bg-white p-4 rounded-xl shadow-md w-full flex flex-col justify-between'>
      <div className='bg-gray-300 h-40 rounded-md mb-4'></div>
      <div className='h-4 bg-gray-300 rounded w-3/4 mb-2'></div>
      <div className='h-4 bg-gray-300 rounded w-1/2'></div>
    </div>
  )
}

export default ProductSkeleton
