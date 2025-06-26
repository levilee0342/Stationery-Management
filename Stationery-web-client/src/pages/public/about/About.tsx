import React from 'react'
import Banner from './Banner'
import Introduction from './Introduction'
import Partners from './Partners'
import Timeline from './Timeline'
import Certificates from './Certificates'

const About: React.FC = () => {
  const partnersData = [
    {
      id: 1,
      logoUrl: 'https://racevietnam.com/Thumbnail/ExtraLarge/Upload/20190414/8bac8ea0b3a5c83d760b38ef604243bb.png',
      name: 'PTIT'
    },
    { id: 2, logoUrl: 'https://www.uit.edu.vn/sites/vi/files/images/Logos/Logo_UIT_Web_Transparent.png', name: 'UIT' },
    {
      id: 3,
      logoUrl:
        'https://th.bing.com/th/id/R.544cd767a1231fb609eba23b17662e7a?rik=MQcNOfjSMi3wOg&riu=http%3a%2f%2fdca.dee.hcmut.edu.vn%2fwp-content%2fuploads%2f2019%2f12%2flogo_bachkhoa.png&ehk=TNFDL2P6BPNe4vcQtECZzKY7iKcLjLheg95Y%2fG9vHcc%3d&risl=&pid=ImgRaw&r=0',
      name: 'HCMTU'
    },
    { id: 4, logoUrl: 'https://giaoduc247.vn/uploads/082021/images/UTE.jpeg', name: 'UTE' },
    { id: 5, logoUrl: 'https://rubee.com.vn/wp-content/uploads/2021/05/Logo-HUTECH.jpg', name: 'HUTECH' },
    {
      id: 6,
      logoUrl: 'https://studyenglishinaustralia.com/db/files/images/content/aa_uni28740-9271203.jpg',
      name: 'RMIT'
    },
    {
      id: 7,
      logoUrl: 'https://th.bing.com/th/id/OIP.oO71DGKAwvMbzUdQiNK0ygHaL3?pid=ImgDet&w=474&h=759&rs=1',
      name: 'HUB'
    },
    {
      id: 8,
      logoUrl:
        'https://www.pacisoft.vn/wp-content/uploads/2018/12/Logo_Tr%C6%B0%E1%BB%9Dng_%C4%90%E1%BA%A1i_H%E1%BB%8Dc_T%C3%A0i_Ch%C3%ADnh_Marketing-1.png',
      name: 'UFM'
    },
    { id: 9, logoUrl: 'https://cdn.nhansu.vn/uploads/images/A0A66343/logo/2023-07/logo.jpg', name: 'HUFLIT' },
    {
      id: 10,
      logoUrl: 'https://inkythuatso.com/uploads/images/2021/12/logo-khoa-hoc-tu-nhien-inkythuatso-01-25-16-11-19.jpg',
      name: 'HUS'
    }
  ]

  const timelineData = [
    { year: 'March 5, 2020', description: 'Opened our first stationery store.' },
    {
      year: 'September 1, 2020',
      description: 'Became an official partner of the Posts and Telecommunications Institute of Technology (PTIT).'
    },
    { year: 'March 1, 2022', description: 'Relocated our store to PTIT’s main campus.' },
    { year: 'October 1, 2022', description: 'Expanded partnerships with our first three universities.' },
    { year: 'May 1, 2024', description: 'Proudly partnered with over 10 prestigious universities.' },
    {
      year: 'February 1, 2025',
      description: 'Honored as one of the most reputable stationery stores in Ho Chi Minh City.'
    }
  ]

  const certificatesData = [
    {
      id: 1,
      imageUrl: 'https://thiepmung.com/uploads/worigin/2022/04/16/tao-bang-khen-thanh-tich-truc-tuyen_f39de.jpg',
      title: 'Quality Certification'
    },
    {
      id: 2,
      imageUrl: 'https://th.bing.com/th/id/OIP.jbg_FKwXra-yLM6WvFvkRwHaHa?rs=1&pid=ImgDetMain',
      title: 'Trusted Partner Award'
    },
    {
      id: 3,
      imageUrl: 'https://bizweb.dktcdn.net/100/082/575/files/dia-chi-nhan-in-bang-khen-gia-re.jpg?v=1557714910755',
      title: 'Top Business Partner Recognition'
    },
    {
      id: 4,
      imageUrl: 'https://noithatvinai.com/wp-content/uploads/Bang-khen-bo-lao-dong-thuong-binh-xa-hoi-1.jpg',
      title: 'Achievement Certificate'
    }
  ]

  return (
    <div className='mt-16'>
      <Banner
        imageUrl='https://vanphongpham247.vn/wp-content/uploads/2022/03/van-phong-pham-247.jpg'
        altText='About our stationery store'
      />
      <Introduction
        imageUrl='/images/logo_stationery.svg'
        title='About Our Shop'
        description='We are a leading stationery store committed to delivering high-quality, diverse, and reliable office supplies. Our extensive product catalog includes pens, notebooks, printing paper, study essentials, office equipment, and accessories. We source products from trusted brands to ensure durability and efficiency. 
        Our dedicated and experienced staff are always ready to provide personalized recommendations for students, teachers, office professionals, and businesses. We also offer **fast delivery services, competitive prices, and excellent customer support** to enhance your shopping experience. Whether you need everyday office essentials or specialized stationery, we’ve got you covered!'
      />
      <Partners partners={partnersData} />
      <Timeline items={timelineData} />
      <Certificates certificates={certificatesData} />
    </div>
  )
}

export default About
