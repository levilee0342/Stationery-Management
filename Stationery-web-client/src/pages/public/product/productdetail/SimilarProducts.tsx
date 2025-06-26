import React, { useEffect, useState } from 'react'
import { apiGetSimilarProducts } from '~/api/product'
import Card from '~/components/card/Card'
import { Product } from '~/types/product'

type SimilarProductsProps = {
  pId: string | undefined
  onAddToCart: (productId: string, colorId: string, sizeId: string, quantity: number) => Promise<void>
  onViewDetails: (id: number) => void
}

export const SimilarProducts: React.FC<SimilarProductsProps> = ({ pId, onAddToCart, onViewDetails }) => {
  const [similarProducts, setSimilarProducts] = useState<Product[]>([])

  useEffect(() => {
    const fetchSimilarProducts = async () => {
      try {
        if (!pId) return
        console.log('ID P', pId)
        const result = await apiGetSimilarProducts(pId)
        console.log('Kq', result)
        if (result.code == 200) {
          setSimilarProducts(result.result)
        }
      } catch (err) {
        console.error('Lỗi khi load sản phẩm tương tự:', err)
      }
    }

    console.log('similarProducts:', similarProducts)

    if (pId) {
      fetchSimilarProducts()
    }
  }, [pId])

  return (
    <div className='mt-12'>
      <h3 className='text-2xl font-bold text-center'>Similar Products</h3>
      <div className='grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6 mt-6'>
        {similarProducts?.map((product) => (
          <Card
            key={product.productId}
            product={product}
            onAddToCart={() =>
              onAddToCart(product.productId, product.productDetail.color.colorId, product.productDetail.size.sizeId, 1)
            }
            onViewDetails={() => onViewDetails(Number(product.productId))}
          />
        ))}
      </div>
    </div>
  )
}
