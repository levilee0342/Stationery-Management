import { motion, HTMLMotionProps } from 'framer-motion';

interface ButtonProps extends HTMLMotionProps<'button'> {
  variant?: 'primary' | 'secondary' | 'danger'
}

const Button: React.FC<ButtonProps> = ({ children, variant = 'primary', className, ...props }) => {
  const baseStyles = 'px-4 py-2 rounded-lg font-semibold transition-all duration-300'
  const variants = {
    primary: 'bg-blue-500 text-white hover:bg-blue-600 shadow-md',
    secondary: 'bg-gray-200 text-gray-700 hover:bg-gray-300 shadow-md',
    danger: 'bg-red-500 text-white hover:bg-red-600 shadow-md'
  }

  return (
    <motion.button
      whileHover={{ scale: 1.05 }}
      whileTap={{ scale: 0.95 }}
      className={`${baseStyles} ${variants[variant]} ${className || ''}`}
      {...props}
    >
      {children}
    </motion.button>
  )
}

export default Button
