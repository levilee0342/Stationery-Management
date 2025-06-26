export const DeleteModal = ({
  isOpen,
  onClose,
  onConfirm
}: {
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
}) => {
  if (!isOpen) return null

  return (
    <div className='fixed inset-0 bg-black bg-opacity-30 flex justify-center items-center z-50'>
      <div className='bg-white p-6 rounded-lg w-[400px]'>
        <h2 className='text-xl font-semibold mb-4'>Confirm Deletion</h2>
        <p>Are you sure you want to delete this product categories?</p>
        <div className='flex justify-end gap-2 mt-4'>
          <button onClick={onClose} className='bg-gray-400 text-white px-4 py-2 rounded'>
            Cancel
          </button>
          <button onClick={onConfirm} className='bg-red-600 text-white px-4 py-2 rounded'>
            Delete
          </button>
        </div>
      </div>
    </div>
  )
}
