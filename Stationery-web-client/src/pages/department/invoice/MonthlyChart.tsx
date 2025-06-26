import { PieChart, Pie, Cell, ResponsiveContainer, Legend, BarChart, Bar, XAxis, YAxis, Tooltip } from 'recharts'

export type Invoice = {
  id: string
  department: string
  amount: number
  date: string
  pdfUrl: string
}

interface MonthlyChartProps {
  invoices: Invoice[]
  selectedYear: string
  selectedMonth: string
}

const COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899']

const MonthlyChart: React.FC<MonthlyChartProps> = ({ invoices, selectedYear, selectedMonth }) => {
  // Tính toán dữ liệu theo tháng
  const monthlyData: { [key: string]: number } = {}
  invoices.forEach((invoice) => {
    const month = invoice.date.slice(0, 7) // Lấy YYYY-MM
    const year = invoice.date.slice(0, 4)
    const monthOnly = invoice.date.slice(5, 7) // Lấy MM
    if (selectedYear === year && (selectedMonth === 'All' || monthOnly === selectedMonth)) {
      monthlyData[month] = (monthlyData[month] || 0) + invoice.amount
    }
  })

  // Chuyển đổi dữ liệu cho Recharts
  const data = Object.keys(monthlyData).map((month, index) => ({
    name: month,
    value: monthlyData[month],
    fill: COLORS[index % COLORS.length]
  }))

  return (
    <div className='bg-white rounded-xl shadow-lg p-8'>
      <h2 className='text-2xl font-semibold text-gray-800 mb-6'>Thống Kê Theo Tháng</h2>
      {/* Biểu đồ Tròn */}
      <div className='mb-8'>
        <h3 className='text-xl font-semibold text-gray-700 mb-4'>Biểu đồ Tròn</h3>
        <div className='h-80'>
          <ResponsiveContainer width='100%' height='100%'>
            <PieChart>
              <Pie data={data} dataKey='value' nameKey='name' cx='50%' cy='50%' outerRadius={100} label>
                {data.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.fill} />
                ))}
              </Pie>
              <Legend />
              <Tooltip formatter={(value: number) => `$${value.toFixed(2)}`} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
      {/* Biểu đồ Cột */}
      <div>
        <h3 className='text-xl font-semibold text-gray-700 mb-4'>Biểu đồ Cột</h3>
        <div className='h-80'>
          <ResponsiveContainer width='100%' height='100%'>
            <BarChart data={data}>
              <XAxis dataKey='name' />
              <YAxis />
              <Tooltip formatter={(value: number) => `$${value.toFixed(2)}`} />
              <Legend />
              <Bar dataKey='value' fill='#3B82F6' />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}

export default MonthlyChart
