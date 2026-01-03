import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  withCredentials: true, // 支持Session
  timeout: 10000
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
  },
  error => {
    if (error.response && error.response.status === 401) {
      // 未登录，跳转到登录页
      window.location.href = '/admin/login'
    }
    return Promise.reject(error)
  }
)

// 用户相关API
export const userApi = {
  register: (data) => api.post('/login/register', data),
  login: (data) => api.post('/login/login', data),
  logout: () => api.post('/login/logout'),
  getCurrentUser: () => api.get('/login/current'),
  getUserInfo: () => api.get('/user/info'),
  updateUserInfo: (data) => api.put('/user/info', data),
  changePassword: (data) => api.put('/user/password', data)
}

// 博客相关API
export const blogApi = {
  getList: (params) => api.get('/blog/list', { params }),
  getDetail: (id) => api.get(`/blog/${id}`),
  getAdminList: (params) => api.get('/blog/admin/list', { params }),
  getAdminDetail: (id) => api.get(`/blog/admin/${id}`),
  save: (data) => api.post('/blog/save', data),
  update: (id, data) => api.put(`/blog/${id}`, data),
  delete: (id) => api.delete(`/blog/${id}`)
}

// 分类相关API
export const typeApi = {
  getList: () => api.get('/type/list'),
  getAdminList: () => api.get('/type/admin/list'),
  save: (data) => api.post('/type/save', data),
  update: (id, data) => api.put(`/type/${id}`, data),
  delete: (id) => api.delete(`/type/${id}`)
}

// 标签相关API
export const tagApi = {
  getList: () => api.get('/tag/list'),
  getAdminList: () => api.get('/tag/admin/list'),
  save: (data) => api.post('/tag/save', data),
  update: (id, data) => api.put(`/tag/${id}`, data),
  delete: (id) => api.delete(`/tag/${id}`)
}

export default api

