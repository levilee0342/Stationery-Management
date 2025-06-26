import moment from 'moment'
import { IoTrashOutline } from 'react-icons/io5'
import { LuPencil } from 'react-icons/lu'
// import { DefaultUser } from '~/assets/images'
import { convertNumberToStar } from '~/utils/helper'
import { FC } from 'react'
import { DefaultUser } from '~/assets/images'
import { Review } from '~/types/comment'

interface YourRatingProps {
  comment: Review
  handleShowModalUpdateRating: () => void
  handleDeleteComment: (params: { commentId: string }) => void
}

const YourRating: FC<YourRatingProps> = ({ comment, handleShowModalUpdateRating, handleDeleteComment }) => {
  return (
    <div className='mt-4 pb-8 border-b border-b-gray-300'>
      <div className='flex gap-1'>
        <img
          className='w-7 h-7 rounded-full'
          src={comment.user?.avatar || DefaultUser}
          alt={comment?.user?.lastName || 'User'}
        />
        <p>
          {comment?.user?.firstName} {comment?.user?.lastName}
        </p>
      </div>
      <div className='ml-[32px]'>
        {/* Rating */}
        <div className='flex items-center gap-2'>
          {comment?.rating && (
            <div className='flex'>
              {convertNumberToStar(comment?.rating).map((el, i) => (
                <div key={i} className='text-yellow-300 text-[14px]'>
                  {el}
                </div>
              ))}
            </div>
          )}
          <p className='text-xs text-gray-500'>{moment(comment?.createdAt).fromNow()}</p>
        </div>
        {/* Content */}
        <div className='mt-3 p-2 shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] rounded-md'>
          <p>{comment.content}</p>
          <div className='flex mt-1 gap-2 text-[14px]'>
            <button className='flex justify-center items-center gap-1' onClick={handleShowModalUpdateRating}>
              <LuPencil />
              <span className='text-xs'>Edit</span>
            </button>
            <button
              className='flex justify-center items-center gap-1'
              onClick={() => handleDeleteComment({ commentId: comment.reviewId })}
            >
              <IoTrashOutline />
              <span className='text-xs'>Delete</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default YourRating
