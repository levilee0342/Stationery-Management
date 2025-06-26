import { ChangeEvent } from 'react'

type TextInputProps = {
  label: string
  name: string
  type?: string
  value: string | number
  onChange?: (e: ChangeEvent<HTMLInputElement>) => void
  readOnly?: boolean
}

export const TextInput = ({ label, name, type = 'text', value, onChange, readOnly = false }: TextInputProps) => {
  return (
    <div className='flex flex-col gap-2'>
      <label htmlFor={name} className='font-medium text-gray-700'>
        {label}
      </label>
      <input
        id={name}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        readOnly={readOnly}
        className={`w-full p-3 ${
          readOnly ? 'bg-gray-100' : 'bg-white'
        } border border-gray-300 rounded-lg shadow-sm text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-500`}
      />
    </div>
  )
}
