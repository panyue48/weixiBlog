/**
 * API接口封装
 */
import { get, post, put, del } from '../utils/request.js'

/**
 * 用户相关API
 */
export const userApi = {
  // 用户注册
  register: (data) => post('/login/register', data),
  
  // 用户登录
  login: (data) => post('/login/login', data),
  
  // 用户注销
  logout: () => post('/login/logout'),
  
  // 获取当前用户
  getCurrent: () => get('/login/current'),
  
  // 获取用户信息
  getUserInfo: () => get('/user/info'),
  
  // 更新用户信息
  updateUserInfo: (data) => put('/user/info', data),
  
  // 修改密码
  changePassword: (data) => put('/user/password', data)
}

/**
 * 博客相关API
 */
export const blogApi = {
  // 获取博客列表（前台）
  getList: (params) => get('/blog/list', params),
  
  // 获取博客详情（前台）
  getDetail: (id) => get(`/blog/${id}`),
  
  // 获取博客列表（后台）
  getAdminList: (params) => get('/blog/admin/list', params),
  
  // 获取博客详情（后台）
  getAdminDetail: (id) => get(`/blog/admin/${id}`),
  
  // 创建博客
  save: (data) => post('/blog/save', data),
  
  // 更新博客
  update: (id, data) => put(`/blog/${id}`, data),
  
  // 删除博客
  delete: (id) => del(`/blog/${id}`)
}

/**
 * 分类相关API
 */
export const typeApi = {
  // 获取分类列表（前台）
  getList: () => get('/type/list'),
  
  // 获取分类列表（后台）
  getAdminList: () => get('/type/admin/list'),
  
  // 创建分类
  save: (data) => post('/type/save', data),
  
  // 更新分类
  update: (id, data) => put(`/type/${id}`, data),
  
  // 删除分类
  delete: (id) => del(`/type/${id}`)
}

/**
 * 标签相关API
 */
export const tagApi = {
  // 获取标签列表（前台）
  getList: () => get('/tag/list'),
  
  // 获取标签列表（后台）
  getAdminList: () => get('/tag/admin/list'),
  
  // 创建标签
  save: (data) => post('/tag/save', data),
  
  // 更新标签
  update: (id, data) => put(`/tag/${id}`, data),
  
  // 删除标签
  delete: (id) => del(`/tag/${id}`)
}

