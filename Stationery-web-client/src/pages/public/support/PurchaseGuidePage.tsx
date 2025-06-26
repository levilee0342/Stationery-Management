import React from 'react'
import { Link } from 'react-router-dom'
import { publicPaths } from '~/constance/paths'

const PurchaseGuidePage: React.FC = () => {
  return (
    <div className='max-w-7xl mx-auto px-4 py-16 bg-gray-100 mt-12'>
      <header className='text-center mb-12'>
        <h1 className='text-4xl font-extrabold text-blue-600 mb-4'>
          How to Buy and Place an Order - Step-by-Step Guide
        </h1>
        <p className='text-lg text-gray-700'>
          Follow our simple shopping guide to purchase products quickly and easily. Learn how to place orders online
          with confidence and convenience.
        </p>
      </header>

      <section className='space-y-12'>
        {/* Step 1 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>Step 1: Browse Our Product Catalog</h2>
          <p className='text-gray-600'>
            Explore our extensive range of high-quality office supplies, stationery, and more. Use the search bar or
            category filters to find exactly what you need. From trending items to exclusive deals, everything is
            available in our online store.
          </p>
        </div>

        {/* Step 2 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>Step 2: Add Items to Your Shopping Cart</h2>
          <p className='text-gray-600'>
            After finding the perfect item, simply click “Add to Cart” to save it. You can continue shopping or
            checkout. Make sure to review your cart to ensure all items are correct before confirming your purchase.
          </p>
        </div>

        {/* Step 3 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>Step 3: Proceed to Checkout</h2>
          <p className='text-gray-600'>
            When you're ready, click the cart icon and select “Checkout.” You can log in to your account or proceed as a
            guest. Provide your shipping details, choose a payment method, and you're almost done!
          </p>
        </div>

        {/* Step 4 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>Step 4: Confirm and Place Your Order</h2>
          <p className='text-gray-600'>
            Double-check your order details, including shipping address and payment information. Once you're satisfied,
            click “Place Order” to complete your purchase. An order confirmation will be sent to your email immediately.
          </p>
        </div>

        {/* Step 5 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>Step 5: Track Your Order</h2>
          <p className='text-gray-600'>
            After your order is placed, you can track its status using the tracking link provided in your confirmation
            email. We ensure fast and reliable shipping, so you can enjoy your products as soon as possible.
          </p>
        </div>
      </section>

      {/* Call to Action */}
      <section className='mt-12 text-center'>
        <h3 className='text-2xl font-semibold text-gray-800 mb-4'>Ready to Shop Online?</h3>
        <p className='text-gray-600 mb-6'>
          Start shopping now and enjoy a seamless and secure online ordering experience at Stationery's P.
        </p>
        <Link
          to={publicPaths.PRODUCT}
          className='inline-block bg-blue-600 text-white font-semibold py-3 px-6 rounded-lg hover:bg-blue-700 transition-colors duration-300'
        >
          Shop Now
        </Link>
      </section>

      {/* SEO-Friendly Footer */}
      <footer className='mt-12 text-center text-gray-500 text-sm'>
        <p>
          Learn how to place orders easily and securely with our shopping guide. Discover the best tips for buying
          office supplies and stationery online.
        </p>
      </footer>
    </div>
  )
}

export default PurchaseGuidePage
