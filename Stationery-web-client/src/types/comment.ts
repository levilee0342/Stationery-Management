type User = {
  userId: string
  firstName: string
  lastName: string
  avatar: string | null
  }
}
interface Review {
  reviewId: string
  user: User
  content: string | null
  rating: number
  reviewImage: string | null
  replies: Review[]
  replyOnUser: User | null
  createdAt: string
}

type AffectedCommentType = {
  type: 'REPLY' | 'EDIT'
  id: string
} | null
type CreateCommentProps = {
  content?: string
  rating?: number
  parentId?: string
  replyOnUser?: string
  productId?: string
}
type CommentProps = {
  userId?: string
  comment: Review
  isAdmin: boolean
  replies?: Review[]
  parentId?: string
  affectedComment?: AffectedCommentType
  setAffectedComment: (data: AffectedCommentType) => void
  handleSubmitComment: ({ content, rating, parentId, replyOnUser }: CreateCommentProps) => void
  handleUpdateComment: (data: { commentId: string; content: string }) => void
  handleDeleteComment: (data: { commentId: string }) => void
}

type CommentFormProps = {
  initValue: string | '' // Giá trị khởi tạo của textarea (optional)
  cancelHandler?: () => void // Hàm xử lý khi nhấn nút Cancel (optional)
  confirmText: string // Văn bản hiển thị trên nút Confirm (required)
  handleSubmitComment: ({ content }: { content: string }) => void // Hàm xử lý khi nhấn nút Confirm (required)
  setAffectedComment?: (comment: null) => void // Hàm để đặt lại affectedComment (optional)
}

export type { AffectedCommentType, Review, CommentProps, CommentFormProps, CreateCommentProps }
