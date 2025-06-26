import { AiOutlineLoading3Quarters } from 'react-icons/ai'
import { HiDeviceMobile } from 'react-icons/hi'

export default function RedirectingToMomo() {
  return (
    <div className='max-w-md mx-auto mt-20 bg-white p-8 rounded-2xl shadow-md text-center'>
      <HiDeviceMobile className='text-pink-500 w-14 h-14 mx-auto mb-4' />
      <h1 className='text-2xl font-semibold text-gray-800'>Đang chuyển đến cổng thanh toán Momo</h1>
      <p className='text-gray-500 mt-2'>Vui lòng không tắt trình duyệt. Bạn sẽ được chuyển trong giây lát...</p>
      <div className='flex justify-center mt-6'>
        <AiOutlineLoading3Quarters className='animate-spin text-pink-500 w-6 h-6' />
      </div>
    </div>
  )
}
