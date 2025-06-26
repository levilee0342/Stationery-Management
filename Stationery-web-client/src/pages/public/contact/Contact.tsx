import React, { useState } from 'react'
import { FaPhone, FaEnvelope, FaFacebook } from 'react-icons/fa'
import { sendSupportEmail } from '~/api/email'
import { showToastError, showToastSuccess } from '~/utils/alert'

const Contact: React.FC = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    subject: '',
    message: ''
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    try {
      await sendSupportEmail({
        fullName: formData.name,
        emailAddress: formData.email,
        subject: formData.subject,
        message: formData.message
      })
      showToastSuccess('Your message has been sent successfully!')
      setFormData({ name: '', email: '', subject: '', message: '' })
    } catch (error) {
      console.error('Error sending message:', error)
      showToastError('Failed to send your message. Please try again later!')
    }
  }

  return (
    <div className='min-h-screen flex items-center justify-center p-8 mt-16'>
      <div className='w-full max-w-5xl bg-white shadow-xl rounded-2xl overflow-hidden'>
        {/* Header */}
        <div className='bg-gradient-to-r from-blue-600 to-blue-800 text-white py-6 px-8 text-center'>
          <h1 className='text-4xl font-bold'>Contact</h1>
          <p className='text-lg opacity-90'>We would love to hear from you!</p>
        </div>

        <div className='grid md:grid-cols-2 gap-8 p-8'>
          {/* Contact Info */}
          <div className='flex flex-col gap-4'>
            <div className='p-6 bg-blue-100 rounded-lg shadow-md'>
              <h2 className='text-xl font-semibold text-blue-700 mb-2'>Contact Info</h2>
              <p className='flex items-center text-gray-700'>
                <FaPhone className='text-blue-600 mr-3' />
                <a href='tel:0969895549' className='text-blue-600 hover:underline'>
                  0969895549
                </a>
              </p>
              <p className='flex items-center text-gray-700'>
                <FaEnvelope className='text-blue-600 mr-3' />
                <a href='mailto:nguyenquockhoa5549@gmail.com' className='text-blue-600 hover:underline'>
                  nguyenquockhoa5549@gmail.com
                </a>
              </p>
              <p className='flex items-center text-gray-700'>
                <FaFacebook className='text-blue-600 mr-3' />
                <a
                  href='https://www.facebook.com/quockhoa.nguyen.374'
                  target='_blank'
                  rel='noopener noreferrer'
                  className='text-blue-600 hover:underline'
                >
                  Facebook Profile
                </a>
              </p>
            </div>

            {/* Hình ảnh bên dưới Contact Info */}
            <div className='mt-4 flex justify-center'>
              <img
                src='https://media.timeout.com/images/100434709/image.jpg'
                alt='Contact'
                className='rounded-lg shadow-md w-full'
              />
            </div>
          </div>

          {/* Contact Form */}
          <div className='p-6 bg-gray-100 rounded-lg shadow-md'>
            <h2 className='text-xl font-semibold text-blue-700 mb-4'>Send a Message</h2>
            <form onSubmit={handleSubmit} className='space-y-4'>
              <input
                type='text'
                name='name'
                value={formData.name}
                onChange={handleChange}
                placeholder='Full Name'
                className='w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500'
                required
              />
              <input
                type='email'
                name='email'
                value={formData.email}
                onChange={handleChange}
                placeholder='Email Address'
                className='w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500'
                required
              />
              <input
                type='text'
                name='subject'
                value={formData.subject}
                onChange={handleChange}
                placeholder='Subject'
                className='w-full p-3 border rounded-lg focus:ring-2 focus:ring-blue-500'
                required
              />
              <textarea
                name='message'
                value={formData.message}
                onChange={handleChange}
                placeholder='Your Message'
                className='w-full p-3 border rounded-lg h-32 focus:ring-2 focus:ring-blue-500'
                required
              ></textarea>
              <button
                type='submit'
                className='w-full py-3 text-lg font-semibold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition'
              >
                Send Message
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Contact
