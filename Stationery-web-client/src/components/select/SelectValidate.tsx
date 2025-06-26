import Select from 'react-select'
import { Control, Controller, FieldValues, Path } from 'react-hook-form'

interface Option {
  label: string
  value: string
}

interface SelectValidateProps<T extends FieldValues> {
  name: Path<T>
  control: Control<T>
  options: Option[]
  label?: string
  placeholder?: string
  isRequired?: boolean
  isSearchable?: boolean
  isClearable?: boolean
  rules?: object
  error?: string
  className?: string
  onChange?: (value: string) => void
}

function SelectValidate<T extends FieldValues>({
  name,
  control,
  options,
  label,
  placeholder = 'Select an option',
  isRequired = false,
  isSearchable = true,
  isClearable = true,
  rules,
  error,
  className = '',
  onChange
}: SelectValidateProps<T>) {
  return (
    <div className={className}>
      {label && (
        <label className='block text-sm font-semibold text-gray-700 mb-1'>
          {label} {isRequired && <span className='text-red-500'>*</span>}
        </label>
      )}
      <Controller
        name={name}
        control={control}
        rules={rules}
        render={({ field }) => (
          <Select
            options={options}
            value={options.find((option) => option.value === field.value)}
            onChange={(option) => {
              field.onChange(option?.value || '')
              if (onChange) onChange(option?.value || '')
            }}
            onBlur={field.onBlur}
            isSearchable={isSearchable}
            isClearable={isClearable}
            placeholder={placeholder}
            menuPortalTarget={document.body}
            styles={{
              menuPortal: (base) => ({ ...base, zIndex: 9999 }),
              control: (base, state) => ({
                ...base,
                borderColor: error ? '#ef4444' : base.borderColor,
                boxShadow: error ? '0 0 0 1px #ef4444' : base.boxShadow,
                '&:hover': {
                  borderColor: error ? '#ef4444' : state.isFocused ? '#3b82f6' : '#d1d5db'
                }
              })
            }}
            className='react-select-container'
            classNamePrefix='react-select'
          />
        )}
      />
      {error && <p className='text-red-500 text-sm mt-1'>{error}</p>}
    </div>
  )
}

export default SelectValidate
