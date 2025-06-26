import { useState, useEffect } from 'react'
import { FaSearch } from 'react-icons/fa'
import { useAppSelector } from '~/hooks/redux'
import {
  getAllInvoices,
  getCurrentMonthInvoiceSummary,
  payCurrentInvoice,
  generateCurrentInvoice,
  checkOverdueInvoices
} from '~/api/invoice'

export type Invoice = {
  id: string
  department: string
  amount: number
  date: string
  pdfUrl: string
}

interface InvoiceListProps {
  selectedYear: string
  selectedMonth: string
  onSelectInvoice: (pdfUrl: string) => void
}

const ITEMS_PER_PAGE = 5

interface ModalProps {
  isOpen: boolean
  title: string
  message: string
  onClose: () => void
  onConfirm?: () => void
  showConfirmButton?: boolean
}

const Modal: React.FC<ModalProps> = ({ isOpen, title, message, onClose, onConfirm, showConfirmButton = false }) => {
  if (!isOpen) return null
  return (
    <div className='fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50'>
      <div className='bg-white rounded-lg p-6 max-w-md w-full'>
        <h3 className='text-lg font-semibold text-gray-800 mb-4'>{title}</h3>
        <p className='text-gray-600 mb-6'>{message}</p>
        <div className='flex justify-end gap-4'>
          <button
            onClick={onClose}
            className='px-4 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition'
          >
            Close
          </button>
          {showConfirmButton && onConfirm && (
            <button
              onClick={onConfirm}
              className='px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition'
            >
              Confirm
            </button>
          )}
        </div>
      </div>
    </div>
  )
}

const InvoiceList: React.FC<InvoiceListProps> = ({ selectedYear, selectedMonth, onSelectInvoice }) => {
  const [invoices, setInvoices] = useState<Invoice[]>([])
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(1)
  const [loading, setLoading] = useState(false)
  const [modal, setModal] = useState<{
    isOpen: boolean
    title: string
    message: string
    showConfirmButton?: boolean
    onConfirm?: () => void
  }>({ isOpen: false, title: '', message: '' })
  const [paymentSummary, setPaymentSummary] = useState<{ totalAmount: number; orderCount: number } | null>(null)
  const { accessToken, userData } = useAppSelector((state) => state.user)

  // Fetch invoices and check overdue invoices on component mount
  useEffect(() => {
    const fetchData = async () => {
      if (!accessToken) {
        setModal({
          isOpen: true,
          title: 'Authentication Required',
          message: 'Please log in to view invoices.'
        })
        return
      }
      setLoading(true)
      try {
        // Fetch invoices
        const invoiceResponses = await getAllInvoices(accessToken)
        const mappedInvoices: Invoice[] = invoiceResponses.map((inv) => ({
          id: inv.purchaseOrderId,
          department: userData?.userId || 'Department',
          amount: inv.amount,
          date: inv.createdAt.split('T')[0],
          pdfUrl: inv.pdfUrl
        }))
        setInvoices(mappedInvoices)

        // Check overdue invoices
        const overdueNotifications = await checkOverdueInvoices(accessToken)
        if (overdueNotifications.length > 0) {
          setModal({
            isOpen: true,
            title: 'Overdue Invoices',
            message: overdueNotifications.join('\n')
          })
        }
      } catch (err: any) {
        setModal({
          isOpen: true,
          title: 'Error',
          message: err.message || 'Failed to fetch invoices or check overdue status.'
        })
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [accessToken, userData])

  // Filter invoices based on selected year, month, and search term
  const filteredInvoices = invoices.filter((invoice) => {
    const year = invoice.date.slice(0, 4)
    const month = invoice.date.slice(5, 7)
    return (
      selectedYear === year &&
      (selectedMonth === 'All' || month === selectedMonth) &&
      invoice.id.toLowerCase().includes(search.toLowerCase())
    )
  })

  const totalPages = Math.ceil(filteredInvoices.length / ITEMS_PER_PAGE)
  const paginatedInvoices = filteredInvoices.slice((page - 1) * ITEMS_PER_PAGE, page * ITEMS_PER_PAGE)

  // Handle Pay Invoice action
  const handlePayInvoice = async () => {
    if (!accessToken) {
      setModal({
        isOpen: true,
        title: 'Authentication Required',
        message: 'Please log in to proceed with payment.'
      })
      return
    }
    setLoading(true)
    setModal({ isOpen: false, title: '', message: '' })

    try {
      // Step 1: Fetch current month summary
      const summary = await getCurrentMonthInvoiceSummary(accessToken)
      setPaymentSummary({ totalAmount: summary.totalAmount, orderCount: summary.orderCount })

      if (summary.orderCount === 0) {
        setModal({
          isOpen: true,
          title: 'No Orders',
          message: 'Bạn chưa có đơn hàng.'
        })
        return
      }

      // Show confirmation modal
      setModal({
        isOpen: true,
        title: 'Confirm Payment',
        message: `You have ${summary.orderCount} unpaid invoices totaling $${summary.totalAmount.toFixed(2)}. Proceed to payment?`,
        showConfirmButton: true,
        onConfirm: async () => {
          setModal({ isOpen: false, title: '', message: '' })
          setLoading(true)
          try {
            // Step 2: Initiate MoMo payment
            const momoResponse = await payCurrentInvoice(accessToken)
            if (momoResponse.payUrl) {
              window.location.href = momoResponse.payUrl // Redirect to MoMo payment page
            } else {
              throw new Error('Failed to initiate MoMo payment')
            }
          } catch (err: any) {
            setModal({
              isOpen: true,
              title: 'Payment Error',
              message: err.message || 'An error occurred during payment.'
            })
          } finally {
            setLoading(false)
          }
        }
      })
    } catch (err: any) {
      setModal({
        isOpen: true,
        title: 'Error',
        message: err.message || 'An error occurred while fetching payment summary.'
      })
    } finally {
      setLoading(false)
    }
  }

  // Handle MoMo redirect callback
  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search)
    const resultCode = urlParams.get('resultCode')
    if (resultCode === '0' && accessToken) {
      setLoading(true)
      generateCurrentInvoice(accessToken)
        .then(() => {
          // Refresh invoices
          getAllInvoices(accessToken)
            .then((invoiceResponses) => {
              const mappedInvoices: Invoice[] = invoiceResponses.map((inv) => ({
                id: inv.purchaseOrderId,
                department: userData?.userId || 'Department',
                amount: inv.amount,
                date: inv.createdAt.split('T')[0],
                pdfUrl: inv.pdfUrl
              }))
              setInvoices(mappedInvoices)
              setModal({
                isOpen: true,
                title: 'Payment Successful',
                message: 'Payment successful! Invoice generated.'
              })
            })
            .catch((err) => {
              setModal({
                isOpen: true,
                title: 'Error',
                message: err.message || 'Failed to refresh invoices.'
              })
            })
        })
        .catch((err) => {
          setModal({
            isOpen: true,
            title: 'Error',
            message: err.message || 'Failed to generate invoice.'
          })
        })
        .finally(() => setLoading(false))
    }
  }, [accessToken, userData])

  return (
    <div className='bg-white rounded-xl shadow-lg p-8'>
      {/* Modal for notifications */}
      <Modal
        isOpen={modal.isOpen}
        title={modal.title}
        message={modal.message}
        onClose={() => setModal({ isOpen: false, title: '', message: '' })}
        onConfirm={modal.onConfirm}
        showConfirmButton={modal.showConfirmButton}
      />

      {/* Loading State */}
      {loading && <div className='mb-4 text-blue-600'>Processing...</div>}
      {paymentSummary && (
        <div className='mb-4 text-gray-700'>
          Unpaid Invoices: {paymentSummary.orderCount} | Total: ${paymentSummary.totalAmount.toFixed(2)}
        </div>
      )}

      <div className='flex justify-between items-center mb-6'>
        <h2 className='text-2xl font-semibold text-gray-800'>Invoice List</h2>
        <button
          onClick={handlePayInvoice}
          disabled={loading || !accessToken}
          className='px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition disabled:opacity-50'
        >
          Pay Invoice
        </button>
      </div>

      <div className='mb-6 flex flex-col sm:flex-row gap-4'>
        <div className='relative flex-1'>
          <FaSearch className='absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400' />
          <input
            type='text'
            placeholder='Search by ID'
            value={search}
            onChange={(e) => {
              setSearch(e.target.value)
              setPage(1)
            }}
            className='pl-10 p-2 border rounded-lg w-full focus:outline-none focus:ring-2 focus:ring-blue-600'
          />
        </div>
      </div>

      <div className='overflow-x-auto'>
        <table className='w-full text-left'>
          <thead>
            <tr className='bg-gradient-to-r from-blue-50 to-blue-100 text-blue-800'>
              <th className='p-4 text-sm font-semibold'>Invoice ID</th>
              <th className='p-4 text-sm font-semibold'>Total</th>
              <th className='p-4 text-sm font-semibold'>Date</th>
              <th className='p-4 text-sm font-semibold'>Action</th>
            </tr>
          </thead>
          <tbody>
            {paginatedInvoices.length > 0 ? (
              paginatedInvoices.map((invoice) => (
                <tr key={invoice.id} className='border-b hover:bg-blue-50/50 transition'>
                  <td className='p-4 text-sm text-gray-700'>{invoice.id.slice(0, 10)}</td>
                  <td className='p-4 text-sm text-gray-700'>${invoice.amount.toFixed(2)}</td>
                  <td className='p-4 text-sm text-gray-700'>{invoice.date}</td>
                  <td className='p-4 text-sm'>
                    <button
                      onClick={() => onSelectInvoice(invoice.pdfUrl)}
                      className='px-3 py-1 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-lg hover:from-blue-700 hover:to-blue-800 transition'
                    >
                      View PDF
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className='p-4 text-center text-gray-500'>
                  No invoices found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className='mt-6 flex justify-between items-center'>
          <button
            onClick={() => setPage((p) => Math.max(p - 1, 1))}
            disabled={page === 1}
            className='px-4 py-2 bg-gray-200 rounded-lg text-gray-700 hover:bg-gray-300 disabled:opacity-50 transition'
          >
            Previous
          </button>
          <span className='text-sm text-gray-700'>
            Page {page} / {totalPages}
          </span>
          <button
            onClick={() => setPage((p) => Math.min(p + 1, totalPages))}
            disabled={page === totalPages}
            className='px-4 py-2 bg-gray-200 rounded-lg text-gray-700 hover:bg-gray-300 disabled:opacity-50 transition'
          >
            Next
          </button>
        </div>
      )}
    </div>
  )
}

export default InvoiceList
