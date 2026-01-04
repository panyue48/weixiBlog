/**
 * 网络请求封装
 * 支持Session认证适配（手动管理Cookie）
 */

import config from '../config/index.js'

// 后端API基础地址（从配置文件读取）
const BASE_URL = config.baseURL

// 存储Cookie
let cookie = ''

/**
 * 请求拦截器
 */
const requestInterceptor = (config) => {
  // 添加Cookie（用于Session认证）
  if (cookie) {
    config.header = config.header || {}
    config.header['Cookie'] = cookie
  }
  
  // 添加通用header
  config.header = config.header || {}
  config.header['Content-Type'] = 'application/json'
  
  // 显示加载提示
  if (config.showLoading !== false) {
    uni.showLoading({
      title: '加载中...',
      mask: true
    })
  }
  
  return config
}

/**
 * 响应拦截器
 */
const responseInterceptor = (response, config) => {
  // 隐藏加载提示
  if (config.showLoading !== false) {
    uni.hideLoading()
  }
  
  // 从响应头获取Cookie（用于Session认证）
  const setCookie = response.header['Set-Cookie'] || response.header['set-cookie']
  if (setCookie) {
    cookie = setCookie
    // 同时存储到本地，以便下次使用
    uni.setStorageSync('cookie', cookie)
  }
  
  const { statusCode, data } = response
  
  // HTTP状态码检查
  if (statusCode !== 200) {
    uni.showToast({
      title: `请求失败: ${statusCode}`,
      icon: 'none'
    })
    return Promise.reject(new Error(`HTTP Error: ${statusCode}`))
  }
  
  // 业务状态码检查
  if (data.code !== 200 && data.code !== undefined) {
    const message = data.message || '请求失败'
    uni.showToast({
      title: message,
      icon: 'none',
      duration: 2000
    })
    
    // 如果是401未授权，清除登录信息
    if (data.code === 401) {
      cookie = ''
      uni.removeStorageSync('cookie')
      uni.removeStorageSync('userInfo')
      uni.removeStorageSync('token')
      // 跳转到登录页
      uni.reLaunch({
        url: '/pages/login/login'
      })
    }
    
    return Promise.reject(new Error(message))
  }
  
  return data
}

/**
 * 统一请求方法
 */
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 从本地存储恢复Cookie
    const storedCookie = uni.getStorageSync('cookie')
    if (storedCookie) {
      cookie = storedCookie
    }
    
    // 请求拦截
    const config = requestInterceptor({
      url: options.url.startsWith('http') ? options.url : BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: options.header || {},
      showLoading: options.showLoading
    })
    
    // 发起请求
    uni.request({
      ...config,
      success: (response) => {
        try {
          const result = responseInterceptor(response, config)
          resolve(result)
        } catch (error) {
          reject(error)
        }
      },
      fail: (error) => {
        // 隐藏加载提示
        if (config.showLoading !== false) {
          uni.hideLoading()
        }
        
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(error)
      }
    })
  })
}

/**
 * GET请求
 */
export const get = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  })
}

/**
 * POST请求
 */
export const post = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

/**
 * PUT请求
 */
export const put = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

/**
 * DELETE请求
 */
export const del = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

export default {
  get,
  post,
  put,
  delete: del
}

