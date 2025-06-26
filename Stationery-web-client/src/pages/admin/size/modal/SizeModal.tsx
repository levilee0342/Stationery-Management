import { useState } from 'react'
import { TextInput } from '~/components/styles/TextInput'
import { Size } from '~/types/product'

interface Props {
  isOpen: boolean
  isEdit: boolean
  size?: Size
  onClose: () => void
  onSubmit: (size: Size) => void
}

const SizeModal = ({ isOpen, isEdit, size, onClose, onSubmit }: Props) => {
  const [form, setForm] = useState<Size>(size || { sizeId: '', name: '', priority: 1 })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setForm((prev) => ({
      ...prev,
      // Nếu trường priority thì ép kiểu số
      [name]: name === 'priority' ? Number(value) : value
    }))
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
        <h2 className='text-2xl font-semibold text-blue-600 mb-6 text-center'>{isEdit ? 'Edit' : 'Add'} Size</h2>
        {!isEdit && <TextInput label='Size Name' name='sizeId' value={form.sizeId} onChange={handleChange} />}
        <TextInput label='Size Name' name='name' value={form.name} onChange={handleChange} />

        <div className='flex flex-col gap-2 mt-4'>
          <label className='font-medium text-gray-700'>Priority</label>
          <input
            name='priority'
            type='number'
            value={form.priority}
            onChange={handleChange}
            className='w-full border border-gray-300 rounded-lg p-2 focus:outline-none focus:ring-2 focus:ring-blue-300'
          />
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

export default SizeModal
