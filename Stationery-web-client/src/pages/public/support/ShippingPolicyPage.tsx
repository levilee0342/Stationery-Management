import React from 'react'

const ShippingPolicyPage: React.FC = () => {
  return (
    <div className='max-w-7xl mx-auto px-4 py-16 bg-gray-100 mt-12'>
      <header className='text-center mb-12'>
        <h1 className='text-4xl font-extrabold text-blue-600 mb-4'>Shipping Policy - Stationery's P</h1>
        <p className='text-lg text-gray-700'>
          At Stationery's P, we aim to provide fast and reliable shipping to ensure your products arrive on time and in
          perfect condition. Below is our detailed shipping policy to help you understand our shipping process.
        </p>
      </header>

      <section className='space-y-12'>
        {/* Section 1 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>1. Shipping Methods and Options</h2>
          <p className='text-gray-600'>We offer various shipping options to meet your needs:</p>
          <ul className='list-disc list-inside text-gray-600 mt-2'>
            <li>
              <strong>Standard Shipping:</strong> Delivery within 5-7 business days.
            </li>
            <li>
              <strong>Expedited Shipping:</strong> Delivery within 2-3 business days.
            </li>
            <li>
              <strong>Same-Day Delivery:</strong> Available for orders placed before 12 PM (only for select locations).
            </li>
          </ul>
        </div>

        {/* Section 2 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>2. Shipping Fees</h2>
          <p className='text-gray-600'>
            Shipping fees are calculated based on the delivery location, weight, and size of your order. You can view
            the exact shipping cost at checkout before completing your purchase.
          </p>
          <p className='text-gray-600 mt-2'>
            <strong>Free Shipping:</strong> We offer free standard shipping for orders over a specified amount. The free
            shipping offer is available in certain regions.
          </p>
        </div>

        {/* Section 3 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>3. Shipping Timeframes</h2>
          <p className='text-gray-600'>
            We process orders promptly to ensure timely delivery. Once your order is placed:
          </p>
          <ul className='list-disc list-inside text-gray-600 mt-2'>
            <li>
              <strong>Processing Time:</strong> 1-2 business days for most items.
            </li>
            <li>
              <strong>Standard Shipping:</strong> 5-7 business days, depending on your location.
            </li>
            <li>
              <strong>Expedited Shipping:</strong> 2-3 business days, available for an additional charge.
            </li>
            <li>
              <strong>Same-Day Delivery:</strong> Available in select areas for orders placed before 12 PM.
            </li>
          </ul>
        </div>

        {/* Section 4 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>4. International Shipping</h2>
          <p className='text-gray-600'>
            We currently offer international shipping to select countries. Shipping costs and delivery times for
            international orders vary based on the destination and shipping method selected.
          </p>
          <p className='text-gray-600 mt-2'>
            Please be aware that international customers are responsible for any additional customs duties, taxes, or
            fees that may apply.
          </p>
        </div>

        {/* Section 5 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>5. Order Tracking</h2>
          <p className='text-gray-600'>
            Once your order has shipped, you will receive a tracking number via email. You can use this tracking number
            to monitor the progress of your delivery. If you encounter any issues with tracking, please contact our
            customer service team.
          </p>
        </div>

        {/* Important Note */}
        <div className='bg-gray-100 p-6 rounded-xl mt-8'>
          <p className='text-gray-600 font-medium'>Important Note:</p>
          <p className='text-gray-600 mt-2'>
            Please ensure that your shipping address is correct and complete. Stationery's P is not responsible for any
            delivery issues caused by incorrect or incomplete addresses.
          </p>
          <p className='text-gray-600 mt-2'>This shipping policy is effective from August 1, 2022.</p>
        </div>
      </section>
    </div>
  )
}

export default ShippingPolicyPage
