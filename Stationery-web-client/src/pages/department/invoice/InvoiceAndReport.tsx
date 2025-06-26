import { useState } from 'react'
import { FaClipboardList } from 'react-icons/fa'
import InvoiceList from './InvoiceList'
import InvoiceViewer from './InvoiceViewer'
import MonthlyChart from './MonthlyChart'

export type Invoice = {
  id: string
  department: string
  amount: number
  date: string
  pdfUrl: string
}

const sampleInvoices: Invoice[] = [
  { id: 'INV001', department: 'HR', amount: 150.75, date: '2025-01-15', pdfUrl: 'https://example.com/invoice1.pdf' },
  { id: 'INV002', department: 'IT', amount: 300.2, date: '2025-01-20', pdfUrl: 'https://example.com/invoice2.pdf' },
  {
    id: 'INV003',
    department: 'Finance',
    amount: 450.0,
    date: '2025-02-10',
    pdfUrl: 'https://example.com/invoice3.pdf'
  },
  { id: 'INV004', department: 'HR', amount: 200.5, date: '2025-02-15', pdfUrl: 'https://example.com/invoice4.pdf' },
  { id: 'INV005', department: 'HR', amount: 250.0, date: '2025-05-05', pdfUrl: 'https://example.com/invoice5.pdf' },
  { id: 'INV006', department: 'IT', amount: 175.3, date: '2025-05-25', pdfUrl: 'https://example.com/invoice6.pdf' },
  {
    id: 'INV007',
    department: 'Finance',
    amount: 320.0,
    date: '2024-12-10',
    pdfUrl: 'https://example.com/invoice7.pdf'
  },
  { id: 'INV008', department: 'HR', amount: 180.0, date: '2024-11-15', pdfUrl: 'https://example.com/invoice8.pdf' }
]

const MONTHS = ['01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12']

const InvoiceAndReport: React.FC = () => {
  const currentYear = '2025' // Dựa trên ngày hiện tại: 06/05/2025
  const currentMonth = '05'
  const [selectedYear, setSelectedYear] = useState<string>(currentYear)
  const [selectedMonth, setSelectedMonth] = useState<string>(currentMonth)
  const [selectedPdfUrl, setSelectedPdfUrl] = useState<string | null>(null)

  const handleSelectInvoice = (pdfUrl: string) => {
    setSelectedPdfUrl(pdfUrl)
  }

  const handleClearInvoice = () => {
    setSelectedPdfUrl(null)
  }

  // Danh sách năm duy nhất
  const years = Array.from(new Set(sampleInvoices.map((inv) => inv.date.slice(0, 4)))).sort()

  return (
    <div className='min-h-screen bg-gradient-to-br from-blue-50 to-gray-100 transition-all duration-300'>
      <div className={`transition-all duration-300 p-10 ml-16 max-w-10xl mx-auto`}>
        {/* Header */}
        <div className='flex items-center justify-between mb-8'>
          <h1 className='text-3xl font-bold mb-6 text-blue-700'>Quản lý hóa đơn</h1>
          <div className='flex gap-4'>
            <select
              value={selectedYear}
              onChange={(e) => {
                setSelectedYear(e.target.value)
                setSelectedMonth('All')
              }}
              className='p-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-600'
            >
              {years.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </select>
            <select
              value={selectedMonth}
              onChange={(e) => setSelectedMonth(e.target.value)}
              className='p-2 border rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-600'
            >
              <option value='All'>Tất cả Tháng</option>
              {MONTHS.map((month) => (
                <option key={month} value={month}>
                  {month}
                </option>
              ))}
            </select>
          </div>
        </div>
        <div className='grid grid-cols-1 lg:grid-cols-2 gap-6'>
          {/* Invoice List */}
          <div className='space-y-6'>
            <InvoiceList
              invoices={sampleInvoices}
              selectedYear={selectedYear}
              selectedMonth={selectedMonth}
              onSelectInvoice={handleSelectInvoice}
            />
          </div>
          {/* Monthly Chart */}
          <div>
            <MonthlyChart invoices={sampleInvoices} selectedYear={selectedYear} selectedMonth={selectedMonth} />
          </div>
        </div>
        {/* Modal Invoice Viewer */}
        <InvoiceViewer pdfUrl={selectedPdfUrl} onClear={handleClearInvoice} />
      </div>
    </div>
  )
}

export default InvoiceAndReport
