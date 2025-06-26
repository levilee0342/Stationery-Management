import { useState, useRef, useEffect, useMemo } from 'react'
import { Search, Clock, Flame, Folder } from 'lucide-react'
import { useNavigate, useSearchParams, createSearchParams } from 'react-router-dom'
import { publicPaths } from '~/constance/paths'
import { ProductSearchParams } from '~/types/filter'
import { useAppSelector } from '~/hooks/redux'
import { apiGetTopSearchKeywords } from '~/api/search'
import { apiGetTopUserCategories } from '~/api/search'
import { apiGetTopHotCategories } from '~/api/search'

interface CategoryResponse {
  categoryId: string
  categoryName: string
  icon: string
  bgColor: string
}

export default function SearchWithSuggestions() {
  const [searchParams] = useSearchParams()
  const currentParams = useMemo(() => Object.fromEntries([...searchParams]) as ProductSearchParams, [searchParams])
  const [search, setSearch] = useState<string>('')
  const [showSuggestions, setShowSuggestions] = useState(false)
  const searchRef = useRef<HTMLDivElement>(null)
  const { userData, accessToken } = useAppSelector((state) => state.user)
  const [popularSuggestions, setPopularSuggestions] = useState<string[]>([])
  const [topCategories, setTopCategories] = useState<CategoryResponse[]>([])
  const navigate = useNavigate()

  const recentSearches = useMemo(() => {
    if (!userData?.searchHistory) return []
    return Array.from(userData.searchHistory)
      .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
      .slice(0, 5)
      .map((item) => item.keyword)
  }, [userData])

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
        setShowSuggestions(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  useEffect(() => {
    const fetchPopular = async () => {
      try {
        const keywords = await apiGetTopSearchKeywords()
        setPopularSuggestions(keywords)
        console.log('Từ khóa phổ biến:', keywords)
      } catch (error) {
        console.error('Lỗi khi tải từ khóa phổ biến:', error)
      }
    }

    const fetchCategories = async () => {
      try {
        let categories: CategoryResponse[] = []
        if (userData) {
          categories = (await apiGetTopUserCategories(accessToken || '')).map((cat) => ({
            ...cat,
            icon: cat.icon || '',
            bgColor: cat.bgColor || ''
          }))
          console.log('Danh mục người dùng:', categories)
        } else {
          categories = (await apiGetTopHotCategories()).map((cat) => ({
            ...cat,
            icon: cat.icon || '',
            bgColor: cat.bgColor || ''
          }))
          console.log('Danh mục hot:', categories)
        }
        setTopCategories(
          categories.map((cat) => ({
            ...cat,
            icon: cat.icon || '',
            bgColor: cat.bgColor || ''
          }))
        )
      } catch (error) {
        console.error('Lỗi khi tải danh mục:', error)
      }
    }

    fetchPopular()
    fetchCategories()
  }, [userData])

  const handleSearchProduct = () => {
    const newParams = { ...currentParams, search }
    navigate({
      pathname: publicPaths.PRODUCT,
      search: createSearchParams(newParams).toString()
    })
    setShowSuggestions(false)
  }

  const handleSuggestionClick = (suggestion: string) => {
    setSearch(suggestion)
    const newParams = { ...currentParams, search: suggestion }
    navigate({
      pathname: publicPaths.PRODUCT,
      search: createSearchParams(newParams).toString()
    })
    setShowSuggestions(false)
  }

  const handleCategoryClick = (categoryId: string) => {
    navigate(`/product?categoryId=${categoryId}`)
    setShowSuggestions(false)
  }

  return (
    <div className='relative w-80' ref={searchRef}>
      <div className='relative'>
        <input
          type='text'
          placeholder='Search product...'
          className='w-full pl-10 pr-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-400 focus:border-blue-500   '
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Enter') handleSearchProduct()
          }}
          onFocus={() => setShowSuggestions(true)}
        />
        <button type='button' onClick={handleSearchProduct} className='absolute top-2.5 left-3 text-gray-500 '>
          <Search size={20} />
        </button>
      </div>

      {/* Dropdown Suggestions */}
      {showSuggestions && (
        <div className='absolute top-full left-0 right-0 mt-2 bg-white border border-gray-200 rounded-lg shadow-xl z-50  '>
          {/* Recent */}
          {recentSearches.length > 0 && (
            <div className='p-4 border-b border-gray-200 '>
              <div className='flex items-center text-sm font-semibold text-gray-500 mb-2 '>
                <Clock size={16} className='mr-2' />
                Recent Searches
              </div>
              <div className='space-y-1'>
                {recentSearches.map((item, index) => (
                  <div
                    key={`recent-${index}`}
                    className='px-3 py-2 hover:bg-blue-100  rounded-md cursor-pointer transition-all duration-150'
                    onClick={() => handleSuggestionClick(item)}
                  >
                    <span className='text-gray-800 '>{item}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Popular */}
          {popularSuggestions?.length > 0 && (
            <div className='p-4 border-b border-gray-200 '>
              <div className='flex items-center text-sm font-semibold text-gray-500 mb-2 '>
                <Flame size={16} className='mr-2 text-orange-500' />
                Popular Searches
              </div>
              <div className='flex flex-wrap gap-2'>
                {popularSuggestions.map((item, index) => (
                  <div
                    key={`popular-${index}`}
                    className='px-4 py-1.5 bg-gradient-to-r from-blue-100 to-blue-200 hover:from-blue-200 hover:to-blue-300   rounded-full cursor-pointer text-sm font-medium shadow-sm text-gray-700  transition-all duration-200'
                    onClick={() => handleSuggestionClick(item)}
                  >
                    {item}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Top Categories */}
          {topCategories.length > 0 && (
            <div className='p-4'>
              <div className='flex items-center text-sm font-semibold text-gray-500 mb-2 '>
                <Folder size={16} className='mr-2' />
                {userData ? 'Your Top Categories' : 'Top Categories'}
              </div>
              <div className='grid grid-cols-2 gap-3'>
                {topCategories.map((cat, idx) => (
                  <div
                    key={`cat-${idx}`}
                    className='bg-white rounded-lg shadow-md p-3 text-center hover:shadow-lg transition-transform transform hover:scale-105 active:scale-95 cursor-pointer'
                    aria-label={cat.categoryName}
                    onClick={() => handleCategoryClick(cat.categoryId)}
                  >
                    <div
                      style={{ backgroundColor: cat.bgColor }}
                      className='w-10 h-10 text-white rounded-full flex items-center justify-center mx-auto mb-2 shadow-lg'
                    >
                      {cat.icon}
                    </div>
                    <p className='text-sm font-medium text-gray-700'>{cat.categoryName}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
