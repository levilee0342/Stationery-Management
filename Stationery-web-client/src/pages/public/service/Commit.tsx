import React from 'react'

// Định nghĩa kiểu dữ liệu cho mỗi cam kết
interface Commitment {
  id: number
  image: string
  alt: string
  title: string
  description: string
}

// Danh sách cam kết
const commitments: Commitment[] = [
  {
    id: 1,
    image: '/images/logo_Warranty.svg',
    alt: 'Warranty',
    title: '1 Month Warranty',
    description: 'We provide a 1-month warranty for all our products.'
  },
  {
    id: 2,
    image: '/images/logo_Exchange.svg',
    alt: 'Product Exchange',
    title: 'Product Exchange',
    description: 'Exchange your product if it is damaged upon delivery.'
  },
  {
    id: 3,
    image: '/images/logo_FastDelivery.svg',
    alt: 'Fast Delivery',
    title: 'Fast Delivery',
    description: 'We ensure fast and reliable delivery of your products.'
  }
]

const Commit: React.FC = () => {
  return (
    <section id='commit' className='container mx-auto bg-white p-8 rounded-lg shadow-lg transition-all mb-4'>
      <h2 className='text-4xl font-bold text-center mb-16 text-gray-800 animate-fade-in'>Our Commit</h2>
      <div className='grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8'>
        {commitments.map(({ id, image, alt, title, description }) => (
          <div key={id} className='flex flex-col items-center text-center'>
            <div className='w-28 h-28 rounded-full bg-blue-500 flex items-center justify-center mb-4 p-2'>
              <img src={image} alt={alt} className='w-20 h-20 object-contain' />
            </div>
            <h3 className='text-xl font-semibold text-blue-600'>{title}</h3>
            <p className='text-gray-700 mt-2'>{description}</p>
          </div>
        ))}
      </div>
    </section>
  )
}

export default Commit
