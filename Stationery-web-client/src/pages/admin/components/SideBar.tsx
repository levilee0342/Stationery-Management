import { useCallback, useEffect, useRef, useState } from 'react'
import { FiMenu } from 'react-icons/fi'
import { IoChevronDownCircleOutline } from 'react-icons/io5'
import { Link, useLocation } from 'react-router-dom'
import { navItems } from '~/constance/navAdmin'
import { useAppDispatch, useAppSelector } from '~/hooks/redux'
import { sideBarActions } from '~/store/slices/sideBar'
import { NavItem } from '~/types/navAdmin'

const SideBar: React.FC = () => {
  const { isExpanded, isMobileOpen, isHovered } = useAppSelector((state) => state.sideBar)
  const location = useLocation()
  const dispatch = useAppDispatch()
  const [openSubmenu, setOpenSubmenu] = useState<{
    type: 'main'
    index: number
  } | null>(null)
  const [subMenuHeight, setSubMenuHeight] = useState<Record<string, number>>({})
  const subMenuRefs = useRef<Record<string, HTMLDivElement | null>>({})
  const handleToggle = () => {
    dispatch(sideBarActions.toggleSidebar())
  }
  // const isActive = (path: string) => location.pathname === path;
  const isActive = useCallback((path: string) => location.pathname === path, [location.pathname])

  useEffect(() => {
    // khi chuyển trang thì sẽ kiểm tra xem path hiện tại có nằm trong subItems của navItems không
    let submenuMatched = false
    ;['main'].forEach((menuType) => {
      const items = navItems
      items.forEach((nav, index) => {
        if (nav.subItems) {
          nav.subItems.forEach((subItem) => {
            if (isActive(subItem.path)) {
              setOpenSubmenu({
                type: menuType as 'main',
                index
              })
              submenuMatched = true
            }
          })
        }
      })
    })

    if (!submenuMatched) {
      setOpenSubmenu(null)
    }
  }, [location, isActive])

  useEffect(() => {
    // lấy chiều cao của menu để render subitem
    if (openSubmenu !== null) {
      const key = `${openSubmenu.type}-${openSubmenu.index}`
      if (subMenuRefs.current[key]) {
        setSubMenuHeight((prevHeights) => ({
          ...prevHeights,
          [key]: subMenuRefs.current[key]?.scrollHeight || 0
        }))
      }
    }
  }, [openSubmenu])
  const handleSubmenuToggle = (index: number, menuType: 'main') => {
    setOpenSubmenu((prevOpenSubmenu) => {
      if (prevOpenSubmenu && prevOpenSubmenu.type === menuType && prevOpenSubmenu.index === index) {
        return null
      }
      return { type: menuType, index }
    })
  }

  const renderMenuItems = (items: NavItem[], menuType: 'main') => (
    <ul className='flex flex-col gap-4'>
      {items.map((nav, index) => (
        <li key={nav.name}>
          {nav.subItems ? (
            <button
              onClick={() => handleSubmenuToggle(index, menuType)}
              className={`menu-item group ${
                openSubmenu?.type === menuType && openSubmenu?.index === index
                  ? 'menu-item-active'
                  : 'menu-item-inactive'
              } cursor-pointer ${!isExpanded && !isHovered ? 'lg:justify-center' : 'lg:justify-start'}`}
            >
              <span
                className={`menu-item-icon-size  ${
                  openSubmenu?.type === menuType && openSubmenu?.index === index
                    ? 'menu-item-icon-active'
                    : 'menu-item-icon-inactive'
                }`}
              >
                {nav.icon}
              </span>
              {(isExpanded || isHovered || isMobileOpen) && <span className='menu-item-text'>{nav.name}</span>}
              {(isExpanded || isHovered || isMobileOpen) && (
                <IoChevronDownCircleOutline
                  className={`ml-auto w-5 h-5 transition-transform duration-200 ${
                    openSubmenu?.type === menuType && openSubmenu?.index === index ? 'rotate-180 text-brand-500' : ''
                  }`}
                />
              )}
            </button>
          ) : (
            nav.path && (
              <Link
                to={nav.path}
                className={`menu-item group ${isActive(nav.path) ? 'menu-item-active' : 'menu-item-inactive'} ${!isExpanded && !isHovered ? 'lg:justify-center' : 'lg:justify-start'}`}
              >
                <span
                  className={`menu-item-icon-size ${
                    isActive(nav.path) ? 'menu-item-icon-active' : 'menu-item-icon-inactive'
                  }`}
                >
                  {nav.icon}
                </span>
                {(isExpanded || isHovered || isMobileOpen) && <span className='menu-item-text'>{nav.name}</span>}
              </Link>
            )
          )}
          {nav.subItems && (isExpanded || isHovered || isMobileOpen) && (
            <div
              ref={(el) => {
                subMenuRefs.current[`${menuType}-${index}`] = el
              }}
              className='overflow-hidden transition-all duration-300'
              style={{
                height:
                  openSubmenu?.type === menuType && openSubmenu?.index === index
                    ? `${subMenuHeight[`${menuType}-${index}`]}px`
                    : '0px'
              }}
            >
              <ul className='mt-2 space-y-1 ml-9'>
                {nav.subItems.map((subItem) => (
                  <li key={subItem.name}>
                    <Link
                      to={subItem.path}
                      className={`menu-dropdown-item ${
                        isActive(subItem.path) ? 'menu-dropdown-item-active' : 'menu-dropdown-item-inactive'
                      }`}
                    >
                      {subItem.name}
                    </Link>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </li>
      ))}
    </ul>
  )
  return (
    <aside
      className={`fixed mt-16 flex flex-col lg:mt-0 top-0 px-5 left-0 bg-white text-gray-900 h-screen transition-all duration-300 ease-in-out z-50 border-r border-gray-200 
        ${isExpanded || isMobileOpen ? 'w-[290px]' : isHovered ? 'w-[290px]' : 'w-[90px]'}
        ${isMobileOpen ? 'translate-x-0' : '-translate-x-full'}
        lg:translate-x-0`}
      onMouseEnter={() => !isExpanded && dispatch(sideBarActions.setIsHovered(true))}
      onMouseLeave={() => dispatch(sideBarActions.setIsHovered(false))}
    >
      <div className={`py-8 flex ${!isExpanded && !isHovered ? 'lg:justify-center' : 'justify-start'}`}>
        <div className='flex items-center justify-between flex-1'>
          <Link to='/'>
            {isExpanded || isHovered || isMobileOpen ? (
              <img
                className='rounded-full w-10 h-10'
                src='/images/logo_stationery.svg'
                alt='Logo'
                width={150}
                height={40}
              />
            ) : (
              <img
                className='rounded-full w-10 h-10'
                src='/images/logo_stationery.svg'
                alt='Logo'
                width={32}
                height={32}
              />
            )}
          </Link>
          <button
            className={`${!isExpanded && !isHovered && 'hidden'} border text-xl p-2 rounded-md`}
            onClick={handleToggle}
          >
            <FiMenu />
          </button>
        </div>
      </div>
      <div className='flex flex-col overflow-y-auto duration-300 ease-linear no-scrollbar'>
        <nav className='mb-6'>
          <div className='flex flex-col gap-4'>
            <div>{renderMenuItems(navItems, 'main')}</div>
          </div>
        </nav>
      </div>
    </aside>
  )
}
export default SideBar
