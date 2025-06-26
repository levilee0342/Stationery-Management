import React from 'react'

const ServiceHeader: React.FC = () => {
  return (
    <section className='relative bg-gradient-to-r from-blue-600 to-indigo-700 text-white py-24 overflow-hidden'>
      <div className='absolute inset-0 opacity-10'>
        <div className="w-full h-full bg-[url('https://www.transparenttextures.com/patterns/paper-fibers.png')]"></div>
      </div>
      <div className='container mx-auto px-4 relative z-10'>
        <h1 className='text-5xl md:text-6xl font-extrabold mb-6 animate-fade-in-down'>
          Premium Office Supplies Service
        </h1>
        <p className='text-2xl max-w-3xl mx-auto leading-relaxed animate-fade-in-up'>
          A comprehensive, professional solution for all your needs with superior quality.
        </p>
      </div>
    </section>
  )
}

export default ServiceHeader
