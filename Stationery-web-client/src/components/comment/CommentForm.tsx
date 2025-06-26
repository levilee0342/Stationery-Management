import { useState } from 'react'
// import Button from '../Button'
import { CommentFormProps } from '~/types/comment'

function CommentForm({
  initValue = '',
  cancelHandler,
  confirmText,
  handleSubmitComment,
  setAffectedComment
}: CommentFormProps) {
  const [value, setValue] = useState(initValue)
  return (
    <div>
      <div>
        <textarea
          value={value}
          onChange={(e) => setValue(e.target.value)}
          placeholder='Enter your comment'
          className='w-full border border-gray-300 shadow-[rgba(0,_0,_0,_0.24)_0px_3px_8px] rounded-md p-2 focus:outline-none mt-4'
          rows={3}
        />
        <div className='flex justify-end gap-3 mt-2'>
          {cancelHandler && (
            <button
              className='d-btn d-btn-secondary'
              onClick={() => {
                if (setAffectedComment) {
                  setAffectedComment(null)
                }
              }}
            >
              Cancel
            </button>
          )}
          <button className='d-btn d-btn-primary' onClick={() => handleSubmitComment({ content: value })}>
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  )
}

export default CommentForm
