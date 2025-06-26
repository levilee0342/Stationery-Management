import React, { useState } from 'react'
import ChatBot from '../chatbot/ChatBot'
import { useAppSelector } from '~/hooks/redux'

const FloatingButtons: React.FC = () => {
  const zaloLink = 'https://zalo.me/0969895549'
  const phoneNumber = 'tel:0969895549'
  const [isChatOpen, setIsChatOpen] = useState(false)
  const { isCartOpen } = useAppSelector((state) => state.cart) // Lấy trạng thái giỏ hàng

  // Ẩn FloatingButtons khi giỏ hàng mở
  if (isCartOpen) {
    return null
  }

  return (
    <>
      <ChatBot isOpen={isChatOpen} onClose={() => setIsChatOpen(false)} />
      <div className='fixed bottom-5 right-5 z-50 flex flex-col-reverse items-end gap-3'>
        <div className='flex flex-col items-end gap-3'>
          <a
            href={phoneNumber}
            className='flex items-center justify-center w-16 h-16 bg-[#00A2FF] rounded-full shadow-xl transition-all hover:scale-110 animate-pulse'
          >
            <img src='https://cdn-icons-png.flaticon.com/512/724/724664.png' alt='Call' className='w-10 h-10' />
          </a>
          <a
            href={zaloLink}
            target='_blank'
            rel='noopener noreferrer'
            className='flex items-center justify-center w-16 h-16 bg-[#00A2FF] rounded-full shadow-xl transition-all hover:scale-110 animate-pulse'
          >
            <img src='https://deliverbit.com/images/7982070.png' alt='Zalo' className='w-10 h-10' />
          </a>
        </div>
        <button
          onClick={() => setIsChatOpen((prev) => !prev)}
          className='w-16 h-16 rounded-full bg-blue-600 flex items-center justify-center shadow-lg hover:scale-110 transition'
        >
          <img src='/images/logo-chatbot.png' alt='Chatbot' className='w-10 h-10' />
        </button>
      </div>
    </>
  )
}

export default FloatingButtons
