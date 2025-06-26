import { HiCheckCircle } from 'react-icons/hi'

export default function OrderSuccess({ orderId }: { orderId: string }) {
  return (
    <div className='max-w-xl mx-auto mt-20 bg-white p-8 rounded-2xl shadow-lg text-center'>
      <HiCheckCircle className='text-green-500 w-16 h-16 mx-auto mb-4' />
      <h1 className='text-3xl font-bold text-gray-800 mb-2'>Đơn hàng đã được xác nhận!</h1>
      <p className='text-gray-600 mb-4'>
        Mã đơn hàng của bạn: <strong>{orderId}</strong>
      </p>
      <p className='text-gray-500'>Cảm ơn bạn đã mua hàng. Đơn hàng sẽ được xử lý và giao sớm nhất có thể.</p>
      <a
        href='/'
        className='inline-block mt-6 px-6 py-3 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition duration-200'
      >
        Tiếp tục mua sắm
      </a>
    </div>
  )
}
