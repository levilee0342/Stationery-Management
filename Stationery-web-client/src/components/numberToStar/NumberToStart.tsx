import { FaStar, FaRegStar, FaRegStarHalfStroke } from 'react-icons/fa6'

type Props = {
  number: number
}

const NumberToStart: React.FC<Props> = ({ number }) => {
  if (!number) {
    return (
      <>
        {Array(5)
          .fill(null)
          .map((_, i) => {
            return <FaRegStar key={i} className='text-yellow-300' />
          })}
      </>
    )
  }

  number = Number(number)
  const stars = []

  for (let i = 1; i <= Math.floor(number); i++) {
    stars.push(<FaStar key={Math.random()} className='text-yellow-300' />)
  }

  if (number % 1 !== 0) {
    stars.push(<FaRegStarHalfStroke key={Math.random()} className='text-yellow-300' />)
    number = Math.ceil(number)
  }

  while (stars.length < 5) {
    stars.push(<FaRegStar key={Math.random()} className='text-yellow-300' />)
  }

  return <>{stars}</>
}

export default NumberToStart
