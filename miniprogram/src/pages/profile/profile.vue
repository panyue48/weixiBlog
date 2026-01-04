<template>
  <view class="container">
    <!-- 个人信息 -->
    <view class="card">
      <view class="card-header">
        <text class="card-title">个人信息</text>
      </view>
      <view class="card-body">
        <view class="form-item">
          <text class="label">用户名</text>
          <input
            :value="userInfo.username"
            class="input"
            disabled
          />
        </view>
        
        <view class="form-item">
          <text class="label">昵称</text>
          <input
            v-model="form.nickname"
            class="input"
            placeholder="请输入昵称"
            placeholder-style="color: #999"
          />
        </view>
        
        <view class="form-item">
          <text class="label">头像URL</text>
          <input
            v-model="form.avatar"
            class="input"
            placeholder="请输入头像URL"
            placeholder-style="color: #999"
          />
          <view v-if="form.avatar" class="avatar-preview">
            <image :src="form.avatar" mode="aspectFill" @error="handleImageError"></image>
          </view>
        </view>
        
        <button class="save-btn" :loading="loading" @click="handleUpdateInfo">
          {{ loading ? '保存中...' : '保存' }}
        </button>
      </view>
    </view>
    
    <!-- 修改密码 -->
    <view class="card">
      <view class="card-header">
        <text class="card-title">修改密码</text>
      </view>
      <view class="card-body">
        <view class="form-item">
          <text class="label">原密码 *</text>
          <input
            v-model="passwordForm.oldPassword"
            class="input"
            type="password"
            placeholder="请输入原密码"
            placeholder-style="color: #999"
          />
        </view>
        
        <view class="form-item">
          <text class="label">新密码 *</text>
          <input
            v-model="passwordForm.newPassword"
            class="input"
            type="password"
            placeholder="请输入新密码（至少6位）"
            placeholder-style="color: #999"
          />
        </view>
        
        <view class="form-item">
          <text class="label">确认新密码 *</text>
          <input
            v-model="passwordForm.confirmPassword"
            class="input"
            type="password"
            placeholder="请再次输入新密码"
            placeholder-style="color: #999"
          />
        </view>
        
        <button class="save-btn" :loading="passwordLoading" @click="handleChangePassword">
          {{ passwordLoading ? '修改中...' : '修改密码' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { userApi } from '../../api/index.js'
import { isLoggedIn, setLoginInfo, getLoginInfo } from '../../utils/auth.js'

const userInfo = ref({})
const form = ref({
  nickname: '',
  avatar: ''
})
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const loading = ref(false)
const passwordLoading = ref(false)

// 检查登录状态
onLoad(() => {
  if (!isLoggedIn()) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
    return
  }
  
  loadUserInfo()
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const res = await userApi.getUserInfo()
    if (res.code === 200 && res.data) {
      userInfo.value = res.data
      form.value = {
        nickname: res.data.nickname || '',
        avatar: res.data.avatar || ''
      }
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  }
}

// 图片加载错误
const handleImageError = () => {
  uni.showToast({
    title: '图片加载失败',
    icon: 'none'
  })
}

// 更新个人信息
const handleUpdateInfo = async () => {
  loading.value = true
  try {
    const res = await userApi.updateUserInfo(form.value)
    if (res.code === 200) {
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      
      // 更新本地存储的用户信息
      const loginInfo = getLoginInfo()
      if (loginInfo.userInfo) {
        loginInfo.userInfo.nickname = form.value.nickname
        loginInfo.userInfo.avatar = form.value.avatar
        setLoginInfo(loginInfo.userInfo)
      }
      
      loadUserInfo()
    } else {
      throw new Error(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    uni.showToast({
      title: error.message || '保存失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 修改密码
const handleChangePassword = async () => {
  // 表单验证
  if (!passwordForm.value.oldPassword) {
    uni.showToast({
      title: '请输入原密码',
      icon: 'none'
    })
    return
  }
  
  if (!passwordForm.value.newPassword) {
    uni.showToast({
      title: '请输入新密码',
      icon: 'none'
    })
    return
  }
  
  if (passwordForm.value.newPassword.length < 6) {
    uni.showToast({
      title: '密码长度至少6位',
      icon: 'none'
    })
    return
  }
  
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    uni.showToast({
      title: '两次输入的密码不一致',
      icon: 'none'
    })
    return
  }
  
  passwordLoading.value = true
  try {
    const res = await userApi.changePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    if (res.code === 200) {
      uni.showToast({
        title: '修改密码成功',
        icon: 'success'
      })
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    } else {
      throw new Error(res.message || '修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    uni.showToast({
      title: error.message || '修改失败',
      icon: 'none'
    })
  } finally {
    passwordLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #FAFAFA;
  padding: 24rpx;
}

.card {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  border: 1rpx solid #F4F4F5;
  overflow: hidden;
  
  .card-header {
    padding: 32rpx;
    border-bottom: 1rpx solid #F4F4F5;
    
    .card-title {
      font-size: 36rpx;
      font-weight: 600;
      color: #09090B;
    }
  }
  
  .card-body {
    padding: 32rpx;
    
    .form-item {
      margin-bottom: 32rpx;
      
      .label {
        display: block;
        font-size: 28rpx;
        font-weight: 500;
        color: #09090B;
        margin-bottom: 16rpx;
      }
      
      .input {
        width: 100%;
        padding: 20rpx;
        background-color: #F4F4F5;
        border-radius: 12rpx;
        font-size: 28rpx;
        color: #09090B;
        border: 2rpx solid transparent;
        
        &:focus {
          background-color: #fff;
          border-color: #2563EB;
        }
        
        &:disabled {
          background-color: #E4E4E7;
          color: #71717A;
        }
      }
      
      .avatar-preview {
        width: 160rpx;
        height: 160rpx;
        border-radius: 50%;
        overflow: hidden;
        margin-top: 16rpx;
        border: 2rpx solid #E4E4E7;
        
        image {
          width: 100%;
          height: 100%;
        }
      }
    }
    
    .save-btn {
      width: 100%;
      height: 88rpx;
      background-color: #2563EB;
      color: #fff;
      border-radius: 12rpx;
      font-size: 32rpx;
      font-weight: 500;
      border: none;
      margin-top: 16rpx;
      
      &::after {
        border: none;
      }
      
      &:active {
        background-color: #1D4ED8;
        transform: scale(0.98);
      }
    }
  }
}
</style>

