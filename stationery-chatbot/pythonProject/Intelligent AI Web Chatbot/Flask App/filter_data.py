import requests
import json

def filter_products(data):
    filtered = []
    for p in data.get("result", []):
        # Thông tin gốc
        item = {
            "productId":    p.get("productId"),
            "name":         p.get("name"),
            "category":     p.get("category", {}).get("categoryName"),
            "description":  p.get("description"),
            "minPrice":     p.get("minPrice"),
            "quantity":     p.get("quantity"),
            "soldQuantity": p.get("soldQuantity"),
            "createdAt":    p.get("createdAt"),
            "imageUrl":     p.get("img"),
            "colors":       [c.get("name", c.get("colorId")) for c in p.get("fetchColor", [])],
        }

        # Chi tiết biến thể
        detail = p.get("productDetail", {}) or {}
        size_info  = detail.get("size")  or {}
        color_info = detail.get("color") or {}

        item.update({
            "detailId":      detail.get("productDetailId"),
            "detailName":    detail.get("name"),
            "availableQty":  detail.get("availableQuantity"),
            "originalPrice": detail.get("originalPrice"),
            "discountPrice": detail.get("discountPrice"),
            "size":          size_info.get("name"),
            "detailColor":   color_info.get("name"),
            "promotions": [
                {
                    "promoCode":     promo.get("promotion", {}).get("promoCode"),
                    "discountType":  promo.get("promotion", {}).get("discountType"),
                    "discountValue": promo.get("promotion", {}).get("discountValue"),
                }
                for promo in detail.get("productPromotions", [])
            ]
        })

        filtered.append(item)

    return filtered

# Ví dụ sử dụng
API_URL = "http://localhost:8080/api/products/get-all-products-for-chatbot"
resp     = requests.get(API_URL)
data     = resp.json()
filtered = filter_products(data)

print(json.dumps(filtered, indent=2, ensure_ascii=False))
