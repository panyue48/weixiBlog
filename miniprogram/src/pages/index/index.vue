<template>
  <view class="container">
    <!-- 未登录时显示欢迎界面 -->
    <view v-if="!isLoggedIn" class="welcome-container">
      <view class="welcome-card">
        <view class="welcome-icon">📝</view>
        <text class="welcome-title">欢迎来到博客系统</text>
        <text class="welcome-subtitle">请登录以查看和管理您的个人博客文档</text>
        
        <view class="features">
          <view class="feature-item">
            <text class="feature-icon">✍️</text>
            <text class="feature-text">创建和编辑您的博客</text>
          </view>
          <view class="feature-item">
            <text class="feature-icon">📚</text>
            <text class="feature-text">管理您的文档分类</text>
          </view>
          <view class="feature-item">
            <text class="feature-icon">🏷️</text>
            <text class="feature-text">添加标签和分类</text>
          </view>
          <view class="feature-item">
            <text class="feature-icon">🔒</text>
            <text class="feature-text">安全的个人空间</text>
          </view>
        </view>
        
        <view class="action-buttons">
          <button class="login-btn" @click="goToLogin">立即登录</button>
          <button class="register-btn" @click="goToRegister">注册账号</button>
        </view>
      </view>
    </view>
    
    <!-- 已登录时显示博客列表 -->
    <view v-else class="blog-container">
      <!-- 用户信息栏 -->
      <view class="user-header">
        <view class="user-info">
          <view class="user-details">
            <text class="user-name">{{ userInfo.username || '用户' }}，欢迎</text>
          </view>
        </view>
        <view class="user-actions">
          <view class="manage-btn" @click="goToBlogManage">
            <text class="manage-icon">📝</text>
            <text class="manage-text">博客管理</text>
          </view>
          <view class="logout-btn" @click="handleLogout">
            <text class="logout-icon">🚪</text>
            <text class="logout-text">退出</text>
          </view>
        </view>
      </view>
      
      <!-- 搜索框 -->
      <view class="search-box">
      <input
        v-model="searchKeyword"
        class="search-input"
        placeholder="搜索博客标题或内容"
        placeholder-style="color: #999"
        @confirm="handleSearch"
        @input="handleSearchInput"
      />
      <view class="search-btn" @click="handleSearch">
        <text>搜索</text>
      </view>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <view class="filter-item" @click="showTypePicker = true">
        <text>{{ selectedTypeName || '全部分类' }}</text>
        <text class="arrow">▼</text>
      </view>
      <view class="filter-item" @click="showTagPicker = true">
        <text>{{ selectedTagName || '全部标签' }}</text>
        <text class="arrow">▼</text>
      </view>
    </view>

    <!-- 博客列表 -->
    <view class="blog-list">
      <view v-if="loading && blogList.length === 0" class="loading">
        <text>加载中...</text>
      </view>
      
      <view v-else-if="blogList.length === 0" class="empty">
        <text>暂无博客</text>
      </view>
      
      <view v-else>
        <blog-card
          v-for="blog in blogList"
          :key="blog.id"
          :blog="blog"
          @click="goToDetail(blog.id)"
        />
        
        <!-- 加载更多 -->
        <view v-if="hasMore" class="load-more" @click="loadMore">
          <text>{{ loadMoreStatus === 'loading' ? '加载中...' : '加载更多' }}</text>
        </view>
        <view v-else-if="blogList.length > 0" class="no-more">
          <text>没有更多了</text>
        </view>
      </view>
    </view>
    
    <!-- 分类选择器弹窗 -->
    <view v-if="showTypePicker" class="picker-mask" @click="showTypePicker = false">
      <view class="picker-content" @click.stop>
        <view class="picker-header">
          <text>选择分类</text>
          <text class="picker-close" @click="showTypePicker = false">✕</text>
        </view>
        <view class="picker-list">
          <view
            v-for="(option, index) in typeOptions"
            :key="index"
            class="picker-item"
            :class="{ active: selectedTypeId === option.value }"
            @click="handleTypeSelect(option)"
          >
            <text>{{ option.label }}</text>
          </view>
        </view>
      </view>
    </view>
    
      <!-- 标签选择器弹窗 -->
      <view v-if="showTagPicker" class="picker-mask" @click="showTagPicker = false">
        <view class="picker-content" @click.stop>
          <view class="picker-header">
            <text>选择标签</text>
            <text class="picker-close" @click="showTagPicker = false">✕</text>
          </view>
          <view class="picker-list">
            <view
              v-for="(option, index) in tagOptions"
              :key="index"
              class="picker-item"
              :class="{ active: selectedTagId === option.value }"
              @click="handleTagSelect(option)"
            >
              <text>{{ option.label }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { blogApi, typeApi, tagApi, userApi } from '../../api/index.js'
import { formatTime, getExcerpt, debounce } from '../../utils/common.js'
import { getLoginInfo, clearLoginInfo } from '../../utils/auth.js'
import BlogCard from '../../components/blog-card/blog-card.vue'
import eventBus from '../../utils/event-bus.js'

// 登录状态
const userInfo = ref(null)
const isLoggedIn = computed(() => {
  // #region agent log
  const loggedIn = !!userInfo.value;
  console.log('[DEBUG-C] index.vue:149 - isLoggedIn computed', {userInfo:userInfo.value,loggedIn:loggedIn});
  // #endregion
  return loggedIn;
})

const searchKeyword = ref('')
const selectedTypeId = ref(null)
const selectedTypeName = ref('')
const selectedTagId = ref(null)
const selectedTagName = ref('')
const blogList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)
const loadMoreStatus = ref('loadmore')
const typeOptions = ref([{ label: '全部分类', value: null }])
const tagOptions = ref([{ label: '全部标签', value: null }])
const showTypePicker = ref(false)
const showTagPicker = ref(false)

// 检查登录状态
const checkLoginStatus = () => {
  // #region agent log
  console.log('[DEBUG-B] index.vue:168-before - checkLoginStatus called - before getLoginInfo', {currentUserInfo:userInfo.value});
  // #endregion
  const loginInfo = getLoginInfo()
  // #region agent log
  console.log('[DEBUG-A] index.vue:168-after - checkLoginStatus called - after getLoginInfo', {loginInfo:loginInfo,willSetUserInfoTo:loginInfo.userInfo});
  // #endregion
  userInfo.value = loginInfo.userInfo
  // #region agent log
  console.log('[DEBUG-B] index.vue:168-return - checkLoginStatus returning', {userInfoValue:userInfo.value,returning:!!loginInfo.userInfo});
  // #endregion
  return !!loginInfo.userInfo
}

// 跳转到登录页面
const goToLogin = () => {
  uni.navigateTo({
    url: '/pages/login/login'
  })
}

// 跳转到注册页面
const goToRegister = () => {
  uni.navigateTo({
    url: '/pages/login/login?mode=register'
  })
}

// 页面加载时初始化
onLoad(() => {
  // #region agent log
  console.log('========================================');
  console.log('[DEBUG-E] INDEX.VUE ONLOAD STARTED!!!!');
  console.log('[DEBUG-E] index.vue:189-start - onLoad started', {userInfoBeforeCheck:userInfo.value});
  console.log('========================================');
  // #endregion
  // 检查登录状态
  const loggedIn = checkLoginStatus()
  // #region agent log
  console.log('[DEBUG-E] index.vue:189-checked - onLoad after checkLoginStatus', {loggedIn:loggedIn,userInfoAfterCheck:userInfo.value});
  // #endregion
  
  // 如果已登录，加载数据
  if (loggedIn) {
    // #region agent log
    console.log('[DEBUG-E] index.vue:189-loading - User is logged in, loading data');
    // #endregion
    // 初始化时加载分类和标签列表
    Promise.all([getTypes(), getTags()]).then(() => {
      getBlogList(true)
    }).catch(error => {
      console.error('[DEBUG-E] 加载分类或标签失败:', error)
      // 即使分类或标签加载失败，也尝试加载博客列表
      getBlogList(true)
    })
  } else {
    // #region agent log
    console.log('[DEBUG-D] index.vue:189-notlogged - User is NOT logged in, showing welcome');
    // #endregion
  }
  
  // 监听分类筛选事件
  eventBus.on('filterByType', handleFilterByType)
  // 监听标签筛选事件
  eventBus.on('filterByTag', handleFilterByTag)
})

// 下拉刷新处理
// 注意：在 uni-app Vue 3 中，onPullDownRefresh 是全局生命周期钩子
// 但由于编译环境可能无法识别，我们暂时注释掉，使用 pages.json 中的配置
// 如果需要下拉刷新功能，可以在 onLoad 中通过其他方式实现
// onPullDownRefresh(() => {
//   if (isLoggedIn.value) {
//     getBlogList(true)
//     setTimeout(() => {
//       uni.stopPullDownRefresh()
//     }, 1000)
//   } else {
//     uni.stopPullDownRefresh()
//   }
// })

// 下拉刷新处理（在 onLoad 中通过 uni API 注册）
// 注意：onPullDownRefresh 是 uni-app 的生命周期钩子，在 Vue 3 中需要确保正确使用

// 处理分类筛选
const handleFilterByType = async (data) => {
  const { typeId } = data
  selectedTypeId.value = typeId
  selectedTagId.value = null // 清除标签筛选
  selectedTagName.value = ''
  
  // 等待分类列表加载完成
  await getTypes()
  const type = typeOptions.value.find(t => t.value === typeId)
  if (type) {
    selectedTypeName.value = type.label
  }
  getBlogList(true)
}

// 处理标签筛选
const handleFilterByTag = async (data) => {
  const { tagId } = data
  selectedTagId.value = tagId
  selectedTypeId.value = null // 清除分类筛选
  selectedTypeName.value = ''
  
  // 等待标签列表加载完成
  await getTags()
  const tag = tagOptions.value.find(t => t.value === tagId)
  if (tag) {
    selectedTagName.value = tag.label
  }
  getBlogList(true)
}

// 页面显示时（从其他tab返回时）
onShow(() => {
  // 重新检查登录状态
  const wasLoggedIn = isLoggedIn.value
  const loggedIn = checkLoginStatus()
  
  // 如果登录状态发生变化
  if (!wasLoggedIn && loggedIn) {
    // 从未登录变为已登录，加载数据
    Promise.all([getTypes(), getTags()]).then(() => {
      getBlogList(true)
    })
  } else if (wasLoggedIn && !loggedIn) {
    // 从已登录变为未登录，清空数据
    blogList.value = []
  }
})

// 获取分类列表
const getTypes = async () => {
  try {
    const res = await typeApi.getList()
    if (res.code === 200 && res.data) {
      const types = res.data.map(type => ({
        label: type.name,
        value: type.id
      }))
      typeOptions.value = [{ label: '全部分类', value: null }, ...types]
      
      // 如果已选择了分类，更新名称
      if (selectedTypeId.value) {
        const type = typeOptions.value.find(t => t.value === selectedTypeId.value)
        if (type) {
          selectedTypeName.value = type.label
        }
      }
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取标签列表
const getTags = async () => {
  try {
    const res = await tagApi.getList()
    if (res.code === 200 && res.data) {
      const tags = res.data.map(tag => ({
        label: tag.name,
        value: tag.id
      }))
      tagOptions.value = [{ label: '全部标签', value: null }, ...tags]
      
      // 如果已选择了标签，更新名称
      if (selectedTagId.value) {
        const tag = tagOptions.value.find(t => t.value === selectedTagId.value)
        if (tag) {
          selectedTagName.value = tag.label
        }
      }
    }
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

// 获取博客列表
const getBlogList = async (reset = false) => {
  if (loading.value) return
  
  if (reset) {
    currentPage.value = 1
    blogList.value = []
    hasMore.value = true
  }
  
  loading.value = true
  loadMoreStatus.value = 'loading'
  
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    if (selectedTypeId.value) {
      params.typeId = selectedTypeId.value
    }
    if (selectedTagId.value) {
      params.tagId = selectedTagId.value
    }
    
    const res = await blogApi.getList(params)
    
    if (res.code === 200 && res.data) {
      const { records, total, pages } = res.data
      
      if (reset) {
        blogList.value = records || []
      } else {
        blogList.value = [...blogList.value, ...(records || [])]
      }
      
      hasMore.value = currentPage.value < pages
      loadMoreStatus.value = hasMore.value ? 'loadmore' : 'nomore'
      
      // 如果博客列表为空，记录日志
      if ((records || []).length === 0 && reset) {
        console.log('[DEBUG] 博客列表为空，可能原因：1. 数据库中没有博客 2. API返回空数据')
      }
    } else {
      // API返回错误
      console.error('获取博客列表失败，返回码:', res.code, '消息:', res.message)
      if (res.message) {
        uni.showToast({
          title: res.message,
          icon: 'none',
          duration: 2000
        })
      }
    }
  } catch (error) {
    console.error('获取博客列表失败:', error)
    console.error('错误详情:', error.message, error.stack)
    uni.showToast({
      title: error.message || '加载失败，请检查网络连接',
      icon: 'none',
      duration: 2000
    })
    loadMoreStatus.value = 'loadmore'
  } finally {
    loading.value = false
  }
}

// 搜索输入（防抖）
const handleSearchInput = debounce(() => {
  getBlogList(true)
}, 500)

// 搜索
const handleSearch = () => {
  getBlogList(true)
}

// 分类选择
const handleTypeSelect = (option) => {
  selectedTypeId.value = option.value
  selectedTypeName.value = option.value ? option.label : ''
  showTypePicker.value = false
  getBlogList(true)
}

// 标签选择
const handleTagSelect = (option) => {
  selectedTagId.value = option.value
  selectedTagName.value = option.value ? option.label : ''
  showTagPicker.value = false
  getBlogList(true)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  currentPage.value++
  getBlogList(false)
}

// 跳转详情
const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/detail/detail?id=${id}`
  })
}

// 跳转到博客管理
const goToBlogManage = () => {
  uni.navigateTo({
    url: '/pages/my-blogs/my-blogs'
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
        
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
        
        // 清空博客列表
        blogList.value = []
      }
    }
  })
}

// 页面挂载
onMounted(() => {
  // 页面挂载时的初始化逻辑已在onLoad中处理
})
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: #FAF5F0; // 米色背景
}

// 欢迎界面样式
.welcome-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #F5E6D3 0%, #E8D5C4 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40rpx;
  
  .welcome-card {
    width: 100%;
    max-width: 600rpx;
    background: #FFF8F0;
    border-radius: 32rpx;
    padding: 64rpx 48rpx;
    box-shadow: 0 12rpx 48rpx rgba(139, 111, 71, 0.15);
    text-align: center;
    border: 1rpx solid rgba(139, 111, 71, 0.1);
    
    .welcome-icon {
      font-size: 120rpx;
      margin-bottom: 32rpx;
    }
    
    .welcome-title {
      display: block;
      font-size: 48rpx;
      font-weight: 600;
      color: #3D2817;
      margin-bottom: 16rpx;
    }
    
    .welcome-subtitle {
      display: block;
      font-size: 28rpx;
      color: #6B5B47;
      margin-bottom: 48rpx;
      line-height: 1.6;
    }
    
    .features {
      margin-bottom: 48rpx;
      
      .feature-item {
        display: flex;
        align-items: center;
        padding: 20rpx 0;
        text-align: left;
        
        .feature-icon {
          font-size: 40rpx;
          margin-right: 20rpx;
        }
        
        .feature-text {
          font-size: 28rpx;
          color: #3D2817;
        }
      }
    }
    
    .action-buttons {
      display: flex;
      flex-direction: column;
      gap: 16rpx;
      
      .login-btn, .register-btn {
        width: 100%;
        height: 96rpx;
        border-radius: 12rpx;
        font-size: 32rpx;
        font-weight: 600;
        border: none;
        transition: all 0.2s ease;
        
        &::after {
          border: none;
        }
      }
      
      .login-btn {
        background: #8B6F47;
        color: #fff;
        box-shadow: 0 8rpx 24rpx rgba(139, 111, 71, 0.3);
        
        &:active {
          transform: scale(0.97);
          background: #6B5B47;
          box-shadow: 0 4rpx 16rpx rgba(139, 111, 71, 0.2);
        }
      }
      
      .register-btn {
        background: #D4A574;
        color: #fff;
        box-shadow: 0 8rpx 24rpx rgba(212, 165, 116, 0.3);
        
        &:active {
          transform: scale(0.97);
          background: #C9A67A;
          box-shadow: 0 4rpx 16rpx rgba(212, 165, 116, 0.2);
        }
      }
    }
  }
}

// 博客容器样式
.blog-container {
  padding: 24rpx;
  min-height: 100vh;
}

// 用户信息栏样式
.user-header {
  background: #FFF8F0;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(139, 111, 71, 0.12);
  border: 1rpx solid rgba(139, 111, 71, 0.1);
  
  .user-info {
    display: flex;
    align-items: center;
    margin-bottom: 24rpx;
    
    .user-details {
      flex: 1;
      
      .user-name {
        display: block;
        font-size: 36rpx;
        font-weight: 600;
        color: #3D2817;
      }
    }
  }
  
  .user-actions {
    display: flex;
    gap: 16rpx;
    
    .manage-btn {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8rpx;
      padding: 20rpx;
      background: #8B6F47;
      border-radius: 12rpx;
      transition: all 0.3s ease;
      
      .manage-icon {
        font-size: 32rpx;
      }
      
      .manage-text {
        font-size: 28rpx;
        color: #fff;
        font-weight: 500;
      }
      
      &:active {
        background: #6B5B47;
        transform: scale(0.98);
      }
    }
    
    .logout-btn {
      flex: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8rpx;
      padding: 20rpx;
      background: #D4A574;
      border-radius: 12rpx;
      transition: all 0.3s ease;
      
      .logout-icon {
        font-size: 32rpx;
      }
      
      .logout-text {
        font-size: 28rpx;
        color: #fff;
        font-weight: 500;
      }
      
      &:active {
        background: #C9A67A;
        transform: scale(0.98);
      }
    }
  }
}

.search-box {
  display: flex;
  align-items: center;
  background: #FFF8F0;
  border-radius: 20rpx;
  padding: 20rpx 24rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(139, 111, 71, 0.1);
  border: 1rpx solid rgba(139, 111, 71, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &:active {
    transform: translateY(-2rpx);
    box-shadow: 0 6rpx 24rpx rgba(139, 111, 71, 0.15);
  }
  
  .search-input {
    flex: 1;
    height: 64rpx;
    font-size: 28rpx;
    color: #3D2817;
    background: transparent;
  }
  
  .search-btn {
    margin-left: 16rpx;
    padding: 14rpx 36rpx;
    background: #8B6F47;
    color: #fff;
    border-radius: 16rpx;
    font-size: 28rpx;
    font-weight: 600;
    box-shadow: 0 4rpx 12rpx rgba(139, 111, 71, 0.3);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:active {
      transform: scale(0.95);
      background: #6B5B47;
      box-shadow: 0 2rpx 8rpx rgba(139, 111, 71, 0.2);
    }
  }
}

.filter-bar {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
  
  .filter-item {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 22rpx 28rpx;
    background: #FFF8F0;
    border-radius: 16rpx;
    font-size: 28rpx;
    color: #3D2817;
    font-weight: 500;
    box-shadow: 0 2rpx 12rpx rgba(139, 111, 71, 0.08);
    border: 1rpx solid rgba(139, 111, 71, 0.1);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:active {
      background: #F5E6D3;
      transform: translateY(-2rpx);
      box-shadow: 0 4rpx 16rpx rgba(139, 111, 71, 0.12);
    }
    
    .arrow {
      font-size: 20rpx;
      color: #6B5B47;
      margin-left: 8rpx;
      transition: transform 0.3s ease;
    }
    
    &:active .arrow {
      transform: rotate(180deg);
    }
  }
}

.blog-list {
  .loading, .empty {
    text-align: center;
    padding: 120rpx 0;
    color: #71717A;
    font-size: 28rpx;
  }
  
  .load-more, .no-more {
    text-align: center;
    padding: 48rpx 0;
    color: #A1A1AA;
    font-size: 24rpx;
  }
  
  .load-more {
    color: #8B6F47;
    font-weight: 600;
    padding: 24rpx;
    background: rgba(139, 111, 71, 0.1);
    border-radius: 16rpx;
    margin: 24rpx 0;
    transition: all 0.3s ease;
    
    &:active {
      background: rgba(139, 111, 71, 0.2);
      transform: translateY(-2rpx);
    }
  }
}

.picker-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.picker-content {
  width: 80%;
  max-height: 60%;
  background: #FFF8F0;
  border-radius: 32rpx;
  overflow: hidden;
  box-shadow: 0 20rpx 60rpx rgba(139, 111, 71, 0.2);
  border: 1rpx solid rgba(139, 111, 71, 0.15);
  animation: slideUp 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes slideUp {
  from {
    transform: translateY(100rpx);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.picker-content {
  .picker-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32rpx;
    border-bottom: 1rpx solid #E4E4E7;
    font-size: 32rpx;
    font-weight: 600;
    color: #3D2817;
    
    .picker-close {
      font-size: 40rpx;
      color: #6B5B47;
      padding: 8rpx;
      
      &:active {
        opacity: 0.6;
      }
    }
  }
  
  .picker-list {
    max-height: 500rpx;
    overflow-y: auto;
    
    .picker-item {
    padding: 28rpx 32rpx;
    border-bottom: 1rpx solid rgba(139, 111, 71, 0.1);
    font-size: 28rpx;
    color: #3D2817;
    transition: all 0.2s ease;
    
    &:active {
      background-color: #F5E6D3;
    }
    
    &.active {
      background: rgba(139, 111, 71, 0.15);
      color: #8B6F47;
      font-weight: 600;
      border-left: 4rpx solid #8B6F47;
    }
    }
  }
}
</style>

