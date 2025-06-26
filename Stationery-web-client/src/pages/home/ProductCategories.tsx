import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { fetchCategories } from '~/store/actions/category'
const ProductCategories: React.FC = () => {
  const dispatch = useAppDispatch()
  const { categories, isLoading, isError } = useAppSelector((state) => state.category)
  const navigate = useNavigate()
  useEffect(() => {
    dispatch(fetchCategories())
  }, [])

  return (
    <section className='py-8 px-4 bg-gray-100'>
      <div className='w-main mx-auto'>
        <h2 className='text-2xl font-bold text-blue-800 mb-6 text-center'>Product Categories</h2>

        <div className='grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4'>
          {isLoading || isError
            ? // Skeleton loading khi đang tải hoặc khi có lỗi
              Array.from({ length: 20 }).map((_, index) => (
                <div key={index} className='bg-white rounded-lg shadow-md p-4 text-center animate-pulse'>
                  <div className='w-14 h-14 bg-gray-300 rounded-full mx-auto mb-2'></div>
                  <div className='h-4 bg-gray-300 rounded w-3/4 mx-auto'></div>
                </div>
              ))
            : // Danh sách danh mục khi tải xong
              categories.map((category) => (
                <div
                  key={category.categoryId}
                  className='bg-white rounded-lg shadow-md p-4 text-center hover:shadow-lg transition-transform transform hover:scale-105 active:scale-95 cursor-pointer'
                  aria-label={category.categoryName}
                  onClick={() => navigate(`/product?categoryId=${category.categoryId}`)}
                >
                  <div
                    style={{ backgroundColor: category.bgColor }}
                    className='w-14 h-14 text-white rounded-full flex items-center justify-center mx-auto mb-2 text-2xl shadow-lg'
                  >
                    {category.icon}
                  </div>
                  <p className='text-sm font-medium text-gray-700'>{category.categoryName}</p>
                </div>
              ))}
        </div>
      </div>
    </section>
  )
}

export default ProductCategories
