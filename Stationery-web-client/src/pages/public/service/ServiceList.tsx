import React from 'react'

interface Service {
  title: string
  description: string
  icon: string
  image: string
}

const ServiceList: React.FC = () => {
  const services: Service[] = [
    {
      title: 'School Supplies',
      description: 'High-quality books, notebooks, pens, and study tools for educational institutions.',
      icon: '🎓',
      image: 'https://images.unsplash.com/photo-1503676260728-1c00da094a0b'
    },
    {
      title: 'Corporate Office Supplies',
      description: 'Comprehensive office supply solutions with fast doorstep delivery.',
      icon: '🏢',
      image: 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d'
    },
    {
      title: 'Custom Design & Printing',
      description: 'Business cards, catalogs, and forms with creative, high-quality designs.',
      icon: '🎨',
      image: 'https://assets.grok.com/users/67be45c7-8635-4103-8055-fdb39a404ca0/Y6B6DxpUSfXAo3lQ-generated_image.jpg'
    },
    {
      title: 'Gift Wrapping Services',
      description: 'Elegant and professional gift packaging for every special occasion.',
      icon: '🎁',
      image: 'https://assets.grok.com/users/67be45c7-8635-4103-8055-fdb39a404ca0/aoK6ptxUXoDOB30Q-generated_image.jpg'
    }
  ];

  return (
    <section className='py-20 relative'>
      <div className='container mx-auto px-4'>
        <h2 className='text-4xl font-bold text-center mb-16 text-gray-800 animate-fade-in'>Premium Services</h2>
        <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-10'>
          {services.map((service, index) => (
            <div 
              key={index}
              className='group bg-white rounded-xl shadow-lg hover:shadow-2xl transform hover:-translate-y-2 transition-all duration-300 overflow-hidden'
            >
              <div className='relative h-48'>
                <img 
                  src={service.image} 
                  alt={service.title}
                  className='w-full h-full object-cover group-hover:scale-110 transition-transform duration-500'
                />
                <div className='absolute inset-0 bg-black bg-opacity-20 flex items-center justify-center'>
                  <span className='text-5xl text-white'>{service.icon}</span>
                </div>
              </div>
              <div className='p-6'>
                <h3 className='text-xl font-semibold text-gray-800 mb-3'>{service.title}</h3>
                <p className='text-gray-600 leading-relaxed'>{service.description}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export default ServiceList
