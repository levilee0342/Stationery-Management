import { FaInfoCircle, FaSignOutAlt, FaUserCog } from 'react-icons/fa'

const dropDownProfile = [
  {
    id: 1,
    name: 'Profile',
    to: 'user/profile',
    icon: <FaInfoCircle className='mr-3 text-lg text-blue-500' />
  },
  {
    id: 2,
    name: 'Admin',
    to: 'admin',
    icon: <FaUserCog className='mr-3 text-lg text-green-500' />
  },
  {
    id: 3,
    name: 'Logout',
    onClick: true,
    style: 'text-red-600 hover:bg-red-50',
    styleParent: 'pt-1 border-t border-1 border-gray-200',
    icon: <FaSignOutAlt className='mr-3 text-lg' />
  }
]
export { dropDownProfile }
