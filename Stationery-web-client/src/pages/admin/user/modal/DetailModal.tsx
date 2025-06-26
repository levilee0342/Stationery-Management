import { TextInput } from '~/components/styles/TextInput'
import { User } from '~/types/user'

export const DetailModal = ({ isOpen, onClose, user }: { isOpen: boolean; onClose: () => void; user: User | null }) => {
  if (!isOpen || !user) return null

  return (
    <div className='fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50'>
      <div
        className='bg-white p-8 rounded-xl shadow-xl w-[500px] md:w-[600px] max-w-lg'
        onClick={(e) => {
          e.stopPropagation()
        }}
      >
        <h2 className='text-3xl font-semibold text-blue-600 mb-6 text-center'>User Details</h2>

        {/* Avatar Section */}
        <div className='flex justify-center mb-6'>
          {user.avatar ? (
            <img
              src={user.avatar}
              alt='Avatar'
              className='w-32 h-32 rounded-full object-cover border-4 border-blue-500 shadow-lg'
            />
          ) : (
            <div className='w-32 h-32 rounded-full bg-gray-300 flex justify-center items-center text-2xl font-semibold text-blue-600'>
              {user.firstName[0]}
              {user.lastName[0]}
            </div>
          )}
        </div>

        {/* User Information Fields */}
        <div className='grid grid-cols-1 sm:grid-cols-2 gap-4'>
          <TextInput name='firstName' label='First Name' value={user.firstName} readOnly />
          <TextInput name='lastName' label='Last Name' value={user.lastName} readOnly />
          <TextInput name='email' label='Email' value={user.email} readOnly />
          <TextInput name='phone' label='Phone' value={user.phone} readOnly />
          <TextInput name='role' label='Role' value={user.role.roleId} readOnly />
          {user.dob && (
            <TextInput name='dob' label='Date of Birth' value={new Date(user.dob).toLocaleDateString()} readOnly />
          )}
        </div>

        {/* Close Button */}
        <div className='mt-6 text-right'>
          <button
            onClick={onClose}
            className='bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition'
          >
            Close
          </button>
        </div>
      </div>
    </div>
  )
}
