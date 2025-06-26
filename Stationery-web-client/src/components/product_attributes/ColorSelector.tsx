import React from 'react'
import { FetchColor } from '~/types/product'

type ColorSelectorProps = {
  colors: FetchColor[]
  selectedColor: string | null
}

const ColorSelector: React.FC<ColorSelectorProps> = ({ colors, selectedColor }) => {
  return (
    <div className='flex text-sm gap-2 mt-2'>
      {colors?.map((item) => (
        <div key={item.colorId}>
          <button
            // onClick={() => handleColorSelect({ colorId: item.colorId, size: item.size })}
            className={`w-5 h-5 rounded-full border-2 transition-all ${
              selectedColor === item.colorId
                ? 'border-black scale-110 ring-1 ring-offset-1 ring-black'
                : 'border-transparent'
            }`}
            style={{ backgroundColor: item.hex }}
          ></button>
        </div>
      ))}
    </div>
  )
}

export default ColorSelector
