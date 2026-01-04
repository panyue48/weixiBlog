/**
 * 本地存储封装
 */

/**
 * 设置存储
 */
export const setStorage = (key, value) => {
  try {
    uni.setStorageSync(key, value)
    return true
  } catch (error) {
    console.error('存储失败:', error)
    return false
  }
}

/**
 * 获取存储
 */
export const getStorage = (key, defaultValue = null) => {
  try {
    const value = uni.getStorageSync(key)
    return value !== '' ? value : defaultValue
  } catch (error) {
    console.error('获取存储失败:', error)
    return defaultValue
  }
}

/**
 * 删除存储
 */
export const removeStorage = (key) => {
  try {
    uni.removeStorageSync(key)
    return true
  } catch (error) {
    console.error('删除存储失败:', error)
    return false
  }
}

/**
 * 清空所有存储
 */
export const clearStorage = () => {
  try {
    uni.clearStorageSync()
    return true
  } catch (error) {
    console.error('清空存储失败:', error)
    return false
  }
}

