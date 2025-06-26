import { memo, useState } from 'react'
import { IoClose } from 'react-icons/io5'
import { FaStar } from 'react-icons/fa'
import { useAppDispatch } from '~/hooks/redux'
import { modalActions } from '~/store/slices/modal'
import { CreateCommentProps } from '~/types/comment'

const startText = ['Rất tệ', 'Tệ', 'Bình thường', 'Tốt', 'Tuyệt']

type RatingModalProps = {
  title: string
  handleSubmitComment: ({ rating, content }: CreateCommentProps) => void
  rating?: number
  content?: string
  confirmText?: string
}

const RatingModal: React.FC<RatingModalProps> = ({
  title,
  handleSubmitComment,
  rating,
  content = '',
  confirmText = 'Submit'
}) => {
  const dispatch = useAppDispatch()
  const [hover, setHover] = useState<number | null>(null)
  const [payload, setPayload] = useState<{ rating: number; content: string }>({ rating, content })

  return (
    <div
      className='max-sm:w-[calc(100vw-10px)] w-[640px] bg-white rounded-md overflow-hidden'
      onClick={(e) => e.stopPropagation()}
    >
      <div className='relative py-3 bg-gray-100 text-bl'>
        <p className='font-bold text-xl pl-2'>Đánh giá và nhận xét</p>
        <IoClose
          className='absolute top-1/2 -translate-y-1/2 right-0 pr-2 text-4xl cursor-pointer'
          onClick={() => dispatch(modalActions.toggleModal({ isOpenModal: false, childrenModal: null }))}
        />
      </div>
      <div className='px-2'>
        <img src={'/images/logo_stationery.svg'} alt='digital world' className='pt-4 mx-auto w-1/4' />
        <p className='text-xl font-medium py-4'>{title}</p>
        <div className='flex justify-around items-center'>
          {startText.map((el, index) => {
            const currentRating = index + 1
            return (
              <div key={el}>
                <label
                  className='cursor-pointer'
                  onMouseEnter={() => setHover(currentRating)}
                  onMouseLeave={() => setHover(null)}
                >
                  <div>
                    <input
                      className='hidden'
                      type='radio'
                      name='rating'
                      value={payload.rating}
                      onChange={() => setPayload({ ...payload, rating: currentRating })}
                    />
                    <FaStar
                      className={`${
                        currentRating <= (hover || payload.rating) ? 'text-yellow-300' : 'text-gray-100'
                      } mx-auto text-xl`}
                    />
                  </div>
                  <p className='text-[16px]'>{el}</p>
                </label>
              </div>
            )
          })}
        </div>
        <textarea
          placeholder='Enter your thoughts'
          value={payload.content}
          onChange={(e) => setPayload({ ...payload, content: e.target.value })}
          className='w-full border border-gray-300 rounded-sm p-1 focus:outline-none mt-4'
          rows={4}
        ></textarea>
        <div className='flex justify-center'>
          <button className='d-btn d-btn-primary mb-2' onClick={() => handleSubmitComment(payload)}>
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  )
}

export default memo(RatingModal)
