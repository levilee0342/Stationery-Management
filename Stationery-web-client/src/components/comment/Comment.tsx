import moment from 'moment'
import { DefaultUser } from '~/assets/images'
import { TiMessages } from 'react-icons/ti'
import { LuPencil } from 'react-icons/lu'
import { IoTrashOutline } from 'react-icons/io5'
import { convertNumberToStar } from '~/utils/helper'
import CommentForm from './CommentForm'
import { CommentProps } from '~/types/comment'

function Comment({
  userId,
  comment,
  isAdmin,
  replies = [],
  parentId,
  affectedComment,
  setAffectedComment,
  handleSubmitComment,
  handleUpdateComment,
  handleDeleteComment
}: CommentProps) {
  const isBelongToUser = userId === comment.user?.userId
  const replyCommentId = parentId ? parentId : comment.reviewId
  const isReply = affectedComment?.type === 'REPLY' && affectedComment?.id === comment.reviewId
  const isEdit = affectedComment?.type === 'EDIT' && affectedComment?.id === comment.reviewId
  return (
    <div className={`mt-4`}>
      <div className='flex gap-1'>
        <img
          className='w-7 h-7 rounded-full'
          src={
            typeof comment.user?.avatar === 'string' ? comment.user?.avatar : comment.user?.avatar?.url || DefaultUser
          }
          alt={comment.user?.lastName}
        />
        <p>
          {comment.user?.firstName} {comment.user?.lastName}
        </p>
      </div>
      <div className='ml-[32px]'>
        {/* Rating and timestamp */}
        <div className='flex items-center gap-2'>
          {comment.rating && (
            <div className='flex'>
              {convertNumberToStar(comment.rating).map((el, i) => (
                <div key={i} className='text-yellow-300 text-[14px]'>
                  {el}
                </div>
              ))}
            </div>
          )}
          <p className='text-xs text-gray-500'>{moment(comment.createdAt).fromNow()}</p>
        </div>

        {/* Comment content */}
        <div className='mt-3 p-2 shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] rounded-md'>
          <p className='text-sm'>
            {comment.replyOnUser && comment.replyOnUser.userId !== comment.user.userId && (
              <span className='text-blue-500 leading-[16px] mr-1'>
                @{comment.replyOnUser.firstName} {comment.replyOnUser.lastName}
              </span>
            )}
            {comment.content}
          </p>

          {/* Action buttons */}
          <div className='flex mt-1 gap-2 text-[14px]'>
            <button
              className='flex justify-center items-center gap-1'
              onClick={() => {
                setAffectedComment({ type: 'REPLY', id: comment.reviewId })
              }}
            >
              <TiMessages />
              <span className='text-xs'>{replies.length} Reply</span>
            </button>

            {isBelongToUser && (
              <button
                className='flex justify-center items-center gap-1'
                onClick={() => {
                  setAffectedComment({ type: 'EDIT', id: comment.reviewId })
                }}
              >
                <LuPencil />
                <span className='text-xs'>Edit</span>
              </button>
            )}

            {isBelongToUser && (
              <button
                className='flex justify-center items-center gap-1'
                onClick={() => handleDeleteComment({ commentId: comment.reviewId })}
              >
                <IoTrashOutline />
                <span className='text-xs'>Delete</span>
              </button>
            )}
          </div>
        </div>

        {/* Comment form for reply or edit */}
        <div>
          {isReply && (
            <CommentForm
              confirmText='Reply'
              initValue={''}
              setAffectedComment={setAffectedComment}
              cancelHandler={() => setAffectedComment(null)}
              handleSubmitComment={({ content }: { content: string }) => {
                handleSubmitComment({ content, parentId: replyCommentId, replyOnUser: comment.user.userId })
              }}
            />
          )}

          {isEdit && (
            <CommentForm
              confirmText='Update'
              initValue={comment.content || ''}
              setAffectedComment={setAffectedComment}
              cancelHandler={() => setAffectedComment(null)}
              handleSubmitComment={({ content }: { content: string; rating: number }) =>
                handleUpdateComment({ commentId: comment.reviewId, content })
              }
            />
          )}

          {/* Render replies */}
          {replies.map((reply) => (
            <Comment
              key={reply.reviewId}
              userId={userId}
              comment={reply}
              isAdmin={isAdmin}
              affectedComment={affectedComment}
              parentId={replyCommentId}
              setAffectedComment={setAffectedComment}
              handleSubmitComment={handleSubmitComment}
              handleUpdateComment={handleUpdateComment}
              handleDeleteComment={handleDeleteComment}
              replies={[]}
            />
          ))}
        </div>
      </div>
    </div>
  )
}

export default Comment
