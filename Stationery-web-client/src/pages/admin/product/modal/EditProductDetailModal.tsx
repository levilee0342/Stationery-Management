import React, { Dispatch, SetStateAction, useState } from 'react'
import Swal from 'sweetalert2'
import { ListProductDetail, ProductDetailForm } from '~/types/product'
import { useForm } from 'react-hook-form'
import { IoTrashOutline } from 'react-icons/io5'
import InputForm from '~/components/input/InputForm'
import { convertToSlug } from '~/utils/stringUtils'
import SelectValidate from '~/components/select/SelectValidate'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { modalActions } from '~/store/slices/modal'
import PacmanLoading from '~/components/loading/PacmanLoading'
import { apiUpdateDetailProduct } from '~/api/product'
import { showToastWarning } from '~/utils/alert'

interface EditProductDetailModalProps {
  sizes: { value: string; label: string }[]
  colors: { value: string; label: string; color: string }[]
  setProducts: Dispatch<SetStateAction<ListProductDetail[]>>
  isOpen: boolean
  productId: string
  productDetailId: string
  detail: ProductDetailForm
  onClose: () => void
}
interface ProductDetailUseForm {
  colorId: string
  sizeId: string
  name: string
  slug: string
  quantity: number
  originalPrice: number
  discountPrice: number
}
interface ListImage {
  file: File
  url: string
  imageId?: string
}

const EditProductDetailModal: React.FC<EditProductDetailModalProps> = ({
  isOpen,
  productId,
  colors,
  sizes,
  productDetailId,
  setProducts,
  detail,
  onClose
}) => {
  const dispatch = useAppDispatch()
  const { accessToken } = useAppSelector((state) => state.user)
  const [listImages, setListImages] = useState<ListImage[]>(detail.images ?? [])
  const [deleteImageId, setSetDeleteImageId] = useState<string[]>([])
  const {
    register,
    handleSubmit,
    control,
    setValue,
    formState: { errors: editPdErrors }
  } = useForm<ProductDetailUseForm>({
    defaultValues: {
      colorId: detail.colorId || '',
      sizeId: detail.sizeId || '',
      slug: detail.slug || '',
      name: detail.name || '',
      quantity: detail.stockQuantity || 0,
      discountPrice: detail.discountPrice || 0,
      originalPrice: detail.originalPrice || 0
    }
  })
  console.log('detail', detail)
  if (!isOpen) return null

  const handleDetailImageChange = (index: number, file: File) => {
    if (file) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const newListImages = [...listImages]
        const imgId = listImages[index]?.imageId || ''
        newListImages[index] = {
          file, // Lưu file gốc để sau này gửi lên server
          url: e.target?.result as string, // URL base64 để hiển thị preview
          imageId: imgId // Giữ lại imageId nếu có, để gửi lên server
        }
        setListImages(newListImages)
      }
      reader.readAsDataURL(file) // Chuyển file thành base64 URL
    }
  }
  console.log(productDetailId, 'productDetailId')
  const handleUpdatePd = async (data: ProductDetailUseForm) => {
    if (!accessToken) {
      showToastWarning('Please login to continue')
      return
    }
    const formData = new FormData()
    const imagesWithFile = listImages.filter((img) => img.file instanceof File)
    const detail: ProductDetailForm = {
      productDetailId: productDetailId,
      originalPrice: data.originalPrice,
      discountPrice: data.discountPrice,
      slug: data.slug,
      name: data.name,
      stockQuantity: data.quantity,
      sizeId: data.sizeId,
      colorId: data.colorId,
      deleteImages: deleteImageId.length > 0 ? deleteImageId : undefined
    }
    console.log(detail)
    formData.append('documents', JSON.stringify(detail))
    imagesWithFile.forEach((img) => {
      if (img.file) {
        formData.append('images', img.file) // hoặc `images[${img.index}]` nếu backend yêu cầu index
        formData.append('imgIdToUpdate', String(img.imageId)) // nếu muốn gửi index kèm
      }
    })
    dispatch(modalActions.toggleModal({ childrenModal: <PacmanLoading />, isOpenModal: true }))
    const res = await apiUpdateDetailProduct({ data: formData, accessToken })
    dispatch(modalActions.toggleModal({ childrenModal: null, isOpenModal: false }))
    if (res.code === 200) {
      Swal.fire({
        icon: 'success',
        title: 'Success',
        text: 'Add product successfully'
      })
      setProducts((prev) =>
        prev.map((p) => {
          if (p.productId === productId) {
            return {
              ...p,
              productDetails: p.productDetails.map((d) => {
                if (detail.productDetailId === productDetailId) {
                  return {
                    ...d,
                    colorId: data.colorId,
                    sizeId: data.sizeId,
                    name: data.name,
                    slug: data.slug,
                    stockQuantity: data.quantity,
                    originalPrice: data.originalPrice,
                    discountPrice: data.discountPrice,
                    images: listImages
                      .filter((img) => img.url != '')
                      .map((img, idx) => ({
                        priority: idx,
                        url: img.url,
                        imageId: img.imageId || ''
                      }))
                  }
                } else {
                  return d
                }
              })
            }
          }
          return p
        })
      )
      onClose()
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: res.message
      })
    }
  }
  console.log('deleteImageId', deleteImageId)
  return (
    <div className='fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4'>
      <div className='bg-white rounded-2xl max-w-2xl w-full p-6 shadow-2xl overflow-auto h-full'>
        <h2 className='text-2xl font-bold text-gray-800 mb-6'>Edit Product Detail</h2>
        <form onSubmit={handleSubmit(handleUpdatePd)} className='space-y-6'>
          <div className='p-6 bg-gray-50 rounded-xl border border-gray-200'>
            <div className='space-y-5'>
              <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
                <SelectValidate
                  name='colorId'
                  control={control}
                  options={colors}
                  label='Color'
                  isRequired
                  rules={{ required: 'Color is required' }}
                  error={editPdErrors.colorId?.message}
                />
                <SelectValidate
                  name='sizeId'
                  control={control}
                  options={sizes}
                  label='Size'
                  isRequired
                  error={editPdErrors.sizeId?.message}
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
                  error={editPdErrors}
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
                  error={editPdErrors}
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
                error={editPdErrors}
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
                  error={editPdErrors}
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
                        return discountPrice <= originalPrice || 'Discount price cannot be greater than original price'
                      }
                    }
                  }}
                  error={editPdErrors}
                />
              </div>

              <div>
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
                          <button
                            type='button'
                            className='absolute bottom-1 right-1 bg-red-500 opacity-85 text-white p-1 rounded-full hover:bg-red-600 transition-colors'
                            onClick={() => {
                              const newList = [...listImages]
                              const imageId = JSON.parse(JSON.stringify(newList[index].imageId || ''))
                              if (imageId) {
                                setSetDeleteImageId((prev) => [...prev, imageId])
                              }
                              newList[index] = { file: {} as File, url: '', imageId: '' } // hoặc splice nếu muốn xóa hẳn phần tử
                              setListImages(newList)
                            }}
                          >
                            <IoTrashOutline />
                          </button>
                        </div>
                      )
                    })}
                  </div>
                </div>
              </div>
            </div>

            <div className='flex gap-4 mt-6'>
              <button
                onClick={onClose}
                className='px-5 py-2.5 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors text-sm font-medium'
              >
                Cancel
              </button>
              <button
                type='submit'
                className={`px-5 py-2.5 text-white bg-blue-500 rounded-lg transition-colors text-sm font-medium`}
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

export default EditProductDetailModal
