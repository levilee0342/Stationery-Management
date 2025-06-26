import React from 'react'
import Card from '~/components/card/Card'
import { Product } from '~/types/product'

type ProductListProps = {
  products: Product[] | null
  loading: boolean
  error: string | null
  onViewDetails: (product: Product) => void
}

// Skeleton component (UI loading dạng card)
const ProductSkeleton = () => (
  <div className='bg-white rounded-lg shadow-md p-4 text-center animate-pulse'>
    <div className='w-full h-64 bg-gray-300 rounded mb-4'></div>
    <div className='h-4 bg-gray-300 rounded w-3/4 mx-auto mb-2'></div>
    <div className='h-4 bg-gray-300 rounded w-1/2 mx-auto'></div>
  </div>
)

export const ProductListDepartment: React.FC<ProductListProps> = ({ products, loading, error, onViewDetails }) => {
  return (
    <div className='w-full'>
      <h1 className='text-4xl font-bold text-blue-800 mb-6 text-center'>Product List</h1>

      {/* Skeleton loading khi đang tải dữ liệu */}
      {loading && (
        <div className='flex flex-wrap gap-3'>
          {Array.from({ length: 8 }).map((_, index) => (
            <div key={index} className='lg:w-[calc(25%-9px)] md:w-[calc(33.33%-8px)] sm:w-[calc(50%-12px)] w-full'>
              <ProductSkeleton />
            </div>
          ))}
        </div>
      )}

      {/* Hiển thị lỗi nếu có */}
      {!loading && error && <p className='text-red-600 text-center text-lg'>Error: {error}</p>}

      {/* Hiển thị sản phẩm khi tải xong */}
      {!loading && !error && products && (
        <div className='flex flex-wrap gap-3'>
          {products.map((product) => (
            <div
              key={product.productId}
              className='lg:w-[calc(25%-9px)] md:w-[calc(33.33%-8px)] sm:w-[calc(50%-12px)] w-full'
            >
              <Card product={product} onViewDetails={() => onViewDetails(product)} />
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
