import React, { useState } from 'react'
import { useForm } from 'react-hook-form'
import { FaUpload, FaEdit, FaTrash } from 'react-icons/fa'
import Swal from 'sweetalert2'
import SelectValidate from '~/components/select/SelectValidate'

import InputForm from '~/components/input/InputForm'
import { convertToSlug } from '~/utils/stringUtils'
import { ProductDetailForm } from '~/types/product'
// update còn lỗi
interface ProductDetailUseForm {
  colorId: string
  sizeId: string
  name: string
  slug: string
  quantity: number
  reviewImage: string[]
  originalPrice: number
  discountPrice: number
}

interface AddProductDetailsFormProps {
  productDetails: ProductDetailForm[]
  sizes: { value: string; label: string }[]
  colors: { value: string; label: string }[]
  onAddDetail: (detail: ProductDetailForm) => void
  onUpdateDetail: (detail: ProductDetailForm) => void
  onDeleteDetail: (detailId: string) => void
  onFinish: () => void
  onCancel: () => void
}
interface ListImage {
  file: File
  url: string
}

const AddProductDetailsForm: React.FC<AddProductDetailsFormProps> = ({
  productDetails,
  sizes,
  colors,
  onAddDetail,
  onUpdateDetail,
  onDeleteDetail,
  onFinish,
  onCancel
}) => {
  const [listImages, setListImages] = useState<ListImage[]>(Array(5).fill({ file: {} as File, url: '' }))
  const {
    register,
    handleSubmit,
    control,
    setValue,
    reset,
    formState: { errors: addPDErrors }
  } = useForm<ProductDetailUseForm>({
    defaultValues: {
      colorId: '',
      sizeId: '',
      slug: '',
      name: '',
      reviewImage: [],
      quantity: 0,
      discountPrice: 0,
      originalPrice: 0
    }
  })
  const [isEditing, setIsEditing] = useState(false)
  const [editingDetailId, setEditingDetailId] = useState<string | null>(null)

  const handleDetailImageChange = (index: number, file: File) => {
    if (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const newListImages = [...listImages]
        newListImages[index] = {
          file, // Lưu file gốc để sau này gửi lên server
          url: e.target?.result as string // URL base64 để hiển thị preview
        }
        setListImages(newListImages)
      }
      reader.readAsDataURL(file) // Chuyển file thành base64 URL
    }
  }
  const handleAddOrUpdateDetail = (data: ProductDetailUseForm) => {
    const detail: ProductDetailForm = {
      originalPrice: data.originalPrice,
      discountPrice: data.discountPrice,
      slug: data.slug,
      name: data.name,
      stockQuantity: data.quantity,
      sizeId: data.sizeId,
      colorId: data.colorId,
      images: listImages.filter((img) => img.url !== '')
    }
    if (isEditing) {
      onUpdateDetail(detail)
      setIsEditing(false)
      setEditingDetailId(null)
    } else {
      onAddDetail(detail)
    }
    // Reset toàn bộ form
    reset({
      colorId: '',
      sizeId: '',
      slug: '',
      quantity: 0,
      originalPrice: 0,
      discountPrice: 0
    })
    // Reset listImages
    setListImages(
      Array(5)
        .fill(null)
        .map(() => ({
          file: {} as File,
          url: ''
        }))
    )
  }

  const handleEditDetail = (detail: ProductDetailForm) => {
    // Lưu thông tin chi tiết đang chỉnh sửa
    setIsEditing(true)
    setEditingDetailId(detail.slug)

    // Điền giá trị vào các trường form
    setValue('colorId', detail.colorId)
    setValue('sizeId', detail.sizeId)
    setValue('slug', detail.slug)
    setValue('quantity', detail.stockQuantity)
    setValue('originalPrice', detail.originalPrice)
    setValue('discountPrice', detail.discountPrice)
    setValue('name', detail.name)

    // Cập nhật danh sách ảnh
    if (detail.images && detail.images.length > 0) {
      // Tạo mảng mới với 5 phần tử trống
      const newListImages = Array(5)
        .fill(null)
        .map(() => ({
          file: {} as File,
          url: ''
        }))

      // Điền các ảnh từ chi tiết sản phẩm vào mảng mới
      detail.images.forEach((img, index) => {
        if (index < 5) {
          newListImages[index] = {
            file: img.file, // Không có file vì đây là ảnh đã lưu trên server
            url: img.url // URL của ảnh từ server
          }
        }
      })

      // Cập nhật state listImages
      setListImages(newListImages)
    }
  }

  const handleDelete = (detailId: string) => {
    Swal.fire({
      title: 'Xác nhận xóa?',
      text: 'Bạn chắc chắn muốn xóa chi tiết sản phẩm này?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Xóa',
      cancelButtonText: 'Hủy'
    }).then((result) => {
      if (result.isConfirmed) {
        onDeleteDetail(detailId)
        Swal.fire('Đã xóa!', 'Chi tiết sản phẩm đã được xóa.', 'success')
        if (isEditing && editingDetailId === detailId) {
          // Reset toàn bộ form
          reset({
            colorId: '',
            sizeId: '',
            slug: '',
            name: '',
            quantity: 0,
            originalPrice: 0,
            discountPrice: 0
          })
          // Reset listImages
          setListImages(
            Array(5)
              .fill(null)
              .map(() => ({
                file: {} as File,
                url: ''
              }))
          )
          setIsEditing(false)
          setEditingDetailId(null)
        }
      }
    })
  }

  return (
    <div className='mb-8 p-6 bg-white rounded-2xl shadow-md border border-gray-100'>
      <h2 className='text-2xl font-bold text-blue-700 mb-6'>Add Product Details</h2>

      {/* Added Details Table */}
      <div className='mb-8'>
        <h3 className='text-lg font-semibold text-gray-700 mb-4'>Added Details</h3>
        {productDetails.length === 0 ? (
          <p className='text-gray-500 italic'>No details added yet.</p>
        ) : (
          <div className='overflow-x-auto'>
            <table className='w-full border-collapse'>
              <thead>
                <tr className='bg-blue-100 text-blue-800 text-sm uppercase'>
                  <th className='px-4 py-3 text-left'>Color</th>
                  <th className='px-4 py-3 text-left'>Size</th>
                  <th className='px-4 py-3 text-left'>Quantity</th>
                  <th className='px-4 py-3 text-left'>Price</th>
                  <th className='px-4 py-3 text-left'>Images</th>
                  <th className='px-4 py-3 text-left'>Actions</th>
                </tr>
              </thead>
              <tbody className='divide-y divide-gray-200'>
                {productDetails.map((detail) => (
                  <tr key={detail.slug} className='hover:bg-gray-50 transition-colors'>
                    <td className='px-4 py-3 text-sm'>
                      <div className='flex items-center gap-2'>
                        {colors.find((c) => c.value == detail.colorId)?.label}
                      </div>
                    </td>
                    <td className='px-4 py-3 text-sm'>{sizes.find((s) => s.value == detail.sizeId)?.label}</td>
                    <td className='px-4 py-3 text-sm'>{detail.stockQuantity.toLocaleString('vi-VN')}</td>
                    <td className='px-4 py-3 text-sm'>{detail.discountPrice.toLocaleString('vi-VN')} VND</td>
                    <td className='px-4 py-3 text-sm'>
                      <div className='flex gap-2'>
                        {detail.images
                          ?.slice(0, 3)
                          .map((img, idx) => (
                            <img
                              key={idx}
                              src={img.url}
                              alt={`Detail-${idx}`}
                              className='w-8 h-8 rounded object-cover'
                            />
                          ))}
                        {detail.images && detail.images.length > 3 && (
                          <span className='text-gray-500'>+{detail.images.length - 3}</span>
                        )}
                      </div>
                    </td>
                    <td className='px-4 py-3 text-sm'>
                      <div className='flex gap-2'>
                        <button
                          onClick={() => handleEditDetail(detail)}
                          className='p-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition-colors'
                        >
                          <FaEdit size={14} />
                        </button>
                        <button
                          onClick={() => handleDelete(detail.slug)}
                          className='p-2 bg-red-500 text-white rounded-lg hover:bg-red-600 transition-colors'
                        >
                          <FaTrash size={14} />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Detail Form */}
      <form onSubmit={handleSubmit(handleAddOrUpdateDetail)} className='space-y-6'>
        <div className='p-6 bg-gray-50 rounded-xl border border-gray-200'>
          <h3 className='text-lg font-semibold text-gray-700 mb-4'>{isEditing ? 'Edit Detail' : 'Add New Detail'}</h3>
          <div className='space-y-5'>
            <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
              <SelectValidate
                name='colorId'
                control={control}
                options={colors}
                label='Color'
                isRequired
                rules={{ required: 'Color is required' }}
                error={addPDErrors.colorId?.message}
              />
              <SelectValidate
                name='sizeId'
                control={control}
                options={sizes}
                label='Size'
                isRequired
                error={addPDErrors.sizeId?.message}
              />
              <InputForm
                id='quantity'
                cssInput='pl-10'
                placeholder='Enter quantity'
                label={'Quantity'}
                register={register}
                validate={{
                  required: 'quantity is required',
                  pattern: {
                    value: /^[0-9]+$/, // Đảm bảo chỉ chứa số
                    message: 'quantity must be a number'
                  },
                  validate: {
                    positive: (value: any) => parseFloat(value) > 0 || 'Quantity must be greater than 0'
                  }
                }}
                error={addPDErrors}
              />
              <InputForm
                id='name'
                cssInput='pl-10'
                placeholder='Enter Name'
                label={'Name'}
                register={register}
                validate={{
                  required: 'Name is required'
                }}
                error={addPDErrors}
              />
            </div>
            <InputForm
              id='slug'
              cssInput='pl-10'
              placeholder='Slug'
              label={'Slug'}
              register={register}
              validate={{
                required: 'Slug is required',
                pattern: {
                  value: /^[a-z0-9]+(?:-[a-z0-9]+)*$/,
                  message: 'Slug must contain only lowercase letters, numbers, and hyphens'
                }
              }}
              onChange={(e) => {
                const value = e.target.value
                setValue('slug', convertToSlug(value))
              }}
              error={addPDErrors}
            />
            <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
              <InputForm
                id='originalPrice'
                cssInput='pl-10'
                label={'Original Price'}
                placeholder='Enter original price'
                register={register}
                validate={{
                  required: 'Original price is required',
                  pattern: {
                    value: /^[0-9]+$/, // Đảm bảo chỉ chứa số
                    message: 'Original price must be a number'
                  },
                  validate: {
                    positive: (value: any) => parseFloat(value) > 0 || 'Original price must be greater than 0'
                  }
                }}
                error={addPDErrors}
              />
              <InputForm
                id='discountPrice'
                label={'Discount Price'}
                cssInput='pl-10'
                placeholder='Enter original price'
                register={register}
                validate={{
                  required: 'Discount price is required',
                  pattern: {
                    value: /^[0-9]+$/, // Đảm bảo chỉ chứa số
                    message: 'Discount price must be a number'
                  },
                  validate: {
                    notGreaterThanOriginal: (value: any, formValues: any) => {
                      const originalPrice = parseInt(formValues.originalPrice || 0)
                      const discountPrice = parseInt(value)
                      console.log('discountPrice', discountPrice)
                      return discountPrice <= originalPrice || 'Discount price cannot be greater than original price'
                    }
                  }
                }}
                error={addPDErrors}
              />
            </div>

            <div>
              <label className='block text-sm font-semibold text-gray-700 mb-2'>Image Gallery</label>
              <div>
                <label className='block text-sm font-semibold text-gray-700 mb-2'>Image Gallery</label>
                <div className='grid grid-cols-5 gap-3'>
                  {listImages.map((img, index) => {
                    return (
                      <div
                        key={index}
                        className='relative h-24 border border-dashed border-gray-300 rounded-lg flex items-center justify-center overflow-hidden bg-gray-100'
                      >
                        {img.url ? (
                          <div className='relative w-full h-full'>
                            <img src={img.url} alt={`Preview ${index + 1}`} className='w-full h-full object-cover' />
                            {img.file && (
                              <div className='absolute top-1 left-1 bg-black/50 text-white text-xs px-1 py-0.5 rounded'>
                                {img.file.name.length > 10 ? `${img.file.name.substring(0, 8)}...` : img.file.name}
                              </div>
                            )}
                          </div>
                        ) : (
                          <span className='text-gray-400 text-xs text-center'>Image {index + 1}</span>
                        )}
                        <input
                          type='file'
                          onChange={(e) => {
                            if (e.target.files && e.target.files[0]) {
                              handleDetailImageChange(index, e.target.files[0])
                            }
                          }}
                          className='absolute inset-0 opacity-0 cursor-pointer'
                        />
                        <div className='absolute bottom-1 right-1 bg-blue-500 text-white p-1 rounded-full hover:bg-blue-600 transition-colors'>
                          <FaUpload className='text-xs' />
                        </div>
                      </div>
                    )
                  })}
                </div>
              </div>
            </div>
          </div>

          <div className='flex gap-4 mt-6'>
            <button
              type='submit'
              className={`px-5 py-2.5 text-white rounded-lg transition-colors text-sm font-medium ${
                isEditing ? 'bg-yellow-500 hover:bg-yellow-600' : 'bg-blue-600 hover:bg-blue-700'
              }`}
            >
              {isEditing ? 'Update Detail' : 'Add Detail'}
            </button>
            <button
              onClick={onFinish}
              className='px-5 py-2.5 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors text-sm font-medium'
            >
              Next
            </button>
            <button
              onClick={onCancel}
              className='px-5 py-2.5 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors text-sm font-medium'
            >
              Cancel
            </button>
          </div>
        </div>
      </form>
    </div>
  )
}

export default AddProductDetailsForm
