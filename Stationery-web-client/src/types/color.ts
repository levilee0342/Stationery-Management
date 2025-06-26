interface SizeSlug {
  size: string
  slug: string
}
interface ColorSize {
  colorId: string
  hex: string
  sizes: SizeSlug[]
}
interface ColorSearchParams {
  page?: string
  search?: string
  limit?: string
}
export type { ColorSize, SizeSlug, ColorSearchParams }
