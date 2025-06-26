import VoteBar from './VoteBar'
import RatingModal from './RatingModal'
import { memo, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Comment from './Comment'
import YourRating from './YourRating'
import { AffectedCommentType, CreateCommentProps } from '~/types/comment'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { modalActions } from '~/store/slices/modal'
import { Review } from '~/types/comment'
import { confirmAleart, showAlertError, showToastError, showToastSuccess, showToastWarning } from '~/utils/alert'
import { apiCreateReview, apiDeleteReview, apiUpdateReview } from '~/api/review'

interface CommentContainerProps {
  pId?: string
  comments?: Review[]
  totalRating?: number
  totalRatingCount?: number
  setFetchAgain: React.Dispatch<React.SetStateAction<boolean>>
}

const CommentContainer: React.FC<CommentContainerProps> = ({ pId, comments, totalRating, setFetchAgain }) => {
  const dispatch = useAppDispatch()
  const navigate = useNavigate()
  const { userData, accessToken } = useAppSelector((state) => state.user)
  const [affectedComment, setAffectedComment] = useState<AffectedCommentType>(null)
  const [rated, setRated] = useState<Review | null>(null)

  const handleSubmitComment = async ({ rating, content, parentId, replyOnUser }: CreateCommentProps) => {
    if (content?.trim() === '') {
      showToastWarning("Comment can't be empty")
      return
    }
    try {
      if (!accessToken) {
        confirmAleart({ message: 'You need to login to comment', confirmText: 'Login' }).then((result) => {
          if (result.isConfirmed) {
            navigate('/login')
          }
        })
        return
      }
      const res = await apiCreateReview({
        productId: pId,
        content,
        rating,
        parentId,
        replyOnUser,
        accessToken
      })
      if (res.code !== 200) {
        showAlertError(res.message)
        return
      }
      dispatch(
        modalActions.toggleModal({
          isOpenModal: false,
          childrenModal: null
        })
      )
      showToastSuccess('Created successfully')
      setFetchAgain((prev) => !prev)
      setAffectedComment(null)
    } catch (error) {
      if (error instanceof Error) {
        showToastError(error.message)
      } else {
        showToastError(error as string)
      }
    }
  }

  const handleUpdateComment = async ({
    commentId,
    content,
    rating
  }: {
    commentId: string
    content: string
    rating?: number
  }) => {
    try {
      if (!accessToken) {
        confirmAleart({ message: 'You need to login to comment', confirmText: 'Login' }).then((result) => {
          if (result.isConfirmed) {
            navigate('/login')
          }
        })
        return
      }
      const res = await apiUpdateReview({
        reviewId: commentId,
        content,
        rating,
        accessToken
      })
      if (res.code !== 200) {
        showAlertError(res.message)
        return
      }
      dispatch(
        modalActions.toggleModal({
          isOpenModal: false,
          childrenModal: null
        })
      )
      showToastSuccess('Edited successfully')
      setFetchAgain((prev) => !prev)
      setAffectedComment(null)
    } catch (error) {
      if (error instanceof Error) {
        showToastError(error.message)
      } else {
        showToastError(error as string)
      }
    }
  }

  const handleDeleteComment = async ({ commentId }: { commentId: string }) => {
    if (!accessToken) {
      confirmAleart({ message: 'You need to login to comment', confirmText: 'Login' }).then((result) => {
        if (result.isConfirmed) {
          navigate('/login')
        }
      })
      return
    }
    try {
      const res = await apiDeleteReview({
        reviewId: commentId,
        accessToken
      })
      if (res.code !== 200) {
        showAlertError(res.message)
        return
      }
      showToastSuccess('Delete successfully')
      setFetchAgain((prev) => !prev)
    } catch (error) {
      if (error instanceof Error) {
        showToastError(error.message)
      } else {
        showToastError(error as string)
      }
    }
  }
  const handleShowModalUpdateRating = () => {
    dispatch(
      modalActions.toggleModal({
        isOpenModal: true,
        childrenModal: (
          <RatingModal
            rating={rated?.rating}
            content={rated?.content || ''}
            title='Edit your rating'
            handleSubmitComment={({ content = '', rating }: CreateCommentProps) => {
              handleUpdateComment({
                rating,
                content,
                commentId: rated?.reviewId || ''
              })
            }}
          />
        )
      })
    )
  }
  useEffect(() => {}, [comments])
  const handleRatingModal = () => {
    dispatch(
      modalActions.toggleModal({
        isOpenModal: true,
        childrenModal: <RatingModal title='hi' handleSubmitComment={handleSubmitComment} />
      })
    )
  }
  useEffect(() => {
    setRated(comments?.find((comment) => comment.user?.userId === userData?.userId) || null)
  }, [comments])
  return (
    <div>
      <VoteBar totalRating={totalRating} comments={comments} />
      {rated ? (
        <YourRating
          comment={rated}
          handleShowModalUpdateRating={handleShowModalUpdateRating}
          handleDeleteComment={handleDeleteComment}
        />
      ) : (
        <div className='flex justify-center'>
          <button className='d-btn d-btn-primary' onClick={handleRatingModal}>
            Đánh giá ngay
          </button>
        </div>
      )}
      {comments?.map((comment) => (
        <Comment
          key={comment.reviewId}
          userId={userData?.userId}
          comment={comment}
          isAdmin={false}
          affectedComment={affectedComment}
          setAffectedComment={setAffectedComment}
          handleSubmitComment={handleSubmitComment}
          handleUpdateComment={handleUpdateComment}
          handleDeleteComment={handleDeleteComment}
          replies={comment.replies}
        />
      ))}
    </div>
  )
}

export default memo(CommentContainer)
