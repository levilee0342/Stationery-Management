import { http } from '~/utils/http'
import { AxiosError } from 'axios'
const apiGetReviewOfProduct = async ({ slug }: { slug?: string }) => {
  try {
    const response = await http.get(`/reviews/${slug}`)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return (error as Error).message // Avoid undefined error
  }
}
const apiCreateReview = async ({
  parentId,
  content,
  rating,
  replyOnUser,
  productId,
  accessToken
}: {
  parentId?: string
  content?: string
  rating?: number
  replyOnUser?: string
  productId?: string
  accessToken: string
}) => {
  try {
    const config = { headers: { Authorization: `Bearer ${accessToken}` } }
    const body = {
      parentId,
      content,
      rating,
      replyOnUser,
      productId
    }
    const response = await http.post('/reviews', body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return (error as Error).message // Avoid undefined error
  }
}
const apiUpdateReview = async ({
  reviewId,
  content,
  rating,
  accessToken
}: {
  reviewId: string
  content?: string
  rating?: number
  accessToken: string
}) => {
  try {
    const body = {
      content,
      rating
    }
    const config = { headers: { Authorization: `Bearer ${accessToken}` } }
    const response = await http.put(`/reviews/${reviewId}`, body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
const apiDeleteReview = async ({ reviewId, accessToken }: { reviewId: string; accessToken: string }) => {
  try {
    const config = { headers: { Authorization: `Bearer ${accessToken}` } }
    const response = await http.delete(`/reviews/${reviewId}`, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
export { apiCreateReview, apiUpdateReview, apiDeleteReview, apiGetReviewOfProduct }
