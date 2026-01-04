<template>
  <view class="blog-card" @click="handleClick">
    <view class="blog-header">
      <text class="blog-title">{{ blog.title }}</text>
      <text class="blog-time">{{ formatTime(blog.createTime) }}</text>
    </view>
    
    <view v-if="blog.firstPicture" class="blog-image">
      <image :src="blog.firstPicture" mode="aspectFill"></image>
    </view>
    
    <view class="blog-content">
      <text class="blog-excerpt">{{ getExcerpt(blog.content) }}</text>
    </view>
    
    <view class="blog-footer">
      <view class="blog-meta">
        <text class="blog-type">{{ blog.typeName }}</text>
        <text class="blog-views">👁 {{ blog.views || 0 }}</text>
      </view>
      <view class="blog-tags" v-if="blog.tags && blog.tags.length > 0">
        <text
          v-for="tag in blog.tags"
          :key="tag.id"
          class="tag"
          :style="{ backgroundColor: tag.color || '#409EFF' }"
        >
          {{ tag.name }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { formatTime, getExcerpt } from '../../utils/common.js'

const props = defineProps({
  blog: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click', props.blog.id)
}
</script>

<style lang="scss" scoped>
.blog-card {
  background: #FFF8F0;
  border-radius: 24rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(139, 111, 71, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1rpx solid rgba(139, 111, 71, 0.1);
  cursor: pointer;
  
  &:active {
    transform: translateY(-4rpx);
    box-shadow: 0 8rpx 32rpx rgba(139, 111, 71, 0.15);
    border-color: rgba(139, 111, 71, 0.2);
  }
  
  .blog-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24rpx;
    
    .blog-title {
      font-size: 34rpx;
      font-weight: 600;
      color: #3D2817;
      flex: 1;
      line-height: 1.5;
    }
    
    .blog-time {
      font-size: 24rpx;
      color: #6B5B47;
      margin-left: 20rpx;
      white-space: nowrap;
    }
  }
  
  .blog-image {
    width: 100%;
    height: 320rpx;
    border-radius: 20rpx;
    overflow: hidden;
    margin-bottom: 24rpx;
    background: linear-gradient(135deg, #F5E6D3 0%, #E8D5C4 100%);
    box-shadow: 0 4rpx 16rpx rgba(139, 111, 71, 0.1);
    
    image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
  
  .blog-content {
    margin-bottom: 24rpx;
    
    .blog-excerpt {
      font-size: 28rpx;
      color: #4A3A2A;
      line-height: 1.7;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      -webkit-line-clamp: 3;
      overflow: hidden;
    }
  }
  
  .blog-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 16rpx;
    
    .blog-meta {
      display: flex;
      align-items: center;
      gap: 16rpx;
      flex-wrap: wrap;
      
      .blog-type {
        font-size: 24rpx;
        color: #8B6F47;
        background: rgba(139, 111, 71, 0.15);
        padding: 8rpx 16rpx;
        border-radius: 12rpx;
        font-weight: 600;
        box-shadow: 0 2rpx 8rpx rgba(139, 111, 71, 0.15);
      }
      
      .blog-views {
        font-size: 24rpx;
        color: #6B5B47;
      }
    }
    
    .blog-tags {
      display: flex;
      gap: 12rpx;
      flex-wrap: wrap;
      
      .tag {
        font-size: 22rpx;
        color: #fff;
        padding: 6rpx 14rpx;
        border-radius: 8rpx; // 更圆润
        font-weight: 500;
        box-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1); // 轻微阴影
      }
    }
  }
}
</style>

