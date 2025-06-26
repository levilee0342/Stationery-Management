import { useState } from 'react'
import Voucher from '~/components/voucher/Voucher'
import { useAppSelector } from '~/hooks/redux'
import { SelectedPromotion } from '~/types/promotion'
import { showAlertError, showToastSuccess } from '~/utils/alert'
export default function DiscountSection({
  orderTotal,
  selectedCoupon,
  setSelectdCoupon,
  setDiscountAmount
}: {
  orderTotal: number
  selectedCoupon: SelectedPromotion | null
  setSelectdCoupon: (promotion: SelectedPromotion | null) => void
  setDiscountAmount: (amount: number) => void
}) {
  const { myVouchers } = useAppSelector((state) => state.voucher)
  const [showVouchers, setShowVouchers] = useState(false)
  const [code, setCode] = useState('')

  const hanleApplyDiscount = (code: string) => {
    console.log('code', selectedCoupon?.code, code, selectedCoupon?.code === code)
    if (selectedCoupon?.code === code) {
      showToastSuccess('Remove voucher successfully')
      setSelectdCoupon(null)
      setCode('')
      setDiscountAmount(0)
      return
    }
    const coupon = myVouchers.find((c) => c.promotion.promoCode === code)
    const promotion = coupon?.promotion
    if (promotion) {
      setCode(code)
      setSelectdCoupon({
        code,
        promotionId: coupon.userPromotionId
      })
      if (orderTotal >= promotion.minOrderValue) {
        if (promotion.discountType === 'PERCENTATE') {
          const valueDiscount = (promotion.maxValue * orderTotal) / 100
          if (valueDiscount > promotion.maxValue) {
            setDiscountAmount(promotion.maxValue)
          } else {
            setDiscountAmount(valueDiscount)
          }
        } else if (promotion.discountType === 'VALUE') {
          setDiscountAmount(promotion.discountValue)
        }
        showToastSuccess('Add voucher successfully')
      } else {
        showAlertError('The discount code is not valid for this order.')
      }
    } else {
      showAlertError('The discount code is not valid.')
    }
  }

  return (
    <div className='mt-6 bg-gray-100 p-4 rounded-lg'>
      <h2 className='text-lg font-semibold text-gray-800 mb-4'>Mã giảm giá</h2>
      <div className='flex space-x-2'>
        <input
          type='text'
          onChange={(e) => setCode(e.target.value)}
          value={code}
          className='w-full p-2 border rounded-lg'
          placeholder='Nhập mã giảm giá'
        />
        <button onClick={() => hanleApplyDiscount(code)} className='px-4 py-2 bg-blue-500 text-white rounded-lg'>
          Áp dụng
        </button>
      </div>
      <button
        onClick={() => setShowVouchers(!showVouchers)}
        className='px-4 py-2 mt-2 text-white bg-blue-500 rounded-lg'
      >
        Voucher của bạn
      </button>
      {showVouchers && (
        <div className='flex flex-wrap gap-3 my-6 border-b'>
          {myVouchers?.map((voucher) => (
            <div className='lg:w-[calc[33%-8px)] md:w-[calc(50%-12px)] w-full' key={voucher.userPromotionId}>
              <Voucher
                promotion={voucher.promotion}
                hanleApplyDiscount={hanleApplyDiscount}
                isActive={selectedCoupon?.promotionId === voucher.userPromotionId}
              />
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
