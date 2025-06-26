import React from 'react'
import { FaRegStar, FaStar } from 'react-icons/fa'
import { FaRegStarHalfStroke } from 'react-icons/fa6'
import { ProductDetail } from '~/types/product'
import { Promotion } from '~/types/promotion'

const formatNumber = (number: number | string) => {
  const numberParse: number = Number(number)
  if (!numberParse || numberParse === 0) {
    return 0
  }
  return numberParse.toLocaleString('de-DE')
}
const priceInPromotion = (productDetail: ProductDetail | null): number => {
  if (!productDetail) return 0
  if (productDetail?.productPromotions.length == 0) {
    return productDetail?.discountPrice || 0
  }
  const promotion: Promotion = productDetail?.productPromotions[0].promotion ?? ({} as Promotion)

  if (promotion.usageLimit > 0 && promotion.minOrderValue <= productDetail.discountPrice) {
    let price
    if (promotion.discountType === 'VALUE') {
      // nếu là giảm giá theo giá trị
      price = productDetail.discountPrice - promotion.discountValue
    } else {
      // nếu là giảm giá theo phần trăm
      const discount = (productDetail.discountPrice * promotion.discountValue) / 100
      if (discount > promotion.maxValue) {
        price = productDetail.discountPrice - promotion.maxValue
      } else {
        price = productDetail.discountPrice - discount
      }
    }
    return price
  }
  return productDetail.discountPrice
}
const convertNumberToStar = (number: number) => {
  if (!number) {
    return new Array(5).fill(React.createElement(FaRegStar))
  }
  number = Number(number)
  const stars = []
  for (let i = 1; i <= number; i++) {
    stars.push(React.createElement(FaStar))
  }
  if (number !== 0 && number % Math.floor(number) !== 0) {
    stars.push(React.createElement(FaRegStarHalfStroke))
    number++
  }
  for (let i = 5; i > number; i--) {
    stars.push(React.createElement(FaRegStar))
  }
  return stars
}
const calculatePercent = (price?: number, priceDiscount?: number) => {
  if (price === 0 || !price || priceDiscount === 0 || !priceDiscount) {
    return 0
  }
  return Number((((price - priceDiscount) / price) * 100).toFixed(2))
}
export { formatNumber, convertNumberToStar, calculatePercent, priceInPromotion }
