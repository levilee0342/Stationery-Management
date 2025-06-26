import React, { Dispatch, SetStateAction } from 'react'
import { FaEdit } from 'react-icons/fa'
import { FaLockOpen } from 'react-icons/fa'
import { FaLock } from 'react-icons/fa'
import Swal from 'sweetalert2'
import { apiUpdateHiddenPD } from '~/api/product'
import { ListProductDetail, ProductDetailForm } from '~/types/product'
import { showToastError, showToastSuccess } from '~/utils/alert'
interface ProductDetailProps {
  p: ListProductDetail
  accessToken: string
  handleEditProductDetail: (productId: string, pdId: string, detail: ProductDetailForm) => void
  setProducts: Dispatch<SetStateAction<ListProductDetail[]>>
}

const ProductDetailTable: React.FC<ProductDetailProps> = ({ p, accessToken, setProducts, handleEditProductDetail }) => {
  console.log('p', p)
  const handleUpdateHidden = async (productDetailId: string, hidden: boolean) => {
    try {
      const result = await Swal.fire({
        title: 'Do you want to change the hidden status?',
        icon: 'info',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        cancelButtonText: 'Hủy',
        confirmButtonText: 'Đồng ý'
      })
      if (result.isDismissed) return
      const res = await apiUpdateHiddenPD({ productDetailId, hidden, accessToken })
      if (res.code === 200) {
        console.log('zp')
        const updatePD = p.productDetails.map((pd) => (pd.productDetailId === productDetailId ? { ...pd, hidden } : pd))
        setProducts((prev) => prev.map((pr) => (pr.productId == p.productId ? { ...p, productDetails: updatePD } : p)))
        showToastSuccess('Update hidden status successfully')
      } else {
        showToastError('Failed to update hidden status')
      }
    } catch (error) {
      console.error('Error updating hidden status:', error)
    }
  }
  return (
    <tr className='bg-blue-50'>
      <td colSpan={11} className='p-0'>
        <div className='overflow-x-auto'>
          <table className='w-full'>
            <thead className='bg-gray-500 text-white text-left'>
              <tr>
                <th className='px-4 py-2 text-sm uppercase tracking-wider'>#</th>
                <th className='px-4 py-2 text-sm uppercase tracking-wider'>Color</th>
                <th className='px-4 py-2 text-sm uppercase tracking-wider'>Size</th>
                <th className='px-4 py-2 text-sm uppercase tracking-wider'>Images</th>
                <th className='px-4 py-2 text-sm uppercase tracking-wider'>Quantity</th>
                <th className='px-4 py-2 text-sm uppercase tracking-wider'>Sold</th>
                <th className='px-4 py-2 text-sm uppercase tracking-wider'>Actions</th>
              </tr>
            </thead>
            <tbody className='divide-y divide-gray-200'>
              {p.productDetails.map((detail, detailIndex) => (
                <tr
                  key={detail.productDetailId}
                  className={`hover:bg-gray-100 ${detail.hidden ? 'opacity-50 bg-gray-100' : ''}`}
                >
                  <td className='px-4 py-2 text-sm'>{detailIndex + 1}</td>
                  <td className='px-4 py-2 text-sm'>{detail.color?.name || 'No color'}</td>
                  <td className='px-4 py-2 text-sm'>{detail.size?.name || 'No size'}</td>
                  <td className='px-4 py-2'>
                    <div className='flex items-center gap-2'>
                      {(detail.images || []).slice(0, 3).map((img, imgIdx) => (
                        <div key={img.imageId} className='relative w-10 h-10'>
                          <img
                            className='w-10 h-10 rounded object-cover'
                            src={img.url}
                            alt={`${detail.color?.name || 'No color'}-${imgIdx}`}
                          />
                          {imgIdx === 2 && (detail.images || []).length > 3 && (
                            <div className='absolute inset-0 bg-black bg-opacity-50 rounded flex items-center justify-center'>
                              <span className='text-sm font-medium text-white'>
                                +{(detail.images || []).length - 3}
                              </span>
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  </td>
                  <td className='px-4 py-2 text-sm'>{detail.stockQuantity.toLocaleString('vi-VN')}</td>
                  <td className='px-4 py-2 text-sm'>{detail.soldQuantity.toLocaleString('vi-VN')}</td>
                  <td className='px-4 py-2'>
                    <div className='flex gap-3'>
                      <button
                        onClick={() => {
                          const mappedImages =
                            detail.images?.map((img) => ({
                              file: undefined as unknown as File, // hoặc null nếu muốn, vì không có file gốc
                              url: img.url,
                              imageId: img.imageId
                            })) || []
                          while (mappedImages.length < 5) {
                            mappedImages.push({
                              file: undefined as unknown as File,
                              url: '',
                              imageId: ''
                            })
                          }
                          const pd: ProductDetailForm = {
                            colorId: detail.color.colorId,
                            name: detail.name,
                            sizeId: detail?.size?.sizeId || '',
                            images: mappedImages,
                            stockQuantity: detail.stockQuantity,
                            originalPrice: detail.originalPrice,
                            discountPrice: detail.discountPrice,
                            slug: detail.slug
                          }
                          handleEditProductDetail(p.productId, detail.productDetailId, pd)
                        }}
                        className='bg-yellow-400 text-white p-2 rounded-lg hover:bg-yellow-500 transition-colors'
                      >
                        <FaEdit size={16} />
                      </button>
                      <button
                        onClick={() => handleUpdateHidden(detail.productDetailId, !detail.hidden)}
                        className={`p-2.5 rounded-md transition-all shadow-sm hover:shadow flex items-center justify-center group relative ${
                          detail.hidden
                            ? 'bg-red-100 text-red-600 hover:bg-red-200'
                            : 'bg-green-100 text-green-600 hover:bg-green-200'
                        }`}
                        title={detail.hidden ? 'Unhide product' : 'Hide product'}
                      >
                        {detail.hidden ? (
                          <FaLock size={16} className='group-hover:scale-110 transition-transform' />
                        ) : (
                          <FaLockOpen size={16} className='group-hover:scale-110 transition-transform' />
                        )}
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </td>
    </tr>
  )
}

export default ProductDetailTable
