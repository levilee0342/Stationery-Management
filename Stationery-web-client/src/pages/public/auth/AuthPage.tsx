import { useSearchParams } from 'react-router-dom'
import LoginForm from './Login'
import RegisterForm from './Register'

export default function AuthPage() {
  const [searchParams] = useSearchParams()
  const isRegister = searchParams.get('mode') === 'register'

  return (
    <div
      className='flex justify-center items-center h-screen bg-center mt-8'
      style={{
        backgroundImage: "url('/images/backgroud-login.svg')",
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover'
      }}
    >
      <div className='relative w-[850px] h-[650px] bg-white shadow-lg rounded-lg overflow-hidden flex'>
        {isRegister ? <RegisterForm /> : <LoginForm />}

        {/* Hiệu ứng Overlay */}
        <div
          className={`absolute inset-0 flex items-center justify-center w-1/2 bg-blue-500 text-white transition-transform duration-700 ${
            isRegister ? 'translate-x-full' : 'translate-x-0'
          }`}
        >
          <div className='text-center'>
            <h2 className='text-3xl font-bold'>{isRegister ? 'Start Your New Journey!' : 'Welcome Back!'}</h2>
            <p className='text-sm mt-2'>
              {isRegister ? 'Already have an account? Log in now!' : "Don't have an account? Sign up now!"}
            </p>
          </div>
        </div>
      </div>
    </div>
  )
}
