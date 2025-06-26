import Select from 'react-select'
import { useState, useEffect, Dispatch } from 'react'
import { FiPercent, FiDollarSign, FiCalendar, FiUsers, FiPackage, FiShoppingCart } from 'react-icons/fi'
import { Promotion, ProductDetailSimple, VoucherFormData, VoucherType } from '~/types/promotion'
import { showAlertError, showToastError, showToastSuccess } from '~/utils/alert'
import { apiCreatePromotion, apiUpdatePromotion } from '~/api/promotion'
import { AxiosError } from 'axios'
interface User {
  userId: string
  firstName: string
  lastName: string
}
interface VoucherModalProps {
  isOpen: boolean
  isEdit: boolean
  promotion?: Promotion
  onClose: () => void
  onSubmit: (data: VoucherFormData) => void
  users?: User[]
  products?: ProductDetailSimple[]
  loadingUsers?: boolean
  setPromotion: Dispatch<React.SetStateAction<Promotion[]>>
  loadingProducts?: boolean
}

const VoucherModal = ({
  isOpen,
  isEdit,
  promotion,
  onClose,
  setPromotion,
  users = [],
  products = [],
  loadingUsers = false,
  loadingProducts = false
}: VoucherModalProps) => {
  const [formData, setFormData] = useState<VoucherFormData>({
    promoCode: '',
    voucherType: 'ALL_PRODUCTS',
    discountType: 'PERCENTAGE',
    discountValue: 0,
    maxValue: 0,
    usageLimit: 0,
    minOrderValue: 0,
    startDate: '',
    endDate: '',
    userIds: [],
    productIds: []
  })
  const formatDateForInput = (dateString: string) => {
    if (!dateString) return ''

    // Nếu dateString có format ISO (có T và time), chỉ lấy phần date
    if (dateString.includes('T')) {
      return dateString.split('T')[0]
    }

    // Nếu đã đúng format yyyy-MM-dd thì return luôn
    if (dateString.match(/^\d{4}-\d{2}-\d{2}$/)) {
      return dateString
    }

    // Các trường hợp khác, parse và format lại
    try {
      const date = new Date(dateString)
      return date.toISOString().split('T')[0]
    } catch (error) {
      console.error('Error parsing date:', dateString)
      return ''
    }
  }

  // Cập nhật useEffect để format dates
  useEffect(() => {
    if (promotion && isEdit) {
      // Xác định voucherType dựa trên dữ liệu có sẵn
      let voucherType: VoucherType = 'ALL_PRODUCTS'
      if (promotion.user && promotion.user.length > 0) {
        voucherType = 'USERS'
      } else if (promotion.pd && promotion.pd.length > 0) {
        voucherType = 'PRODUCTS'
      }

      setFormData({
        promoCode: promotion.promoCode,
        voucherType,
        discountType: promotion.discountType,
        discountValue: promotion.discountValue,
        maxValue: promotion.maxValue || 0, // Handle null case
        usageLimit: promotion.usageLimit,
        minOrderValue: promotion.minOrderValue,
        startDate: formatDateForInput(promotion.startDate), // Format date cho input
        endDate: formatDateForInput(promotion.endDate), // Format date cho input
        userIds: promotion.user?.map((u) => u.userId) || [],
        productIds: promotion.pd?.map((p) => p.productDetailId) || []
      })
    } else {
      // Reset form cho add new
      setFormData({
        promoCode: '',
        voucherType: 'ALL_PRODUCTS',
        discountType: 'PERCENTAGE',
        discountValue: 0,
        maxValue: 0,
        usageLimit: 0,
        minOrderValue: 0,
        startDate: '',
        endDate: '',
        userIds: [],
        productIds: []
      })
    }
  }, [promotion, isEdit, isOpen])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: value
    }))
  }

  const handleNumberChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({
      ...prev,
      [name]: Number(value)
    }))
  }

  const handleVoucherTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const voucherType = e.target.value as VoucherType
    setFormData((prev) => ({
      ...prev,
      voucherType,
      userIds: [],
      productIds: []
    }))
  }

  const handleUserChange = (selectedOptions: any) => {
    setFormData((prev) => ({
      ...prev,
      userIds: selectedOptions ? selectedOptions.map((option: any) => option.value) : []
    }))
  }

  const handleProductChange = (selectedOptions: any) => {
    setFormData((prev) => ({
      ...prev,
      productIds: selectedOptions ? selectedOptions.map((option: any) => option.value) : []
    }))
  }

  const handleSubmit = async () => {
    // Tạo payload theo yêu cầu API
    const payload: VoucherFormData = {
      promoCode: formData.promoCode,
      voucherType: formData.voucherType,
      discountType: formData.discountType,
      discountValue: formData.discountValue,
      maxValue: formData.maxValue,
      usageLimit: formData.usageLimit,
      minOrderValue: formData.minOrderValue,
      startDate: formData.startDate + 'T00:00:00',
      endDate: formData.endDate + 'T23:59:59'
    }

    // Chỉ thêm userIds hoặc productIds dựa trên voucherType
    if (formData.voucherType === 'USERS' && formData.userIds && formData.userIds.length > 0) {
      payload.userIds = formData.userIds
    } else if (formData.voucherType === 'PRODUCTS' && formData.productIds && formData.productIds.length > 0) {
      payload.productIds = formData.productIds
    }

    if (isEdit && formData.promoCode) {
      try {
        payload.promotionId = promotion?.promotionId || ''
        const res = await apiUpdatePromotion(payload)
        if (res.code !== 200) {
          showAlertError('Failed to update voucher: ' + res.data.message)
          return
        }

        // Cập nhật promotion trong state
        const updatedPromotion: Promotion = {
          ...promotion,
          promoCode: formData.promoCode,
          discountType: formData.discountType,
          discountValue: formData.discountValue,
          maxValue: formData.maxValue,
          usageLimit: formData.usageLimit,
          minOrderValue: formData.minOrderValue,
          startDate: formData.startDate,
          endDate: formData.endDate
          // Không cập nhật user và pd khi edit
        }

        setPromotion((prev) => prev.map((p) => (p.promotionId === promotion?.promotionId ? updatedPromotion : p)))

        showToastSuccess('Voucher updated successfully!')
        onClose()
      } catch (error) {
        if (error instanceof Error || error instanceof AxiosError) {
          showToastError(error.message)
        }
      }
    } else {
      // Thêm vào đầu array
      // CREATE - Thêm voucherType và có thể thêm userIds/productIds tùy theo loại
      payload.voucherType = formData.voucherType

      // Chỉ thêm userIds khi voucherType === 'USERS'
      if (formData.voucherType === 'USERS' && formData.userIds && formData.userIds.length > 0) {
        payload.userIds = formData.userIds
      }

      // Chỉ thêm productIds khi voucherType === 'PRODUCTS'
      if (formData.voucherType === 'PRODUCTS' && formData.productIds && formData.productIds.length > 0) {
        payload.productIds = formData.productIds
      }

      try {
        const res = await apiCreatePromotion(payload)
        if (res.code !== 200) {
          showAlertError('Failed to create voucher: ' + res.data.message)
          return
        }

        // Tạo promotion mới từ response hoặc từ form data
        const newPromotion: Promotion = {
          promotionId: formData.promoCode, // Lấy từ API response
          promoCode: formData.promoCode,
          discountType: formData.discountType,
          discountValue: formData.discountValue,
          maxValue: formData.maxValue == 0 ? null : formData.maxValue,
          usageLimit: formData.usageLimit,
          minOrderValue: formData.minOrderValue,
          startDate: formData.startDate,
          endDate: formData.endDate,
          createdAt: new Date().toISOString(),
          // Thêm users nếu voucherType là USERS
          user:
            formData.voucherType === 'USERS' && formData.userIds && formData.userIds.length > 0
              ? users
                  .filter((u) => formData.userIds!.includes(u.userId))
                  .map((u) => ({
                    userId: u.userId,
                    firstName: u.firstName,
                    lastName: u.lastName
                  }))
              : undefined,
          // Thêm products nếu voucherType là PRODUCTS
          pd:
            formData.voucherType === 'PRODUCTS' && formData.productIds && formData.productIds.length > 0
              ? products
                  .filter((p) => formData.productIds!.includes(p.productDetailId))
                  .map((p) => ({
                    productDetailId: p.productDetailId,
                    name: p.name
                  }))
              : undefined
        }

        // Thêm vào đầu array
        setPromotion((prev) => [newPromotion, ...prev])

        showToastSuccess('Voucher created successfully!')
        onClose()
      } catch (error) {
        if (error instanceof Error || error instanceof AxiosError) {
          showToastError(error)
        }
      }
    }
    console.log(payload)
  }

  const isFormValid = () => {
    if (!formData.promoCode || !formData.startDate || !formData.endDate) return false
    if (formData.voucherType === 'USERS' && (!formData.userIds || formData.userIds.length === 0)) return false
    if (formData.voucherType === 'PRODUCTS' && (!formData.productIds || formData.productIds.length === 0)) return false
    return true
  }

  if (!isOpen) return null

  const userOptions = users.map((user) => ({
    value: user.userId,
    label: `${user.firstName} ${user.lastName}`
  }))

  const productOptions = products.map((product) => ({
    value: product.productDetailId,
    label: product.name
  }))

  const selectedUsers = userOptions.filter((option) => formData.userIds?.includes(option.value))
  const selectedProducts = productOptions.filter((option) => formData.productIds?.includes(option.value))

  return (
    <div className='fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4 backdrop-blur-sm'>
      <div className='bg-white rounded-xl p-8 w-full max-w-4xl shadow-2xl border border-gray-100 max-h-[90vh] overflow-y-auto'>
        {/* Header */}
        <div className='flex justify-between items-center mb-6'>
          <h2 className='text-3xl font-bold text-gradient bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent'>
            {isEdit ? 'Edit Voucher' : 'Create New Voucher'}
          </h2>
          <button onClick={onClose} className='text-gray-400 hover:text-gray-600 text-2xl' aria-label='Close modal'>
            &times;
          </button>
        </div>

        {/* User Selection - Di chuyển lên đầu */}
        {formData.voucherType === 'USERS' && (
          <div className='bg-emerald-50 p-5 rounded-xl border border-emerald-100 mb-6'>
            <h3 className='text-lg font-semibold text-emerald-800 mb-4 flex items-center'>
              <FiUsers className='mr-3 text-xl' /> Select Users *
              {isEdit && <span className='ml-2 text-sm text-gray-500 font-normal'>(Cannot be changed)</span>}
              {loadingUsers && (
                <div className='ml-2 animate-spin rounded-full h-4 w-4 border-b-2 border-emerald-600'></div>
              )}
            </h3>

            <div>
              <label className='block text-sm font-medium text-emerald-600 mb-2'>
                Choose specific users ({users.length} available)
              </label>

              {loadingUsers ? (
                <div className='flex items-center justify-center py-8 text-emerald-600'>
                  <div className='animate-spin rounded-full h-8 w-8 border-b-2 border-emerald-600 mr-3'></div>
                  Loading users...
                </div>
              ) : (
                <Select
                  isMulti
                  options={userOptions}
                  value={selectedUsers}
                  onChange={handleUserChange}
                  className='text-base'
                  classNamePrefix='select'
                  placeholder={users.length > 0 ? 'Search and select users...' : 'No users available'}
                  isSearchable
                  isDisabled={isEdit || users.length === 0} //
                  noOptionsMessage={() => 'No users found'}
                  formatOptionLabel={(option) => (
                    <div className='flex items-center py-1'>
                      <div className='w-8 h-8 bg-emerald-100 rounded-full flex items-center justify-center mr-3'>
                        <FiUsers className='text-emerald-600' size={14} />
                      </div>
                      <div>
                        <div className='font-medium'>{option.label}</div>
                        <div className='text-xs text-gray-500'>ID: {option.value}</div>
                      </div>
                    </div>
                  )}
                  styles={{
                    control: (base) => ({
                      ...base,
                      borderColor: '#a7f3d0',
                      minHeight: '48px',
                      boxShadow: 'none',
                      '&:hover': {
                        borderColor: '#6ee7b7'
                      }
                    }),
                    menu: (base) => ({
                      ...base,
                      fontSize: '1rem',
                      zIndex: 9999
                    }),
                    option: (base, state) => ({
                      ...base,
                      backgroundColor: state.isSelected ? '#10b981' : state.isFocused ? '#d1fae5' : 'white',
                      color: state.isSelected ? 'white' : '#374151'
                    }),
                    multiValue: (base) => ({
                      ...base,
                      backgroundColor: '#dcfce7'
                    }),
                    multiValueLabel: (base) => ({
                      ...base,
                      color: '#16a34a',
                      fontWeight: '500'
                    }),
                    multiValueRemove: (base) => ({
                      ...base,
                      color: '#16a34a',
                      ':hover': {
                        backgroundColor: '#16a34a',
                        color: 'white'
                      }
                    })
                  }}
                />
              )}

              {selectedUsers.length > 0 && (
                <div className='mt-2 text-sm text-emerald-600'>Selected {selectedUsers.length} user(s)</div>
              )}
            </div>
          </div>
        )}

        {/* Product Selection - Di chuyển lên đầu */}
        {formData.voucherType === 'PRODUCTS' && (
          <div className='bg-rose-50 p-5 rounded-xl border border-rose-100 mb-6'>
            <h3 className='text-lg font-semibold text-rose-800 mb-4 flex items-center'>
              <FiPackage className='mr-3 text-xl' /> Select Products *
              {isEdit && <span className='ml-2 text-sm text-gray-500 font-normal'>(Cannot be changed)</span>}
              {loadingProducts && (
                <div className='ml-2 animate-spin rounded-full h-4 w-4 border-b-2 border-rose-600'></div>
              )}
            </h3>
            <div>
              <label className='block text-sm font-medium text-rose-600 mb-2'>
                Choose specific products ({products.length} available)
              </label>
              {loadingProducts ? (
                <div className='flex items-center justify-center py-8 text-rose-600'>
                  <div className='animate-spin rounded-full h-8 w-8 border-b-2 border-rose-600 mr-3'></div>
                  Loading products...
                </div>
              ) : (
                <Select
                  isMulti
                  options={productOptions}
                  value={selectedProducts}
                  onChange={handleProductChange}
                  isDisabled={isEdit || products.length === 0}
                  className='text-base'
                  classNamePrefix='select'
                  isSearchable
                  placeholder={
                    isEdit
                      ? 'Product selection cannot be changed'
                      : products.length > 0
                        ? 'Search and select products...'
                        : 'No products available'
                  }
                  noOptionsMessage={() => 'No products found'}
                  formatOptionLabel={(option) => (
                    <div className='flex items-center py-1'>
                      <div className='w-8 h-8 bg-rose-100 rounded-full flex items-center justify-center mr-3'>
                        <FiPackage className='text-rose-600' size={14} />
                      </div>
                      <div>
                        <div className='font-medium'>{option.label}</div>
                        <div className='text-xs text-gray-500'>ID: {option.value}</div>
                      </div>
                    </div>
                  )}
                  styles={{
                    control: (base) => ({
                      ...base,
                      borderColor: '#fda4af',
                      minHeight: '48px',
                      boxShadow: 'none',
                      '&:hover': {
                        borderColor: '#fb7185'
                      }
                    }),
                    menu: (base) => ({
                      ...base,
                      fontSize: '1rem',
                      zIndex: 9999
                    }),
                    option: (base, state) => ({
                      ...base,
                      backgroundColor: state.isSelected ? '#e11d48' : state.isFocused ? '#ffe4e6' : 'white',
                      color: state.isSelected ? 'white' : '#374151'
                    }),
                    multiValue: (base) => ({
                      ...base,
                      backgroundColor: '#fce7f3'
                    }),
                    multiValueLabel: (base) => ({
                      ...base,
                      color: '#be185d',
                      fontWeight: '500'
                    }),
                    multiValueRemove: (base) => ({
                      ...base,
                      color: '#be185d',
                      ':hover': {
                        backgroundColor: '#be185d',
                        color: 'white'
                      }
                    })
                  }}
                />
              )}
              {selectedProducts.length > 0 && (
                <div className='mt-2 text-sm text-rose-600'>Selected {selectedProducts.length} product(s)</div>
              )}
            </div>
          </div>
        )}

        <div className='grid grid-cols-1 lg:grid-cols-2 gap-8'>
          {/* Left Column */}
          <div className='space-y-6'>
            {/* Voucher Code */}
            <div className='bg-blue-50 p-5 rounded-xl border border-blue-100'>
              <label className='block text-lg font-semibold text-blue-800 mb-3 flex items-center'>
                <FiPackage className='mr-3 text-xl' /> Voucher Code *
              </label>
              <input
                type='text'
                name='promoCode'
                value={formData.promoCode}
                onChange={handleChange}
                className='w-full border border-blue-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-blue-300 focus:border-transparent bg-white'
                placeholder='SUMMER2023'
                required
              />
            </div>

            {/* Voucher Type */}
            <div className='bg-indigo-50 p-5 rounded-xl border border-indigo-100'>
              <label className='block text-lg font-semibold text-indigo-800 mb-3 flex items-center'>
                <FiUsers className='mr-3 text-xl' /> Voucher Type *
              </label>
              <select
                name='voucherType'
                value={formData.voucherType}
                onChange={handleVoucherTypeChange}
                disabled={isEdit} // Disable khi edit
                className='w-full border border-indigo-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-indigo-300 focus:border-transparent bg-white'
                required
              >
                <option value='ALL_PRODUCTS'>All Products</option>
                <option value='ALL_USERS'>All Users</option>
                <option value='PRODUCTS'>Specific Products</option>
                <option value='USERS'>Specific Users</option>
              </select>
            </div>

            {/* Discount Settings */}
            <div className='bg-purple-50 p-5 rounded-xl border border-purple-100'>
              <h3 className='text-lg font-semibold text-purple-800 mb-4 flex items-center'>
                {formData.discountType === 'PERCENTAGE' ? (
                  <FiPercent className='mr-3 text-xl' />
                ) : (
                  <FiDollarSign className='mr-3 text-xl' />
                )}
                Discount Settings
              </h3>

              <div className='mb-4'>
                <label className='block text-sm font-medium text-purple-600 mb-2'>Discount Type *</label>
                <select
                  name='discountType'
                  value={formData.discountType}
                  onChange={handleChange}
                  className='w-full border border-purple-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-purple-300 focus:border-transparent bg-white'
                  required
                >
                  <option value='PERCENTAGE'>Percentage</option>
                  <option value='VALUE'>Fixed Amount</option>
                </select>
              </div>

              <div className='grid grid-cols-2 gap-4'>
                <div>
                  <label className='block text-sm font-medium text-purple-600 mb-2'>Discount Value *</label>
                  <input
                    type='number'
                    name='discountValue'
                    value={formData.discountValue}
                    onChange={handleNumberChange}
                    min='0'
                    max={formData.discountType === 'PERCENTAGE' ? '100' : undefined}
                    className='w-full border border-purple-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-purple-300 focus:border-transparent bg-white'
                    placeholder={formData.discountType === 'PERCENTAGE' ? '10' : '20000'}
                    required
                  />
                </div>

                <div>
                  <label className='block text-sm font-medium text-purple-600 mb-2'>Max Value</label>
                  <input
                    type='number'
                    name='maxValue'
                    value={formData.maxValue}
                    onChange={handleNumberChange}
                    min='0'
                    className='w-full border border-purple-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-purple-300 focus:border-transparent bg-white'
                    placeholder='50000'
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Right Column */}
          <div className='space-y-6'>
            {/* Usage Limits */}
            <div className='bg-teal-50 p-5 rounded-xl border border-teal-100'>
              <h3 className='text-lg font-semibold text-teal-800 mb-4 flex items-center'>
                <FiShoppingCart className='mr-3 text-xl' /> Usage Limits
              </h3>

              <div className='mb-4'>
                <label className='block text-sm font-medium text-teal-600 mb-2'>Usage Limit</label>
                <input
                  type='number'
                  name='usageLimit'
                  value={formData.usageLimit}
                  onChange={handleNumberChange}
                  min='0'
                  className='w-full border border-teal-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-teal-300 focus:border-transparent bg-white'
                  placeholder='100'
                />
              </div>

              <div>
                <label className='block text-sm font-medium text-teal-600 mb-2'>Min Order Value</label>
                <input
                  type='number'
                  name='minOrderValue'
                  value={formData.minOrderValue}
                  onChange={handleNumberChange}
                  min='0'
                  className='w-full border border-teal-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-teal-300 focus:border-transparent bg-white'
                  placeholder='20000'
                />
              </div>
            </div>

            {/* Date Range */}
            <div className='bg-amber-50 p-5 rounded-xl border border-amber-100'>
              <h3 className='text-lg font-semibold text-amber-800 mb-4 flex items-center'>
                <FiCalendar className='mr-3 text-xl' /> Active Period *
              </h3>

              <div className='grid grid-cols-2 gap-4'>
                <div>
                  <label className='block text-sm font-medium text-amber-600 mb-2'>Start Date *</label>
                  <input
                    type='date'
                    name='startDate'
                    value={formData.startDate}
                    onChange={handleChange}
                    className='w-full border border-amber-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-amber-300 focus:border-transparent bg-white'
                    required
                  />
                </div>

                <div>
                  <label className='block text-sm font-medium text-amber-600 mb-2'>End Date *</label>
                  <input
                    type='date'
                    name='endDate'
                    value={formData.endDate}
                    onChange={handleChange}
                    min={formData.startDate}
                    className='w-full border border-amber-200 rounded-lg p-3 text-base focus:outline-none focus:ring-2 focus:ring-amber-300 focus:border-transparent bg-white'
                    required
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Footer Buttons */}
        <div className='flex justify-end gap-4 mt-8 pt-6 border-t border-gray-200'>
          <button
            onClick={onClose}
            className='px-6 py-3 rounded-lg text-base font-medium text-gray-700 hover:bg-gray-100 transition-colors border border-gray-300'
          >
            Cancel
          </button>
          <button
            onClick={handleSubmit}
            disabled={!isFormValid()}
            className={`px-6 py-3 rounded-lg text-base font-medium text-white transition-all shadow-md hover:shadow-lg ${
              isFormValid()
                ? 'bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700'
                : 'bg-gray-400 cursor-not-allowed'
            }`}
          >
            {isEdit ? 'Update Voucher' : 'Create Voucher'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default VoucherModal
