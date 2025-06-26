import React, { useState, useEffect } from 'react'
import { Image } from '~/types/product'

type ProductImagesProps = {
  images: Image[]
}

export const ProductImages: React.FC<ProductImagesProps> = ({ images }) => {
  const [currentImage, setCurrentImage] = useState<Image | null>(null)

  // Initialize with first image
  useEffect(() => {
    if (images.length > 0 && !currentImage) {
      setCurrentImage(images[0])
    }
  }, [images])

  // Auto-rotate images
  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentImage((prev) => {
        if (!prev) return images[0]
        const currentIndex = images.findIndex((img) => img.imageId === prev.imageId)
        const nextIndex = (currentIndex + 1) % images.length
        return images[nextIndex]
      })
    }, 5000)
    return () => clearInterval(interval)
  }, [images])

  const handleThumbnailClick = (image: Image) => {
    setCurrentImage(image)
  }

  if (images.length === 0) return null

  return (
    <div className='w-full md:w-1/2 space-y-4'>
      {/* Main image with background */}
      <div className='relative bg-gray-100 dark:bg-gray-800 rounded-lg p-4 flex items-center justify-center h-96'>
        <img src={currentImage?.url || images[0]?.url} alt='product' className='max-h-full max-w-full object-contain' />
      </div>

      {/* Thumbnail gallery */}
      <div className='flex flex-wrap gap-2'>
        {images.map((image) => (
          <button
            key={image.imageId}
            onClick={() => handleThumbnailClick(image)}
            className={`w-16 h-16 rounded border-2 transition-all ${
              currentImage?.imageId === image.imageId
                ? 'border-blue-500 scale-105'
                : 'border-transparent hover:border-gray-300'
            }`}
          >
            <img src={image.url} alt='thumbnail' className='w-full h-full object-cover rounded' />
          </button>
        ))}
      </div>
    </div>
  )
}
