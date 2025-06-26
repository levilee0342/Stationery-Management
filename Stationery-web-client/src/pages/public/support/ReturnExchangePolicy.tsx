import React from 'react'

const ReturnExchangePolicy: React.FC = () => {
  return (
    <div className='max-w-7xl mx-auto px-4 py-16 mt-12'>
      <h1 className='text-4xl font-extrabold text-blue-600 mb-8 text-center'>Return & Exchange Policy</h1>

      <section className='space-y-8 text-gray-700'>
        <div>
          <h2 className='text-3xl font-semibold text-blue-500 mb-4'>Introduction</h2>
          <p className='leading-relaxed'>
            At <strong>Stationery's P</strong>, customer satisfaction is our top priority. We strive to provide
            high-quality products and excellent service. This Return & Exchange Policy outlines the process and
            conditions under which our customers can return or exchange products.
          </p>
        </div>

        <div>
          <h2 className='text-3xl font-semibold text-blue-500 mb-4'>Return Policy</h2>
          <p className='leading-relaxed'>
            We accept returns within <strong>30 days</strong> from the date of purchase. Products must be in their
            original condition, unused, and in the original packaging to be eligible for a return. To initiate a return,
            please contact our customer service team at{' '}
            <a href='mailto:nguyenquockhoa5549@gmail.com' className='text-blue-600'>
              support@stationerysp.com
            </a>
            .
          </p>
          <ul className='list-disc pl-6 mt-4'>
            <li>Returns are processed within 7 business days after receiving the product.</li>
            <li>Refunds will be issued to the original payment method.</li>
            <li>Items purchased on sale or clearance are not eligible for return.</li>
          </ul>
        </div>

        <div>
          <h2 className='text-3xl font-semibold text-blue-500 mb-4'>Exchange Policy</h2>
          <p className='leading-relaxed'>
            If you wish to exchange an item for a different size, color, or product, we offer exchanges within{' '}
            <strong>30 days</strong> of the purchase date. Please ensure the product is unused, and in its original
            condition, with all tags and packaging intact.
          </p>
          <ul className='list-disc pl-6 mt-4'>
            <li>Exchanges are subject to availability of the desired item.</li>
            <li>If the requested exchange item is out of stock, a refund will be issued.</li>
            <li>
              For exchanges, please contact our support team at{' '}
              <a href='mailto:support@stationerysp.com' className='text-blue-600'>
                support@stationerysp.com
              </a>
              .
            </li>
          </ul>
        </div>

        <div>
          <h2 className='text-3xl font-semibold text-blue-500 mb-4'>Non-Returnable Items</h2>
          <p className='leading-relaxed'>Certain items are non-returnable, including but not limited to:</p>
          <ul className='list-disc pl-6 mt-4'>
            <li>Customized products</li>
            <li>Opened stationery kits</li>
            <li>Gift cards</li>
            <li>Perishable items</li>
          </ul>
        </div>

        <div>
          <h2 className='text-3xl font-semibold text-blue-500 mb-4'>How to Request a Return or Exchange</h2>
          <p className='leading-relaxed'>To request a return or exchange, please follow these steps:</p>
          <ol className='list-decimal pl-6 mt-4'>
            <li>
              Contact our customer support team via email at{' '}
              <a href='mailto:support@stationerysp.com' className='text-blue-600'>
                support@stationerysp.com
              </a>
              .
            </li>
            <li>
              Provide your order number, the item(s) you wish to return or exchange, and the reason for the return or
              exchange.
            </li>
            <li>
              Our team will guide you through the next steps and provide you with a return shipping label, if
              applicable.
            </li>
          </ol>
        </div>

        <div>
          <h2 className='text-3xl font-semibold text-blue-500 mb-4'>Contact Us</h2>
          <p className='leading-relaxed'>
            If you have any questions or concerns about our Return & Exchange Policy, please feel free to reach out to
            our customer service team:
          </p>
          <p className='mt-4'>
            Email:{' '}
            <a href='mailto:support@stationerysp.com' className='text-blue-600'>
              support@stationerysp.com
            </a>
          </p>
          <p className='mt-2'>Phone: (123) 456-7890</p>
        </div>
      </section>
    </div>
  )
}

export default ReturnExchangePolicy
