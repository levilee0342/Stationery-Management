import React from 'react'
import { motion } from 'framer-motion'

interface Certificate {
  id: number
  imageUrl: string
  title: string
}

interface CertificatesProps {
  certificates: Certificate[]
}

const Certificates: React.FC<CertificatesProps> = ({ certificates }) => {
  return (
    <section className='py-12 bg-gray-50'>
      <div className='container mx-auto px-4'>
        <h2 className='text-3xl text-blue-800 font-semibold text-center mb-8'>Certificates & Awards</h2>

        <div className='grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6'>
          {certificates.map((cert) => (
            <motion.div
              key={cert.id}
              whileHover={{ scale: 1.05 }}
              className='bg-white p-4 rounded-xl shadow-lg border border-gray-200 transition-transform duration-300'
            >
              <img
                src={cert.imageUrl}
                alt={cert.title}
                className='w-full h-48 object-cover rounded-md mb-4 border border-gray-300'
              />
              <h3 className='text-lg font-semibold text-center text-gray-700'>{cert.title}</h3>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  )
}

export default Certificates
