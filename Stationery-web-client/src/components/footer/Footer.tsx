import { MapPin, Mail, Phone } from 'lucide-react'
import { FaFacebookF, FaTiktok, FaInstagram } from 'react-icons/fa'
import { Link } from 'react-router-dom'
import { publicPaths } from '~/constance/paths'

const Footer = () => {
  return (
    <footer className='bg-gray-800 text-white py-8'>
      <div className='container mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 px-6 mb-5'>
        {/* Thông tin địa chỉ */}
        <div className='space-y-3'>
          <h3 className='font-bold text-xl mb-3'>Address Information</h3>
          <div className='flex items-start gap-3'>
            <MapPin className='w-6 h-6 mt-1' />
            <span>97 Man Thien Street, Hiep Phu, Thu Duc, Ho Chi Minh City</span>
          </div>
          <div className='flex items-center gap-3'>
            <Mail className='w-6 h-6' />
            <span>info@shopx.com</span>
          </div>
          <div className='flex items-center gap-3'>
            <Phone className='w-6 h-6' />
            <span>0969 895 549</span>
          </div>
        </div>

        {/* Liên kết nhanh */}
        <div>
          <h3 className='font-bold text-lg mb-3'>Quick Links</h3>
          <div className='flex flex-col space-y-2'>
            <Link to={publicPaths.PUBLIC} className='hover:text-blue-400 transition'>
              Home
            </Link>
            <Link to={publicPaths.ABOUT} className='hover:text-blue-400 transition'>
              About
            </Link>
            <Link to={publicPaths.PRODUCT} className='hover:text-blue-400 transition'>
              Product
            </Link>
            <Link to={publicPaths.SERVICE} className='hover:text-blue-400 transition'>
              Service
            </Link>
            <Link to={publicPaths.CONTACT} className='hover:text-blue-400 transition'>
              Contact
            </Link>
          </div>
        </div>

        {/* Hỗ trợ */}
        <div>
          <h3 className='font-bold text-lg mb-3'>Support</h3>
          <ul className='space-y-2'>
            <li>
              <Link to={publicPaths.SUPPORT_PURCHASE_GUIDE} className='hover:text-blue-400 transition'>
                Shopping & Ordering Guide
              </Link>
            </li>
            <li>
              <Link to={publicPaths.SUPPORT_WARRANTY_POLICY} className='hover:text-blue-400 transition'>
                Warranty Policy
              </Link>
            </li>
            <li>
              <Link to={publicPaths.SHIPPING_POLICY} className='hover:text-blue-400 transition'>
                Shipping Policy
              </Link>
            </li>
            <li>
              <Link to={publicPaths.RETURN_EXCHANGE_POLICY} className='hover:text-blue-400 transition'>
                Return & Exchange Policy
              </Link>
            </li>
          </ul>
        </div>

        {/* Đăng ký cập nhật và mạng xã hội */}
        <div>
          <h3 className='font-bold text-lg mb-3'>Subscribe for Updates</h3>
          <div className='flex space-x-4 mb-4'>
            <a
              href='https://www.facebook.com'
              target='_blank'
              rel='noopener noreferrer'
              className='bg-blue-600 p-3 rounded-full hover:bg-blue-700 transition-colors'
            >
              <FaFacebookF className='text-white text-xl' />
            </a>
            <a
              href='https://www.tiktok.com'
              target='_blank'
              rel='noopener noreferrer'
              className='bg-black p-3 rounded-full hover:bg-gray-800 transition-colors'
            >
              <FaTiktok className='text-white text-xl' />
            </a>
            <a
              href='https://www.instagram.com'
              target='_blank'
              rel='noopener noreferrer'
              className='bg-gradient-to-r from-pink-500 via-purple-500 to-indigo-500 p-3 rounded-full hover:opacity-80 transition-opacity'
            >
              <FaInstagram className='text-white text-xl' />
            </a>
          </div>

          {/* Form nhập email */}
          <div className='flex flex-col sm:flex-row space-y-2 sm:space-y-0 sm:space-x-2'>
            <input
              type='email'
              placeholder='Enter your email'
              className='w-full p-2 rounded text-gray-900 focus:ring focus:ring-blue-400'
            />
            <button className='bg-blue-600 px-4 py-2 rounded hover:bg-blue-700 transition'>Subscribe</button>
          </div>
        </div>
      </div>

      {/* Bản đồ */}
      <div className='flex justify-center w-full px-4'>
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

      {/* Bản quyền */}
      <hr className='border-gray-600 my-6' />
      <div className='text-center mt-6 text-sm'>
        <p>&copy; 2025 Stationery's P. All rights reserved.</p>
      </div>
    </footer>
  )
}

export default Footer
