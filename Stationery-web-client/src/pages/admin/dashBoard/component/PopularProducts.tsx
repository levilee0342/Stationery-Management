import classNames from 'classnames'
// import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
// import { getAllProducts } from "~/apis/product";
import { formatNumber } from '~/utils/helper'
const bestSeller = [
  {
    _id: 'prod1',
    slug: 'wireless-bluetooth-headphones',
    primaryImage: {
      url: 'https://example.com/images/headphones.jpg'
    },
    title: 'Wireless Bluetooth Headphones with Noise Cancellation',
    quantity: 75,
    discountPrice: 1290000
  },
  {
    _id: 'prod2',
    slug: 'smartphone-13-pro',
    primaryImage: {
      url: 'https://example.com/images/smartphone.jpg'
    },
    title: 'Smartphone 13 Pro 128GB Midnight Black',
    quantity: 12,
    discountPrice: 24990000
  },
  {
    _id: 'prod3',
    slug: 'stainless-steel-water-bottle',
    primaryImage: {
      url: 'https://example.com/images/waterbottle.jpg'
    },
    title: 'Stainless Steel Water Bottle 1L',
    quantity: 0,
    discountPrice: 350000
  },
  {
    _id: 'prod4',
    slug: 'gaming-mouse-rgb',
    primaryImage: {
      url: 'https://example.com/images/mouse.jpg'
    },
    title: 'RGB Gaming Mouse with Programmable Buttons',
    quantity: 150,
    discountPrice: 890000
  },
  {
    _id: 'prod5',
    slug: 'portable-power-bank',
    primaryImage: {
      url: 'https://example.com/images/powerbank.jpg'
    },
    title: '10000mAh Portable Power Bank Fast Charging',
    quantity: 45,
    discountPrice: 650000
  }
]

function PopularProducts() {
  // const [bestSeller, setBestSeller] = useState([]);

  // const fetchBestSellter = async () => {
  //   const response = await getAllProducts({
  //     params: { sort: "-soldQuantity",limit:5 },
  //   });
  //   if (response?.success) setBestSeller(response.data);
  // };
  // useEffect(() => {
  //   fetchBestSellter();
  // }, []);
  return (
    <div className='w-[20rem] bg-white p-4 rounded-sm border border-gray-200'>
      <strong className='text-gray-700 font-medium'>Popular Products</strong>
      <div className='mt-4 flex flex-col gap-3'>
        {bestSeller?.map((product) => (
          <Link key={product._id} to={`/${product.slug}`} className='flex items-start hover:no-underline'>
            <div className='w-10 h-10 min-w-[2.5rem] bg-gray-200 rounded-sm'>
              <img
                className='w-full h-full object-cover rounded-sm'
                src={product.primaryImage.url}
                alt={product.title}
              />
            </div>
            <div className='ml-4 flex-1'>
              <p className='text-sm text-gray-800 line-clamp-2'>{product.title}</p>
              <span
                className={classNames(
                  product.quantity === 0
                    ? 'text-red-500'
                    : product.quantity > 50
                      ? 'text-green-500'
                      : 'text-orange-500',
                  'text-xs font-medium'
                )}
              >
                {product.quantity === 0 ? 'Hết hàng' : 'Còn ' + product.quantity + ' sản phẩm'}
              </span>
            </div>
            <div className='text-xs text-gray-400 pl-1.5'>{formatNumber(product.discountPrice)}đ</div>
          </Link>
        ))}
      </div>
    </div>
  )
}

export default PopularProducts
