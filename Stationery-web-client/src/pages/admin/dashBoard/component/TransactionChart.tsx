import { Line, LineChart, CartesianGrid, Legend, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'

const data = [
  { name: 'Tháng 1', Income: 2400 },
  { name: 'Tháng 2', Income: 1398 },
  { name: 'Tháng 3', Income: 9800 },
  { name: 'Tháng 4', Income: 3908 },
  { name: 'Tháng 5', Income: 4800 },
  { name: 'Tháng 6', Income: 3800 },
  { name: 'Tháng 7', Income: 4300 },
  { name: 'Tháng 8', Income: 9800 },
  { name: 'Tháng 9', Income: 3908 },
  { name: 'Tháng 10', Income: 4800 },
  { name: 'Tháng 11', Income: 3800 },
  { name: 'Tháng 12', Income: 4300 }
]

function TransactionChart() {
  return (
    <div className='h-[22rem] bg-white p-4 rounded-sm border border-gray-200 flex flex-col flex-1'>
      <strong className='text-gray-700 font-medium'>Transactions</strong>
      <div className='mt-3 w-full flex-1 text-xs'>
        <ResponsiveContainer width='100%' height='100%'>
          <LineChart width={500} height={300} data={data} margin={{ top: 20, right: 10, left: -10, bottom: 0 }}>
            <defs>
              <linearGradient id='colorIncome' x1='0' y1='0' x2='0' y2='1'>
                <stop offset='5%' stopColor='#0ea5e9' stopOpacity={0.4} />
                <stop offset='95%' stopColor='#0ea5e9' stopOpacity={0} />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray='3 3' vertical={false} />
            <XAxis dataKey='name' />
            <YAxis />
            <Tooltip />
            <Legend />
            <Line
              type='monotone'
              dataKey='Income'
              name='Thu nhập'
              stroke='#0ea5e9'
              strokeWidth={2}
              dot={{ r: 3 }}
              activeDot={{ r: 6 }}
              fill='url(#colorIncome)'
              fillOpacity={1}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  )
}

export default TransactionChart
