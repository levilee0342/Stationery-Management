import { useState } from 'react'
import { TextInput } from '~/components/styles/TextInput'
import { Color } from '~/types/product'

interface Props {
  isOpen: boolean
  isEdit: boolean
  color?: Color
  onClose: () => void
  onSubmit: (color: Color) => void
}

const ColorModal = ({ isOpen, isEdit, color, onClose, onSubmit }: Props) => {
  const [form, setForm] = useState<Color>(color || { colorId: '', name: '', hex: '#ffffff' })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = () => {
    onSubmit(form)
  }

  if (!isOpen) return null

  return (
    <div
      className='fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40'
      onClick={(e) => e.stopPropagation()}
    >
      <div className='bg-white p-6 rounded-xl w-full max-w-md shadow-lg' onClick={(e) => e.stopPropagation()}>
        <h2 className='text-2xl font-semibold text-blue-600 mb-6 text-center'>{isEdit ? 'Edit' : 'Add'} Color</h2>
        {!isEdit && <TextInput label='Color Id' name='colorId' value={form.colorId} onChange={handleChange} />}
        <TextInput label='Color Name' name='name' value={form.name} onChange={handleChange} />

        <div className='flex flex-col gap-2 mt-4'>
          <label className='font-medium text-gray-700'>Color Hex</label>
          <div className='flex items-center gap-4'>
            <input
              name='hex'
              value={form.hex}
              onChange={handleChange}
              type='color'
              className='w-10 h-10 border-none p-0 appearance-none cursor-pointer'
            />
            <span className='text-gray-700 text-sm'>{form.hex}</span>
          </div>
        </div>

        <div className='flex justify-end gap-2 mt-6'>
          <button onClick={onClose} className='px-4 py-2 bg-gray-300 rounded'>
            Cancel
          </button>
          <button onClick={handleSubmit} className='px-4 py-2 bg-blue-600 text-white rounded'>
            {isEdit ? 'Update' : 'Add'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default ColorModal
