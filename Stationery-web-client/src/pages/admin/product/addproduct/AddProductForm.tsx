import { Controller, useForm } from 'react-hook-form'
import { ProductForm } from '~/types/product'
import MarkDownEditor from '~/components/markDownEditor/MarkDownEditor'
import SelectValidate from '~/components/select/SelectValidate'

interface AddProductFormProps {
  initialProduct?: Partial<ProductForm>
  listCategories: { value: string; label: string }[]
  onSubmit: (product: Partial<ProductForm>) => void
  onCancel: () => void
}

const AddProductForm: React.FC<AddProductFormProps> = ({ initialProduct, listCategories, onSubmit, onCancel }) => {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors: addProductErrors }
  } = useForm<{
    name: string
    description: string
    categoryId: string
  }>({
    defaultValues: {
      name: initialProduct?.name || '',
      description: initialProduct?.description || '',
      categoryId: initialProduct?.categoryId || ''
    }
  })

  // Xử lý submit form
  const onFormSubmit = (data: { name: string; description: string; categoryId: string }) => {
    onSubmit(data)
  }

  return (
    <div className='mb-8 p-8 rounded-3xl shadow-xl border border-blue-200 transition-all'>
      <h2 className='text-3xl font-bold text-blue-800 mb-6 text-center'>Add New Product</h2>

      <form className='space-y-4' onSubmit={handleSubmit(onFormSubmit)}>
        {/* Form Fields */}
        <div>
          <label className='block text-sm font-semibold text-gray-700 mb-1'>
            Name <span className='text-red-500'>*</span>
          </label>
          <input
            type='text'
            {...register('name', { required: 'Name is required' })}
            className={`w-full px-3 py-2 border ${
              addProductErrors.name ? 'border-red-500 ring-1 ring-red-500' : 'border-gray-300'
            } rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 transition`}
            placeholder='Enter product name'
          />
          {addProductErrors.name && <p className='text-red-500 text-sm'>{addProductErrors.name.message}</p>}
        </div>
        <SelectValidate
          name='categoryId'
          control={control}
          options={listCategories}
          label='Category'
          isRequired
          rules={{ required: 'Category is required' }}
          error={addProductErrors.categoryId?.message}
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
                error={addProductErrors.description?.message}
                classParent={addProductErrors.description ? 'description-error' : ''}
              />
            )}
          />
        </div>
        {/* Buttons */}
        <div className='flex justify-end gap-4 mt-10'>
          <button
            type='button'
            onClick={onCancel}
            className='px-6 py-2.5 bg-white border border-gray-300 text-gray-700 rounded-lg hover:bg-gray-100 transition-colors text-sm font-medium'
          >
            Cancel
          </button>
          <button
            type='submit'
            className='px-6 py-2.5 bg-gradient-to-r from-blue-600 to-blue-500 text-white rounded-lg hover:from-blue-700 hover:to-blue-600 transition text-sm font-semibold shadow-md'
          >
            Next
          </button>
        </div>
      </form>
    </div>
  )
}
export default AddProductForm
