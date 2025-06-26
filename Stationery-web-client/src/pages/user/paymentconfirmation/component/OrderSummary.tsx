import { OrderDetails } from '~/types/order'
import { formatNumber } from '~/utils/helper'

export default function OrderSummary({ order, discountAmount }: { order: OrderDetails; discountAmount: number }) {
  return (
    <div className='mt-6 bg-gray-100 p-4 rounded-lg'>
      <h2 className='text-xl font-semibold text-gray-800'>Đơn hàng của bạn: </h2>

      <div className='mt-4'>
        {order?.items?.map((item) => {
          return (
            <div key={item.productDetailId} className='flex items-center justify-between p-2 border-b'>
              <div className='flex items-center gap-4'>
                <img src={item.imageUrl} alt={item.productName} className='w-16 h-16 rounded-lg object-cover' />
                <div>
                  <p className='font-semibold'>{item.productName}</p>
                  <p className='text-gray-600'>Số lượng: {item.quantity}</p>
                  <p className='text-gray-600'>
                    Màu: {item.colorName} - Size: {item.sizeName}
                  </p>
                </div>
              </div>

              <div className='text-right'>
                <p className='text-blue-600 font-semibold'>{formatNumber(item.discountPrice)} VND</p>
                {/* {item.discountValue > 0 && ( */}
                {/* <> */}
                <p className='text-sm text-gray-500 line-through'>{formatNumber(item.originalPrice)} VND</p>
                <p className='text-sm text-green-600'>
                  Tiết kiệm {formatNumber(item.originalPrice - item.discountPrice)} VND
                </p>
                {/* </> */}
                {/* )} */}
              </div>
            </div>
          )
        })}
      </div>

      {/* Tổng tiền */}
      <div className='mt-6 text-right space-y-2 font-semibold'>
        {discountAmount > 0 ? (
          <>
            <div className='flex justify-end items-center gap-2 text-gray-600'>
              <span>Giá ban đầu:</span>
              <span className='line-through'>{order.totalAmount.toLocaleString()} VND</span>
            </div>
            <div className='flex justify-end items-center gap-2 text-gray-600'>
              <span>Giảm giá voucher:</span>
              <span className=' text-red-600'>- {discountAmount.toLocaleString()} VND</span>
            </div>
            <div className='flex justify-end items-center gap-2 text-gray-600 text-lg'>
              <span>Tổng tiền sau giảm giá:</span>
              <span className='text-blue-600 '>{(order.totalAmount - discountAmount).toLocaleString()} VND</span>
            </div>
          </>
        ) : (
          <div className='flex justify-end items-center gap-2 text-lg text-gray-600'>
            <span>Tổng cộng:</span>
            <span className='text-blue-600 '>{order.totalAmount.toLocaleString()} VND</span>
          </div>
        )}
      </div>
    </div>
  )
}
