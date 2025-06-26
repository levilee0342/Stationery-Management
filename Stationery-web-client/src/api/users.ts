import { http } from '~/utils/http'
import { AxiosError } from 'axios'

const apiGetUserInfo = async ({ token }: { token: string | null }) => {
  try {
    const config = { headers: { Authorization: `Bearer ${token}` } }
    const response = await http.get('/users/info', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return (error as Error).message // Avoid undefined error
  }
}

const apiGetUserById = async ({ userId }: { userId: string }) => {
  try {
    const response = await http.get(`/users/info/${userId}`)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

const apiChangePassword = async ({
  email,
  oldPassword,
  newPassword
}: {
  email: string
  oldPassword: string
  newPassword: string
}) => {
  try {
    const response = await http.post('/users/change-password', {
      email,
      oldPassword,
      newPassword
    })
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return (error as Error).message // Avoid undefined error
  }
}
const apiUpdateUserInfo = async ({ formData, accessToken }: { formData: FormData; accessToken: string }) => {
  try {
    const config = { headers: { Authorization: `Bearer ${accessToken}`, 'Content-Type': 'multipart/form-data' } }
    const response = await http.put('/users/update-user', formData, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
const apiUpdateUserAdmin = async ({
  formData,
  userId,
  accessToken
}: {
  formData: FormData
  userId: string
  accessToken: string
}) => {
  try {
    const config = { headers: { Authorization: `Bearer ${accessToken}`, 'Content-Type': 'multipart/form-data' } }
    const response = await http.put(`/users/admin/update-user/${userId}`, formData, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}

const apiGetAllUsers = async ({
  page,
  limit,
  search,
  roleId,
  accessToken
}: {
  page: string
  limit: string
  search?: string
  roleId?: string
  accessToken: string
}) => {
  try {
    const params = {
      page,
      limit,
      roleId,
      search
    }
    const config = {
      params,
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.get('/users/admin/get-users', config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}
const apiBlockUser = async ({ userId, accessToken }: { userId: string; accessToken: string }) => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const response = await http.put(`/users/admin/block-user/${userId}`, {}, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data // Return server error response if available
    }
    return error // Avoid undefined error
  }
}

export {
  apiGetUserInfo,
  apiChangePassword,
  apiUpdateUserInfo,
  apiUpdateUserAdmin,
  apiGetAllUsers,
  apiBlockUser,
  apiGetUserById
}
