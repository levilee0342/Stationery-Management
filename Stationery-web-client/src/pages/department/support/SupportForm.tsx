import React, { useState } from 'react'
import { showAlertError, showAlertSucess } from '~/utils/alert'

export interface SupportFormData {
  name: string
  email: string
  message: string
}

const SupportForm: React.FC = () => {
  const [formData, setFormData] = useState<SupportFormData>({
    name: '',
    email: '',
    message: ''
  })
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false)

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsSubmitting(true)
    try {
      // Giả lập gửi email (thay bằng API thực tế)
      await new Promise((resolve) => setTimeout(resolve, 1000))
      showAlertSucess('Thành công!')
      setFormData({ name: '', email: '', message: '' })
    } catch (error) {
      showAlertError('Đã có lỗi xảy ra!')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className='bg-white shadow-lg rounded-lg p-6 w-full'>
      <h2 className='text-xl font-semibold text-gray-800 mb-4'>Gửi yêu cầu hỗ trợ</h2>
      <form onSubmit={handleSubmit} className='space-y-4'>
        <div>
          <label className='block text-sm font-medium text-gray-700'>Họ và tên</label>
          <input
            type='text'
            name='name'
            value={formData.name}
            onChange={handleChange}
            required
            className='mt-1 w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
            placeholder='Nhập họ và tên'
          />
        </div>
        <div>
          <label className='block text-sm font-medium text-gray-700'>Email</label>
          <input
            type='email'
            name='email'
            value={formData.email}
            onChange={handleChange}
            required
            className='mt-1 w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
            placeholder='Nhập email của bạn'
          />
        </div>
        <div>
          <label className='block text-sm font-medium text-gray-700'>Nội dung yêu cầu</label>
          <textarea
            name='message'
            value={formData.message}
            onChange={handleChange}
            required
            className='mt-1 w-full p-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500'
            rows={5}
            placeholder='Mô tả vấn đề bạn cần hỗ trợ'
          />
        </div>
        <button
          type='submit'
          disabled={isSubmitting}
          className={`w-full py-2 px-4 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition ${
            isSubmitting ? 'opacity-50 cursor-not-allowed' : ''
          }`}
        >
          {isSubmitting ? 'Đang gửi...' : 'Gửi yêu cầu'}
        </button>
      </form>
    </div>
  )
}

export default SupportForm
