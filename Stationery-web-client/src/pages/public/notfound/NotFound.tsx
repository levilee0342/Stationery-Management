import React from 'react'
import { Sparkles, ArrowLeft } from 'lucide-react'

const NotFound: React.FC = () => {
  return (
    <div className='flex items-center justify-center min-h-screen bg-gradient-to-br from-blue-50 via-blue-50 to-pink-50 p-6'>
      <div className='text-center bg-white p-10 rounded-2xl shadow-2xl max-w-md w-full'>
        <div className='flex justify-center mb-4'>
          <img src='/images/robot404.gif' alt='404 Illustration' className='w-80 h-80 object-contain' />
        </div>
        <h1 className='text-5xl font-extrabold text-blue-600'>Oops!</h1>
        <p className='text-xl font-medium text-gray-700 mt-2'>We couldn’t find that page.</p>
        <p className='text-gray-500 mt-4 text-sm'>
          It looks like the page you’re looking for has wandered off with a pencil or maybe never existed at all.
        </p>
        <div className='mt-6 flex justify-center'>
          <a
            href='/'
            className='inline-flex items-center gap-2 bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 transition-all'
          >
            <ArrowLeft size={16} />
            Back to Home
          </a>
        </div>
        <div className='mt-6 text-xs text-gray-400 flex items-center justify-center gap-1'>
          <Sparkles size={14} />
          Made with love by Stationery's P
        </div>
      </div>
    </div>
  )
}

export default NotFound
