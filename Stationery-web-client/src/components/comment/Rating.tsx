import { FC, useEffect, useRef, memo } from 'react'
import { FaStar } from 'react-icons/fa'

interface RatingProps {
  number: number
  numberOfReviews: number
  totalReviews: number
}

const Rating: FC<RatingProps> = ({ number, numberOfReviews, totalReviews }) => {
  const barRef = useRef<HTMLDivElement>(null)
  useEffect(() => {
    if (barRef.current) {
      barRef.current.style.right = `${100 - (numberOfReviews / totalReviews || 0) * 100}%`
    }
  }, [totalReviews, numberOfReviews])

  return (
    <div className='flex items-center'>
      <div className='flex items-center justify-between font-medium gap-1 w-[5%] text-sm'>
        {number}
        <FaStar className='text-yellow-300' />
      </div>
      <div className='relative rounded-md overflow-hidden mx-3 w-[80%] h-[8px] bg-gray-200'>
        <div ref={barRef} className='absolute inset-0 bg-primary'></div>
      </div>
      <span className='w-[15%] whitespace-nowrap text-xs text-gray-600'>
        {numberOfReviews} {numberOfReviews == 1 ? 'review' : 'reviews'}
      </span>
    </div>
  )
}

export default memo(Rating)
