import React, { useEffect, useState } from 'react'
import { SubmitHandler, useForm } from 'react-hook-form'
import { FaEdit, FaSave, FaCamera } from 'react-icons/fa'
import { apiUpdateUserInfo } from '~/api/users'
import InputForm from '~/components/input/InputForm'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { fetchCurrentUser } from '~/store/actions/user'
import { UserProfileForm } from '~/types/user'
import { showAlertError, showAlertSucess } from '~/utils/alert'

const ProfileHeader: React.FC = () => {
  const dispatch = useAppDispatch()
  const { userData, accessToken } = useAppSelector((state) => state.user)
  const [isEditing, setIsEditing] = useState(false)
  const [previewAvatar, setPreviewAvatar] = useState<string | null>(null)
  const [file, setFile] = useState<File | null>(null)
  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors: profileErrors }
  } = useForm<UserProfileForm>({
    defaultValues: {
      firstName: userData?.firstName || '',
      lastName: userData?.lastName || '',
      email: userData?.email || '',
      dob: userData?.dob || '',
      phone: userData?.phone || ''
    }
  })

  const handleAvatarChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      setFile(file)
      const previewUrl = URL.createObjectURL(file) // Tạo URL tạm thời cho hình ảnh
      setPreviewAvatar(previewUrl) // Cập nhật state để hiển thị preview
    }
  }
  const handleSaveProfile = async (updatedData: UserProfileForm | null, file: File | null) => {
    if (!updatedData) {
      console.warn('No data to update')
      return
    }
    console.log(updatedData)
    const formData = new FormData()
    if (file) {
      formData.append('file', file)
    }
    formData.append('document', JSON.stringify({ ...updatedData }))
    if (!accessToken) {
      showAlertError('You must be logged in to update your profile.')
      return
    }
    const res = await apiUpdateUserInfo({ accessToken, formData })
    if (res.code === 200) {
      showAlertSucess('Update profile successfully')
      dispatch(fetchCurrentUser({ token: accessToken }))
    } else {
      showAlertError(res.message || 'Failed to update profile')
    }
    // Gọi API để cập nhật dữ liệu nếu cần
  }
  const handleSubmitProfile: SubmitHandler<UserProfileForm> = async (data: UserProfileForm | null) => {
    console.log(data)
    handleSaveProfile(data, file)
    setIsEditing(false)
  }

  const handleEditToggle = async () => {
    if (isEditing) {
      const data = getValues()
      await handleSaveProfile(data, file)
      setIsEditing(false)
    } else {
      setIsEditing(true)
    }
  }
  useEffect(() => {
    setPreviewAvatar(userData?.avatar || null)
  }, [userData])
  return (
    <div className='bg-white/70 backdrop-blur-md p-8 rounded-2xl shadow-2xl max-w-4xl mx-auto border border-gray-200'>
      <div className='flex justify-between items-center mb-8 border-b pb-4'>
        <h2 className='text-3xl font-bold text-gray-800 tracking-wide'>Profile Information</h2>
        <button
          onClick={handleEditToggle}
          className={`flex items-center gap-2 px-5 py-2.5 rounded-full shadow-md text-white font-medium transition-all duration-300 ${
            isEditing
              ? 'bg-gradient-to-r from-green-500 to-green-600 hover:from-green-600 hover:to-green-700'
              : 'bg-gradient-to-r from-blue-500 to-blue-600 hover:from-blue-600 hover:to-blue-700'
          }`}
        >
          {isEditing ? <FaSave /> : <FaEdit />}
          {isEditing ? 'Save' : 'Edit'}
        </button>
      </div>

      <div className='flex items-center space-x-6 mb-10'>
        <div className='group relative'>
          <label htmlFor='profile-avatar'>
            <img
              src={previewAvatar || 'https://via.placeholder.com/100'}
              alt='Avatar'
              className='w-32 h-32 rounded-full object-cover border-4 border-white shadow-lg transition-transform group-hover:scale-105'
            />
            <input type='file' id='profile-avatar' onChange={handleAvatarChange} className='hidden' />
            {isEditing && (
              <div className='absolute bottom-1 right-1 bg-blue-600 p-2 rounded-full cursor-pointer hover:bg-blue-700 transition-all shadow-md'>
                <FaCamera className='text-white text-sm' />
              </div>
            )}
          </label>
        </div>
        <div>
          <h3 className='text-2xl font-semibold text-gray-900'>
            {userData?.firstName} {userData?.lastName}
          </h3>
          <p className='text-gray-500'>Displayed on your public profile</p>
        </div>
      </div>

      <form onSubmit={handleSubmit(handleSubmitProfile)} className='space-y-8'>
        <div className='grid grid-cols-1 md:grid-cols-2 gap-8'>
          {/* First Name */}
          <div>
            <label className='block text-sm font-semibold text-gray-700 mb-2'>First Name</label>
            {isEditing ? (
              <InputForm
                id='firstName'
                placeholder='First name'
                register={register}
                validate={{
                  required: 'First name is required',
                  minLength: {
                    value: 5, // Minimum length of 5 characters
                    message: 'First name must be at least 5 characters long'
                  }
                }}
                error={profileErrors}
              />
            ) : (
              <p className='text-gray-900 bg-gray-100 p-3 rounded-lg shadow-inner'>
                {userData?.firstName || 'Chưa cập nhật'}
              </p>
            )}
          </div>

          {/* Last Name */}
          <div>
            <label className='block text-sm font-semibold text-gray-700 mb-2'>Last Name</label>
            {isEditing ? (
              <InputForm
                id='lastName'
                placeholder='Last name'
                register={register}
                validate={{
                  required: 'Last name is required',
                  minLength: {
                    value: 5, // Minimum length of 5 characters
                    message: 'First name must be at least 5 characters long'
                  }
                }}
                error={profileErrors}
              />
            ) : (
              <p className='text-gray-900 bg-gray-100 p-3 rounded-lg shadow-inner'>
                {userData?.lastName || 'Chưa cập nhật'}
              </p>
            )}
          </div>

          {/* Email */}
          <div>
            <label className='block text-sm font-semibold text-gray-700 mb-2'>Email</label>
            {isEditing ? (
              <InputForm
                id='email'
                placeholder='Email'
                register={register}
                validate={{
                  required: 'Email is required',
                  pattern: {
                    value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
                    message: 'Please enter a valid email address'
                  }
                }}
                error={profileErrors}
              />
            ) : (
              <p className='text-gray-900 bg-gray-100 p-3 rounded-lg shadow-inner'>
                {userData?.email || 'Chưa cập nhật'}
              </p>
            )}
          </div>

          {/* DOB */}
          <div>
            <label className='block text-sm font-semibold text-gray-700 mb-2'>Date of Birth</label>
            {isEditing ? (
              <InputForm
                id='dob'
                placeholder='Dob'
                type='date'
                register={register}
                error={profileErrors}
                // className='w-full border border-gray-300 rounded-lg p-2.5 focus:ring-2 focus:ring-blue-500 focus:outline-none shadow-sm transition-all text-gray-900'
              />
            ) : (
              <p className='text-gray-900 bg-gray-100 p-3 rounded-lg shadow-inner'>
                {userData?.dob || 'Chưa cập nhật'}
              </p>
            )}
          </div>

          {/* Phone */}
          <div>
            <label className='block text-sm font-semibold text-gray-700 mb-2'>Phone Number</label>
            {isEditing ? (
              <InputForm
                id='phone'
                placeholder='Phone'
                register={register}
                validate={{
                  required: 'Phone number is required',
                  pattern: {
                    value: /^[0-9]{10}$/, // Adjust the regex based on your phone number format
                    message: 'Please enter a valid phone number 10 digits)'
                  }
                }}
                error={profileErrors}
              />
            ) : (
              <p className='text-gray-900 bg-gray-100 p-3 rounded-lg shadow-inner'>
                {userData?.phone || 'Chưa cập nhật'}
              </p>
            )}
          </div>
          <div>
            <label className='block text-sm font-semibold text-gray-700 mb-2'>Phone Number</label>
            {isEditing ? (
              <InputForm
                id='phone'
                placeholder='Phone'
                register={register}
                validate={{
                  required: 'Phone number is required',
                  pattern: {
                    value: /^[0-9]{10}$/, // Adjust the regex based on your phone number format
                    message: 'Please enter a valid phone number 10 digits)'
                  }
                }}
                error={profileErrors}
              />
            ) : (
              <p className='text-gray-900 bg-gray-100 p-3 rounded-lg shadow-inner'>
                {userData?.phone || 'Chưa cập nhật'}
              </p>
            )}
          </div>
        </div>
      </form>
    </div>
  )
}

export default ProfileHeader
