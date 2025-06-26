import { memo } from 'react'
import { DOTS, usePagination } from '~/hooks/usePaginnation'
import { HiChevronLeft, HiChevronRight } from 'react-icons/hi'

type PaginationProps = {
  siblingCount?: number
  totalPageCount: number
  onPageChange: (page: number) => void
  currentPage: number
}

const Pagination: React.FC<PaginationProps> = ({ siblingCount = 1, totalPageCount, currentPage, onPageChange }) => {
  const paginationRange = usePagination({ siblingCount, currentPage, totalPageCount })
  return (
    <div className='flex items-center justify-center flex-wrap gap-2 p-4'>
      <button
        type='button'
        disabled={currentPage === 1}
        className={`w-10 h-10 flex items-center justify-center rounded-lg border text-gray-600 transition-all
          ${currentPage === 1 ? 'cursor-not-allowed bg-gray-100' : 'hover:bg-gray-200 hover:text-black'}`}
        onClick={() => onPageChange(currentPage - 1)}
      >
        <HiChevronLeft className='w-5 h-5' />
      </button>

      {paginationRange?.map((el: string | number, index: number) => {
        if (el === DOTS) {
          return (
            <span key={index} className='w-10 h-10 flex items-center justify-center text-gray-400'>
              ...
            </span>
          )
        }

        const isActive = currentPage === el
        return (
          <button
            key={index}
            className={`w-10 h-10 flex items-center justify-center rounded-lg border transition-all font-medium
              ${isActive ? 'bg-blue-600 text-white border-blue-600' : 'text-gray-600 hover:bg-blue-100 hover:text-blue-600'}`}
            onClick={() => onPageChange(el as number)}
          >
            {el}
          </button>
        )
      })}

      <button
        type='button'
        disabled={currentPage === totalPageCount}
        className={`w-10 h-10 flex items-center justify-center rounded-lg border text-gray-600 transition-all
          ${currentPage === totalPageCount ? 'cursor-not-allowed bg-gray-100' : 'hover:bg-gray-200 hover:text-black'}`}
        onClick={() => onPageChange(currentPage + 1)}
      >
        <HiChevronRight className='w-5 h-5' />
      </button>
    </div>
  )
}

export default memo(Pagination)
