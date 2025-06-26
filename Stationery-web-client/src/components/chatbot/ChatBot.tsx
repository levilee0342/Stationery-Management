import React, { useState, useEffect, useRef } from 'react'

interface ChatBotProps {
  isOpen: boolean
  onClose: () => void
}

const ChatBot: React.FC<ChatBotProps> = ({ isOpen, onClose }) => {
  const [messages, setMessages] = useState<{ sender: string; text: string }[]>([])
  const [input, setInput] = useState('')
  const messagesEndRef = useRef<HTMLDivElement>(null)

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const sendMessage = async () => {
    if (!input.trim()) return

    const userMessage = { sender: 'user', text: input }
    setMessages((prev) => [...prev, userMessage])

    try {
      const response = await fetch('http://localhost:5000/handle_message', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: input })
      })

      const data = await response.json()
      const botMessage = { sender: 'bot', text: data.response }
      setMessages((prev) => [...prev, botMessage])
    } catch (error) {
      console.error('Error sending message:', error)
      const errorMessage = { sender: 'bot', text: `Error: ${(error as Error).message}` }
      setMessages((prev) => [...prev, errorMessage])
    }

    setInput('')
  }

  if (!isOpen) return null

  return (
    <div className='fixed bottom-0 right-28 z-50 w-[420px] h-[600px] bg-white rounded-3xl shadow-2xl flex flex-col border border-gray-200 animate-fade-in'>
      <div className='px-5 py-4 bg-gradient-to-r from-blue-700 to-blue-500 text-white text-lg font-semibold rounded-t-3xl flex justify-between items-center'>
        <span>🤖 Trợ lý AI ChatBot</span>
        <button onClick={onClose} className='text-white text-2xl font-bold hover:scale-125 transition-transform'>
          ×
        </button>
      </div>

      <div className='flex-1 overflow-y-auto px-5 py-4 bg-gray-50 space-y-3 scrollbar-thin scrollbar-thumb-blue-300'>
        {messages.map((msg, index) => (
          <div
            key={index}
            className={`max-w-[80%] px-4 py-3 text-base rounded-2xl shadow transition-transform duration-200 ease-out ${
              msg.sender === 'user'
                ? 'ml-auto bg-blue-100 text-gray-900 animate-slide-in-right'
                : 'mr-auto bg-white border border-gray-200 animate-slide-in-left'
            }`}
          >
            <div dangerouslySetInnerHTML={{ __html: msg.text }} />
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className='px-4 py-3 border-t bg-white flex items-center gap-2'>
        <input
          type='text'
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
          className='flex-1 px-4 py-2 text-base border border-gray-300 rounded-full focus:outline-none focus:ring-2 focus:ring-blue-400'
          placeholder='Nhập nội dung chat...'
        />
        <button
          onClick={sendMessage}
          className='bg-blue-500 hover:bg-blue-600 text-white px-5 py-2 rounded-full text-base font-medium transition-all'
        >
          Gửi
        </button>
      </div>
    </div>
  )
}

export default ChatBot
