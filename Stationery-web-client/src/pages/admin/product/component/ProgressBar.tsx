import React from 'react'
import { FaCheck } from 'react-icons/fa'

interface ProgressBarProps {
  step: 0 | 1 | 2 | 3
}

const ProgressBar: React.FC<ProgressBarProps> = ({ step }) => {
  return (
    <div className='mb-8 p-6 bg-gradient-to-r from-blue-100 via-white to-blue-100 rounded-3xl shadow-xl border border-blue-200'>
      <h2 className='text-2xl font-bold text-blue-700 mb-6 text-center tracking-wide'>🚀 Add Product Progress</h2>

      <div className='flex items-center justify-between'>
        {/* Step 1 */}
        <div className='flex flex-col items-center flex-1'>
          <div
            className={`w-12 h-12 rounded-full flex items-center justify-center text-lg font-bold shadow transition-all duration-300 ${
              step >= 1 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}
          >
            {step >= 1 ? <FaCheck /> : '1'}
          </div>
          <p className='text-sm mt-2 text-gray-700 font-medium'>Add Product</p>
        </div>

        {/* Line */}
        <div className='flex-1 px-2'>
          <div
            className={`h-1 w-full rounded-full transition-all duration-300 ${
              step >= 2 ? 'bg-blue-600' : 'bg-gray-300'
            }`}
          />
        </div>

        {/* Step 2 */}
        <div className='flex flex-col items-center flex-1'>
          <div
            className={`w-12 h-12 rounded-full flex items-center justify-center text-lg font-bold shadow transition-all duration-300 ${
              step >= 2 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}
          >
            {step >= 2 ? <FaCheck /> : '2'}
          </div>
          <p className='text-sm mt-2 text-gray-700 font-medium'>Add Product Details</p>
        </div>

        {/* Line */}
        <div className='flex-1 px-2'>
          <div
            className={`h-1 w-full rounded-full transition-all duration-300 ${
              step >= 3 ? 'bg-blue-600' : 'bg-gray-300'
            }`}
          />
        </div>

        {/* Step 3 */}
        <div className='flex flex-col items-center flex-1'>
          <div
            className={`w-12 h-12 rounded-full flex items-center justify-center text-lg font-bold shadow transition-all duration-300 ${
              step >= 3 ? 'bg-blue-600 text-white' : 'bg-gray-300 text-gray-600'
            }`}
          >
            {step >= 3 ? <FaCheck /> : '3'}
          </div>
          <p className='text-sm mt-2 text-gray-700 font-medium'>Confirm</p>
        </div>
      </div>
    </div>
  )
}

export default ProgressBar
