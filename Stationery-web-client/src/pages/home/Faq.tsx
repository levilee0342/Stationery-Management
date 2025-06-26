import { useState } from 'react'
import { motion } from 'framer-motion'

interface FaqItem {
  question: string
  answer: string
}

const faqData: FaqItem[] = [
  { question: 'What is your return policy?', answer: 'You can return any item within 30 days of purchase.' },
  { question: 'Do you offer international shipping?', answer: 'Yes, we ship to most countries worldwide.' },
  {
    question: 'How can I track my order?',
    answer: 'Once your order is shipped, we will provide you with a tracking number via email.'
  }
]

const Faq: React.FC = () => {
  const [openIndex, setOpenIndex] = useState<number | null>(null)

  const toggleFAQ = (index: number) => {
    setOpenIndex(openIndex === index ? null : index)
  }

  return (
    <section id='faq' className='container mx-auto bg-white p-8 rounded-lg shadow-lg transition-all mb-4'>
      <h2 className='text-2xl font-bold text-center mb-8 text-blue-800'>Frequently Asked Questions</h2>
      <div className='space-y-4'>
        {faqData.map((faq, index) => (
          <div key={index} className='border-b border-gray-300 pb-4'>
            <button
              className='w-full text-left text-lg font-semibold text-blue-700 flex justify-between items-center py-2 hover:text-blue-900 transition'
              onClick={() => toggleFAQ(index)}
              aria-expanded={openIndex === index}
            >
              {faq.question}
              <span className='text-xl'>{openIndex === index ? '−' : '+'}</span>
            </button>
            <motion.div
              initial={{ opacity: 0, height: 0 }}
              animate={openIndex === index ? { opacity: 1, height: 'auto' } : { opacity: 0, height: 0 }}
              className='overflow-hidden'
            >
              <p className='text-gray-700 pl-4 mt-2'>{faq.answer}</p>
            </motion.div>
          </div>
        ))}
      </div>
    </section>
  )
}

export default Faq
