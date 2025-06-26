type NavItem = {
  name: string
  icon: React.ReactNode
  path?: string
  subItems?: {
    name: string
    path: string
    pro?: boolean
    new?: boolean
  }[]
}
export type { NavItem }
