import React from 'react';

interface IntroductionProps {
  imageUrl: string
  title: string
  description: string
}

const Introduction: React.FC<IntroductionProps> = ({ imageUrl, title, description }) => {
  return (
    <section className='py-12 container mx-auto px-4'>
      <div className='flex flex-col md:flex-row items-center gap-8'>
        <div className='w-full md:w-1/2'>
          <img src={imageUrl} alt='Shop introduction' className='w-full rounded-lg shadow-lg' />
        </div>
        <div className='w-full md:w-1/2'>
          <h2 className='text-3xl font-semibold mb-4 text-blue-800'>{title}</h2>
          <p className='text-gray-600 leading-relaxed'>{description}</p>
        </div>
      </div>
    </section>
  );
};

export default Introduction;