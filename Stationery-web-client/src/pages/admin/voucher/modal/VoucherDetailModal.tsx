import { FiCalendar, FiPercent, FiUsers, FiPackage, FiDollarSign, FiClock } from 'react-icons/fi'
import { User } from '~/types/user'

interface Promotion {
  promotionId: string
  promoCode: string
  discountType: DiscountType
  discountValue: number
  usageLimit: number
  maxValue: number
  minOrderValue: number
  startDate: string
  endDate: string
  createdAt: string
  voucherType: VoucherType
  userIds?: string[]
}

type DiscountType = 'PERCENTAGE' | 'VALUE'
type VoucherType = 'PRODUCT' | 'USER'

interface VoucherDetailModalProps {
  isOpen: boolean
  promotion?: Promotion
  users: User[]
  onClose: () => void
}

const VoucherDetailModal = ({ isOpen, promotion, users, onClose }: VoucherDetailModalProps) => {
  if (!isOpen || !promotion) return null

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    })
  }

  const getStatus = () => {
    const now = new Date()
    const start = new Date(promotion.startDate)
    const end = new Date(promotion.endDate)

    if (now < start) return 'UPCOMING'
    if (now > end) return 'EXPIRED'
    return 'ACTIVE'
  }

  const statusColor = {
    ACTIVE: 'bg-green-100 text-green-800',
    EXPIRED: 'bg-red-100 text-red-800',
    UPCOMING: 'bg-blue-100 text-blue-800'
  }

  return (
    <div className='fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4'>
      <div className='bg-white rounded-xl p-8 w-full max-w-4xl shadow-2xl'>
        {/* Header */}
        <div className='flex justify-between items-start mb-6'>
          <div>
            <h2 className='text-3xl font-bold text-gray-900'>{promotion.promoCode}</h2>
            <div className='flex items-center mt-2 space-x-4'>
              <span className={`px-3 py-1 rounded-full text-sm font-medium ${statusColor[getStatus()]}`}>
                {getStatus()}
              </span>
              <span className='text-gray-500 text-sm'>Created: {formatDate(promotion.createdAt)}</span>
            </div>
          </div>
          <button onClick={onClose} className='text-gray-400 hover:text-gray-600 text-2xl' aria-label='Close modal'>
            &times;
          </button>
        </div>

        {/* Main Content */}
        <div className='grid grid-cols-1 lg:grid-cols-2 gap-6'>
          {/* Left Column */}
          <div className='space-y-6'>
            {/* Voucher Type */}
            <div className='bg-blue-50 p-5 rounded-xl border border-blue-100'>
              <div className='flex items-center mb-3'>
                <div className='bg-blue-100 p-2 rounded-lg mr-3'>
                  <FiPackage className='text-blue-600 text-xl' />
                </div>
                <h3 className='text-lg font-semibold text-blue-800'>Voucher Type</h3>
              </div>
              <p className='text-gray-700 pl-11'>
                {promotion.voucherType === 'PRODUCT' ? 'Product Voucher (Applies to all products)' : 'User Voucher'}
              </p>
            </div>

            {/* Discount Information */}
            <div className='bg-purple-50 p-5 rounded-xl border border-purple-100'>
              <div className='flex items-center mb-3'>
                <div className='bg-purple-100 p-2 rounded-lg mr-3'>
                  {promotion.discountType === 'PERCENTAGE' ? (
                    <FiPercent className='text-purple-600 text-xl' />
                  ) : (
                    <FiDollarSign className='text-purple-600 text-xl' />
                  )}
                </div>
                <h3 className='text-lg font-semibold text-purple-800'>Discount Information</h3>
              </div>

              <div className='grid grid-cols-2 gap-4 pl-11'>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Type</p>
                  <p className='font-medium'>{promotion.discountType}</p>
                </div>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Value</p>
                  <p className='font-medium'>
                    {promotion.discountValue}
                    {promotion.discountType === 'PERCENTAGE' ? '%' : '$'}
                  </p>
                </div>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Max Value</p>
                  <p className='font-medium'>${promotion.maxValue}</p>
                </div>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Min Order</p>
                  <p className='font-medium'>${promotion.minOrderValue}</p>
                </div>
              </div>
            </div>
          </div>

          {/* Right Column */}
          <div className='space-y-6'>
            {/* Validity Period */}
            <div className='bg-green-50 p-5 rounded-xl border border-green-100'>
              <div className='flex items-center mb-3'>
                <div className='bg-green-100 p-2 rounded-lg mr-3'>
                  <FiCalendar className='text-green-600 text-xl' />
                </div>
                <h3 className='text-lg font-semibold text-green-800'>Validity Period</h3>
              </div>

              <div className='grid grid-cols-2 gap-4 pl-11'>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Start Date</p>
                  <p className='font-medium'>{formatDate(promotion.startDate)}</p>
                </div>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>End Date</p>
                  <p className='font-medium'>{formatDate(promotion.endDate)}</p>
                </div>
                <div className='col-span-2'>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Duration</p>
                  <p className='font-medium'>
                    {Math.ceil(
                      (new Date(promotion.endDate).getTime() - new Date(promotion.startDate).getTime()) /
                        (1000 * 60 * 60 * 24)
                    )}{' '}
                    days
                  </p>
                </div>
              </div>
            </div>

            {/* Usage Information */}
            <div className='bg-amber-50 p-5 rounded-xl border border-amber-100'>
              <div className='flex items-center mb-3'>
                <div className='bg-amber-100 p-2 rounded-lg mr-3'>
                  <FiClock className='text-amber-600 text-xl' />
                </div>
                <h3 className='text-lg font-semibold text-amber-800'>Usage Information</h3>
              </div>

              <div className='grid grid-cols-2 gap-4 pl-11'>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Usage Limit</p>
                  <p className='font-medium'>{promotion.usageLimit}</p>
                </div>
                <div>
                  <p className='text-xs text-gray-500 uppercase tracking-wider'>Created</p>
                  <p className='font-medium'>{formatDate(promotion.createdAt)}</p>
                </div>
              </div>
            </div>

            {/* User Information (if applicable) */}
            {promotion.voucherType === 'USER' && (
              <div className='bg-indigo-50 p-5 rounded-xl border border-indigo-100'>
                <div className='flex items-center mb-3'>
                  <div className='bg-indigo-100 p-2 rounded-lg mr-3'>
                    <FiUsers className='text-indigo-600 text-xl' />
                  </div>
                  <h3 className='text-lg font-semibold text-indigo-800'>User Information</h3>
                </div>

                <div className='pl-11'>
                  <p className='text-sm text-gray-700 mb-2'>
                    {promotion.userIds && promotion.userIds.length > 0
                      ? `Applied to ${promotion.userIds.length} specific users`
                      : 'Applied to all users'}
                  </p>

                  {promotion.userIds && promotion.userIds.length > 0 && (
                    <div className='mt-3'>
                      <p className='text-xs text-gray-500 uppercase tracking-wider mb-1'>Selected Users</p>
                      <div className='max-h-40 overflow-y-auto pr-2'>
                        {promotion.userIds.map((id) => {
                          const user = users.find((u) => u.userId === id)
                          return (
                            <div key={id} className='flex items-center py-2 border-b border-gray-100 last:border-0'>
                              <div className='w-6 h-6 rounded-full bg-indigo-100 flex items-center justify-center mr-2'>
                                <span className='text-xs text-indigo-600'>{user?.lastName?.charAt(0) || 'U'}</span>
                              </div>
                              <span className='text-sm'>{user?.lastName || `User ${id}`}</span>
                            </div>
                          )
                        })}
                      </div>
                    </div>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default VoucherDetailModal
