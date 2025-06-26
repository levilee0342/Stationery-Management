interface Promotion {
  promotionId: string
  promoCode: string
  discountType: DiscountType
  discountValue: number
  usageLimit: number
  maxValue: number
  minOrderValue: number
  startDate: string // ISO format e.g. "2025-04-11"
  endDate: string
  createdAt: string

  user?: {
    userId: string
    firstName: string
    lastName: string
  }[]
  pd?: ProductDetailSimple[]
}
interface ProductDetailSimple {
  productDetailId: string
  name: string
}
interface ProductPromotion {
  productPromotionId: string
  promotion: Promotion
}
interface UserPromotion {
  userPromotionId: string
  promotion: Promotion
}
type DiscountType = 'PERCENTAGE' | 'VALUE'

interface SelectedPromotion {
  promotionId: string
  code: string
}

interface PromotionSearchParams {
  page?: string
  limit?: string
  search?: string
}
type VoucherType = 'ALL_PRODUCTS' | 'ALL_USERS' | 'PRODUCTS' | 'USERS'

interface VoucherFormData {
  promotionId?: string
  promoCode: string
  voucherType: VoucherType
  discountType: DiscountType
  discountValue: number
  maxValue: number
  usageLimit: number
  minOrderValue: number
  startDate: string
  endDate: string
  userIds?: string[]
  productIds?: string[]
}

interface UpdatePromotion {
  promotionId: string
  promoCode: string
  voucherType: VoucherType
  discountType: DiscountType
  discountValue: number
  maxValue: number
  usageLimit: number
  minOrderValue: number
  startDate: string
  endDate: string
}

export type {
  VoucherType,
  VoucherFormData,
  ProductDetailSimple,
  DiscountType,
  Promotion,
  ProductPromotion,
  UserPromotion,
  SelectedPromotion,
  PromotionSearchParams,
  UpdatePromotion
}
