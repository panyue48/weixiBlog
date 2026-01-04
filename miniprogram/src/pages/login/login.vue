<template>
  <view class="container">
    <view class="login-box">
      <view class="header">
        <text class="title">{{ mode === 'register' ? '注册' : '登录' }}</text>
        <text class="subtitle">{{ mode === 'register' ? '创建新账户' : '欢迎回来' }}</text>
      </view>
      
      <view v-if="!userInfo" class="login-form">
        <view class="form-item">
          <input
            v-model="form.username"
            class="input"
            placeholder="请输入用户名"
            placeholder-style="color: #999"
          />
        </view>
        
        <view class="form-item">
          <input
            v-model="form.password"
            class="input"
            type="password"
            placeholder="请输入密码"
            placeholder-style="color: #999"
          />
        </view>
        
        <view v-if="mode === 'register'" class="form-item">
          <input
            v-model="form.nickname"
            class="input"
            placeholder="请输入昵称（可选）"
            placeholder-style="color: #999"
          />
        </view>
        
        <view v-if="mode === 'register'" class="form-item">
          <input
            v-model="form.email"
            class="input"
            placeholder="请输入邮箱（可选）"
            placeholder-style="color: #999"
          />
        </view>
        
        <button
          class="login-btn"
          :loading="loading"
          @click="mode === 'register' ? handleRegister() : handleLogin()"
        >
          {{ mode === 'register' ? '注册' : '登录' }}
        </button>
        
        <view class="switch-mode">
          <text v-if="mode === 'login'" class="switch-text">
            还没有账户？
            <text class="switch-link" @click="switchToRegister">立即注册</text>
          </text>
          <text v-else class="switch-text">
            已有账户？
            <text class="switch-link" @click="switchToLogin">立即登录</text>
          </text>
        </view>
        
        <view class="tips">
          <text>{{ mode === 'register' ? '注册后可以发布和管理你的博客' : '登录后可以发布和管理你的博客' }}</text>
        </view>
      </view>
      
      <view v-else class="user-info">
        <view class="avatar-section">
          <image
            v-if="userInfo.avatar"
            :src="userInfo.avatar"
            class="avatar"
            mode="aspectFill"
          ></image>
          <view v-else class="avatar-placeholder">
            <text>👤</text>
          </view>
          <text class="nickname">{{ userInfo.nickname || userInfo.username }}</text>
        </view>
        
        <view class="info-list">
          <view class="info-item">
            <text class="label">用户名:</text>
            <text class="value">{{ userInfo.username }}</text>
          </view>
          <view class="info-item" v-if="userInfo.nickname">
            <text class="label">昵称:</text>
            <text class="value">{{ userInfo.nickname }}</text>
          </view>
        </view>
        
        <!-- 管理功能入口 -->
        <view class="manage-menu">
          <view class="menu-item" @click="goToMyBlogs">
            <text class="menu-icon">📝</text>
            <text class="menu-text">我的博客</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToTypeManage">
            <text class="menu-icon">📂</text>
            <text class="menu-text">分类管理</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToTagManage">
            <text class="menu-icon">🏷️</text>
            <text class="menu-text">标签管理</text>
            <text class="menu-arrow">›</text>
          </view>
          <view class="menu-item" @click="goToProfile">
            <text class="menu-icon">👤</text>
            <text class="menu-text">个人信息</text>
            <text class="menu-arrow">›</text>
          </view>
        </view>
        
        <button
          class="logout-btn"
          @click="handleLogout"
        >
          退出登录
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { userApi } from '../../api/index.js'
import { setLoginInfo, clearLoginInfo, getLoginInfo } from '../../utils/auth.js'

// 获取页面参数，判断是登录还是注册模式
const mode = ref('login')

onLoad((options) => {
  if (options && options.mode === 'register') {
    mode.value = 'register'
  }
})

const form = ref({
  username: '',
  password: '',
  nickname: '',
  email: ''
})

const userInfo = ref(null)
const loading = ref(false)

// 检查登录状态
const checkLogin = () => {
  const loginInfo = getLoginInfo()
  if (loginInfo.userInfo) {
    userInfo.value = loginInfo.userInfo
  }
}

// 切换模式
const switchToRegister = () => {
  mode.value = 'register'
  form.value = {
    username: '',
    password: '',
    nickname: '',
    email: ''
  }
}

const switchToLogin = () => {
  mode.value = 'login'
  form.value = {
    username: '',
    password: '',
    nickname: '',
    email: ''
  }
}

// 注册
const handleRegister = async () => {
  if (!form.value.username || !form.value.password) {
    uni.showToast({
      title: '请输入用户名和密码',
      icon: 'none'
    })
    return
  }
  
  loading.value = true
  
  try {
    const res = await userApi.register({
      username: form.value.username,
      password: form.value.password,
      nickname: form.value.nickname || null,
      email: form.value.email || null
    })
    
    if (res.code === 200 && res.data) {
      // 保存登录信息
      setLoginInfo(res.data)
      userInfo.value = res.data
      
      uni.showToast({
        title: '注册成功',
        icon: 'success'
      })
      
      // 延迟返回上一页或跳转到首页
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    } else {
      throw new Error(res.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
    uni.showToast({
      title: error.message || '注册失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 登录
const handleLogin = async () => {
  if (!form.value.username || !form.value.password) {
    uni.showToast({
      title: '请输入用户名和密码',
      icon: 'none'
    })
    return
  }
  
  loading.value = true
  
  try {
    const res = await userApi.login({
      username: form.value.username,
      password: form.value.password
    })
    
    if (res.code === 200 && res.data) {
      // 保存登录信息
      setLoginInfo(res.data)
      userInfo.value = res.data
      
      uni.showToast({
        title: '登录成功',
        icon: 'success'
      })
      
      // 延迟返回上一页或跳转到首页
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    } else {
      throw new Error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
    uni.showToast({
      title: error.message || '登录失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 跳转到我的博客
const goToMyBlogs = () => {
  uni.navigateTo({
    url: '/pages/my-blogs/my-blogs'
  })
}

// 跳转到分类管理
const goToTypeManage = () => {
  uni.navigateTo({
    url: '/pages/type-manage/type-manage'
  })
}

// 跳转到标签管理
const goToTagManage = () => {
  uni.navigateTo({
    url: '/pages/tag-manage/tag-manage'
  })
}

// 跳转到个人信息
const goToProfile = () => {
  uni.navigateTo({
    url: '/pages/profile/profile'
  })
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await userApi.logout()
        } catch (error) {
          console.error('退出登录失败:', error)
        }
        
        clearLoginInfo()
        userInfo.value = null
        form.value.username = ''
        form.value.password = ''
        
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
  background: linear-gradient(135deg, #F5E6D3 0%, #E8D5C4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
}

.login-box {
  width: 100%;
  max-width: 600rpx;
  background: #FFF8F0;
  border-radius: 24rpx;
  padding: 64rpx 40rpx;
  box-shadow: 0 12rpx 48rpx rgba(139, 111, 71, 0.15);
  border: 1rpx solid rgba(139, 111, 71, 0.1);
  box-sizing: border-box;
}

.header {
  text-align: center;
  margin-bottom: 64rpx;
  
  .title {
    display: block;
    font-size: 52rpx;
    font-weight: 600;
    color: #3D2817;
    margin-bottom: 16rpx;
  }
  
  .subtitle {
    display: block;
    font-size: 28rpx;
    color: #6B5B47;
  }
}

.login-form {
  .form-item {
    margin-bottom: 32rpx;
    width: 100%;
    box-sizing: border-box;
    
    .input {
      width: 100%;
      height: 96rpx;
      padding: 0 28rpx;
      background: rgba(139, 111, 71, 0.08);
      border-radius: 12rpx;
      font-size: 28rpx;
      color: #3D2817;
      border: 2rpx solid transparent;
      transition: all 0.2s ease;
      box-sizing: border-box;
      
      &:focus {
        background: #FFF8F0;
        border-color: #8B6F47;
      }
    }
  }
  
  .login-btn {
    width: 100%;
    height: 96rpx;
    background: #8B6F47;
    color: #fff;
    border-radius: 12rpx;
    font-size: 32rpx;
    font-weight: 600;
    margin-top: 24rpx;
    border: none;
    transition: all 0.2s ease;
    box-shadow: 0 4rpx 12rpx rgba(139, 111, 71, 0.3);
    
    &::after {
      border: none;
    }
    
    &:active {
      background: #6B5B47;
      transform: scale(0.98);
    }
  }
  
  .switch-mode {
    text-align: center;
    margin-top: 32rpx;
    
    .switch-text {
      color: #6B5B47;
      font-size: 28rpx;
      
      .switch-link {
        color: #8B6F47;
        font-weight: 500;
        margin-left: 8rpx;
      }
    }
  }
  
  .tips {
    text-align: center;
    margin-top: 16rpx;
    color: #6B5B47;
    font-size: 24rpx;
  }
}

.user-info {
  .avatar-section {
    text-align: center;
    margin-bottom: 48rpx;
    
    .avatar {
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
      border: 4rpx solid #E4E4E7;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
    }
    
    .avatar-placeholder {
      width: 160rpx;
      height: 160rpx;
      border-radius: 50%;
      background-color: #E4E4E7;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-size: 80rpx;
      margin: 0 auto;
      box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
    }
    
    .nickname {
      display: block;
      margin-top: 24rpx;
      font-size: 34rpx;
      font-weight: 600;
      color: #3D2817;
    }
  }
  
  .info-list {
    margin-bottom: 32rpx;
    
    .info-item {
      display: flex;
      justify-content: space-between;
      padding: 24rpx 0;
      border-bottom: 1rpx solid #F4F4F5;
      
      .label {
        color: #6B5B47;
        font-size: 28rpx;
      }
      
      .value {
        color: #3D2817;
        font-size: 28rpx;
        font-weight: 600;
      }
    }
  }
  
  .manage-menu {
    margin-bottom: 32rpx;
    background: rgba(139, 111, 71, 0.08);
    border-radius: 12rpx;
    padding: 16rpx;
    
    .menu-item {
      display: flex;
      align-items: center;
      padding: 24rpx;
      background: #FFF8F0;
      border-radius: 12rpx;
      margin-bottom: 12rpx;
      transition: all 0.2s ease;
      
      &:last-child {
        margin-bottom: 0;
      }
      
      &:active {
        background: #F5E6D3;
        transform: scale(0.98);
      }
      
      .menu-icon {
        font-size: 40rpx;
        margin-right: 20rpx;
      }
      
      .menu-text {
        flex: 1;
        font-size: 30rpx;
        color: #3D2817;
        font-weight: 500;
      }
      
      .menu-arrow {
        font-size: 32rpx;
        color: #6B5B47;
      }
    }
  }
  
  .logout-btn {
    width: 100%;
    height: 96rpx;
    background: #D4A574;
    color: #fff;
    border-radius: 12rpx;
    font-size: 32rpx;
    font-weight: 600;
    border: none;
    transition: all 0.2s ease;
    box-shadow: 0 4rpx 12rpx rgba(212, 165, 116, 0.3);
    
    &::after {
      border: none;
    }
    
    &:active {
      background: #C9A67A;
      transform: scale(0.98);
    }
  }
}
</style>

