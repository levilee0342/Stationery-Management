import moment from 'moment'
import { AxiosError } from 'axios'
import React, { Fragment, useEffect, useMemo, useState } from 'react'
import { FaEdit, FaLock, FaLockOpen, FaSearch } from 'react-icons/fa'
import Select from 'react-select'
import { ListProductDetail, ProductDetail, ProductDetailForm } from '~/types/product'
import EditProductModal from '../modal/EditProductModal'
import EditProductDetailModal from '../modal/EditProductDetailModal'
import {
  apiGetAllProductsAdmin,
  apiGetProductDetailsByProductId,
  apiUpdateHiddenProduct,
  apiUpdateProduct
} from '~/api/product'
import { showToastError, showToastSuccess } from '~/utils/alert'
import { useAppSelector } from '~/hooks/redux'
import Pagination from '~/components/pagination/Pagination'
import { categoriesToOptions } from '~/utils/optionSelect'
import { ProductSearchParams } from '~/types/filter'
import { useSearchParams } from 'react-router-dom'
import ProductDetailTable from './ProductDetailTable'
import Swal from 'sweetalert2'
import { useDebounce } from '~/hooks/useDebounce'
interface ProductListProps {
  sizes: { value: string; label: string }[]
  colors: { value: string; label: string; color: string }[]
}
const tableHeaderTitleList = [
  '#',
  'Image',
  'Name',
  'Description',
  'Category',
  'Quantity',
  'Sold',
  'Rating',
  'Created Date',
  'Actions'
]
const ProductList: React.FC<ProductListProps> = ({ sizes, colors }) => {
  const { categories } = useAppSelector((state) => state.category)
  const { accessToken } = useAppSelector((state) => state.user)
  const [products, setProducts] = useState<ListProductDetail[]>([])
  const [searchParams, setSearchParams] = useSearchParams()
  const currentParams = useMemo(() => Object.fromEntries([...searchParams]) as ProductSearchParams, [searchParams])
  const [selectedProduct, setSelectedProduct] = useState<string[]>([])
  const [editProduct, setEditProduct] = useState<ListProductDetail | null>(null)
  const [searchTerm, setSearchTerm] = useState(currentParams.search || '')
  const debouncedSearchTerm = useDebounce(searchTerm, 500)
  const [editProductDetail, setEditProductDetail] = useState<{
    pId: string
    pdId: string
    detail: ProductDetailForm
  } | null>(null)
  const [totalPageCount, setTotalPageCount] = useState(0)
  const [listCategories, setListCategories] = useState<{ label: string; value: string }[]>([])

  const handleEditProduct = (product: ListProductDetail) => {
    setEditProduct(product)
  }

  const handleEditProductDetail = (pId: string, pdId: string, detail: ProductDetailForm) => {
    setEditProductDetail({ pId, pdId, detail })
  }

  const handleRowClick = (product: ListProductDetail) => {
    setSelectedProduct((prev) =>
      prev.includes(product.productId) ? prev.filter((id) => id !== product.productId) : [...prev, product.productId]
    )
  }

  const hanleEditProduct = async (data: {
    productId: string
    name: string
    description: string
    categoryId: string
  }) => {
    try {
      if (!accessToken) {
        showToastError('Please login to perform this action.')
        return
      }
      const response = await apiUpdateProduct({ data, accessToken })
      if (response.code == 200) {
        showToastSuccess('Update product successfully')
        setProducts(
          products.map((p) => {
            if (p.productId == response.result.productId) {
              return response.result
            } else {
              return p
            }
          })
        )
      } else {
        showToastError(response.message)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }
  const getAllProducDetails = async ({ productId }: { productId: string }): Promise<ProductDetail[] | []> => {
    try {
      const response = await apiGetProductDetailsByProductId({
        productId
      })
      if (response.code == 200) {
        return response.result
      } else {
        showToastError(response.message)
        return []
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
    return []
  }
  const getAllProducts = async (currentParams: ProductSearchParams) => {
    try {
      if (!accessToken) {
        showToastError('Please login to perform this action.')
        return
      }
      const { page, search, categoryId } = currentParams
      const response = await apiGetAllProductsAdmin({
        page: page || '0',
        search: search,
        categoryId: categoryId,
        limit: '10',
        accessToken
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
  const handleUpdateHidden = async (productId: string, hidden: boolean) => {
    try {
      if (!accessToken) {
        showToastError('Please login to perform this action.')
        return
      }
      const result = await Swal.fire({
        title: 'Do you want to change the hidden status?',
        icon: 'info',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Đồng ý'
      })
      if (result.isDismissed) return
      const response = await apiUpdateHiddenProduct({
        productId,
        hidden,
        accessToken
      })
      if (response.code == 200) {
        setProducts((prev) => prev.map((item) => (item.productId === productId ? { ...item, hidden } : item)))
        showToastSuccess(`Product ${hidden ? 'hidden' : 'unhidden'} successfully`)
      } else {
        showToastError(response.message)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }
  useEffect(() => {
    getAllProducts(currentParams)
  }, [currentParams])
  useEffect(() => {
    setListCategories(categoriesToOptions(categories))
  }, [categories])
  useEffect(() => {
    const newParams = { ...currentParams, page: '1' }
    if (debouncedSearchTerm.trim()) {
      newParams.search = debouncedSearchTerm
    } else {
      delete newParams.search
    }
    setSearchParams(newParams)
  }, [debouncedSearchTerm])
  return (
    <div>
      <div className='flex gap-4 mb-6'>
        <div className='relative w-1/3'>
          <span className='absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400'>
            <FaSearch />
          </span>
          <input
            type='text'
            placeholder='Search product...'
            onChange={(e) => {
              setSearchTerm(e.target.value)
            }}
            value={searchTerm}
            className='pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
          />
        </div>
        <div className='w-1/4'>
          <Select
            options={listCategories}
            isSearchable
            isClearable
            value={listCategories.find((item) => item.value === currentParams.categoryId) || null}
            onChange={(data) => {
              if (data) {
                setSearchParams(() => ({ ...currentParams, categoryId: data.value }))
              } else {
                setSearchParams(() => {
                  const newParams = { ...currentParams }
                  delete newParams.categoryId
                  return new URLSearchParams(newParams as Record<string, string>)
                })
              }
            }}
          />
        </div>
      </div>
      <div className='bg-white rounded-lg shadow-md overflow-hidden'>
        <div className='overflow-x-auto rounded-xl shadow-lg'>
          <table className='w-full border-collapse border border-blue-200'>
            <thead className='bg-blue-600 text-white text-left'>
              <tr>
                {tableHeaderTitleList.map((title) => (
                  <th key={title} className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>
                    {title}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className='divide-y divide-gray-200'>
              {products?.map((p, index) => {
                const isSelected = selectedProduct.includes(p.productId)
                return (
                  <Fragment key={p.productId}>
                    <tr
                      onClick={async () => {
                        handleRowClick(p)
                        if (isSelected) return
                        const productDetail: ProductDetail[] = await getAllProducDetails({ productId: p.productId })
                        setProducts((prev) =>
                          prev.map((item) =>
                            item.productId === p.productId ? { ...item, productDetails: productDetail } : item
                          )
                        )
                      }}
                      className='border-b border-teal-200 hover:bg-teal-50 transition-colors'
                    >
                      <td className='px-4 py-3 text-sm'>{index + 1}</td>
                      <td className='px-4 py-3'>
                        <img className='w-12 h-12 rounded object-cover' src={p.img} alt={p.name} />
                      </td>
                      <td className='px-4 py-3 text-sm max-w-[200px]'>
                        <p className='line-clamp-2' title={p.name}>
                          {p.name}
                        </p>
                      </td>
                      <td className='px-4 py-3 text-sm max-w-[200px]'>
                        <p className='line-clamp-2' title={p.description}>
                          {p.description}
                        </p>
                      </td>
                      <td className='px-4 py-3 text-sm'>{p.category.categoryName}</td>
                      <td className='px-4 py-3 text-sm'>{p.quantity.toLocaleString('vi-VN')}</td>
                      <td className='px-4 py-3 text-sm'>{p.soldQuantity.toLocaleString('vi-VN')}</td>
                      <td className='px-4 py-3 text-sm'>{p.totalRating}</td>
                      <td className='px-4 py-3 text-sm'>{moment(p.createdAt).format('DD/MM/YYYY HH:mm')}</td>
                      <td className='px-4 py-3'>
                        <div className='flex items-center gap-3'>
                          <button
                            onClick={(e) => {
                              e.stopPropagation()
                              handleEditProduct(p)
                            }}
                            className='bg-yellow-400 text-white p-2 rounded-lg hover:bg-yellow-500 transition-colors'
                          >
                            <FaEdit size={16} />
                          </button>
                          <button
                            onClick={() => handleUpdateHidden(p.productId, !p.hidden)}
                            className={`p-2.5 rounded-md transition-all shadow-sm hover:shadow flex items-center justify-center group relative ${
                              p.hidden
                                ? 'bg-red-100 text-red-600 hover:bg-red-200'
                                : 'bg-green-100 text-green-600 hover:bg-green-200'
                            }`}
                            title={p.hidden ? 'Unhide product' : 'Hide product'}
                          >
                            {p.hidden ? (
                              <FaLock size={16} className='group-hover:scale-110 transition-transform' />
                            ) : (
                              <FaLockOpen size={16} className='group-hover:scale-110 transition-transform' />
                            )}
                          </button>
                        </div>
                      </td>
                    </tr>
                    {isSelected && p.productDetails?.length > 0 && (
                      <ProductDetailTable
                        p={p}
                        accessToken={accessToken || ''}
                        setProducts={setProducts}
                        handleEditProductDetail={handleEditProductDetail}
                      />
                    )}
                  </Fragment>
                )
              })}
            </tbody>
          </table>
        </div>
      </div>
      <Pagination totalPageCount={totalPageCount} />

      {/* Modal chỉnh sửa sản phẩm */}
      {editProduct && (
        <EditProductModal
          listCategories={listCategories}
          isOpen={!!editProduct}
          product={editProduct}
          onClose={() => setEditProduct(null)}
          onSave={hanleEditProduct}
        />
      )}

      {/* Modal chỉnh sửa chi tiết sản phẩm */}
      {editProductDetail && (
        <EditProductDetailModal
          sizes={sizes}
          colors={colors}
          setProducts={setProducts}
          isOpen={!!editProductDetail}
          productDetailId={editProductDetail.pdId}
          productId={editProductDetail.pId}
          detail={editProductDetail.detail}
          onClose={() => setEditProductDetail(null)}
        />
      )}
    </div>
  )
}

export default ProductList
