import { PacmanLoader } from 'react-spinners'

function PacmanLoading() {
  return (
    <div className='flex items-center justify-center w-screen h-screen'>
      <PacmanLoader color='#2563EB' />
    </div>
  )
}

export default PacmanLoading
