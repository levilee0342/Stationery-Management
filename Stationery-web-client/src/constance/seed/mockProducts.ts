import { ListProductDetail } from '~/types/product'

export const mockProducts: ListProductDetail[] = [
  {
    productId: '1',
    name: 'Bút Gel Thiên Long',
    description: 'Bút gel mực xanh, viết mượt, phù hợp học sinh',
    slug: 'but-gel-thien-long',
    category: { categoryId: 'cat1', categoryName: 'Stationery' },
    minPrice: 12000,
    quantity: 100,
    soldQuantity: 35,
    totalRating: 4.55,
    createdAt: '2024-03-01T08:00:00Z',
    img: 'https://product.hstatic.net/1000230347/product/combo-20_gel-08_9fcd5b0dad8246c18155a0694757c407_1024x1024.jpg',
    productDetails: [
      {
        productDetailId: 'pd1',
        name: 'Bút Gel TL-027 Xanh',
        slug: 'but-gel-tl-027-xanh',
        originalPrice: 15000,
        stockQuantity: 50,
        soldQuantity: 20,
        discountPrice: 12000,
        size: { sizeId: 's1', name: 'Standard', priority: 1 },
        color: { colorId: 'c1', name: 'Xanh', hex: '#0000FF' },
        totalRating: 4.5,
        description: 'Bút gel màu xanh, mực đều, bền',
        promotion: null,
        images: [
          {
            imageId: 'img1',
            url: 'https://product.hstatic.net/200000050856/product/gel-08-tim_a2ada24efb384a2d9564195478de8a54.jpg',
            priority: 1
          },
          {
            imageId: 'img2',
            url: 'https://vppdangchau.net/wp-content/uploads/2020/10/but-gel-sunbean-08.jpg',
            priority: 2
          }
        ],
        productId: '1',
        createdAt: '2024-03-01T08:00:00Z'
      },
      {
        productDetailId: 'pd2',
        name: 'Bút Gel TL-027 Đỏ',
        slug: 'but-gel-tl-027-do',
        originalPrice: 15000,
        stockQuantity: 50,
        soldQuantity: 15,
        discountPrice: 12000,
        size: { sizeId: 's1', name: 'Standard', priority: 1 },
        color: { colorId: 'c2', name: 'Đỏ', hex: '#FF0000' },
        totalRating: 4.6,
        description: 'Bút gel màu đỏ, mực đều, bền',
        promotion: null,
        images: [
          {
            imageId: 'img3',
            url: 'https://via.placeholder.com/150/FF0000',
            priority: 1
          }
        ],
        productId: '1',
        createdAt: '2024-03-01T08:00:00Z'
      }
    ],
    fetchColor: [
      { colorId: 'c1', hex: '#0000FF', slug: 'color-xanh' },
      { colorId: 'c2', hex: '#FF0000', slug: 'color-do' }
    ]
  },
  {
    productId: '2',
    name: 'Vở Campus A4',
    description: 'Vở kẻ ngang, giấy chất lượng cao, 200 trang',
    slug: 'vo-campus-a4',
    category: { categoryId: 'cat1', categoryName: 'Stationery' },
    minPrice: 18000,
    quantity: 300,
    soldQuantity: 90,
    totalRating: 4.75,
    createdAt: '2024-02-10T09:00:00Z',
    img: 'https://tse1.mm.bing.net/th/id/OIP.1rW_x_7zTXL2DeNXw3jJcQHaHa?rs=1&pid=ImgDetMain',
    productDetails: [
      {
        productDetailId: 'pd3',
        name: 'Vở Campus A4 Line',
        slug: 'vo-campus-a4-line',
        originalPrice: 20000,
        stockQuantity: 200,
        soldQuantity: 60,
        discountPrice: 18000,
        size: { sizeId: 's2', name: 'A4', priority: 1 },
        color: { colorId: 'c3', name: 'Trắng', hex: '#FFFFFF' },
        totalRating: 4.7,
        description: 'Vở giấy trắng, kẻ ngang, 200 trang',
        promotion: null,
        images: [
          {
            imageId: 'img4',
            url: 'https://tse4.mm.bing.net/th/id/OIP.OZHlLbXLIyM7C8xFynNPogHaHa?w=500&h=500&rs=1&pid=ImgDetMain',
            priority: 1
          }
        ],
        productId: '2',
        createdAt: '2024-02-10T09:00:00Z'
      },
      {
        productDetailId: 'pd4',
        name: 'Vở Campus A4 Caro',
        slug: 'vo-campus-a4-caro',
        originalPrice: 20000,
        stockQuantity: 100,
        soldQuantity: 30,
        discountPrice: 18000,
        size: { sizeId: 's2', name: 'A4', priority: 1 },
        color: { colorId: 'c3', name: 'Trắng', hex: '#FFFFFF' },
        totalRating: 4.8,
        description: 'Vở giấy trắng, kẻ caro, 200 trang',
        promotion: null,
        images: [
          {
            imageId: 'img5',
            url: 'https://via.placeholder.com/150/FFFFFF',
            priority: 1
          }
        ],
        productId: '2',
        createdAt: '2024-02-10T09:00:00Z'
      }
    ],
    fetchColor: [{ colorId: 'c3', hex: '#FFFFFF', slug: 'color-trang' }]
  },
  {
    productId: '3',
    name: 'Kẹp Giấy Deli',
    description: 'Kẹp giấy màu đen, kích thước 41mm',
    slug: 'kep-giay-deli',
    category: { categoryId: 'cat1', categoryName: 'Stationery' },
    minPrice: 15000,
    quantity: 150,
    soldQuantity: 50,
    totalRating: 4.25,
    createdAt: '2024-01-25T11:30:00Z',
    img: 'https://sangha.vn/image/kep-giay-mau-deli-33mm-e39716-0e.png',
    productDetails: [
      {
        productDetailId: 'pd5',
        name: 'Kẹp Giấy Deli 41mm',
        slug: 'kep-giay-deli-41mm',
        originalPrice: 17000,
        stockQuantity: 80,
        soldQuantity: 30,
        discountPrice: 15000,
        size: { sizeId: 's3', name: '41mm', priority: 1 },
        color: { colorId: 'c4', name: 'Đen', hex: '#000000' },
        totalRating: 4.2,
        description: 'Kẹp giấy chắc chắn, màu đen',
        promotion: null,
        images: [
          {
            imageId: 'img6',
            url: 'https://tse2.mm.bing.net/th/id/OIP.AZiF--_LPso6-mGycfJbLgHaHa?rs=1&pid=ImgDetMain',
            priority: 1
          }
        ],
        productId: '3',
        createdAt: '2024-01-25T11:30:00Z'
      },
      {
        productDetailId: 'pd6',
        name: 'Kẹp Giấy Deli 41mm Bạc',
        slug: 'kep-giay-deli-41mm-bac',
        originalPrice: 18000,
        stockQuantity: 70,
        soldQuantity: 20,
        discountPrice: 16000,
        size: { sizeId: 's3', name: '41mm', priority: 1 },
        color: { colorId: 'c6', name: 'Bạc', hex: '#C0C0C0' },
        totalRating: 4.3,
        description: 'Kẹp giấy chắc chắn, màu bạc',
        promotion: null,
        images: [
          {
            imageId: 'img7',
            url: 'https://via.placeholder.com/150/C0C0C0',
            priority: 1
          }
        ],
        productId: '3',
        createdAt: '2024-01-25T11:30:00Z'
      }
    ],
    fetchColor: [
      { colorId: 'c4', hex: '#000000', slug: 'color-den' },
      { colorId: 'c6', hex: '#C0C0C0', slug: 'color-bac' }
    ]
  },
  {
    productId: '4',
    name: 'Thước Kẻ 30cm',
    description: 'Thước nhựa trong suốt, dài 30cm',
    slug: 'thuoc-ke-30cm',
    category: { categoryId: 'cat1', categoryName: 'Stationery' },
    minPrice: 10000,
    quantity: 200,
    soldQuantity: 70,
    totalRating: 4.55,
    createdAt: '2024-04-01T07:45:00Z',
    img: 'https://tse1.mm.bing.net/th/id/OIP.wCSqX4egoiwuwLEX-eP58gHaHa?rs=1&pid=ImgDetMain',
    productDetails: [
      {
        productDetailId: 'pd7',
        name: 'Thước Kẻ 30cm Trong Suốt',
        slug: 'thuoc-ke-30cm-trong-suot',
        originalPrice: 12000,
        stockQuantity: 120,
        soldQuantity: 40,
        discountPrice: 10000,
        size: { sizeId: 's4', name: '30cm', priority: 1 },
        color: { colorId: 'c5', name: 'Trong Suốt', hex: '#FFFFFF' },
        totalRating: 4.6,
        description: 'Thước nhựa bền, dễ sử dụng',
        promotion: null,
        images: [
          {
            imageId: 'img8',
            url: 'https://tse4.mm.bing.net/th/id/OIP.l-gustOik3fDvBJJz4NSagHaHa?w=1000&h=1000&rs=1&pid=ImgDetMain',
            priority: 1
          }
        ],
        productId: '4',
        createdAt: '2024-04-01T07:45:00Z'
      },
      {
        productDetailId: 'pd8',
        name: 'Thước Kẻ 30cm Xanh',
        slug: 'thuoc-ke-30cm-xanh',
        originalPrice: 13000,
        stockQuantity: 80,
        soldQuantity: 30,
        discountPrice: 11000,
        size: { sizeId: 's4', name: '30cm', priority: 1 },
        color: { colorId: 'c1', name: 'Xanh', hex: '#0000FF' },
        totalRating: 4.5,
        description: 'Thước nhựa bền, màu xanh',
        promotion: null,
        images: [
          {
            imageId: 'img9',
            url: 'https://via.placeholder.com/150/0000FF',
            priority: 1
          }
        ],
        productId: '4',
        createdAt: '2024-04-01T07:45:00Z'
      }
    ],
    fetchColor: [
      { colorId: 'c5', hex: '#FFFFFF', slug: 'color-trong-suot' },
      { colorId: 'c1', hex: '#0000FF', slug: 'color-xanh' }
    ]
  },
  {
    productId: '5',
    name: 'Bút Chì Kim Bấm Pentel',
    description: 'Bút chì kim bấm 0.5mm, thân nhựa cao cấp',
    slug: 'but-chi-kim-bam-pentel',
    category: { categoryId: 'cat1', categoryName: 'Stationery' },
    minPrice: 25000,
    quantity: 120,
    soldQuantity: 45,
    totalRating: 4.8,
    createdAt: '2024-05-01T10:00:00Z',
    img: 'https://via.placeholder.com/150/000000',
    productDetails: [
      {
        productDetailId: 'pd9',
        name: 'Bút Chì Kim Bấm Pentel Đen',
        slug: 'but-chi-kim-bam-pentel-den',
        originalPrice: 28000,
        stockQuantity: 60,
        soldQuantity: 25,
        discountPrice: 25000,
        size: { sizeId: 's5', name: '0.5mm', priority: 1 },
        color: { colorId: 'c4', name: 'Đen', hex: '#000000' },
        totalRating: 4.9,
        description: 'Bút chì kim bấm 0.5mm, màu đen',
        promotion: null,
        images: [
          {
            imageId: 'img10',
            url: 'https://via.placeholder.com/150/000000',
            priority: 1
          }
        ],
        productId: '5',
        createdAt: '2024-05-01T10:00:00Z'
      },
      {
        productDetailId: 'pd10',
        name: 'Bút Chì Kim Bấm Pentel Xanh',
        slug: 'but-chi-kim-bam-pentel-xanh',
        originalPrice: 28000,
        stockQuantity: 60,
        soldQuantity: 20,
        discountPrice: 25000,
        size: { sizeId: 's5', name: '0.5mm', priority: 1 },
        color: { colorId: 'c1', name: 'Xanh', hex: '#0000FF' },
        totalRating: 4.7,
        description: 'Bút chì kim bấm 0.5mm, màu xanh',
        promotion: null,
        images: [
          {
            imageId: 'img11',
            url: 'https://via.placeholder.com/150/0000FF',
            priority: 1
          }
        ],
        productId: '5',
        createdAt: '2024-05-01T10:00:00Z'
      }
    ],
    fetchColor: [
      { colorId: 'c4', hex: '#000000', slug: 'color-den' },
      { colorId: 'c1', hex: '#0000FF', slug: 'color-xanh' }
    ]
  }
]
