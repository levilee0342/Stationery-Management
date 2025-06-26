import { useState } from 'react'
import { FaTimes } from 'react-icons/fa'

interface InvoiceViewerProps {
  pdfUrl: string | null
  onClear: () => void
}

const InvoiceViewer: React.FC<InvoiceViewerProps> = ({ pdfUrl, onClear }) => {
  const [error, setError] = useState(false)

  const handleError = () => {
    setError(true)
  }

  if (!pdfUrl) return null

  return (
    <div className='fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50'>
      <div className='bg-white rounded-xl shadow-lg p-8 w-full max-w-4xl h-[80vh] relative'>
        <div className='flex justify-between items-center mb-6'>
          <h2 className='text-2xl font-semibold text-gray-800'>Xem Hóa Đơn</h2>
          <button
            onClick={onClear}
            className='flex items-center gap-2 px-4 py-2 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 transition'
          >
            <FaTimes size={16} />
            Đóng
          </button>
        </div>
        {error ? (
          <p className='text-red-500 text-sm'>Không thể tải PDF. Vui lòng thử hóa đơn khác.</p>
        ) : (
          <iframe
            src={pdfUrl}
            className='w-full h-[calc(80vh-100px)] border rounded-lg'
            title='Invoice PDF'
            onError={handleError}
          ></iframe>
        )}
      </div>
    </div>
  )
}

export default InvoiceViewer
