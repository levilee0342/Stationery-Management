import React from 'react'
import { useForm } from 'react-hook-form'
import { FaLock } from 'react-icons/fa'
import { apiChangePassword } from '~/api/users'
import { useAppSelector } from '~/hooks/redux'
import { showAlertError, showAlertSucess } from '~/utils/alert'

interface FormData {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}

const PasswordChange: React.FC = () => {
  const { userData } = useAppSelector((state) => state.user)
  const userEmail = userData?.email || ''
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting },
    reset
  } = useForm<FormData>()

  const onSubmit = async (data: FormData) => {
    if (data.newPassword !== data.confirmPassword) {
      showAlertError('Passwords do not match!')
      return
    }

    try {
      const res = await apiChangePassword({
        email: userEmail,
        oldPassword: data.oldPassword,
        newPassword: data.newPassword
      })
      if (res.status !== 200) {
        showAlertError(res.message)
        return
      }
      showAlertSucess('Password changed successfully!')
      reset()
    } catch {
      showAlertError('Failed to change password. Please try again.')
    }
  }

  return (
    <div className='bg-white p-8 rounded-2xl shadow-xl max-w-md mx-auto mt-10'>
      {/* Header */}
      <div className='flex items-center justify-center mb-6'>
        <h2 className='text-2xl font-semibold text-gray-800 flex items-center'>
          <FaLock className='mr-2 text-blue-500' /> Change Password
        </h2>
      </div>

      {/* Form */}
      <form onSubmit={handleSubmit(onSubmit)} className='space-y-5'>
        <div className='relative'>
          <FaLock className='absolute top-3 left-3 text-gray-500' />
          <input
            {...register('oldPassword', { required: 'Please enter your old password' })}
            type='password'
            placeholder='Old Password'
            className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
          />
          {errors.oldPassword?.message && <p className='text-red-500 text-sm'>{errors.oldPassword.message}</p>}
        </div>

        <div className='relative'>
          <FaLock className='absolute top-3 left-3 text-gray-500' />
          <input
            {...register('newPassword', {
              required: 'Please enter a new password',
              minLength: { value: 6, message: 'Password must be at least 6 characters long' },
              pattern: { value: /^[a-zA-Z0-9]+$/, message: 'Password can only contain letters and numbers' }
            })}
            type='password'
            placeholder='New Password'
            className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
          />
          {errors.newPassword?.message && <p className='text-red-500 text-sm'>{errors.newPassword.message}</p>}
        </div>

        <div className='relative'>
          <FaLock className='absolute top-3 left-3 text-gray-500' />
          <input
            {...register('confirmPassword', {
              required: 'Please confirm your new password',
              validate: (value) => value === watch('newPassword') || 'Passwords do not match'
            })}
            type='password'
            placeholder='Confirm New Password'
            className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
          />
          {errors.confirmPassword?.message && <p className='text-red-500 text-sm'>{errors.confirmPassword.message}</p>}
        </div>

        <button
          type='submit'
          disabled={isSubmitting}
          className={`w-full py-3 rounded-lg transition-all duration-200 shadow-md ${
            isSubmitting
              ? 'bg-gray-400 cursor-not-allowed'
              : 'bg-gradient-to-r from-blue-500 to-blue-600 text-white hover:from-blue-600 hover:to-blue-700'
          }`}
        >
          {isSubmitting ? 'Processing...' : 'Update Password'}
        </button>
      </form>
    </div>
  )
}

export default PasswordChange
