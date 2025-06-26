import React, { useState } from 'react'

interface CancelOrderModalProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: (reason: string) => void
}

const CancelOrderModal: React.FC<CancelOrderModalProps> = ({ isOpen, onClose, onConfirm }) => {
  const [selectedReason, setSelectedReason] = useState('')
  const [customReason, setCustomReason] = useState('')

  const predefinedReasons = [
    'Thay đổi ý định',
    'Tìm được giá tốt hơn',
    'Thời gian giao hàng quá lâu',
    'Sai địa chỉ/Thông tin giao hàng'
  ]

  const isCustom = selectedReason === 'other'
  const finalReason = isCustom ? customReason : selectedReason

  const handleConfirm = () => {
    if (finalReason.trim() === '') return
    onConfirm(finalReason)
    setSelectedReason('')
    setCustomReason('')
  }

  if (!isOpen) return null

  return (
    <div className='fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-40'>
      <div className='bg-white rounded-xl p-6 w-[90%] max-w-md shadow-lg'>
        <h2 className='text-xl font-semibold mb-4'>Lý do hủy đơn hàng</h2>

        <div className='space-y-2'>
          {predefinedReasons.map((reason, index) => (
            <label key={index} className='flex items-center space-x-2'>
              <input
                type='radio'
                name='cancel-reason'
                value={reason}
                checked={selectedReason === reason}
                onChange={() => setSelectedReason(reason)}
              />
              <span>{reason}</span>
            </label>
          ))}
          <label className='flex items-center space-x-2'>
            <input
              type='radio'
              name='cancel-reason'
              value='other'
              checked={selectedReason === 'other'}
              onChange={() => setSelectedReason('other')}
            />
            <span>Lý do khác</span>
          </label>

          {isCustom && (
            <textarea
              value={customReason}
              onChange={(e) => setCustomReason(e.target.value)}
              className='w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-blue-400'
              rows={3}
              placeholder='Nhập lý do...'
            />
          )}
        </div>

        <div className='mt-4 flex justify-end space-x-3'>
          <button onClick={onClose} className='px-4 py-2 bg-gray-300 rounded hover:bg-gray-400'>
            Hủy
          </button>
          <button onClick={handleConfirm} className='px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600'>
            Xác nhận
          </button>
        </div>
      </div>
    </div>
  )
}

export default CancelOrderModal
