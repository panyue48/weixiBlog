<template>
  <view class="container">
    <view class="login-box">
      <view class="avatar-section">
        <image
          v-if="userInfo && userInfo.avatar"
          :src="userInfo.avatar"
          class="avatar"
          mode="aspectFill"
        ></image>
        <view v-else class="avatar-placeholder">
          <text>👤</text>
        </view>
        <text class="nickname">{{ userInfo ? userInfo.nickname : '未登录' }}</text>
      </view>
      
      <view v-if="!userInfo" class="login-section">
        <u-button
          type="primary"
          :loading="loading"
          @click="handleWechatLogin"
          custom-style="width: 100%; margin-top: 40rpx;"
        >
          微信一键登录
        </u-button>
        
        <view class="tips">
          <text>登录后可以发布和管理你的博客</text>
        </view>
      </view>
      
      <view v-else class="user-section">
        <view class="info-item">
          <text class="label">用户ID:</text>
          <text class="value">{{ userInfo.id }}</text>
        </view>
        <view class="info-item">
          <text class="label">昵称:</text>
          <text class="value">{{ userInfo.nickname }}</text>
        </view>
        
        <u-button
          type="error"
          @click="handleLogout"
          custom-style="width: 100%; margin-top: 40rpx;"
        >
          退出登录
        </u-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userApi } from '../../api/index.js'

const userInfo = ref(null)
const loading = ref(false)

// 检查登录状态
const checkLogin = () => {
  const stored = uni.getStorageSync('userInfo')
  if (stored) {
    userInfo.value = stored
  }
}

// 微信登录
const handleWechatLogin = async () => {
  loading.value = true
  
  try {
    // 获取微信登录code
    const loginRes = await new Promise((resolve, reject) => {
      uni.login({
        provider: 'weixin',
        success: resolve,
        fail: reject
      })
    })
    
    if (!loginRes.code) {
      throw new Error('获取微信登录code失败')
    }
    
    // 获取用户信息（可选）
    let userProfile = null
    try {
      const profileRes = await new Promise((resolve, reject) => {
        uni.getUserProfile({
          desc: '用于完善用户资料',
          success: resolve,
          fail: reject
        })
      })
      userProfile = profileRes.userInfo
    } catch (e) {
      console.log('获取用户信息失败，使用默认信息')
    }
    
    // 调用后端登录接口
    const res = await userApi.login({
      code: loginRes.code,
      nickname: userProfile?.nickName || '微信用户',
      avatar: userProfile?.avatarUrl || ''
    })
    
    if (res.code === 200 && res.data) {
      // 保存用户信息
      userInfo.value = res.data
      uni.setStorageSync('userInfo', res.data)
      
      uni.showToast({
        title: '登录成功',
        icon: 'success'
      })
      
      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      throw new Error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    
    // 测试环境：如果微信登录失败，使用模拟登录
    if (error.message && error.message.includes('getUserProfile')) {
      handleMockLogin()
    } else {
      uni.showToast({
        title: error.message || '登录失败',
        icon: 'none'
      })
    }
  } finally {
    loading.value = false
  }
}

// 模拟登录（用于开发测试）
const handleMockLogin = async () => {
  try {
    const res = await userApi.login({
      code: 'test_code_' + Date.now(),
      nickname: '测试用户',
      avatar: ''
    })
    
    if (res.code === 200 && res.data) {
      userInfo.value = res.data
      uni.setStorageSync('userInfo', res.data)
      
      uni.showToast({
        title: '登录成功（测试模式）',
        icon: 'success'
      })
      
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }
  } catch (error) {
    uni.showToast({
      title: '登录失败',
      icon: 'none'
    })
  }
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('userInfo')
        uni.removeStorageSync('token')
        userInfo.value = null
        
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
      }
    }
  })
}

onMounted(() => {
  checkLogin()
})
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}

.login-box {
  width: 100%;
  max-width: 600rpx;
  background-color: #fff;
  border-radius: 20rpx;
  padding: 60rpx 40rpx;
  box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.1);
}

.avatar-section {
  text-align: center;
  margin-bottom: 60rpx;
  
  .avatar {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    border: 4rpx solid #f0f0f0;
  }
  
  .avatar-placeholder {
    width: 160rpx;
    height: 160rpx;
    border-radius: 50%;
    background-color: #f0f0f0;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-size: 80rpx;
    margin: 0 auto;
  }
  
  .nickname {
    display: block;
    margin-top: 20rpx;
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
  }
}

.login-section {
  .tips {
    text-align: center;
    margin-top: 30rpx;
    color: #999;
    font-size: 24rpx;
  }
}

.user-section {
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 20rpx 0;
    border-bottom: 1rpx solid #f0f0f0;
    
    .label {
      color: #666;
      font-size: 28rpx;
    }
    
    .value {
      color: #333;
      font-size: 28rpx;
      font-weight: bold;
    }
  }
}
</style>

