import React from 'react'
import { Product } from '~/types/product'
import { Controller, useForm } from 'react-hook-form'
import MarkDownEditor from '~/components/markDownEditor/MarkDownEditor'
import SelectValidate from '~/components/select/SelectValidate'

interface EditProductModalProps {
  isOpen: boolean
  product: Product
  onClose: () => void
  onSave: (updatedProduct: { productId: string; name: string; description: string; categoryId: string }) => void
  listCategories: { value: string; label: string }[]
}

const EditProductModal: React.FC<EditProductModalProps> = ({ isOpen, product, onClose, onSave, listCategories }) => {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors: editProductErrors }
  } = useForm<{
    name: string
    description: string
    categoryId: string
  }>({
    defaultValues: {
      name: product.name || '',
      description: product.description || '',
      categoryId: product.category.categoryId || ''
    }
  })
  if (!isOpen) return null

  const handleUpdateProduct = (data: { name: string; description: string; categoryId: string }) => {
    const updatedProduct = {
      name: data.name,
      description: data.description,
      categoryId: data.categoryId,
      productId: product.productId
    }
    onSave(updatedProduct)
    onClose()
  }

  return (
    <div className=''>
      <div className='fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4'>
        <form onSubmit={handleSubmit(handleUpdateProduct)} className=''>
          <div className='bg-white rounded-2xl max-w-2xl w-full p-6 shadow-2xl'>
            <div className='sticky top-0 bg-gradient-to-r from-blue-700 to-blue-600 text-white p-6 rounded-t-2xl flex justify-between items-center mb-4'>
              <h2 className='text-3xl font-bold'>Edit Product</h2>
              <button
                onClick={onClose}
                className='bg-white text-blue-700 px-4 py-2 rounded-lg font-semibold hover:bg-gray-100 transition-colors'
              >
                ✕
              </button>
            </div>
            <div className='space-y-5'>
              <div>
                <label className='block text-sm font-semibold text-gray-700 mb-1'>
                  Name <span className='text-red-500'>*</span>
                </label>
                <input
                  type='text'
                  {...register('name', { required: 'Name is required' })}
                  className={`w-full px-3 py-2 border ${
                    editProductErrors.name ? 'border-red-500 ring-1 ring-red-500' : 'border-gray-300'
                  } rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition`}
                  placeholder='Enter product name'
                />
                {editProductErrors.name && <p className='text-red-500 text-sm'>{editProductErrors.name.message}</p>}
              </div>
              <SelectValidate
                name='categoryId'
                control={control}
                options={listCategories}
                label='Category'
                isRequired
                rules={{ required: 'Category is required' }}
                error={editProductErrors.categoryId?.message}
              />
              <div>
                <label className='block text-sm font-semibold text-gray-700 mb-1'>
                  Description <span className='text-red-500'>*</span>
                </label>
                <Controller
                  name='description'
                  control={control}
                  rules={{ required: 'Description is required' }}
                  render={({ field }) => (
                    <MarkDownEditor
                      height={350}
                      value={field.value}
                      changeValue={(value) => field.onChange(value)}
                      iconRequire={true}
                      error={editProductErrors.description?.message}
                      classParent={editProductErrors.description ? 'description-error' : ''}
                    />
                  )}
                />
              </div>
            </div>

            <div className='mt-8 flex justify-end gap-4'>
              <button
                onClick={onClose}
                className='px-5 py-2 bg-gray-300 text-gray-800 rounded-lg hover:bg-gray-400 transition'
              >
                Cancel
              </button>
              <button
                type='submit'
                className='px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition'
              >
                Save
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  )
}

export default EditProductModal
