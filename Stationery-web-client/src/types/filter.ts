interface ProductSearchParams {
  page: string
  limit: string
  minPrice?: string
  maxPrice?: string
  sortBy?: string
  categoryId?: string
  search?: string
  [key: string]: string | undefined
}
export type { ProductSearchParams }
