import React, { Dispatch, SetStateAction, useState } from 'react'
import { useForm } from 'react-hook-form'
import { FiCamera } from 'react-icons/fi'
import { apiUpdateUserAdmin } from '~/api/users'
import SelectValidate from '~/components/select/SelectValidate'
import { User } from '~/types/user'
import { showAlertError, showAlertSucess } from '~/utils/alert'

type Props = {
  isOpen: boolean
  onClose: () => void
  setUsers: Dispatch<SetStateAction<User[] | undefined>>
  user: User
  accessToken: string
  isEdit?: boolean
}
type UserFormData = {
  firstName: string
  lastName: string
  email: string
  roleId: string
  phone: string
  dob: string
}
const roleOptions = [
  { value: '111', label: 'Admin' },
  { value: '112', label: 'User' },
  { value: '113', label: 'Department' }
]
const UserModal = ({ isOpen, onClose, setUsers, accessToken, user, isEdit }: Props) => {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors }
  } = useForm<UserFormData>({
    defaultValues: {
      firstName: user?.firstName || '',
      lastName: user?.lastName || '',
      email: user?.email || '',
      roleId: user?.role?.roleId || '',
      phone: user?.phone || '',
      dob: user?.dob || ''
    }
  })
  const [file, setFile] = useState<File | null>(null)
  const [previewUrl, setPreviewUrl] = useState<string | null>(user.avatar)
  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0]
    if (selectedFile) {
      setFile(selectedFile)
      setPreviewUrl(URL.createObjectURL(selectedFile))
    }
  }
  const onSubmit = async (data: UserFormData) => {
    const formData = new FormData()
    console.log(data)
    if (file) {
      formData.append('file', file)
    }
    formData.append('document', JSON.stringify({ ...data }))
    if (!accessToken) {
      showAlertError('You must be logged in to update your profile.')
      return
    }
    const res = await apiUpdateUserAdmin({ accessToken, formData, userId: user.userId })
    if (res.code === 200) {
      showAlertSucess('Update profile successfully')
      const newUser: User = {
        userId: user.userId,
        firstName: res.result.firstName,
        lastName: res.result.lastName,
        email: res.result.email,
        phone: res.result.phone,
        dob: res.result.dob,
        role: res.result.role,
        avatar: res.result.avatar ?? user.avatar,
        block: res.result.block ?? user.block
      }
      setUsers((prev) => prev?.map((u) => (u.userId === user.userId ? newUser : u)))
      onClose()
    } else {
      showAlertError(res.message || 'Failed to update profile')
    }
  }
  if (!isOpen) return null

  return (
    <div className='fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50'>
      <div
        className='bg-white p-8 rounded-3xl shadow-xl w-full max-w-xl'
        onClick={(e) => {
          e.stopPropagation()
        }}
      >
        <h2 className='text-2xl font-semibold text-blue-600 mb-6 text-center'>Edit user</h2>
        <form onSubmit={handleSubmit(onSubmit)} className='space-y-6'>
          <div className='relative w-32 h-32 mx-auto'>
            {previewUrl ? (
              <img
                src={previewUrl}
                alt='Avatar'
                className='w-full h-full rounded-full object-cover border-4 border-blue-500 shadow-md'
              />
            ) : (
              <div className='w-full h-full rounded-full bg-gray-300 flex items-center justify-center text-xl text-gray-600'>
                No Avatar
              </div>
            )}
            <label className='absolute bottom-0 right-0 bg-blue-600 text-white rounded-full p-2 cursor-pointer shadow-md hover:bg-blue-700 transition'>
              <FiCamera size={18} />
              <input
                type='file'
                accept='image/*'
                onChange={handleImageChange}
                className='absolute inset-0 w-full h-full opacity-0 cursor-pointer'
              />
            </label>
          </div>

          {/* Grid input fields */}
          <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
            <div>
              <label className='block text-sm font-semibold text-gray-700 mb-1'>
                First Name <span className='text-red-500'>*</span>
              </label>
              <input
                type='text'
                {...register('firstName', { required: 'First name is required' })}
                className={`w-full px-3 py-2 border ${
                  errors.firstName ? 'border-red-500 ring-1 ring-red-500' : 'border-gray-300'
                } rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition`}
                placeholder='Enter first name'
              />
              {errors.firstName && <p className='text-red-500 text-sm'>{errors.firstName.message}</p>}
            </div>

            <div>
              <label className='block text-sm font-semibold text-gray-700 mb-1'>
                Last Name <span className='text-red-500'>*</span>
              </label>
              <input
                type='text'
                {...register('lastName', { required: 'Last name is required' })}
                className={`w-full px-3 py-2 border ${
                  errors.lastName ? 'border-red-500 ring-1 ring-red-500' : 'border-gray-300'
                } rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition`}
                placeholder='Enter last name'
              />
              {errors.lastName && <p className='text-red-500 text-sm'>{errors.lastName.message}</p>}
            </div>

            <div>
              <label className='block text-sm font-semibold text-gray-700 mb-1'>
                Email <span className='text-red-500'>*</span>
              </label>
              <input
                type='email'
                {...register('email', {
                  required: 'Email is required',
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: 'Invalid email format'
                  }
                })}
                className={`w-full px-3 py-2 border ${
                  errors.email ? 'border-red-500 ring-1 ring-red-500' : 'border-gray-300'
                } rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition`}
                placeholder='Enter email address'
              />
              {errors.email && <p className='text-red-500 text-sm'>{errors.email.message}</p>}
            </div>

            <div>
              <label className='block text-sm font-semibold text-gray-700 mb-1'>Phone</label>
              <input
                type='tel'
                {...register('phone', {
                  pattern: {
                    value: /^[0-9+\-\s()]{10,15}$/,
                    message: 'Please enter a valid phone number'
                  }
                })}
                className={`w-full px-3 py-2 border ${
                  errors.phone ? 'border-red-500 ring-1 ring-red-500' : 'border-gray-300'
                } rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition`}
                placeholder='Enter phone number'
              />
              {errors.phone && <p className='text-red-500 text-sm'>{errors.phone.message}</p>}
            </div>

            <div>
              <label className='block text-sm font-semibold text-gray-700 mb-1'>Date of Birth</label>
              <input
                type='date'
                {...register('dob', {
                  validate: (value) => {
                    if (!value) return true
                    const date = new Date(value)
                    const today = new Date()
                    return date < today || 'Date of birth cannot be in the future'
                  }
                })}
                className={`w-full px-3 py-2 border ${
                  errors.dob ? 'border-red-500 ring-1 ring-red-500' : 'border-gray-300'
                } rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition`}
              />
              {errors.dob && <p className='text-red-500 text-sm'>{errors.dob.message}</p>}
            </div>
            {/* Role select */}
            <SelectValidate
              name='roleId'
              control={control}
              options={roleOptions}
              label='Role'
              isRequired
              rules={{ required: 'Role is required' }}
              error={errors?.roleId?.message}
            />
          </div>

          {/* Active status select */}

          {/* Action buttons */}
          <div className='flex justify-end gap-4 pt-2'>
            <button
              onClick={onClose}
              className='px-6 py-2 bg-gray-400 text-white rounded-lg hover:bg-gray-500 transition'
            >
              Cancel
            </button>
            <button type='submit' className='px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition'>
              {isEdit ? 'Update' : 'Create'}
            </button>
          </div>
        </form>
      </div>
    </div>
  )
}

export default UserModal
