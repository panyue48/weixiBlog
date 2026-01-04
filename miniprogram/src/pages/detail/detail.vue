<template>
  <view class="container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    
    <view v-else-if="blog" class="blog-detail">
      <!-- 标题 -->
      <view class="blog-header">
        <text class="blog-title">{{ blog.title }}</text>
        <view class="blog-meta">
          <text class="blog-time">{{ formatDateTime(blog.createTime) }}</text>
          <text class="blog-views">👁 {{ blog.views || 0 }}</text>
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
          <text class="author-name">{{ blog.userNickname || blog.username || '匿名用户' }}</text>
          <text class="blog-type">{{ blog.typeName }}</text>
        </view>
      </view>
      
      <!-- 首图 -->
      <view v-if="blog.firstPicture" class="blog-image">
        <image :src="blog.firstPicture" mode="widthFix"></image>
      </view>
      
      <!-- 标签 -->
      <view v-if="blog.tags && blog.tags.length > 0" class="blog-tags">
        <text
          v-for="tag in blog.tags"
          :key="tag.id"
          class="tag"
          :style="{ backgroundColor: tag.color || '#409EFF' }"
        >
          {{ tag.name }}
        </text>
      </view>
      
      <!-- 内容 -->
      <view class="blog-content">
        <markdown-viewer :content="blog.content"></markdown-viewer>
      </view>
    </view>
    
    <view v-else class="empty">
      <text>博客不存在</text>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { blogApi } from '../../api/index.js'
import { formatDateTime } from '../../utils/common.js'
import MarkdownViewer from '../../components/markdown-viewer/markdown-viewer.vue'

const blogId = ref(null)
const blog = ref(null)
const loading = ref(false)

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


onMounted(() => {
  loadBlogDetail()
})
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: #FAF5F0;
  padding: 24rpx;
}

.loading, .empty {
  text-align: center;
  padding: 120rpx 0;
  color: #71717A;
  font-size: 28rpx;
}

.blog-detail {
  background: #FFF8F0;
  border-radius: 24rpx;
  padding: 40rpx;
  box-shadow: 0 8rpx 32rpx rgba(139, 111, 71, 0.12);
  border: 1rpx solid rgba(139, 111, 71, 0.1);
  
  .blog-header {
    margin-bottom: 32rpx;
    padding-bottom: 24rpx;
    border-bottom: 1rpx solid #F4F4F5;
    
    .blog-title {
      display: block;
      font-size: 44rpx;
      font-weight: 600;
      color: #3D2817;
      line-height: 1.5;
      margin-bottom: 20rpx;
    }
    
    .blog-meta {
      display: flex;
      align-items: center;
      gap: 20rpx;
      font-size: 24rpx;
      color: #6B5B47;
      
      .blog-time {
        flex: 1;
      }
    }
  }
  
  .author-info {
    display: flex;
    align-items: center;
    padding: 28rpx;
    background: rgba(139, 111, 71, 0.08);
    border-radius: 16rpx;
    margin-bottom: 32rpx;
    border: 1rpx solid rgba(139, 111, 71, 0.15);
    
    .author-avatar {
      width: 88rpx;
      height: 88rpx;
      border-radius: 50%;
      margin-right: 20rpx;
      border: 2rpx solid #E4E4E7;
    }
    
    .author-avatar-placeholder {
      width: 88rpx;
      height: 88rpx;
      border-radius: 50%;
      background-color: #E4E4E7;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 44rpx;
      margin-right: 20rpx;
    }
    
    .author-details {
      flex: 1;
      
      .author-name {
        display: block;
        font-size: 30rpx;
        font-weight: 600;
        color: #3D2817;
        margin-bottom: 12rpx;
      }
      
      .blog-type {
        font-size: 24rpx;
        color: #8B6F47;
        background: rgba(139, 111, 71, 0.15);
        padding: 8rpx 16rpx;
        border-radius: 12rpx;
        font-weight: 600;
        display: inline-block;
        box-shadow: 0 2rpx 8rpx rgba(139, 111, 71, 0.15);
      }
    }
  }
  
  .blog-image {
    width: 100%;
    margin-bottom: 32rpx;
    border-radius: 20rpx;
    overflow: hidden;
    background: linear-gradient(135deg, #F5E6D3 0%, #E8D5C4 100%);
    box-shadow: 0 4rpx 16rpx rgba(139, 111, 71, 0.1);
    
    image {
      width: 100%;
      display: block;
    }
  }
  
  .blog-tags {
    margin-bottom: 32rpx;
    padding-bottom: 24rpx;
    border-bottom: 1rpx solid #F4F4F5;
    display: flex;
    gap: 12rpx;
    flex-wrap: wrap;
    
    .tag {
      font-size: 24rpx;
      color: #fff;
      padding: 8rpx 16rpx;
      border-radius: 8rpx;
      font-weight: 500;
      box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
    }
  }
  
  .blog-content {
    font-size: 30rpx;
    line-height: 1.8;
    color: #4A3A2A;
  }
}
</style>

