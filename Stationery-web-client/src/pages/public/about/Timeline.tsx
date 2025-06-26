import React from 'react'
import { motion } from 'framer-motion'

interface TimelineItem {
  year: string
  description: string
}

interface TimelineProps {
  items: TimelineItem[]
}

const Timeline: React.FC<TimelineProps> = ({ items }) => {
  return (
    <section className='py-12 container mx-auto px-4'>
      <h2 className='text-3xl text-blue-800 font-semibold text-center mb-8'>Development Journey</h2>
      
      <div className='relative'>
        {/* Dòng timeline */}
        <div className='absolute left-1/2 top-0 h-full w-1 bg-gradient-to-b from-blue-400 to-blue-600 transform -translate-x-1/2'></div>

        {items.map((item, index) => (
          <motion.div
            key={index}
            initial={{ opacity: 0, y: 50 }}
            whileInView={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: index * 0.2 }}
            viewport={{ once: true }}
            className={`relative flex items-center mb-10 ${
              index % 2 === 0 ? 'md:flex-row' : 'md:flex-row-reverse'
            } flex-col`}
          >
            {/* Nội dung Timeline */}
            <div className='md:w-1/2 w-full px-6'>
              <div className='bg-white p-5 rounded-lg shadow-lg border-l-4 border-blue-500'>
                <h3 className='text-xl font-bold text-blue-600'>{item.year}</h3>
                <p className='text-gray-600 mt-2'>{item.description}</p>
              </div>
            </div>

            {/* Điểm mốc Timeline */}
            <div className='absolute left-1/2 transform -translate-x-1/2 w-6 h-6 bg-white border-4 border-blue-500 rounded-full'></div>
          </motion.div>
        ))}
      </div>
    </section>
  )
}

export default Timeline
