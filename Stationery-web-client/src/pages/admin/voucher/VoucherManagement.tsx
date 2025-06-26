import { useEffect, useMemo, useState } from 'react'
import { FaPlus, FaEdit, FaTrash, FaSearch, FaEye, FaUser, FaBox } from 'react-icons/fa'
import VoucherModal from './modal/VoucherModal'
import VoucherDetailModal from './modal/VoucherDetailModal'
import { PromotionSearchParams, Promotion, ProductDetailSimple } from '~/types/promotion'
import { showToastError } from '~/utils/alert'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { apiGetAllPromotionPagination } from '~/api/promotion'
import { LIMIT } from '~/constance/variable'
import { AxiosError } from 'axios'
import { useSearchParams } from 'react-router-dom'
import { useDebounce } from '~/hooks/useDebounce'
import Pagination from '~/components/pagination/Pagination'
import UserListModal from './modal/UserListModal'
import ProductListModal from './modal/ProductListModal'
import { User } from '~/types/user'
import { Product } from '~/types/product'
import { apiGetAllUsers } from '~/api/users'
import { apiGetAllProductDetailSimple } from '~/api/product'

const ManageVouchers = () => {
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isEdit, setIsEdit] = useState(false)
  const [selectedPromotion, setSelectedPromotion] = useState<Promotion | undefined>(undefined)
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false)
  const [searchParams, setSearchParams] = useSearchParams()
  const currentParams = useMemo(() => Object.fromEntries([...searchParams]) as PromotionSearchParams, [searchParams])
  const [promotions, setPromotions] = useState<Promotion[]>([])
  const [totalPageCount, setTotalPageCount] = useState(0)
  const [searchTerm, setSearchTerm] = useState(currentParams.search || '')
  const debouncedSearchTerm = useDebounce(searchTerm, 500)
  const { accessToken } = useAppSelector((state) => state.user)
  const [allUsers, setAllUsers] = useState<User[]>([])
  const [allProducts, setAllProducts] = useState<ProductDetailSimple[]>([])
  const [loadingUsers, setLoadingUsers] = useState(false)
  const [loadingProducts, setLoadingProducts] = useState(false)
  // Thêm state cho modals
  const [isUserListModalOpen, setIsUserListModalOpen] = useState(false)
  const [isProductListModalOpen, setIsProductListModalOpen] = useState(false)
  const [selectedPromotionForList, setSelectedPromotionForList] = useState<Promotion | undefined>(undefined)
  console.log(selectedPromotionForList)
  const handleShowUsers = (promotion: Promotion) => {
    setSelectedPromotionForList(promotion)
    setIsUserListModalOpen(true)
  }

  const handleShowProducts = (promotion: Promotion) => {
    setSelectedPromotionForList(promotion)
    setIsProductListModalOpen(true)
  }

  const handleCloseUserListModal = () => {
    setIsUserListModalOpen(false)
    setSelectedPromotionForList(undefined)
  }

  const handleCloseProductListModal = () => {
    setIsProductListModalOpen(false)
    setSelectedPromotionForList(undefined)
  }

  const handleOpenAddModal = () => {
    setIsEdit(false)
    setSelectedPromotion(undefined)
    setIsModalOpen(true)
    setIsDetailModalOpen(true)
  }

  const handleOpenEditModal = (promotion: Promotion) => {
    setIsEdit(true)
    setSelectedPromotion(promotion)
    setIsModalOpen(true)
  }

  const handleOpenDetailModal = (promotion: Promotion) => {
    setSelectedPromotion(promotion)
    setIsDetailModalOpen(true)
  }

  const handleCloseModal = () => {
    setIsModalOpen(false)
    setSelectedPromotion(undefined)
    setIsDetailModalOpen(false)
  }

  const handleSubmitModal = (promotion: Promotion) => {
    // Here you would typically save to API
    console.log('Saving promotion:', promotion)
    setIsModalOpen(false)
    setSelectedPromotion(undefined)
  }

  const handleCloseDetailModal = () => {
    setIsDetailModalOpen(false)
    setSelectedPromotion(undefined)
  }
  const getAllPromotionPagination = async (currentParams: PromotionSearchParams) => {
    try {
      if (!accessToken) {
        showToastError('Please login to perform this action.')
        return
      }
      const { page, search } = currentParams
      const response = await apiGetAllPromotionPagination({
        page: page || '0',
        search: search,
        limit: LIMIT.toString(),
        accessToken
      })
      if (response.code == 200) {
        setPromotions(response.result.content)
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
    getAllPromotionPagination(currentParams)
  }, [currentParams])
  useEffect(() => {
    const newParams = { ...currentParams, page: '1' }
    if (debouncedSearchTerm.trim()) {
      newParams.search = debouncedSearchTerm
    } else {
      delete newParams.search
    }
    setSearchParams(newParams)
  }, [debouncedSearchTerm])
  useEffect(() => {
    fetchAllUsers()
    fetchAllProductDetailSimple()
  }, [])

  const fetchAllUsers = async () => {
    if (!accessToken) return

    setLoadingUsers(true)
    try {
      const response = await apiGetAllUsers({
        page: '0',
        limit: '1000', // Lấy tất cả users
        accessToken
      })
      if (response.code === 200) {
        setAllUsers(response.result.content)
      }
    } catch (error) {
      console.error('Error fetching users:', error)
      showToastError('Failed to load users')
    } finally {
      setLoadingUsers(false)
    }
  }

  const fetchAllProductDetailSimple = async () => {
    if (!accessToken) return

    setLoadingProducts(true)
    try {
      // Giả sử có API này
      const response = await apiGetAllProductDetailSimple()
      if (response.code === 200) {
        setAllProducts(response.result)
      }
    } catch (error) {
      console.error('Error fetching products:', error)
      showToastError('Failed to load products')
    } finally {
      setLoadingProducts(false)
    }
  }
  return (
    <div className='p-6 w-full mx-auto bg-white shadow-lg rounded-xl'>
      {/* Header Section */}
      <div className='flex justify-between items-center mb-6'>
        <h1 className='text-3xl font-semibold text-blue-800'>Voucher Management</h1>
        <button
          className='bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700 transition-colors'
          onClick={handleOpenAddModal}
          aria-label='Add new voucher'
        >
          <FaPlus size={16} />
          Add Voucher
        </button>
      </div>

      {/* Tabs */}
      <div className='mb-6'>
        <div className='border-b border-gray-200'></div>
      </div>

      {/* Search Frame */}
      <div className='flex gap-4 mb-6'>
        {/* <div className='relative w-1/3'>
          <span className='absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400'>
            <FaSearch />
          </span>
          <input
            type='text'
            placeholder='Search by code or value...'
            className='pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
          />
        </div> */}
      </div>

      {/* Table Section */}
      <div className='overflow-x-auto rounded-xl shadow-lg'>
        <table className='w-full border-collapse border border-blue-200'>
          <thead>
            <tr className='bg-blue-600 text-white text-left'>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>#</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Code</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Type</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Value</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Data</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Start Date</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>End Date</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Actions</th>
            </tr>
          </thead>
          <tbody>
            {promotions.map((p, index) => {
              return (
                <tr key={p.promotionId} className='border-b border-teal-200 hover:bg-teal-50 transition-colors'>
                  <td className='px-4 py-3'>{index + 1}</td>
                  <td className='px-4 py-3 font-medium'>{p.promoCode}</td>
                  <td className='px-4 py-3'>
                    <span
                      className={`px-2 py-1 rounded-full text-xs font-medium ${
                        p.discountType === 'PERCENTAGE' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'
                      }`}
                    >
                      {p.discountType === 'PERCENTAGE' ? 'Percentage' : 'Value'}
                    </span>
                  </td>
                  <td className='px-4 py-3'>
                    {p.discountValue} {p.discountType === 'PERCENTAGE' ? '%' : 'đ'}
                  </td>
                  <td className='px-4 py-3'>
                    <div className='flex gap-2'>
                      {(p.user?.length ?? 0) > 0 && (
                        <button
                          className='bg-green-100 text-green-600 px-3 py-1 rounded-full text-xs font-medium hover:bg-green-200 transition-colors flex items-center gap-1'
                          onClick={() => handleShowUsers(p)}
                          title='View target users'
                        >
                          <FaUser size={12} />
                          User
                        </button>
                      )}

                      {/* Product Target Button */}
                      {(p.pd?.length ?? 0) > 0 && (
                        <button
                          className='bg-purple-100 text-purple-600 px-3 py-1 rounded-full text-xs font-medium hover:bg-purple-200 transition-colors flex items-center gap-1'
                          onClick={() => handleShowProducts(p)}
                          title='View target products'
                        >
                          <FaBox size={12} />
                          Product
                        </button>
                      )}
                    </div>
                  </td>
                  <td className='px-4 py-3'>{p.startDate}</td>
                  <td className='px-4 py-3'>{p.endDate}</td>
                  <td className='px-4 py-3 flex gap-2'>
                    <button
                      className='bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors'
                      onClick={() => handleOpenDetailModal(p)}
                      title='View details'
                    >
                      <FaEye size={16} />
                    </button>
                    <button
                      className='bg-yellow-400 text-white p-2 rounded-lg hover:bg-yellow-500 transition-colors'
                      onClick={() => handleOpenEditModal(p)}
                    >
                      <FaEdit size={16} />
                    </button>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
        <Pagination totalPageCount={totalPageCount} />
      </div>
      <UserListModal
        isOpen={isUserListModalOpen}
        promotion={selectedPromotionForList}
        onClose={handleCloseUserListModal}
      />

      {/* Product List Modal */}
      <ProductListModal
        isOpen={isProductListModalOpen}
        promotion={selectedPromotionForList}
        onClose={handleCloseProductListModal}
      />
      {/* Modal */}
      <VoucherModal
        isOpen={isModalOpen}
        isEdit={isEdit}
        promotion={selectedPromotion}
        onClose={handleCloseModal}
        onSubmit={handleSubmitModal}
        setPromotion={setPromotions}
        users={allUsers}
        products={allProducts}
        loadingUsers={loadingUsers}
        loadingProducts={loadingProducts}
      />
      {/* View Details Modal */}
      <VoucherDetailModal
        isOpen={isDetailModalOpen}
        promotion={selectedPromotion}
        // users={mockUsers}
        onClose={handleCloseDetailModal}
      />
    </div>
  )
}

export default ManageVouchers
