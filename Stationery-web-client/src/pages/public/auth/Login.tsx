import { AxiosError } from 'axios'
import { FaEnvelope, FaEye, FaEyeSlash, FaLock } from 'react-icons/fa'
import { FcGoogle } from 'react-icons/fc'
import { useNavigate, useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'
import InputForm from '~/components/input/InputForm'
import { SubmitHandler, useForm } from 'react-hook-form'
import { FormLogin, ForgotPasswordRequest, ResetPasswordRequest } from '~/types/auth'
import { showToastError, showToastSuccess } from '~/utils/alert'
import { apiLogin, apiForgotPassword, apiResetPassword } from '~/api/authenticate'
import { useAppDispatch } from '~/hooks/redux'
import { userActions } from '~/store/slices/user'
import { apiGetUserInfo } from '~/api/users'
import { User } from '~/types/user'

export default function LoginForm() {
  const navigate = useNavigate()
  const location = useLocation()
  const [loading, setLoading] = useState(false)
  const dispatch = useAppDispatch()
  const [isShowPassword, setIsShowPassword] = useState(false)
  const [isForgotPassword, setIsForgotPassword] = useState(false)
  const [emailForOtp, setEmailForOtp] = useState('')
  const [isOtpSent, setIsOtpSent] = useState(false)

  // Form login
  const {
    register: registerLogin,
    handleSubmit: handleSubmitLogin,
    formState: { errors: loginErrors }
  } = useForm<FormLogin>()

  // Form quên mật khẩu
  const {
    register: registerForgot,
    handleSubmit: handleSubmitForgot,
    formState: { errors: forgotErrors }
  } = useForm<ForgotPasswordRequest>()

  // Form đặt lại mật khẩu
  const {
    register: registerReset,
    handleSubmit: handleSubmitReset,
    formState: { errors: resetErrors }
  } = useForm<ResetPasswordRequest>()

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search)
    const token = queryParams.get('token')
    if (token) {
      setLoading(true)
      apiGetUserInfo({ token })
        .then((response) => {
          if (response.code == 200) {
            const userData: User = response.result
            dispatch(userActions.login({ accessToken: token, userData }))
            showToastSuccess('Login successfully')
            navigate('/')
          } else {
            showToastError(response.message || 'Login failed')
            navigate('/auth')
          }
        })
        .catch((error) => {
          showToastError(error.message)
          navigate('/auth') // Chuyển hướng về trang đăng nhập nếu có lỗi
        })
        .finally(() => {
          setLoading(false)
          // Xóa dòng này để không ghi đè chuyển hướng
          // navigate('/login', { replace: true });
        })
    }
  }, [location, dispatch, navigate])

  // Login submit
  const onSubmitLogin: SubmitHandler<FormLogin> = async (data) => {
    try {
      setLoading(true)
      const response = await apiLogin(data)
      if (typeof response !== 'string' && response.result) {
        const { accessToken, userData } = response.result
        dispatch(userActions.login({ accessToken, userData }))
        showToastSuccess('Login successfully')
        if (userData.role?.roleId == '113') {
          navigate('/department')
        } else {
          navigate('/')
        }
      } else {
        showToastError(response.message || 'Login failed')
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    } finally {
      setLoading(false)
    }
  }

  // Forgot password submit
  const onSubmitForgot: SubmitHandler<ForgotPasswordRequest> = async (data) => {
    try {
      setLoading(true)
      const response = await apiForgotPassword(data)
      if (typeof response !== 'string' && response.message) {
        setEmailForOtp(data.email)
        setIsOtpSent(true)
        showToastSuccess('OTP has been sent to your email')
      } else {
        showToastError(response.message || 'Failed to send OTP')
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    } finally {
      setLoading(false)
    }
  }

  // Reset password submit
  const onSubmitReset: SubmitHandler<ResetPasswordRequest> = async (data) => {
    try {
      setLoading(true)
      const response = await apiResetPassword({ email: emailForOtp, otp: data.otp }, data.newPassword)
      if (typeof response !== 'string' && response.message) {
        showToastSuccess('Password reset successfully')
        setIsForgotPassword(false)
        setIsOtpSent(false)
        navigate('/auth')
      } else {
        showToastError(response.message || 'Failed to reset password')
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    } finally {
      setLoading(false)
    }
  }

  const renderLoginForm = () => (
    <div className='w-1/2 p-8 flex flex-col justify-center translate-x-full'>
      <h2 className='text-3xl font-bold text-gray-700 mb-6 text-center'>Login</h2>
      <form onSubmit={handleSubmitLogin(onSubmitLogin)}>
        <InputForm
          iconLeft={<FaEnvelope className='absolute top-3 left-3 text-gray-500' />}
          id='email'
          cssInput='pl-10'
          placeholder='Email'
          register={registerLogin}
          validate={{
            required: 'Email is required',
            pattern: {
              value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
              message: 'Please enter a valid email address'
            }
          }}
          error={loginErrors}
        />
        <InputForm
          iconLeft={<FaLock className='absolute top-3 left-3 text-gray-500' />}
          iconRight={
            <div onClick={() => setIsShowPassword((prev) => !prev)}>
              {isShowPassword ? (
                <FaEyeSlash className='absolute top-3 right-3 text-gray-500' />
              ) : (
                <FaEye className='absolute top-3 right-3 text-gray-500' />
              )}
            </div>
          }
          id='password'
          cssInput='pl-10'
          type={isShowPassword ? 'text' : 'password'}
          placeholder='Password'
          register={registerLogin}
          validate={{ required: 'Password is required' }}
          error={loginErrors}
        />
        <button
          type='submit'
          className='w-full bg-blue-500 text-white p-3 rounded-lg hover:bg-blue-600 transition mt-1'
          disabled={loading}
        >
          {loading ? 'Processing...' : 'Login'}
        </button>
      </form>
      <button
        className='w-full mt-3 flex items-center justify-center gap-2 bg-white border p-3 rounded-lg shadow-md hover:bg-gray-100 transition'
        disabled={loading}
        onClick={() => (window.location.href = 'http://localhost:8080/api/oauth2/authorization/google')}
      >
        <FcGoogle className='text-xl' />
        <span>{loading ? 'Processing...' : 'Login with Google'}</span>
      </button>
      <p
        className='text-blue-600 text-sm mt-3 text-center hover:text-blue-500 cursor-pointer'
        onClick={() => setIsForgotPassword(true)}
      >
        Forgot password?
      </p>
      <p className='text-gray-600 text-sm mt-3 text-center'>
        Don't have an account?{' '}
        <span
          className='text-blue-600 cursor-pointer hover:text-blue-500'
          onClick={() => navigate('/auth?mode=register')}
        >
          Sign up now
        </span>
      </p>
    </div>
  )

  const renderForgotPasswordForm = () => (
    <div className='w-1/2 p-8 flex flex-col justify-center translate-x-full'>
      <h2 className='text-3xl font-bold text-gray-700 mb-6 text-center'>
        {isOtpSent ? 'Reset Password' : 'Forgot Password'}
      </h2>
      {!isOtpSent ? (
        <form onSubmit={handleSubmitForgot(onSubmitForgot)}>
          <InputForm
            iconLeft={<FaEnvelope className='absolute top-3 left-3 text-gray-500' />}
            id='email'
            cssInput='pl-10'
            placeholder='Enter your email'
            register={registerForgot}
            validate={{
              required: 'Email is required',
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: 'Invalid email format'
              }
            }}
            error={forgotErrors}
          />
          <button
            type='submit'
            className='w-full bg-blue-500 text-white p-3 rounded-lg hover:bg-blue-600 transition mt-1'
            disabled={loading}
          >
            {loading ? 'Processing...' : 'Send OTP'}
          </button>
        </form>
      ) : (
        <form onSubmit={handleSubmitReset(onSubmitReset)}>
          <InputForm
            id='otp'
            cssInput='pl-4'
            placeholder='Enter OTP'
            register={registerReset}
            validate={{ required: 'OTP is required' }}
            error={resetErrors}
          />
          <InputForm
            iconLeft={<FaLock className='absolute top-3 left-3 text-gray-500' />}
            id='newPassword'
            cssInput='pl-10'
            type='password'
            placeholder='New Password'
            register={registerReset}
            validate={{
              required: 'New password is required',
              minLength: { value: 6, message: 'Password must be at least 6 characters' }
            }}
            error={resetErrors}
          />
          <button
            type='submit'
            className='w-full bg-blue-500 text-white p-3 rounded-lg hover:bg-blue-600 transition mt-1'
            disabled={loading}
          >
            {loading ? 'Processing...' : 'Reset Password'}
          </button>
        </form>
      )}
      <p
        className='text-blue-600 text-sm mt-3 text-center hover:text-blue-500 cursor-pointer'
        onClick={() => {
          setIsForgotPassword(false)
          setIsOtpSent(false)
        }}
      >
        Back to Login
      </p>
    </div>
  )

  return isForgotPassword ? renderForgotPasswordForm() : renderLoginForm()
}
