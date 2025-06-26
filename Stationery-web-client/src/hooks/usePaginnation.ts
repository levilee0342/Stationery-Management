import { useMemo } from 'react'

const DOTS = '...'

type UsePaginationProps = {
  siblingCount?: number
  currentPage: number
  totalPageCount: number
}

const usePagination = ({ siblingCount = 1, currentPage, totalPageCount }: UsePaginationProps): (number | string)[] => {
  return useMemo(() => {
    const totalPageNumbers = siblingCount + 5
    if (totalPageCount <= totalPageNumbers) {
      return range(1, totalPageCount)
    }

    const leftSiblingIndex = Math.max(currentPage - siblingCount, 1)
    const rightSiblingIndex = Math.min(currentPage + siblingCount, totalPageCount)
    const shouldShowLeftDots = currentPage - siblingCount > 2
    const shouldShowRightDots = currentPage + siblingCount < totalPageCount - 2
    const items = 3 + 2 * siblingCount

    if (shouldShowLeftDots && !shouldShowRightDots) {
      return [1, DOTS, ...range(totalPageCount - items + 1, totalPageCount)]
    }
    if (!shouldShowLeftDots && shouldShowRightDots) {
      return [...range(1, items), DOTS, totalPageCount]
    }
    if (shouldShowLeftDots && shouldShowRightDots) {
      return [1, DOTS, ...range(leftSiblingIndex, rightSiblingIndex), DOTS, totalPageCount]
    }
    return []
  }, [siblingCount, currentPage, totalPageCount])
}

const range = (from: number, to: number): number[] => {
  const length = to - from + 1
  return Array.from({ length }, (_, idx) => idx + from)
}

export { usePagination, DOTS }
