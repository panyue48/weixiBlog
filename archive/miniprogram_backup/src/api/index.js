/**
 * API接口封装
 */
import { get, post, put, del } from '../utils/request.js'

/**
 * 用户相关API
 */
export const userApi = {
  // 微信登录
  login: (data) => post('/login/wechat', data)
}

/**
 * 博客相关API
 */
export const blogApi = {
  // 获取博客列表
  getList: (params) => get('/blog/list', params),
  
  // 获取博客详情
  getDetail: (id) => get(`/blog/${id}`),
  
  // 保存博客
  save: (data, userId) => post(`/blog/save?userId=${userId}`, data),
  
  // 更新博客
  update: (id, data, userId) => put(`/blog/${id}?userId=${userId}`, data),
  
  // 删除博客
  delete: (id, userId) => del(`/blog/${id}?userId=${userId}`)
}

/**
 * 分类相关API
 */
export const typeApi = {
  // 获取所有分类
  getAll: () => get('/type/list')
}

/**
 * 标签相关API
 */
export const tagApi = {
  // 获取所有标签
  getAll: () => get('/tag/list')
}

