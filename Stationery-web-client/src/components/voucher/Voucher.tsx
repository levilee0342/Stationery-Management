import { Ticket, CheckCircle } from 'lucide-react'
import moment from 'moment'
import React from 'react'
import { Promotion } from '~/types/promotion'
import { formatNumber } from '~/utils/helper'

interface VoucherProps {
  promotion?: Promotion
  hanleApplyDiscount?: (code: string) => void
  isActive?: boolean
}

const Voucher: React.FC<VoucherProps> = ({ promotion, isActive = false, hanleApplyDiscount }) => {
  if (!promotion) return null
  const labelColor = isActive ? 'text-yellow-900' : 'text-blue-100'
  const valueColor = isActive ? 'text-blue-800 font-semibold' : 'text-white'

  return (
    <div
      key={promotion.promotionId}
      className={`relative p-4 mb-4 rounded-lg transition shadow-md overflow-hidden ${
        isActive
          ? 'bg-gradient-to-r from-yellow-100 via-yellow-300 to-yellow-400 text-blue-900 ring-2 ring-yellow-600 scale-[1.02]'
          : 'bg-gradient-to-r from-blue-500 to-blue-700 text-white'
      }`}
    >
      {/* Ribbon nếu đang active */}
      {isActive && (
        <div className='absolute top-0 right-0 px-2 py-1 bg-yellow-600 text-white text-xs font-bold rounded-bl-lg shadow-md'>
          Đã chọn
        </div>
      )}

      <div className='flex items-center'>
        <div
          className={`flex items-center justify-center w-12 h-12 rounded-full shadow-md mr-4 ${
            isActive ? 'bg-yellow-200 text-yellow-700' : 'bg-white text-blue-600'
          }`}
        >
          {isActive ? <CheckCircle size={24} /> : <Ticket size={24} />}
        </div>
        <div className='flex-1 text-sm space-y-1'>
          <div className='flex flex-wrap gap-4'>
            <div>
              <span className={`font-bold mr-1 ${labelColor}`}>Giảm:</span>
              <span className={valueColor}>
                {promotion.discountType === 'PERCENTATE'
                  ? `${promotion.discountValue}%`
                  : formatNumber(promotion.discountValue)}
              </span>
            </div>
            <div>
              <span className={`font-bold mr-1 ${labelColor}`}>Đơn tối thiểu:</span>
              <span className={valueColor}>{formatNumber(promotion.minOrderValue)}</span>
            </div>
            <div>
              <span className={`font-bold mr-1 ${labelColor}`}>Mã:</span>
              <span className={`${valueColor} tracking-wide`}>{promotion.promoCode}</span>
            </div>
          </div>
          <p className='text-xs italic opacity-80'>HSD: {moment(promotion.endDate).format('DD/MM/YYYY')}</p>
        </div>
      </div>

      <div className='mt-4 flex space-x-2'>
        <button
          className={`w-full px-3 py-2 rounded-lg text-sm font-semibold transition ${
            isActive ? 'bg-white text-yellow-800 hover:bg-yellow-100' : 'bg-white text-blue-600 hover:bg-gray-200'
          }`}
          onClick={() => navigator.clipboard.writeText(promotion.promoCode)}
        >
          Copy Code
        </button>
        <button
          className={`w-full px-3 py-2 rounded-lg text-sm font-semibold transition ${
            isActive ? 'bg-yellow-600 text-white cursor-not-allowed' : 'bg-yellow-400 text-blue-900 hover:bg-yellow-500'
          }`}
          onClick={() => hanleApplyDiscount && hanleApplyDiscount(promotion.promoCode)}
        >
          {isActive ? 'Remove' : 'Apply'}
        </button>
      </div>
    </div>
  )
}

export default Voucher
