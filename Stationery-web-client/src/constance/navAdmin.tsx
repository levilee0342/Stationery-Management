import { NavItem } from '~/types/navAdmin'
import { FaShoppingCart, FaUsers } from 'react-icons/fa'
import { TbCategoryFilled } from 'react-icons/tb'
import { BsTicketPerforatedFill } from 'react-icons/bs'
import { RiProductHuntLine } from 'react-icons/ri'
import { adminPaths } from './paths'
import { IoHome } from 'react-icons/io5'
import { IoMdColorFill } from 'react-icons/io'
import { BiFontSize } from 'react-icons/bi'

const navItems: NavItem[] = [
  {
    icon: <IoHome />,
    name: 'Dashboard',
    path: '/admin'
  },
  {
    icon: <FaUsers />,
    name: 'Users',
    path: '/admin/users'
  },
  {
    icon: <TbCategoryFilled />,
    name: 'Product Categories',
    path: '/admin/product-categories'
  },
  {
    icon: <IoMdColorFill />,
    name: 'Colors',
    path: '/admin/product-colors'
  },
  {
    icon: <BiFontSize />,
    name: 'Sizes',
    path: '/admin/product-sizes'
  },
  {
    icon: <RiProductHuntLine />,
    name: 'Products',
    path: '/admin/products'
  },
  {
    icon: <FaShoppingCart />,
    name: 'Orders',
    path: '/admin/orders'
  },
  {
    icon: <BsTicketPerforatedFill />,
    name: 'Vouchers',
    path: adminPaths.VOUCHER
  }
]
export { navItems }
