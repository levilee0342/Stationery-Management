/* eslint-disable react/prop-types */
import { formatNumber } from '~/utils/helper'
import { MdShoppingCart } from 'react-icons/md'
import { GiPayMoney } from 'react-icons/gi'
import { GiReceiveMoney } from 'react-icons/gi'
import { FaUsers } from 'react-icons/fa6'
function DashBoardStats() {
  return (
    <div className='flex gap-4'>
      <BoxWrapper>
        <div className='flex items-center justify-center'>
          <div className='rounded-full h-12 w-12 flex justify-center items-center bg-sky-500'>
            <GiReceiveMoney className=' text-2xl text-white ' />
          </div>
          <div className='pl-4'>
            <span className='text-sm text-gray-500 font-light'>Tổng doanh thu</span>
            <div className=''>
              <strong className='text-xl text-gray-700 font-semibold'>{formatNumber(12345000)}đ</strong>
              <span className='text-sm text-green-500 pl-2'>+123</span>
            </div>
          </div>
        </div>
      </BoxWrapper>
      <BoxWrapper>
        <div className='flex items-center justify-center'>
          <div className='rounded-full h-12 w-12 flex justify-center items-center bg-yellow-400'>
            <FaUsers className=' text-2xl text-white ' />
          </div>
          <div className='pl-4'>
            <span className='text-sm text-gray-500 font-light'>Tổng khách hàng</span>
            <div className=''>
              <strong className='text-xl text-gray-700 font-semibold'>{formatNumber(200)}</strong>
              <span className='text-sm text-green-500 pl-2'>+20</span>
            </div>
          </div>
        </div>
      </BoxWrapper>
      <BoxWrapper>
        <div className='flex items-center justify-center'>
          <div className='rounded-full h-12 w-12 flex justify-center items-center bg-green-500'>
            <MdShoppingCart className=' text-2xl text-white ' />
          </div>
          <div className='pl-4'>
            <span className='text-sm text-gray-500 font-light'>Tổng đơn đặt hàng</span>
            <div className=''>
              <strong className='text-xl text-gray-700 font-semibold'>{formatNumber(1000)}</strong>
              <span className='text-sm text-green-500 pl-2'>+100</span>
            </div>
          </div>
        </div>
      </BoxWrapper>
    </div>
  )
}
function BoxWrapper({ children }) {
  return <div className='bg-white rounded-sm p-4 flex flex-1 border border-gray-200 items-center'>{children}</div>
}
export default DashBoardStats
