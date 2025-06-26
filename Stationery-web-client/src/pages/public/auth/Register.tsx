import { FaUser, FaEnvelope, FaLock } from 'react-icons/fa'
import { useNavigate } from 'react-router-dom'
import { useForm, SubmitHandler } from 'react-hook-form'
import { useState, useEffect } from 'react'
import { showToastError, showToastSuccess } from '~/utils/alert'
import { apiRegister, apiVerifyOtp, apiResendOtp } from '~/api/register'

interface FormData {
  firstName: string
  lastName: string
  email: string
  password: string
  confirmPassword: string
}

interface OtpData {
  otp: string
}

export default function RegisterForm() {
  const navigate = useNavigate()
  const [showOtpForm, setShowOtpForm] = useState(false)
  const [email, setEmail] = useState('')
  const [timeLeft, setTimeLeft] = useState(300)
  const [otpError, setOtpError] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting }
  } = useForm<FormData>({
    defaultValues: {
      firstName: '',
      lastName: '',
      email: '',
      password: '',
      confirmPassword: ''
    }
  })

  const {
    register: registerOtp,
    handleSubmit: handleOtpSubmit,
    formState: { isSubmitting: isOtpSubmitting }
  } = useForm<OtpData>()

  const password = watch('password')

  useEffect(() => {
    if (showOtpForm && timeLeft > 0) {
      const timer = setInterval(() => {
        setTimeLeft((prev) => prev - 1)
      }, 1000)
      return () => clearInterval(timer)
    }
    if (showOtpForm && timeLeft === 0) {
      showToastSuccess('OTP has expired. Please request a new one.')
      // Không ẩn form để người dùng có thể yêu cầu OTP mới ngay lập tức
    }
  }, [showOtpForm, timeLeft])

  const onSubmit: SubmitHandler<FormData> = async (data) => {
    try {
      const response = await apiRegister(data)
      console.log('Reponse', response)
      if (response.success) {
        showToastSuccess(response.data?.message)
        setEmail(data.email)
        setShowOtpForm(true)
        setTimeLeft(300) // Reset timer
      } else {
        showToastError(response.error)
      }
    } catch (error) {
      console.error('Registration failed:', error)
      showToastError('Registration failed')
    }
  }

  const onOtpSubmit: SubmitHandler<OtpData> = async (data) => {
    if (timeLeft === 0) {
      setOtpError('OTP has expired. Please request a new one.')
      return
    }
    try {
      const response = await apiVerifyOtp({
        email,
        otp: parseInt(data.otp)
      })
      if (!response.success) {
        setOtpError(response.error || 'Invalid OTP. Please try again.')
      } else {
        showToastSuccess('Registration successful')
        navigate('/auth?mode=login')
      }
    } catch (error) {
      console.error('OTP verification failed:', error)
      setOtpError('An error occurred. Please try again.')
    }
  }

  const handleResendOtp = async () => {
    try {
      const result = await apiResendOtp({ email })
      if (typeof result === 'string') {
        showToastSuccess(result)
        setTimeLeft(300)
      } else {
        showToastSuccess('OTP sent successfully')
        setTimeLeft(300)
      }
    } catch (error) {
      console.error('Failed to resend OTP:', error)
      showToastSuccess('Failed to resend OTP')
    }
  }

  const formatTime = (seconds: number) => {
    const minutes = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${minutes}:${secs < 10 ? '0' + secs : secs}`
  }

  return (
    <div className='w-1/2 p-8 flex flex-col justify-center bg-blue-100'>
      <h2 className='text-3xl font-bold text-gray-700 mb-6 text-center'>Sign Up</h2>

      {!showOtpForm ? (
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className='relative mb-4'>
            <FaUser className='absolute top-3 left-3 text-gray-500' />
            <input
              {...register('firstName', { required: 'First name is required' })}
              type='text'
              placeholder='First Name'
              className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
              aria-label='First Name'
            />
            {errors.firstName && <p className='text-red-500 text-sm'>{errors.firstName.message}</p>}
          </div>

          <div className='relative mb-4'>
            <FaUser className='absolute top-3 left-3 text-gray-500' />
            <input
              {...register('lastName', { required: 'Last name is required' })}
              type='text'
              placeholder='Last Name'
              className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
              aria-label='Last Name'
            />
            {errors.lastName && <p className='text-red-500 text-sm'>{errors.lastName.message}</p>}
          </div>

          <div className='relative mb-4'>
            <FaEnvelope className='absolute top-3 left-3 text-gray-500' />
            <input
              {...register('email', {
                required: 'Email is required',
                pattern: { value: /\S+@\S+\.\S+/, message: 'Email is invalid' }
              })}
              type='email'
              placeholder='Email'
              className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
              aria-label='Email'
            />
            {errors.email && <p className='text-red-500 text-sm'>{errors.email.message}</p>}
          </div>

          <div className='relative mb-4'>
            <FaLock className='absolute top-3 left-3 text-gray-500' />
            <input
              {...register('password', {
                required: 'Password is required',
                minLength: { value: 7, message: 'Password must be more than 6 characters' },
                pattern: { value: /^[a-zA-Z0-9]+$/, message: 'Password can only contain letters and numbers' }
              })}
              type='password'
              placeholder='Password'
              className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
              aria-label='Password'
            />
            {errors.password && <p className='text-red-500 text-sm'>{errors.password.message}</p>}
          </div>

          <div className='relative mb-4'>
            <FaLock className='absolute top-3 left-3 text-gray-500' />
            <input
              {...register('confirmPassword', {
                required: 'Please confirm your password',
                validate: (value) => value === password || 'Passwords do not match'
              })}
              type='password'
              placeholder='Confirm Password'
              className='w-full p-3 pl-10 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none'
              aria-label='Confirm Password'
            />
            {errors.confirmPassword && <p className='text-red-500 text-sm'>{errors.confirmPassword.message}</p>}
          </div>

          <button
            type='submit'
            className='w-full bg-blue-500 text-white p-3 rounded-lg hover:bg-blue-600 transition disabled:bg-blue-300'
            disabled={isSubmitting}
          >
            {isSubmitting ? 'Processing...' : 'Sign Up'}
          </button>
        </form>
      ) : (
        <div>
          <form onSubmit={handleOtpSubmit(onOtpSubmit)} noValidate>
            <div className='relative mb-4'>
              <input
                {...registerOtp('otp', { required: 'OTP is required' })}
                type='text'
                placeholder='Enter OTP'
                className='w-full p-3 pl-3 border rounded-lg focus:ring-2 focus:ring-blue-400 outline-none mb-4'
              />
              {otpError && <p className='text-red-500 text-sm text-center'>{otpError}</p>}
            </div>
            <button
              type='submit'
              className='w-full bg-blue-500 text-white p-3 rounded-lg hover:bg-blue-600 transition disabled:bg-blue-300'
              disabled={isOtpSubmitting || timeLeft === 0}
            >
              {isOtpSubmitting ? 'Verifying...' : 'Verify OTP'}
            </button>
          </form>

          <div className='mt-4 text-center'>
            <p className='text-gray-600'>Time remaining: {formatTime(timeLeft)}</p>
            <button
              onClick={handleResendOtp}
              disabled={timeLeft > 0}
              className='mt-2 text-blue-600 hover:text-blue-500 disabled:text-gray-400'
            >
              Resend OTP
            </button>
          </div>
        </div>
      )}

      <p className='text-gray-600 text-sm mt-3 text-center'>
        Already have an account?{' '}
        <span className='text-blue-600 cursor-pointer hover:text-blue-500' onClick={() => navigate('/auth?mode=login')}>
          Log in now
        </span>
      </p>
    </div>
  )
}
