import { useEffect, useState } from 'react'
import { AxiosError } from 'axios'
import { apiGetAllProductsWithDefaultPD } from '~/api/product'
import ProductSection from '~/components/productSection/ProductSection'
import { showToastError } from '~/utils/alert'
import { Product } from '~/types/product'

const NewProduct = () => {
  const [products, setProducts] = useState<Product[]>([])
  const [totalPageCount, setTotalPageCount] = useState(0)
  const [currentPage, setCurrentPage] = useState(1)
  const getNewProducts = async ({ currentPage }: { currentPage: string }) => {
    try {
      const response = await apiGetAllProductsWithDefaultPD({
        page: currentPage,
        limit: '4',
        sortBy: 'soldQuantity'
      })
      if (response.code == 200) {
        setProducts(response.result.content)
        setTotalPageCount(response.result.page.totalPages)
      } else {
        showToastError(response.message || response.error)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }

  useEffect(() => {
    getNewProducts({ currentPage })
  }, [currentPage])
  return (
    <ProductSection
      title='Best Sales'
      products={products}
      totalPageCount={totalPageCount}
      currentPage={currentPage}
      setCurrentPage={setCurrentPage}
    />
  )
}

export default NewProduct
