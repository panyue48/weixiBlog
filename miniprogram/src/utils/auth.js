/**
 * 认证相关工具
 */

/**
 * 保存登录信息
 */
export const setLoginInfo = (userInfo, token = null) => {
  uni.setStorageSync('userInfo', userInfo)
  if (token) {
    uni.setStorageSync('token', token)
  }
}

/**
 * 获取登录信息
 */
export const getLoginInfo = () => {
  // #region agent log
  const storedUserInfo = uni.getStorageSync('userInfo');
  const storedToken = uni.getStorageSync('token');
  console.log('[DEBUG-A] auth.js:18 - getLoginInfo called', {storedUserInfo:storedUserInfo,storedToken:storedToken});
  // #endregion
  return {
    userInfo: storedUserInfo,
    token: storedToken
  }
}

/**
 * 判断是否登录
 */
export const isLoggedIn = () => {
  const userInfo = uni.getStorageSync('userInfo')
  return !!userInfo
}

/**
 * 清除登录信息
 */
export const clearLoginInfo = () => {
  uni.removeStorageSync('userInfo')
  uni.removeStorageSync('token')
  uni.removeStorageSync('cookie')
}

/**
 * 获取当前用户ID
 */
export const getCurrentUserId = () => {
  const userInfo = uni.getStorageSync('userInfo')
  return userInfo ? userInfo.id : null
}

