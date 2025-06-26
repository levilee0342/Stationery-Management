import React from 'react';

const TestimonialSection: React.FC = () => {
  return (
    <section className='py-20 bg-white'>
      <div className='container mx-auto px-4'>
        <h2 className='text-4xl font-bold text-center mb-16 text-gray-800 animate-fade-in'>Customer Testimonials</h2>
        <div className='grid grid-cols-1 md:grid-cols-3 gap-10'>
          {[
            { quote: 'Excellent service, high-quality products!', name: 'Mr.Alexander', role: 'Principal' },
            { quote: 'Fast delivery, friendly staff.', name: 'Mrs.Isabella', role: 'Manager' },
            { quote: 'Reasonable prices, great for long-term cooperation.', name: 'Mr.Ethan', role: 'Business Owner' }
          ].map((item, index) => (
            <div 
              key={index}
              className='bg-gray-50 p-8 rounded-xl shadow-md hover:shadow-lg transition-all duration-300'
            >
              <p className='text-gray-600 italic mb-6'>'{item.quote}'</p>
              <div className='flex items-center'>
                <div className='w-12 h-12 bg-indigo-600 rounded-full flex items-center justify-center text-white font-bold'>
                  {item.name[0]}
                </div>
                <div className='ml-4'>
                  <p className='font-semibold text-gray-800'>{item.name}</p>
                  <p className='text-sm text-gray-500'>{item.role}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export default TestimonialSection
