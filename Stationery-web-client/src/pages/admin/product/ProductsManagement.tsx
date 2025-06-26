import { useEffect, useState } from 'react'
import Swal from 'sweetalert2'
import ProgressBar from './component/ProgressBar'
import AddProductForm from './addproduct/AddProductForm'
import AddProductDetailsForm from './addproduct/AddProductDetailsForm'
import ConfirmProductForm from './addproduct/ConfirmProductForm'
import ProductList from './component/ProductList'
import { FaPlus } from 'react-icons/fa'
import { ProductDetailForm, ProductForm } from '~/types/product'
import { showToastError } from '~/utils/alert'
import { categoriesToOptions, colorToOptions, sizeToOptions } from '~/utils/optionSelect'
import { apiGetAllSizes } from '~/api/size'
import { AxiosError } from 'axios'
import { apiGetAllColors } from '~/api/color'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { apiCreateProduct } from '~/api/product'
import { modalActions } from '~/store/slices/modal'
import { PacmanLoader } from 'react-spinners'
import { fetchCategories } from '~/store/actions/category'

function ProductsManagement() {
  const dispatch = useAppDispatch()
  const [step, setStep] = useState<0 | 1 | 2 | 3>(0)
  const { accessToken } = useAppSelector((state) => state.user)
  const [newProduct, setNewProduct] = useState<ProductForm>({
    name: '',
    description: '',
    slug: '',
    categoryId: '',
    productDetails: [] as ProductDetailForm[]
  })
  const [colors, setColors] = useState<{ value: string; label: string; color: string }[]>([])
  const [sizes, setSizes] = useState<{ value: string; label: string }[]>([])
  const { categories } = useAppSelector((state) => state.category)
  const [listCategories, setListCategories] = useState<
    {
      label: string
      value: string
    }[]
  >([])

  const resetProcess = () => {
    setStep(0)
    setNewProduct({
      name: '',
      description: '',
      slug: '',
      categoryId: '',
      productDetails: []
    })
  }

  const handleConfirm = async (newProduct: ProductForm) => {
    try {
      if (!accessToken) {
        showToastError('Vui lòng đăng nhập để thực hiện thao tác này.')
        return
      }
      const formData = new FormData()

      newProduct.productDetails.forEach((detail) => {
        const colorId = detail.colorId
        detail.images?.forEach((image) => {
          formData.append(`files_${colorId}`, image.file)
        })
      })
      const productData = {
        ...newProduct,
        productDetails: newProduct.productDetails.map((detail) => {
          // Tạo một object mới với tất cả thuộc tính trừ images
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { images, ...detailWithoutImages } = detail
          return detailWithoutImages
        })
      }
      formData.append('document', JSON.stringify(productData))
      dispatch(modalActions.toggleModal({ childrenModal: <PacmanLoader />, isOpenModal: true }))
      const res = await apiCreateProduct({ data: formData, accessToken })
      dispatch(modalActions.toggleModal({ childrenModal: null, isOpenModal: false }))
      if (res.code === 200) {
        Swal.fire({
          icon: 'success',
          title: 'Success',
          text: 'Add product successfully'
        })
      } else {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: res.message
        })
      }
      resetProcess()
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }

  const getAllColors = async () => {
    try {
      const response = await apiGetAllColors()
      if (response.code == 200) {
        setColors(colorToOptions(response.result))
      } else {
        showToastError(response.message)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }
  const getAllSizes = async () => {
    try {
      const response = await apiGetAllSizes()
      if (response.code == 200) {
        setSizes(sizeToOptions(response.result))
      } else {
        showToastError(response.message)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }
  useEffect(() => {
    getAllColors()
    getAllSizes()
    setListCategories(categoriesToOptions(categories))
    dispatch(fetchCategories())
  }, [])

  return (
    <div className='p-6 w-full mx-auto bg-white shadow-lg rounded-xl'>
      <div className='flex justify-between items-center mb-6'>
        <h1 className='text-3xl font-semibold text-blue-800'>Products Management</h1>
        {step === 0 && (
          <button
            onClick={() => setStep(1)}
            className='bg-blue-600 text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-blue-700 transition-colors'
          >
            <FaPlus size={16} />
            Add New Product
          </button>
        )}
      </div>

      {step > 0 && <ProgressBar step={step} />}

      {step === 1 && (
        <AddProductForm
          initialProduct={newProduct}
          listCategories={listCategories}
          onSubmit={(product) => {
            setNewProduct((prev) => ({ ...prev, ...product }))
            setStep(2)
          }}
          onCancel={resetProcess}
        />
      )}

      {step === 2 && (
        <AddProductDetailsForm
          productDetails={newProduct.productDetails}
          sizes={sizes}
          colors={colors}
          onAddDetail={(detail) => {
            setNewProduct((prev) => {
              // Kiểm tra trùng lặp
              return {
                ...prev,
                productDetails: [...prev.productDetails, detail]
              }
            })
          }}
          onUpdateDetail={(detail) => {
            setNewProduct((prev) => {
              // Kiểm tra trùng lặp
              const newDetails = prev.productDetails.map((d) => {
                if (d.slug === detail.slug) {
                  return { ...detail }
                }
                return d
              })
              return {
                ...prev,
                productDetails: newDetails
              }
            })
          }}
          onDeleteDetail={(slug) => {
            setNewProduct((prev) => {
              const newDetails = prev.productDetails.filter((d) => d.slug !== slug)
              return {
                ...prev,
                productDetails: newDetails
              }
            })
          }}
          onFinish={() => {
            setStep(3)
          }}
          onCancel={resetProcess}
        />
      )}

      {step === 3 && (
        <ConfirmProductForm
          product={newProduct}
          listCategories={listCategories}
          colors={colors}
          sizes={sizes}
          productDetails={newProduct.productDetails}
          onConfirm={() => handleConfirm(newProduct)}
          onBack={() => setStep(2)}
          onCancel={resetProcess}
        />
      )}

      {step === 0 && <ProductList sizes={sizes} colors={colors} />}
    </div>
  )
}

export default ProductsManagement
