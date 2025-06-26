import React from 'react'
import ServiceHeader from './ServiceHeader'
import ServiceList from './ServiceList'
import TestimonialSection from './TestimonialSection'
import ContactSection from './ContactSection'
import Commit from './Commit'

const Service: React.FC = () => {
  return (
    <div className='min-h-screen bg-gradient-to-b from-gray-50 to-white overflow-hidden mt-16'>
      <ServiceHeader />
      <ServiceList />
      <Commit />
      <TestimonialSection />
      <ContactSection />
    </div>
  );
};

export default Service;