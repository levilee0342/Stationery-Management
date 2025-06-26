import React from 'react'
import { FaEnvelope, FaPhone, FaFacebook, FaTwitter } from 'react-icons/fa'

const ContactInfo: React.FC = () => {
  return (
    <div className='bg-white shadow-lg rounded-lg p-6'>
      <h2 className='text-xl font-semibold text-gray-800 mb-4'>Thông tin liên hệ</h2>
      <p className='text-sm text-gray-600 mb-4'>Liên hệ chúng tôi nếu bạn cần hỗ trợ:</p>
      <div className='space-y-3'>
        <div className='flex items-center gap-2'>
          <FaEnvelope className='text-blue-600' />
          <p className='text-sm text-gray-600'>Email: support@stationeryp.com</p>
        </div>
        <div className='flex items-center gap-2'>
          <FaPhone className='text-blue-600' />
          <p className='text-sm text-gray-600'>Hotline: 0123 456 789</p>
        </div>
        <div className='flex items-center gap-2'>
          <FaFacebook className='text-blue-600' />
          <p className='text-sm text-gray-600'>Facebook: facebook.com/stationeryp</p>
        </div>
        <div className='flex items-center gap-2'>
          <FaTwitter className='text-blue-600' />
          <p className='text-sm text-gray-600'>Twitter: twitter.com/stationeryp</p>
        </div>
      </div>
      {/* Bản đồ */}
      <div className='flex justify-center w-full px-4 mt-4'>
        <iframe
          src='https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3918.520141589769!2d106.78408977485802!3d10.847986989305186!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31752772b245dff1%3A0xb838977f3d419d!2zSOG7jWMgdmnhu4duIEPDtG5nIG5naOG7hyBCxrB1IENow61uaCBWaeG7hW4gVGjDtG5nIGPGoSBz4bufIHThuqFpIFRQLkhDTQ!5e0!3m2!1svi!2s!4v1740816613965!5m2!1svi!2s'
          width='100%'
          height='300'
          style={{ border: 0 }}
          allowFullScreen
          loading='lazy'
          referrerPolicy='no-referrer-when-downgrade'
        />
      </div>
    </div>
  )
}

export default ContactInfo
