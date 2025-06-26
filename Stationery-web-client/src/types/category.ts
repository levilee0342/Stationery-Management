export interface Category {
  categoryId: string
  categoryName: string
  icon?: string
  bgColor?: string
}

export interface CategorySearchParams {
  search?: string
  page?: string
  limit?: string
}
