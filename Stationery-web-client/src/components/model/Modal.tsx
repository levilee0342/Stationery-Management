// Modal.tsx
import React, { useEffect } from 'react'
import { useAppDispatch } from '~/hooks/redux'
import { modalActions } from '~/store/slices/modal'

interface ModalProps {
  children: React.ReactNode
  isOpen: boolean
}

const Modal: React.FC<ModalProps> = ({ children, isOpen }) => {
  const dispatch = useAppDispatch()
  // Handle ESC key press to close modal
  function onClose() {
    dispatch(modalActions.toggleModal({ childrenModal: null, isOpenModal: false }))
  }
  useEffect(() => {
    const handleEsc = (event: KeyboardEvent) => {
      if (event.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', handleEsc)
    return () => window.removeEventListener('keydown', handleEsc)
  }, [])

  if (!isOpen) return null

  return (
    <div
      className='fixed inset-0 z-[9999] overflow-y-auto'
      onClick={() => {
        // e.stopPropagation()
        onClose()
      }}
    >
      {/* Overlay */}
      <div className='fixed  z-[1] inset-0 bg-black/30 transition-opacity duration-300' onClick={onClose} />

      {/* Modal Content */}
      <div
        className='z-[999] min-h-screen relative transform transition-all duration-300 ease-in-out 
            scale-95 opacity-0 animate-modal-enter flex justify-center items-center'
      >
        {/* Content */}
        {children}
      </div>
    </div>
  )
}

export default Modal
