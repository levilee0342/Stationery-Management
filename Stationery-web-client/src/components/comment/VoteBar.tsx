import { FC } from 'react'
import { convertNumberToStar } from '~/utils/helper'
import Rating from './Rating'
import { Review } from '~/types/comment'

type VoteBarProps = {
  totalRating?: number
  comments?: Review[]
}

const VoteBar: FC<VoteBarProps> = ({ totalRating, comments }) => {
  const totalReviews = comments?.length || 0
  return (
    <div className='flex'>
      <div className='flex-4 flex items-center justify-center border-r border-r-gray-300'>
        <div className='flex flex-col items-center justify-center'>
          <p className='font-bold text-xl'>{totalRating}/5</p>
          <div className='flex gap-1 my-1'>
            {convertNumberToStar(totalRating ?? 0).map((el: string, i: number) => (
              <div key={i} className='text-yellow-300 text-[16px]'>
                {el}
              </div>
            ))}
          </div>
          <p>
            <strong className='font-bold'>{totalReviews}</strong> đánh giá
          </p>
        </div>
      </div>
      <div className='flex-8 px-3 py-2'>
        {[...Array(5).keys()].reverse().map((el) => (
          <Rating
            key={el}
            number={el + 1}
            numberOfReviews={comments?.filter((item) => item.rating === el + 1).length || 0}
            totalReviews={totalReviews}
          />
        ))}
      </div>
    </div>
  )
}

export default VoteBar
