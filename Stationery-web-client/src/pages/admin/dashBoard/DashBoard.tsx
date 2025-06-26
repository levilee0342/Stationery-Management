import PopularProducts from '~/pages/department/dashboard/PopularProducts'
import DashBoardStats from './component/DashBoardStats'
import TransactionChart from './component/TransactionChart'
import { useState } from 'react'
import { Product } from '~/types/product'
import RequestHistory from '~/pages/department/dashboard/RequestHistory'

function DashBoard() {
  const [products, setProducts] = useState<Product[]>([])
  return (
    <div className=''>
      <div className='flex flex-col gap-4'>
        <DashBoardStats />
        <div className='flex flex-row gap-4 w-full'>
          <TransactionChart />
          {/* <BuyerProfilePieChart /> */}
        </div>
        <div className='flex flex-row gap-4 w-full'>
          <RequestHistory />
          <PopularProducts setProducts={setProducts} />
        </div>
      </div>
    </div>
  )
}

export default DashBoard
