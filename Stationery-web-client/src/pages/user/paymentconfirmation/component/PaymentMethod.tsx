import { RadioGroup } from '@headlessui/react'

export default function PaymentMethod({
  selectedPayment,
  setSelectedPayment
}: {
  selectedPayment: string
  setSelectedPayment: (method: string) => void
}) {
  return (
    <div className='mt-6 bg-gray-100 p-4 rounded-lg'>
      <h2 className='text-lg font-semibold text-gray-800 mb-4'>Payment Method</h2>
      <RadioGroup value={selectedPayment} onChange={setSelectedPayment} className='space-y-2'>
        {['COD', 'Momo'].map((method) => (
          <RadioGroup.Option key={method} value={method} className='cursor-pointer'>
            {({ checked }) => (
              <div
                className={`flex items-center p-2 border rounded-lg cursor-pointer space-x-2 ${
                  checked ? 'bg-gray-200' : 'bg-gray-100'
                }`}
              >
                <input type='radio' checked={checked} readOnly className='form-radio h-4 w-4 text-blue-600' />
                <span>{method === 'COD' ? 'Cash on Delivery' : 'Momo'}</span>
              </div>
            )}
          </RadioGroup.Option>
        ))}
      </RadioGroup>
    </div>
  )
}
