import axios from 'axios'

export const http = axios.create({
  baseURL: import.meta.env.VITE_SERVER_URL,
  timeout: 20000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Define JWT payload structure
// interface JwtPayload {
//   exp: number
// }

// Add a request interceptor
// http.interceptors.request.use(
//   async (config: AxiosRequestConfig) => {
//     const authHeader = config.headers?.Authorization as string | undefined
//     const accessToken = authHeader?.startsWith('Bearer') ? authHeader.split(' ')[1] : null

//     if (!accessToken) return config

//     const decodedAccessToken = jwtDecode<JwtPayload>(accessToken)
//     const currentTime = Date.now() / 1000

//     if (decodedAccessToken.exp < currentTime) {
//       const dispatch = store.dispatch
//       dispatch(appActions.toggleModal({ isShowModal: false, childrenModal: null }))

//       try {
//         const res = await apiRefreshToken()
//         if (res?.success) {
//           config.headers = config.headers || {}
//           config.headers.Authorization = `Bearer ${res.accessToken}`
//           dispatch(userActions.setAccessToken({ accessToken: res.accessToken }))
//         } else {
//           await Swal.fire('Oops!', 'Đăng nhập đã hết hạn vui lòng đăng nhập lại', 'info').then(() => {
//             dispatch(userActions.logout())
//             window.location.href = '/login'
//           })
//         }
//       } catch (error) {
//         console.error('Error refreshing token:', error)
//       }
//     }

//     return config
//   },
//   (error) => {
//     return Promise.reject(error)
//   }
// )

// Add a response interceptor
// http.interceptors.response.use(
//   (response: AxiosResponse) => response,
//   (error) => Promise.reject(error)
// )
