<template>
  <view class="container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    
    <view v-else-if="tags.length === 0" class="empty">
      <empty-state icon="🏷️" text="暂无标签"></empty-state>
    </view>
    
    <view v-else class="tags-grid">
      <view
        v-for="tag in tags"
        :key="tag.id"
        class="tag-card"
        @click="goToTag(tag.id)"
      >
        <view class="tag-icon" :style="{ backgroundColor: tag.color || getTagColor(tags.indexOf(tag)) }">
          <text class="tag-emoji">🏷️</text>
        </view>
        <text class="tag-name">{{ tag.name }}</text>
        <text class="tag-count">{{ tag.blogCount || 0 }} 篇</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { tagApi, blogApi } from '../../api/index.js'
import EmptyState from '../../components/empty-state/empty-state.vue'
import eventBus from '../../utils/event-bus.js'

// 下拉刷新处理
onPullDownRefresh(async () => {
  await loadTags()
  uni.stopPullDownRefresh()
})

const tags = ref([])
const loading = ref(false)

// 预设颜色列表
const colorList = [
  '#2563EB', '#22C55E', '#F59E0B', '#EF4444', '#8B5CF6',
  '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1'
]

// 根据标签索引获取颜色（保持一致性）
const getTagColor = (index) => {
  return colorList[index % colorList.length]
}

// 加载标签列表
const loadTags = async () => {
  loading.value = true
  
  try {
    const res = await tagApi.getList()
    if (res.code === 200 && res.data) {
      tags.value = (res.data || []).map((tag, index) => ({
        ...tag,
        color: tag.color || getTagColor(index),
        blogCount: 0 // 初始化博客数量
      }))
      
      // 加载每个标签的博客数量
      for (let tag of tags.value) {
        try {
          const blogRes = await blogApi.getList({
            tagId: tag.id,
            current: 1,
            size: 1
          })
          if (blogRes.code === 200 && blogRes.data) {
            tag.blogCount = blogRes.data.total || 0
          } else {
            tag.blogCount = 0
          }
        } catch (error) {
          console.error(`获取标签 ${tag.name} 的博客数量失败:`, error)
          tag.blogCount = 0
        }
      }
    }
  } catch (error) {
    console.error('加载标签失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 跳转到首页并筛选该标签
const goToTag = (tagId) => {
  // 使用事件总线传递筛选条件
  eventBus.emit('filterByTag', { tagId })
  // 切换到首页
  uni.switchTab({
    url: '/pages/index/index'
  })
}

onMounted(() => {
  loadTags()
})
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #FAF5F0 !important;
  background: #FAF5F0 !important;
  padding: 24rpx;
}

.loading, .empty {
  text-align: center;
  padding: 120rpx 0;
  color: #6B5B47;
  font-size: 28rpx;
}

.tags-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  
  .tag-card {
    background: #FFF8F0;
    border-radius: 20rpx;
    padding: 40rpx 32rpx;
    box-shadow: 0 4rpx 20rpx rgba(139, 111, 71, 0.1);
    border: 1rpx solid rgba(139, 111, 71, 0.1);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    cursor: pointer;
    
    &:active {
      transform: translateY(-4rpx);
      box-shadow: 0 8rpx 32rpx rgba(139, 111, 71, 0.15);
      border-color: rgba(139, 111, 71, 0.2);
    }
    
    .tag-icon {
      width: 96rpx;
      height: 96rpx;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 20rpx;
      box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);
      
      .tag-emoji {
        font-size: 48rpx;
      }
    }
    
    .tag-name {
      font-size: 32rpx;
      font-weight: 600;
      color: #3D2817;
      margin-bottom: 12rpx;
      text-align: center;
    }
    
    .tag-count {
      font-size: 24rpx;
      color: #6B5B47;
    }
  }
}
</style>

