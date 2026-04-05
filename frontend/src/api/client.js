import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Accept': 'application/json'
  }
})

// Admin auth interceptor
api.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token && config.url.startsWith('/admin')) {
    config.headers.Authorization = token
  }
  return config
})

export default api
