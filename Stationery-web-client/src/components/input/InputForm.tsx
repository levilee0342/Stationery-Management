import { InputHTMLAttributes, memo } from 'react'
import { FieldErrors, UseFormRegister } from 'react-hook-form'
import { FormLogin } from '~/types/auth'

interface InputFormProps extends InputHTMLAttributes<HTMLInputElement> {
  cssParents?: string
  cssInput?: string
  id: string
  iconLeft?: React.ReactNode
  iconRight?: React.ReactNode
  validate?: object
  iconRequire?: boolean
  label?: string | null
  register: UseFormRegister<any>
  error?: FieldErrors<FormLogin>
}

function InputForm({
  cssParents = '',
  cssInput = '',
  id,
  validate,
  iconRequire,
  label,
  iconLeft,
  iconRight,
  register,
  error,
  ...rest
}: InputFormProps) {
  const hasError = error && error[id as keyof FormLogin]

  return (
    <div className={`${cssParents} mt-1 relative`}>
      {label && (
        <label htmlFor={id} className='block mb-1'>
          {iconRequire && <span className='text-red-500'>*</span>}
          {label}
        </label>
      )}

      <div className='relative flex items-center'>
        {iconLeft}

        <input
          type='text'
          id={id}
          {...register(id, validate)}
          className={`
            ${hasError ? 'border-red-500 ring-1 ring-red-500' : 'border-text-dark-gray'} 
            ${iconLeft ? 'pl-10' : ''}
            ${iconRight ? 'pr-10' : ''}
            ${cssInput}
            placeholder:text-dark-light border-[1px] rounded-md p-2 w-full outline-none focus:border-primary
          `}
          {...rest}
        />

        {iconRight}
      </div>

      <div className='h-[19px] flex items-center mt-1'>
        {hasError ? <small className='text-red-500 text-sm'>{hasError.message}</small> : null}
      </div>
    </div>
  )
}

export default memo(InputForm)
