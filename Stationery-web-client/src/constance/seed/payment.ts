// constance/payment.ts
export type OrderItem = {
    id: number;
    name: string;
    price: number;
    quantity: number;
    image: string;
  };
  
  export type OrderDetails = {
    orderId: string;
    items: OrderItem[];
    totalAmount: number;
  };
  
  export type Province = { code: number; name: string };
  export type District = { code: number; name: string; wards: Ward[] };
  export type Ward = { code: number; name: string };
  export type Coupon = { id: number; code: string; discount: number; minOrder: number; expiry: string };
  
  export type ShippingAddressType = {  // Renamed from ShippingAddress to ShippingAddressType
    id: number;
    address: string;
    phone: string;
    isDefault: boolean;
  };
  
  export const sampleOrder: OrderDetails = {
    orderId: "ORD123456",
    items: [
      {
        id: 1,
        name: "Stylish Notebook",
        price: 200000,
        quantity: 1,
        image: "https://stbcuulong.edu.vn/wp-content/uploads/2023/06/L4_KNTT_TiengViet4.1-scaled.jpg",
      },
      {
        id: 2,
        name: "Premium Pen",
        price: 150000,
        quantity: 2,
        image: "https://stbcuulong.edu.vn/wp-content/uploads/2023/06/L4_KNTT_TiengViet4.1-scaled.jpg",
      },
    ],
    totalAmount: 200000 + 150000 * 2,
  };