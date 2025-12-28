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
      const userInfo = uni.getStorageSync('userInfo')
      if (!userInfo) {
        console.log('用户未登录')
      } else {
        console.log('用户已登录:', userInfo)
      }
    }
  }
}
</script>

<style lang="scss">
/* 引入uView基础样式 */
@import 'uview-ui/index.scss';

/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-size: 28rpx;
  color: #333;
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

