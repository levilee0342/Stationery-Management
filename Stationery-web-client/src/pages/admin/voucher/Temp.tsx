// import { useEffect, useMemo, useState } from 'react'
// import { FaPlus, FaEdit, FaTrash, FaSearch, FaEye } from 'react-icons/fa'
// import VoucherModal from './modal/VoucherModal'
// import VoucherDetailModal from './modal/VoucherDetailModal'
// import { ProductPromotion, PromotionSearchParams, UserPromotion, DiscountType } from '~/types/promotion'
// import { showToastError } from '~/utils/alert'
// import { useAppSelector } from '~/hooks/redux'
// import { apiGetAllProductPromotionPagination } from '~/api/promotion'
// import { LIMIT } from '~/constance/variable'
// import { AxiosError } from 'axios'
// import { useSearchParams } from 'react-router-dom'
// import { useDebounce } from '~/hooks/useDebounce'
// import Pagination from '~/components/pagination/Pagination'

// interface Promotion {
//   promotionId: string
//   promoCode: string
//   discountType: DiscountType
//   discountValue: number
//   usageLimit: number
//   maxValue: number
//   minOrderValue: number
//   startDate: string
//   endDate: string
//   createdAt: string
//   voucherType: VoucherType
//   userIds?: string[] // Only for USER vouchers
// }

// type VoucherType = 'PRODUCT' | 'USER'

// const mockUsers = [
//   { id: 'user1', name: 'John Doe' },
//   { id: 'user2', name: 'Jane Smith' }
// ]

// // const samplePromotions: Promotion[] = [
// //   {
// //     promotionId: '1',
// //     promoCode: 'DISCOUNT10',
// //     discountType: 'PERCENTAGE',
// //     discountValue: 10,
// //     usageLimit: 100,
// //     maxValue: 50,
// //     minOrderValue: 20,
// //     startDate: '2025-04-01',
// //     endDate: '2025-12-31',
// //     createdAt: '2025-04-01T00:00:00Z',
// //     voucherType: 'PRODUCT'
// //   },
// //   {
// //     promotionId: '2',
// //     promoCode: 'SALE20',
// //     discountType: 'VALUE',
// //     discountValue: 20,
// //     usageLimit: 50,
// //     maxValue: 100,
// //     minOrderValue: 100,
// //     startDate: '2025-04-01',
// //     endDate: '2025-12-31',
// //     createdAt: '2025-04-01T00:00:00Z',
// //     voucherType: 'USER',
// //     userIds: ['user1']
// //   },
// //   {
// //     promotionId: '3',
// //     promoCode: 'ALLUSERS',
// //     discountType: 'PERCENTAGE',
// //     discountValue: 15,
// //     usageLimit: 200,
// //     maxValue: 75,
// //     minOrderValue: 50,
// //     startDate: '2025-05-01',
// //     endDate: '2025-12-31',
// //     createdAt: '2025-04-15T00:00:00Z',
// //     voucherType: 'USER'
// //     // No userIds means it's for all users
// //   }
// // ]

// const ManageVouchers = () => {
//   const [isModalOpen, setIsModalOpen] = useState(false)
//   const [isEdit, setIsEdit] = useState(false)
//   const [selectedPromotion, setSelectedPromotion] = useState<Promotion | undefined>(undefined)
//   const [activeTab, setActiveTab] = useState<'PRODUCT' | 'USER'>('PRODUCT')
//   const [isDetailModalOpen, setIsDetailModalOpen] = useState(false)
//   const [searchParams, setSearchParams] = useSearchParams()
//   const currentParams = useMemo(() => Object.fromEntries([...searchParams]) as PromotionSearchParams, [searchParams])
//   const [promotions, setPromotions] = useState<UserPromotion[] | ProductPromotion[]>([])
//   const [totalPageCount, setTotalPageCount] = useState(0)
//   const [searchTerm, setSearchTerm] = useState(currentParams.search || '')
//   const debouncedSearchTerm = useDebounce(searchTerm, 500)
//   const { accessToken } = useAppSelector((state) => state.user)
//   const handleOpenAddModal = () => {
//     setIsEdit(false)
//     setSelectedPromotion(undefined)
//     setIsModalOpen(true)
//     setIsDetailModalOpen(true)
//   }

//   const handleOpenEditModal = (promotion: Promotion) => {
//     setIsEdit(true)
//     setSelectedPromotion(promotion)
//     setIsModalOpen(true)
//   }

//   const handleOpenDetailModal = (promotion: Promotion) => {
//     setSelectedPromotion(promotion)
//     setIsDetailModalOpen(true)
//   }

//   const handleCloseModal = () => {
//     setIsModalOpen(false)
//     setSelectedPromotion(undefined)
//     setIsDetailModalOpen(false)
//   }

//   const handleSubmitModal = (promotion: Promotion) => {
//     // Here you would typically save to API
//     console.log('Saving promotion:', promotion)
//     setIsModalOpen(false)
//     setSelectedPromotion(undefined)
//   }

//   const handleCloseDetailModal = () => {
//     setIsDetailModalOpen(false)
//     setSelectedPromotion(undefined)
//   }
//   const getProductPromotion = async (currentParams: PromotionSearchParams) => {
//     try {
//       if (!accessToken) {
//         showToastError('Please login to perform this action.')
//         return
//       }
//       const { page, search } = currentParams
//       const response = await apiGetAllProductPromotionPagination({
//         page: page || '0',
//         search: search,
//         limit: LIMIT.toString(),
//         accessToken
//       })
//       if (response.code == 200) {
//         setPromotions(response.result.content)
//         setTotalPageCount(response.result.page.totalPages)
//       } else {
//         showToastError(response.message || response.error)
//       }
//     } catch (error) {
//       if (error instanceof Error || error instanceof AxiosError) {
//         showToastError(error.message)
//       }
//     }
//   }
//   useEffect(() => {
//     getProductPromotion(currentParams)
//   }, [currentParams])
//   useEffect(() => {
//     const newParams = { ...currentParams, page: '1' }
//     if (debouncedSearchTerm.trim()) {
//       newParams.search = debouncedSearchTerm
//     } else {
//       delete newParams.search
//     }
//     setSearchParams(newParams)
//   }, [debouncedSearchTerm])
//   return (
//     <div className='p-6 w-full mx-auto bg-white shadow-lg rounded-xl'>
//       {/* Header Section */}
//       <div className='flex justify-between items-center mb-6'>
//         <h1 className='text-3xl font-semibold text-blue-800'>Voucher Management</h1>
//         <button
//           className='bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700 transition-colors'
//           onClick={handleOpenAddModal}
//           aria-label='Add new voucher'
//         >
//           <FaPlus size={16} />
//           Add Voucher
//         </button>
//       </div>

//       {/* Tabs */}
//       <div className='mb-6'>
//         <div className='border-b border-gray-200'>
//           <nav className='-mb-px flex gap-4'>
//             {/* <button
//               className={`border-b-2 px-4 py-2 font-medium text-sm ${
//                 activeTab === 'PRODUCT'
//                   ? 'border-blue-600 text-blue-600'
//                   : 'border-transparent text-gray-500 hover:text-blue-600 hover:border-blue-600'
//               }`}
//               onClick={() => setActiveTab('PRODUCT')}
//             >
//               Product Vouchers
//             </button>
//             <button
//               className={`border-b-2 px-4 py-2 font-medium text-sm ${
//                 activeTab === 'USER'
//                   ? 'border-blue-600 text-blue-600'
//                   : 'border-transparent text-gray-500 hover:text-blue-600 hover:border-blue-600'
//               }`}
//               onClick={() => setActiveTab('USER')}
//             >
//               User Vouchers
//             </button> */}
//           </nav>
//         </div>
//       </div>

//       {/* Search Frame */}
//       <div className='flex gap-4 mb-6'>
//         <div className='relative w-1/3'>
//           <span className='absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400'>
//             <FaSearch />
//           </span>
//           <input
//             type='text'
//             placeholder='Search by code or value...'
//             className='pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
//           />
//         </div>
//       </div>

//       {/* Table Section */}
//       <div className='overflow-x-auto rounded-xl shadow-lg'>
//         <table className='w-full border-collapse border border-blue-200'>
//           <thead>
//             <tr className='bg-blue-600 text-white text-left'>
//               <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>#</th>
//               <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Code</th>
//               <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Type</th>
//               <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Value</th>
//               {/* <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Applied To</th> */}
//               <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Start Date</th>
//               <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>End Date</th>
//               <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Actions</th>
//             </tr>
//           </thead>
//           <tbody>
//             {promotions.map((pi, index) => {
//               const isProductPromotion = 'productPromotionId' in pi
//               const promotion = pi.promotion as Promotion
//               const promotionId = isProductPromotion
//                 ? (pi as ProductPromotion).productPromotionId
//                 : (pi as UserPromotion).userPromotionId
//               return (
//                 <tr key={promotionId} className='border-b border-teal-200 hover:bg-teal-50 transition-colors'>
//                   <td className='px-4 py-3'>{index + 1}</td>
//                   <td className='px-4 py-3 font-medium'>{promotionId}</td>
//                   <td className='px-4 py-3'>{promotion.discountType === 'PERCENTAGE' ? 'Percentage' : 'Value'}</td>
//                   <td className='px-4 py-3'>
//                     {promotion.discountValue} {promotion.discountType === 'PERCENTAGE' ? '%' : 'đ'}
//                   </td>
//                   {/* <td className='px-4 py-3'>
//                     {isProductPromotion
//                       ? 'All Products'
//                       : promotion.userIds && promotion.userIds.length > 0
//                         ? mockUsers
//                             .filter((u) => promotion.userIds?.includes(u.id))
//                             .map((u) => u.name)
//                             .join(', ')
//                         : 'All Users'}
//                   </td> */}
//                   <td className='px-4 py-3'>{promotion.startDate}</td>
//                   <td className='px-4 py-3'>{promotion.endDate}</td>
//                   <td className='px-4 py-3 flex gap-2'>
//                     <button
//                       className='bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600 transition-colors'
//                       onClick={() => handleOpenDetailModal(promotion)}
//                       title='View details'
//                     >
//                       <FaEye size={16} />
//                     </button>
//                     <button
//                       className='bg-yellow-400 text-white p-2 rounded-lg hover:bg-yellow-500 transition-colors'
//                       onClick={() => handleOpenEditModal(promotion)}
//                     >
//                       <FaEdit size={16} />
//                     </button>
//                     <button className='bg-red-500 text-white p-2 rounded-lg hover:bg-red-600 transition-colors'>
//                       <FaTrash size={16} />
//                     </button>
//                   </td>
//                 </tr>
//               )
//             })}
//           </tbody>
//         </table>
//         <Pagination totalPageCount={totalPageCount} />
//       </div>

//       {/* Modal */}
//       <VoucherModal
//         isOpen={isModalOpen}
//         isEdit={isEdit}
//         promotion={selectedPromotion}
//         onClose={handleCloseModal}
//         onSubmit={handleSubmitModal}
//         users={mockUsers}
//       />
//       {/* View Details Modal */}
//       <VoucherDetailModal
//         isOpen={isDetailModalOpen}
//         promotion={selectedPromotion}
//         users={mockUsers}
//         onClose={handleCloseDetailModal}
//       />
//     </div>
//   )
// }

// export default ManageVouchers
