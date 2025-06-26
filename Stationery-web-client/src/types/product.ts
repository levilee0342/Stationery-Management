import { Review } from './comment'
import { ProductPromotion } from './promotion'

interface Size {
  sizeId: string
  name: string
  priority: number
}

export interface ProductDetail {
  productDetailId: string
  name: string
  slug: string
  originalPrice: number
  stockQuantity: number
  soldQuantity: number
  discountPrice: number
  size: Size
  color: Color
  description: string
  productPromotions: ProductPromotion[]
  images: Image[] | null
  productId: string
  createdAt: string
  hidden: boolean
}

interface Image {
  imageId: string
  url: string
  priority: number
}

interface Color {
  colorId: string
  name: string
  hex: string
}

interface ProductColor {
  productColorId: string
  color: Color
  productDetails: ProductDetail[]
  images: Image[]
  sizes: Size[]
}

interface FetchColor {
  colorId: string
  hex: string
  slug: string
}

interface Product {
  productId: string
  description: string
  slug: string
  name: string
  category: {
    categoryId: string
    categoryName: string
  }
  soldQuantity: number
  quantity: number
  totalRating: number
  productDetail: ProductDetail
  fetchColor: FetchColor[]
  img: string
  createdAt: string
  hidden: boolean
}

interface ProductDeatilResponse extends Product {
  image: Image[]
  reviews: Review[]
  hidden: boolean
}

interface ListProductDetail extends Omit<Product, 'productDetail'> {
  categoryId: string
  productDetails: ProductDetail[]
}

interface ProductDetailResponse {
  code: number
  result: ProductDetail[]
}

interface ProductDetailForm {
  productDetailId?: string
  slug: string
  originalPrice: number
  stockQuantity: number
  discountPrice: number
  name: string
  sizeId: string
  colorId: string
  images?: {
    file: File
    url: string
    imageId?: string
  }[]
  deleteImages?: string[]
}
interface ProductForm {
  name: string
  description: string
  slug: string
  categoryId: string
  productDetails: ProductDetailForm[]
}
export type {
  ProductDetailForm,
  ProductForm,
  Product,
  Color,
  ProductColor,
  ProductDeatilResponse,
  Image,
  Size,
  FetchColor,
  ListProductDetail,
  ProductDetailResponse
}
