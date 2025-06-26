import { useEffect, useMemo, useState } from 'react'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { modalActions } from '~/store/slices/modal'
import { FaEdit, FaSearch, FaEye, FaLock, FaLockOpen } from 'react-icons/fa'
import UserModal from './modal/UserModal'
import { DetailModal } from './modal/DetailModal'
import Select from 'react-select'
import { showToastError, showToastSuccess } from '~/utils/alert'
import { apiBlockUser, apiGetAllUsers } from '~/api/users'
import { User, UserSearchParams } from '~/types/user'
import { AxiosError } from 'axios'
import { useSearchParams } from 'react-router-dom'
import Pagination from '~/components/pagination/Pagination'

const roleOptions = [
  { value: '111', label: 'Admin' },
  { value: '112', label: 'User' },
  { value: '113', label: 'Department' }
]

const UserManagement = () => {
  const [users, setUsers] = useState<User[]>()
  const [totalPageCount, setTotalPageCount] = useState(0)
  const [searchParams, setSearchParams] = useSearchParams()
  const currentParams = useMemo(() => Object.fromEntries([...searchParams]) as UserSearchParams, [searchParams])
  const { accessToken, userData } = useAppSelector((state) => state.user)
  const dispatch = useAppDispatch()
  console.log(accessToken)

  const closeModal = () => {
    dispatch(modalActions.toggleModal({ isOpenModal: false, childrenModal: null }))
  }

  const handleEditUser = (user: User) => {
    dispatch(
      modalActions.toggleModal({
        isOpenModal: true,
        childrenModal: (
          <UserModal
            isOpen={true}
            isEdit={true}
            user={user}
            onClose={closeModal}
            setUsers={setUsers}
            accessToken={accessToken || ''}
          />
        )
      })
    )
  }

  const handleViewUserDetails = (user: User) => {
    dispatch(
      modalActions.toggleModal({
        isOpenModal: true,
        childrenModal: <DetailModal isOpen={true} user={user} onClose={closeModal} />
      })
    )
  }

  const getAllUsers = async (currentParams: UserSearchParams) => {
    try {
      if (!accessToken) {
        showToastError('Please login to perform this action.')
        return
      }
      const { page, search, roleId } = currentParams
      const response = await apiGetAllUsers({
        page: page || '0',
        search: search,
        limit: '10',
        roleId,
        accessToken
      })
      if (response.code == 200) {
        setUsers(response.result.content)
        setTotalPageCount(response.result.page.totalPages)
      } else {
        showToastError(response.message || response.error)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }
  const updateBlockStatus = async (userId: string) => {
    if (!accessToken) {
      showToastError('Please login to perform this action.')
      return
    }
    try {
      const response = await apiBlockUser({
        userId,
        accessToken
      })
      if (response.code == 200) {
        setUsers((prev) => prev?.map((u) => (u.userId === userId ? { ...u, block: !u.block } : u)))
        showToastSuccess('User status updated successfully.')
      } else {
        showToastError(response.message || response.error)
      }
    } catch (error) {
      if (error instanceof Error || error instanceof AxiosError) {
        showToastError(error.message)
      }
    }
  }

  const renderRoleBadge = (role: string) => {
    const base = 'px-2 py-1 rounded-full text-xs font-semibold'
    switch (role.toLowerCase()) {
      case 'admin':
        return <span className={`${base} bg-red-100 text-red-700`}>ADMIN</span>
      case 'user':
        return <span className={`${base} bg-blue-100 text-blue-700`}>USER</span>
      case 'department':
        return <span className={`${base} bg-green-100 text-green-700`}>DEPARTMENT</span>
      default:
        return <span className={`${base} bg-gray-100 text-gray-700`}>UNKNOWN</span>
    }
  }

  useEffect(() => {
    getAllUsers(currentParams)
  }, [currentParams])
  console.log(users)
  return (
    <div className='p-6 w-full mx-auto bg-white shadow-lg rounded-xl'>
      {/* Header Section */}
      <div className='flex justify-between items-center mb-6'>
        <h1 className='text-3xl font-semibold text-blue-800'>User Management</h1>
      </div>

      {/* Filters */}
      <div className='flex gap-4 mb-6'>
        <div className='relative w-1/3'>
          <span className='absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none text-gray-400'>
            <FaSearch />
          </span>
          <input
            type='text'
            placeholder='Search by name or email...'
            className='pl-10 pr-4 py-2 w-full border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-300'
            value={currentParams.search || ''}
            onChange={(e) => {
              if (e.target.value.trim().length === 0) {
                setSearchParams(() => {
                  const newParams = { ...currentParams }
                  delete newParams.search
                  return new URLSearchParams(newParams as Record<string, string>)
                })
              } else {
                setSearchParams(() => ({ ...currentParams, page: '1', search: e.target.value }))
              }
            }}
          />
        </div>

        <div className='w-1/4'>
          <Select
            options={roleOptions}
            defaultValue={roleOptions[0]}
            className='basic-single'
            classNamePrefix='select'
            isSearchable={true}
            isClearable={true}
            value={roleOptions.find((item) => item.value === currentParams.roleId) || null}
            onChange={(data) => {
              if (data) {
                setSearchParams(() => ({ ...currentParams, roleId: data.value }))
              } else {
                setSearchParams(() => {
                  const newParams = { ...currentParams }
                  delete newParams.roleId
                  return new URLSearchParams(newParams as Record<string, string>)
                })
              }
            }}
          />
        </div>
      </div>

      {/* Table Section */}
      <div className='overflow-x-auto rounded-xl shadow-lg'>
        <table className='w-full border-collapse border border-blue-200'>
          <thead>
            <tr className='bg-blue-600 text-white text-left'>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>#</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>First Name</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Last Name</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Email</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Dob</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Phone</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Role</th>
              <th className='px-4 py-3 font-medium text-sm uppercase tracking-wider'>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users?.map((user, index) => {
              const isCurrentUser = user.userId === userData?.userId
              const rowClass = user.block ? 'opacity-50' : ''
              const highlightClass = isCurrentUser ? 'bg-yellow-50' : ''

              return (
                <tr
                  key={user.userId}
                  className={`border-b border-teal-200 hover:bg-teal-50 transition-colors ${rowClass} ${highlightClass}`}
                >
                  <td className='px-4 py-3'>{index + 1}</td>
                  <td className='px-4 py-3 font-medium'>{user.firstName}</td>
                  <td className='px-4 py-3 font-medium'>{user.lastName}</td>
                  <td className='px-4 py-3'>{user.email}</td>
                  <td className='px-4 py-3'>
                    {user.dob ? user.dob : <span className='text-gray-400 italic'>Chưa cập nhật</span>}
                  </td>
                  <td className='px-4 py-3'>
                    {user.phone ? user.phone : <span className='text-gray-400 italic'>Chưa cập nhật</span>}
                  </td>
                  <td className='px-4 py-3'>{renderRoleBadge(user?.role?.roleName || '')}</td>
                  <td className='px-4 py-3 flex gap-2'>
                    <button
                      className='bg-blue-500 text-white p-2 rounded-lg hover:bg-blue-600'
                      onClick={() => handleViewUserDetails(user)}
                    >
                      <FaEye />
                    </button>
                    <button
                      className='bg-yellow-400 text-white p-2 rounded-lg hover:bg-yellow-500'
                      onClick={() => handleEditUser(user)}
                    >
                      <FaEdit />
                    </button>
                    <button
                      className={`p-2 rounded-lg transition-colors ${
                        user.block
                          ? 'bg-red-100 text-red-600 hover:bg-red-200'
                          : 'bg-green-100 text-green-600 hover:bg-green-200'
                      }`}
                      onClick={() => updateBlockStatus(user.userId)}
                    >
                      {user.block ? <FaLock /> : <FaLockOpen />}
                    </button>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
        <Pagination totalPageCount={totalPageCount} />
      </div>
    </div>
  )
}

export default UserManagement
