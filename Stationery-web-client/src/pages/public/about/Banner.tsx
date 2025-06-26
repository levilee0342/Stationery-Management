import React from 'react'

interface BannerProps {
  imageUrl: string
  altText: string
}

const Banner: React.FC<BannerProps> = ({ imageUrl, altText }) => {
  return (
    <section className='w-full h-[400px] relative'>
      <img src={imageUrl} alt={altText} className='w-full h-full object-cover' />
      <div className='absolute inset-0 bg-black bg-opacity-40 flex items-center justify-center'>
        <h1 className='text-white text-5xl md:text-6xl font-extrabold mb-6 animate-fade-in-down'>About Us</h1>
      </div>
    </section>
  )
}

export default Banner
