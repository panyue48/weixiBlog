<template>
  <view class="container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    
    <view v-else-if="types.length === 0" class="empty">
      <empty-state icon="📂" text="暂无分类"></empty-state>
    </view>
    
    <view v-else class="types-grid">
      <view
        v-for="type in types"
        :key="type.id"
        class="type-card"
        @click="goToType(type.id)"
      >
        <view class="type-icon">📁</view>
        <text class="type-name">{{ type.name }}</text>
        <text class="type-count">{{ type.blogCount || 0 }} 篇</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { typeApi, blogApi } from '../../api/index.js'
import EmptyState from '../../components/empty-state/empty-state.vue'
import eventBus from '../../utils/event-bus.js'

// 下拉刷新处理
onPullDownRefresh(async () => {
  await loadTypes()
  uni.stopPullDownRefresh()
})

const types = ref([])
const loading = ref(false)

// 加载分类列表
const loadTypes = async () => {
  loading.value = true
  
  try {
    const res = await typeApi.getList()
    if (res.code === 200 && res.data) {
      types.value = res.data || []
      
      // 加载每个分类的博客数量
      for (let type of types.value) {
        try {
          const blogRes = await blogApi.getList({
            typeId: type.id,
            current: 1,
            size: 1
          })
          if (blogRes.code === 200 && blogRes.data) {
            type.blogCount = blogRes.data.total || 0
          } else {
            type.blogCount = 0
          }
        } catch (error) {
          console.error(`获取分类 ${type.name} 的博客数量失败:`, error)
          type.blogCount = 0
        }
      }
    }
  } catch (error) {
    console.error('加载分类失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 跳转到首页并筛选该分类
const goToType = (typeId) => {
  // 使用事件总线传递筛选条件
  eventBus.emit('filterByType', { typeId })
  // 切换到首页
  uni.switchTab({
    url: '/pages/index/index'
  })
}

onMounted(() => {
  loadTypes()
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

.types-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24rpx;
  
  .type-card {
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
    
    .type-icon {
      font-size: 64rpx;
      margin-bottom: 20rpx;
      filter: drop-shadow(0 2rpx 8rpx rgba(0, 0, 0, 0.1));
    }
    
    .type-name {
      font-size: 32rpx;
      font-weight: 600;
      color: #3D2817;
      margin-bottom: 12rpx;
      text-align: center;
    }
    
    .type-count {
      font-size: 24rpx;
      color: #6B5B47;
    }
  }
}
</style>

