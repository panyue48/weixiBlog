<template>
  <view id="app">
    <!-- 这里可以放置全局组件 -->
  </view>
</template>

<script>
export default {
  onLaunch: function() {
    console.log('App Launch')
    // 检查登录状态
    this.checkLogin()
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  },
  methods: {
    checkLogin() {
      // 开发模式下，可以选择清除登录状态（每次重新编译时）
      // 如果需要保持登录状态，可以注释掉下面的代码
      // #ifdef MP-WEIXIN
      // 开发模式下清除登录状态，模拟新用户
      const isDev = true // 从config读取或根据环境判断
      if (isDev) {
        // 开发模式下清除登录状态，每次重新编译时显示欢迎页面
        // 如果需要测试已登录功能，可以注释掉下面的代码
        uni.removeStorageSync('userInfo')
        uni.removeStorageSync('token')
        uni.removeStorageSync('cookie')
        console.log('[DEBUG-APP] 开发模式：已清除登录状态，将显示欢迎页面')
      }
      // #endif
      
      const userInfo = uni.getStorageSync('userInfo')
      console.log('[DEBUG-APP] App.vue checkLogin - checking storage')
      console.log('[DEBUG-APP] userInfo from storage:', userInfo)
      if (!userInfo) {
        console.log('[DEBUG-APP] 用户未登录')
      } else {
        console.log('[DEBUG-APP] 用户已登录:', userInfo)
      }
    }
  }
}
</script>

<style lang="scss">
/* 全局样式 */
page {
  background-color: #FAF5F0;
  font-size: 28rpx;
  color: #3D2817;
}

.container {
  padding: 20rpx;
}

/* 通用按钮样式 */
.btn-primary {
  background-color: #3c9cff;
  color: #fff;
}

.btn-success {
  background-color: #19be6b;
  color: #fff;
}

.btn-error {
  background-color: #fa3534;
  color: #fff;
}
</style>

