import { Category } from '~/types/category'
import { Color, Size } from '~/types/product'

const categoriesToOptions = (categories: Category[]) => {
  return categories.map((category) => {
    return {
      label: category.categoryName,
      value: category.categoryId
    }
  })
}
const colorToOptions = (colors: Color[]) => {
  return colors.map((color) => {
    return {
      label: color.name,
      value: color.colorId,
      color: color.hex
    }
  })
}
const sizeToOptions = (sizes: Size[]) => {
  return sizes.map((size) => {
    return {
      label: size.name,
      value: size.sizeId
    }
  })
}
export { categoriesToOptions, colorToOptions, sizeToOptions }
