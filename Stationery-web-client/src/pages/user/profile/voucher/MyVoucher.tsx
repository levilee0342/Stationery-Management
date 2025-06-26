import { MdDiscount } from 'react-icons/md'
import Voucher from '~/components/voucher/Voucher'
import { useAppSelector } from '~/hooks/redux'

const MyVoucher = () => {
  const { myVouchers } = useAppSelector((state) => state.voucher)
  return (
    <div className='bg-white p-6 rounded-lg shadow-md'>
      <h2 className='text-3xl font-bold text-gray-800 flex items-center mb-8'>
        <span className={`bg-gradient-to-r from-blue-400 to-blue-500 text-white p-3 rounded-xl mr-4 shadow-md`}>
          <MdDiscount size={24} />
        </span>
        <span className='bg-clip-text text-transparent bg-gradient-to-r from-gray-700 to-gray-900'>My Voucher</span>
      </h2>
      <div className='flex flex-wrap gap-3  mb-6 border-b'>
        {myVouchers?.map((voucher) => (
          <div
            className='lg:w-[calc(25%-9px)] md:w-[calc(33.33%-8px)] sm:w-[calc(50%-12px)] w-full'
            key={voucher.userPromotionId}
          >
            <Voucher promotion={voucher.promotion} />
          </div>
        ))}
      </div>
    </div>
  )
}

export default MyVoucher
