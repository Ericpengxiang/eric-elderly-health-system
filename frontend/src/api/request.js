import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 30000,
})

request.interceptors.request.use((config) => {
  const user = useUserStore()
  if (user.accessToken) {
    config.headers.Authorization = `Bearer ${user.accessToken}`
  }
  return config
})

request.interceptors.response.use(
  (res) => {
    const payload = res.data
    if (payload && typeof payload.code === 'number' && payload.code !== 200) {
      ElMessage.error(payload.message || '请求失败')
      return Promise.reject(new Error(payload.message || 'error'))
    }
    return payload
  },
  async (err) => {
    const status = err.response?.status
    const original = err.config
    if (status === 401 && original && !original._retry) {
      original._retry = true
      const user = useUserStore()
      try {
        if (user.refreshToken) {
          const { data } = await axios.post('/api/auth/refresh', {
            refreshToken: user.refreshToken,
          })
          if (data?.code === 200 && data.data?.accessToken) {
            user.setTokens(data.data.accessToken, data.data.refreshToken)
            original.headers.Authorization = `Bearer ${data.data.accessToken}`
            return request(original)
          }
        }
      } catch (e) {
        user.logout()
        router.push({ name: 'login' })
      }
      user.logout()
      router.push({ name: 'login' })
      return Promise.reject(err)
    }
    ElMessage.error(err.response?.data?.message || err.message || '网络错误')
    return Promise.reject(err)
  }
)

export default request
