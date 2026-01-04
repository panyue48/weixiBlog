/**
 * 应用配置文件
 * 用于管理不同环境下的配置信息
 */

// 判断当前环境
let isDev
// #ifdef H5
isDev = process.env.NODE_ENV === 'development'
// #endif
// #ifndef H5
// 小程序环境，通过编译条件判断
// 开发环境：使用本地地址
// 生产环境：使用服务器地址
isDev = true // 开发时设为 true，发布时改为 false
// #endif

// API配置
const apiConfig = {
  // 开发环境配置
  development: {
    baseURL: 'http://localhost:8083/api'
  },
  // 生产环境配置
  production: {
    baseURL: 'https://your-domain.com/api' // 请替换为实际的生产环境地址
  }
}

// 根据环境获取配置
const env = isDev ? 'development' : 'production'
const config = apiConfig[env]

export default {
  // API基础地址
  baseURL: config.baseURL,
  
  // 请求超时时间（毫秒）
  timeout: 10000,
  
  // 是否显示加载提示
  showLoading: true
}

