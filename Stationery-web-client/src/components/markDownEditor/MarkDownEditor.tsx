import { Editor } from '@tinymce/tinymce-react'
import { memo, useEffect, useState } from 'react'

interface MarkDownEditorProps {
  value?: string
  changeValue: (value: string) => void
  height: number
  error?: string
  iconRequire?: boolean
  classParent?: string
}

function MarkDownEditor({ value = '', changeValue, height, error, classParent }: MarkDownEditorProps) {
  const [localValue, setLocalValue] = useState<string>(value)

  useEffect(() => {
    setLocalValue(value)
  }, [value])

  return (
    <div className={classParent}>
      <div className={`${error ? 'border border-red-500 ring-1 ring-red-500 rounded-lg' : ''}`}>
        <Editor
          apiKey={import.meta.env.VITE_API_KEY_TINY as string}
          value={localValue}
          init={{
            height,
            menubar: true,
            plugins: [
              'advlist',
              'autolink',
              'lists',
              'link',
              'image',
              'charmap',
              'preview',
              'anchor',
              'searchreplace',
              'visualblocks',
              'code',
              'fullscreen',
              'insertdatetime',
              'media',
              'table',
              'code',
              'help',
              'wordcount'
            ],
            toolbar:
              'undo redo | blocks | ' +
              'bold italic forecolor | alignleft aligncenter ' +
              'alignright alignjustify | bullist numlist outdent indent | ' +
              'removeformat | help',
            content_style: 'body { font-family:Helvetica,Arial,sans-serif; font-size:14px }'
          }}
          onEditorChange={(content) => {
            setLocalValue(content)
            changeValue(content)
          }}
        />
      </div>
      {error && <p className='text-red-500 text-sm'>{error}</p>}
    </div>
  )
}

export default memo(MarkDownEditor)
