<template>
  <view class="container">
    <view v-if="loading" class="loading">
      <text>加载中...</text>
    </view>
    
    <view v-else-if="archiveList.length === 0" class="empty">
      <empty-state icon="📅" text="暂无博客"></empty-state>
    </view>
    
    <view v-else class="archive-list">
      <view
        v-for="(group, yearMonth) in archiveList"
        :key="yearMonth"
        class="archive-group"
      >
        <view class="archive-header">
          <text class="archive-date">{{ yearMonth }}</text>
          <text class="archive-count">{{ group.length }} 篇</text>
        </view>
        <view class="archive-items">
          <view
            v-for="blog in group"
            :key="blog.id"
            class="archive-item"
            @click="goToDetail(blog.id)"
          >
            <view class="item-left">
              <text class="item-date">{{ formatDay(blog.createTime) }}</text>
              <text class="item-title">{{ blog.title }}</text>
            </view>
            <view class="item-right">
              <text class="item-views">👁 {{ blog.views || 0 }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onPullDownRefresh } from '@dcloudio/uni-app'
import { blogApi } from '../../api/index.js'
import EmptyState from '../../components/empty-state/empty-state.vue'

// 下拉刷新处理
onPullDownRefresh(async () => {
  await loadAllBlogs()
  uni.stopPullDownRefresh()
})

const blogs = ref([])
const loading = ref(false)

// 按年月分组
const archiveList = computed(() => {
  const groups = {}
  
  blogs.value.forEach(blog => {
    const date = new Date(blog.createTime)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const yearMonth = `${year}年${month}月`
    
    if (!groups[yearMonth]) {
      groups[yearMonth] = []
    }
    groups[yearMonth].push(blog)
  })
  
  // 按日期排序（最新的在前）
  Object.keys(groups).forEach(key => {
    groups[key].sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  })
  
  // 按年月排序（最新的在前）
  const sortedGroups = {}
  Object.keys(groups)
    .sort((a, b) => {
      const dateA = new Date(a.replace('年', '-').replace('月', ''))
      const dateB = new Date(b.replace('年', '-').replace('月', ''))
      return dateB - dateA
    })
    .forEach(key => {
      sortedGroups[key] = groups[key]
    })
  
  return sortedGroups
})

// 格式化日期（显示日）
const formatDay = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const day = String(date.getDate()).padStart(2, '0')
  return day
}

// 加载所有博客
const loadAllBlogs = async () => {
  loading.value = true
  
  try {
    // 获取所有博客（不分页）
    let allBlogs = []
    let currentPage = 1
    const pageSize = 50
    let hasMore = true
    
    while (hasMore) {
      const res = await blogApi.getList({
        current: currentPage,
        size: pageSize
      })
      
      if (res.code === 200 && res.data) {
        const { records, pages } = res.data
        allBlogs = [...allBlogs, ...(records || [])]
        
        if (currentPage >= pages) {
          hasMore = false
        } else {
          currentPage++
        }
      } else {
        hasMore = false
      }
    }
    
    blogs.value = allBlogs
  } catch (error) {
    console.error('加载博客失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

// 跳转到详情页
const goToDetail = (id) => {
  uni.navigateTo({
    url: `/pages/detail/detail?id=${id}`
  })
}

onMounted(() => {
  loadAllBlogs()
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
  color: #6B5B47;
  font-size: 28rpx;
}

.archive-list {
  .archive-group {
    margin-bottom: 48rpx;
    
    .archive-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24rpx;
      padding-bottom: 16rpx;
      border-bottom: 2rpx solid rgba(139, 111, 71, 0.2);
      
      .archive-date {
        font-size: 36rpx;
        font-weight: 600;
        color: #3D2817;
      }
      
      .archive-count {
        font-size: 24rpx;
        color: #6B5B47;
      }
    }
    
    .archive-items {
      .archive-item {
        background: #FFF8F0;
        border-radius: 12rpx;
        padding: 24rpx;
        margin-bottom: 16rpx;
        box-shadow: 0 2rpx 8rpx rgba(139, 111, 71, 0.08);
        border: 1rpx solid rgba(139, 111, 71, 0.1);
        display: flex;
        justify-content: space-between;
        align-items: center;
        transition: all 0.3s ease;
        
        &:active {
          transform: translateX(4rpx);
          box-shadow: 0 4rpx 12rpx rgba(139, 111, 71, 0.12);
        }
        
        .item-left {
          flex: 1;
          display: flex;
          align-items: center;
          gap: 20rpx;
          
          .item-date {
            font-size: 32rpx;
            font-weight: 600;
            color: #8B6F47;
            min-width: 60rpx;
            text-align: center;
          }
          
          .item-title {
            font-size: 28rpx;
            color: #3D2817;
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
        
        .item-right {
          .item-views {
            font-size: 24rpx;
            color: #6B5B47;
          }
        }
      }
    }
  }
}
</style>

