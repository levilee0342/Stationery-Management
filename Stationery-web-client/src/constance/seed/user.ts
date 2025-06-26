type User = {
  id: number
  avatar?: string
  firstName: string
  lastName: string
  email: string
  phone: string
  role: string
  active: boolean
  dob: string
}

export const fakeUsers: User[] = [
  {
    id: 1,
    avatar:
      'https://scontent.fsgn8-3.fna.fbcdn.net/v/t39.30808-6/487855764_1461527871897420_8022113998614909219_n.jpg?_nc_cat=109&ccb=1-7&_nc_sid=6ee11a&_nc_eui2=AeFlOPqFIh0JZLUgjh3pc24gPxl2AgFALzs_GXYCAUAvO8QGbRiAUtP0l-gdyh_SWsVSSTHmsQwWeXZvBy70NBfq&_nc_ohc=clA5uv7pQVYQ7kNvwEj2Op0&_nc_oc=AdnJf-x-xyKnRNsk6TqSCP5Cgx8gHsKLM_22vxDevwI571lx09fF5t2YHeL2piKE5CB3IuMCzVFrId0CGn25P-nX&_nc_zt=23&_nc_ht=scontent.fsgn8-3.fna&_nc_gid=IDI7nzxc0h-DgcHx15564g&oh=00_AfEnnr7YQoLOwzepDDl8VuYdMAslJdZ_UdS5UdErvqOgfg&oe=680AB092',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    phone: '0969895549',
    role: 'Admin',
    active: true,
    dob: '1990-05-15' // Thêm ngày sinh
  },
  {
    id: 2,
    avatar: 'https://scontent.fsgn8-3.fna.fbcdn.net/v/t39.30808-6/487855764_1461527871897420_8022113998614909219_n.jpg',
    firstName: 'Jane',
    lastName: 'Smith',
    email: 'jane.smith@example.com',
    phone: '0969895549',
    role: 'User',
    active: false,
    dob: '1985-07-22' // Thêm ngày sinh
  },
  {
    id: 3,
    avatar: 'https://scontent.fsgn8-3.fna.fbcdn.net/v/t39.30808-6/487855764_1461527871897420_8022113998614909219_n.jpg',
    firstName: 'Alice',
    lastName: 'Brown',
    email: 'alice.brown@example.com',
    phone: '0969895549',
    role: 'Moderator',
    active: true,
    dob: '1993-12-09' // Thêm ngày sinh
  }
]
