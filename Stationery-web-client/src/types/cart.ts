import { ProductPromotion } from './promotion'

export interface CartItem {
  productId: string
  productDetailId: string
  productName: string
  imageUrl: string
  productPromotion: ProductPromotion[]
  quantity: number
  colorName: string
  colorId: string
  sizeName: string
  sizeId: string
  originalPrice: number
  discountPrice: number
  discountValue: number
  createdAt: string
}
