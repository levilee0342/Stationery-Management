import { AxiosError } from 'axios'
import { http } from '~/utils/http' // đổi path nếu cần

type AddAddressParams = {
  addressId: string
  addressName: string
  recipient: string
  phone: string
  isDefault?: boolean // mặc định false nếu không truyền
  accessToken: string
}

export const apiAddAddress = async ({
  addressId,
  addressName,
  recipient,
  phone,
  isDefault = false,
  accessToken
}: AddAddressParams): Promise<string | any> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }
    const body = {
      addressId,
      addressName,
      recipient,
      phone,
      isDefault
    }
    const response = await http.post('/address', body, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

export const apiSetDefaultAddress = async ({
  addressId,
  accessToken
}: {
  addressId: string
  accessToken: string
}): Promise<string | any> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }

    const response = await http.put(`/address/default/${addressId}`, null, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}

export const apiDeleteAddress = async ({
  addressId,
  accessToken
}: {
  addressId: string
  accessToken: string
}): Promise<string | any> => {
  try {
    const config = {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    }

    const response = await http.delete(`/address/${addressId}`, config)
    return response.data
  } catch (error) {
    if (error instanceof AxiosError && error.response) {
      return error.response.data
    }
    return (error as Error).message
  }
}
