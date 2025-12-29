<template>
  <view class="container">
    <view v-if="loading" class="loading">
      <u-loading-icon mode="spinner"></u-loading-icon>
      <text>加载中...</text>
    </view>
    
    <view v-else-if="blog" class="blog-detail">
      <!-- 标题 -->
      <view class="blog-header">
        <text class="blog-title">{{ blog.title }}</text>
        <view class="blog-meta">
          <text class="blog-time">{{ formatTime(blog.createTime) }}</text>
          <text class="blog-views">👁 {{ blog.views }}</text>
        </view>
      </view>
      
      <!-- 作者信息 -->
      <view class="author-info">
        <image
          v-if="blog.userAvatar"
          :src="blog.userAvatar"
          class="author-avatar"
          mode="aspectFill"
        ></image>
        <view v-else class="author-avatar-placeholder">👤</view>
        <view class="author-details">
          <text class="author-name">{{ blog.userNickname || '匿名用户' }}</text>
          <text class="blog-type">{{ blog.typeName }}</text>
        </view>
      </view>
      
      <!-- 首图 -->
      <view v-if="blog.firstPicture" class="blog-image">
        <image :src="blog.firstPicture" mode="widthFix"></image>
      </view>
      
      <!-- 标签 -->
      <view v-if="blog.tags && blog.tags.length > 0" class="blog-tags">
        <u-tag
          v-for="tag in blog.tags"
          :key="tag.id"
          :text="tag.name"
          :bg-color="tag.color || '#409EFF'"
          type="info"
          size="mini"
          plain
          custom-style="margin-right: 10rpx; margin-bottom: 10rpx;"
        ></u-tag>
      </view>
      
      <!-- 内容 -->
      <view class="blog-content">
        <rich-text :nodes="formatContent(blog.content)"></rich-text>
      </view>
      
      <!-- 操作按钮 -->
      <view class="action-bar">
        <u-button
          v-if="isMyBlog"
          type="primary"
          @click="handleEdit"
          custom-style="margin-right: 20rpx; flex: 1;"
        >
          编辑
        </u-button>
        <u-button
          v-if="isMyBlog"
          type="error"
          @click="handleDelete"
          custom-style="flex: 1;"
        >
          删除
        </u-button>
      </view>
    </view>
    
    <view v-else class="empty">
      <u-empty mode="data" text="博客不存在"></u-empty>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { blogApi } from '../../api/index.js'

const blogId = ref(null)
const blog = ref(null)
const loading = ref(false)

// 判断是否是自己的博客
const isMyBlog = computed(() => {
  if (!blog.value) return false
  const userInfo = uni.getStorageSync('userInfo')
  return userInfo && userInfo.id === blog.value.userId
})

// 加载博客详情
const loadBlogDetail = async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage.options?.id
  
  if (!id) {
    uni.showToast({
      title: '参数错误',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
    return
  }
  
  blogId.value = parseInt(id)
  loading.value = true
  
  try {
    const res = await blogApi.getDetail(blogId.value)
    if (res.code === 200 && res.data) {
      blog.value = res.data
    } else {
      throw new Error(res.message || '加载失败')
    }
  } catch (error) {
    console.error('加载博客详情失败:', error)
    uni.showToast({
      title: error.message || '加载失败',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hour}:${minute}`
}

// 格式化内容（简单的Markdown转HTML）
const formatContent = (content) => {
  if (!content) return ''
  
  // 简单的Markdown处理
  let html = content
    // 标题
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    // 粗体
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    // 斜体
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    // 代码块
    .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
    // 行内代码
    .replace(/`(.*?)`/g, '<code>$1</code>')
    // 链接
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>')
    // 换行
    .replace(/\n/g, '<br/>')
  
  return html
}

// 编辑博客
const handleEdit = () => {
  uni.navigateTo({
    url: `/pages/write/write?id=${blogId.value}`
  })
}

// 删除博客
const handleDelete = () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这篇博客吗？删除后无法恢复。',
    success: async (res) => {
      if (res.confirm) {
        const userInfo = uni.getStorageSync('userInfo')
        if (!userInfo || !userInfo.id) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          })
          return
        }
        
        try {
          const result = await blogApi.delete(blogId.value, userInfo.id)
          if (result.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } else {
            throw new Error(result.message || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          uni.showToast({
            title: error.message || '删除失败',
            icon: 'none'
          })
        }
      }
    }
  })
}

onMounted(() => {
  loadBlogDetail()
})
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.loading, .empty {
  text-align: center;
  padding: 100rpx 0;
  color: #999;
}

.blog-detail {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 30rpx;
  
  .blog-header {
    margin-bottom: 30rpx;
    
    .blog-title {
      display: block;
      font-size: 40rpx;
      font-weight: bold;
      color: #333;
      line-height: 1.4;
      margin-bottom: 20rpx;
    }
    
    .blog-meta {
      display: flex;
      align-items: center;
      gap: 20rpx;
      font-size: 24rpx;
      color: #999;
      
      .blog-time {
        flex: 1;
      }
    }
  }
  
  .author-info {
    display: flex;
    align-items: center;
    padding: 20rpx;
    background-color: #f8f8f8;
    border-radius: 10rpx;
    margin-bottom: 30rpx;
    
    .author-avatar {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      margin-right: 20rpx;
    }
    
    .author-avatar-placeholder {
      width: 80rpx;
      height: 80rpx;
      border-radius: 50%;
      background-color: #e0e0e0;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 40rpx;
      margin-right: 20rpx;
    }
    
    .author-details {
      flex: 1;
      
      .author-name {
        display: block;
        font-size: 28rpx;
        font-weight: bold;
        color: #333;
        margin-bottom: 10rpx;
      }
      
      .blog-type {
        font-size: 24rpx;
        color: #3c9cff;
        background-color: #e8f4ff;
        padding: 4rpx 12rpx;
        border-radius: 4rpx;
      }
    }
  }
  
  .blog-image {
    width: 100%;
    margin-bottom: 30rpx;
    border-radius: 10rpx;
    overflow: hidden;
    
    image {
      width: 100%;
      display: block;
    }
  }
  
  .blog-tags {
    margin-bottom: 30rpx;
    padding-bottom: 20rpx;
    border-bottom: 1rpx solid #f0f0f0;
  }
  
  .blog-content {
    font-size: 30rpx;
    line-height: 1.8;
    color: #333;
    margin-bottom: 40rpx;
    
    :deep(h1) {
      font-size: 36rpx;
      font-weight: bold;
      margin: 20rpx 0;
    }
    
    :deep(h2) {
      font-size: 32rpx;
      font-weight: bold;
      margin: 18rpx 0;
    }
    
    :deep(h3) {
      font-size: 30rpx;
      font-weight: bold;
      margin: 16rpx 0;
    }
    
    :deep(p) {
      margin: 10rpx 0;
    }
    
    :deep(code) {
      background-color: #f5f5f5;
      padding: 2rpx 6rpx;
      border-radius: 4rpx;
      font-family: monospace;
    }
    
    :deep(pre) {
      background-color: #f5f5f5;
      padding: 20rpx;
      border-radius: 8rpx;
      overflow-x: auto;
      margin: 20rpx 0;
      
      code {
        background-color: transparent;
        padding: 0;
      }
    }
    
    :deep(strong) {
      font-weight: bold;
    }
    
    :deep(em) {
      font-style: italic;
    }
    
    :deep(a) {
      color: #3c9cff;
      text-decoration: underline;
    }
  }
  
  .action-bar {
    display: flex;
    padding-top: 30rpx;
    border-top: 1rpx solid #f0f0f0;
  }
}
</style>

