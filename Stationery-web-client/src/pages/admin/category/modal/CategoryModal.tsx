import { useState } from 'react'
import { TextInput } from '~/components/styles/TextInput'
import { Category } from '~/types/category'

interface Props {
  isOpen: boolean
  isEdit: boolean
  category?: Category
  onClose: () => void
  onSubmit: (category: Category) => void
}

const CategoryModal = ({ isOpen, isEdit, category, onClose, onSubmit }: Props) => {
  const [form, setForm] = useState<Category>(
    category || { categoryId: '', categoryName: '', icon: '', bgColor: '#ffffff' }
  )

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
        <h2 className='text-2xl font-semibold text-blue-600 mb-6 text-center'>{isEdit ? 'Edit' : 'Add'} Category</h2>
        {!isEdit && <TextInput label='Category Id' name='categoryId' value={form.categoryId} onChange={handleChange} />}
        {/* Tên danh mục */}
        <TextInput label='Category Name' name='categoryName' value={form.categoryName} onChange={handleChange} />

        {/* Icon */}
        <TextInput label='Icon (e.g. 🎮)' name='icon' value={form.icon || ''} onChange={handleChange} />

        {/* Chọn màu */}
        <div className='flex flex-col gap-2 mt-4'>
          <label className='font-medium text-gray-700'>Background Color</label>
          <div className='flex items-center gap-4'>
            {/* Thanh chọn màu tròn */}
            <input
              name='bgColor'
              value={form.bgColor}
              onChange={handleChange}
              type='color'
              className='w-10 h-10 border-none p-0 appearance-none cursor-pointer'
            />
            {/* Preview mã màu */}
            <span className='text-gray-700 text-sm'>{form.bgColor}</span>
          </div>
        </div>

        {/* Buttons */}
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

export default CategoryModal
