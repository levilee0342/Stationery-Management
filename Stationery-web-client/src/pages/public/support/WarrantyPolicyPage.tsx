import React from 'react';

const WarrantyPolicyPage: React.FC = () => {
  return (
    <div className='max-w-7xl mx-auto px-4 py-16 bg-gray-100'>
      <header className='text-center mb-12'>
        <h1 className='text-4xl font-extrabold text-blue-600 mb-4'>Warranty Policy - Stationery's P</h1>
        <p className='text-lg text-gray-700'>
          At Stationery's P, we are committed to offering high-quality products and excellent after-sales support. Below, you’ll find our comprehensive warranty policy to help you understand how to claim warranties and ensure satisfaction with your purchase.
        </p>
      </header>

      <section className='space-y-12'>
        {/* Section 1 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>1. Where Can I Claim a Warranty for My Product?</h2>
          <p className='text-gray-600'>
            <strong>Official Manufacturer Warranty:</strong> For electronic stationery items, such as electronic pens, calculators, or gadgets with an official warranty certificate from the manufacturer, you can bring your product directly to the manufacturer’s authorized service center for warranty support.
          </p>
          <p className='text-gray-600 mt-2'>
            <strong>Warranty via Stationery's P:</strong> If you prefer assistance from us, contact our customer service hotline at <span className='font-bold'>1900-XXX-XXX</span> or visit our <a href='https://www.stationeryp.com/warranty-policy' className='text-blue-500 underline'>warranty policy page</a> for detailed guidance.
          </p>
        </div>

        {/* Section 2 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>2. Can I Get a Free Warranty for My Product?</h2>
          <p className='text-gray-600'>
            Your product qualifies for a free manufacturer warranty under the following conditions:
          </p>
          <ul className='list-disc list-inside text-gray-600 mt-2'>
            <li>The product is still within the warranty period.</li>
            <li>The warranty sticker or certificate remains intact.</li>
            <li>The product has a technical defect due to manufacturing issues.</li>
          </ul>
          <p className='text-gray-600 mt-2'>Warranty fees may apply in these cases:</p>
          <ul className='list-disc list-inside text-gray-600 mt-2'>
            <li>The product is out of the warranty period.</li>
            <li>The product is damaged due to improper use.</li>
            <li>Damage caused by user error rather than a manufacturing defect.</li>
          </ul>
        </div>

        {/* Section 3 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>
            3. How Long Does It Take to Receive My Warranted Product?
          </h2>
          <p className='text-gray-600'>
            Processing time is typically <strong>21–45 days</strong> if submitted through Stationery's P, excluding
            shipping time. Directly contacting the manufacturer may expedite the process.
          </p>
        </div>

        {/* Section 4 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>
            4. What Warranty Methods Does Stationery's P Offer?
          </h2>
          <ul className='list-disc list-inside text-gray-600 mt-2'>
            <li>
              <strong>Invoice:</strong> Present your purchase invoice.
            </li>
            <li>
              <strong>Warranty Certificate:</strong> Use the certificate provided with your product.
            </li>
            <li>
              <strong>Warranty Sticker:</strong> Ensure the sticker remains intact.
            </li>
            <li>
              <strong>Electronic Warranty:</strong> Activate your warranty online.
            </li>
          </ul>
        </div>

        {/* Section 5 */}
        <div className='bg-white p-8 rounded-lg shadow-md'>
          <h2 className='text-3xl font-semibold text-gray-800 mb-3'>
            5. Does Stationery's P Offer Warranties for Free Gifts?
          </h2>
          <p className='text-gray-600'>
            Unfortunately, warranty support is not provided for free gifts included with purchases.
          </p>
        </div>

        {/* Important Note */}
        <div className='bg-gray-100 p-6 rounded-xl mt-8'>
          <p className='text-gray-600 font-medium'>Important Note:</p>
          <p className='text-gray-600 mt-2'>
            To ensure smooth processing, please provide clear photos or videos of the defective product. Stationery's P reserves the right to decline claims lacking sufficient evidence.
          </p>
          <p className='text-gray-600 mt-2'>This warranty policy is effective from August 1, 2022.</p>
        </div>
      </section>
    </div>
  );
};

export default WarrantyPolicyPage;
