import React from 'react'
import SupportForm from './SupportForm'
import ContactInfo from './ContactInfo'

const SupportPage: React.FC = () => {
  return (
    <div className='min-h-screen bg-gray-100 p-10 ml-16'>
      <div className='max-w-8xl'>
        <h1 className='text-3xl font-bold mb-8 text-blue-700'>Hỗ trợ khách hàng</h1>
        <div className='grid grid-cols-1 md:grid-cols-2 gap-6'>
          <SupportForm />
          <ContactInfo />
        </div>
      </div>
    </div>
  )
}

export default SupportPage
