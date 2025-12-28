<template>
  <view class="container">
    <!-- 搜索框 -->
    <view class="search-box">
      <u-search
        v-model="searchKeyword"
        placeholder="搜索博客标题或内容"
        :show-action="true"
        action-text="搜索"
        @search="handleSearch"
        @custom="handleSearch"
        @clear="handleClear"
      ></u-search>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <u-dropdown :active-color="themeColor">
        <u-dropdown-item v-model="filterTypeId" :options="typeOptions" @change="handleFilterChange"></u-dropdown-item>
        <u-dropdown-item v-model="filterTagId" :options="tagOptions" @change="handleFilterChange"></u-dropdown-item>
      </u-dropdown>
    </view>

    <!-- 博客列表 -->
    <view class="blog-list">
      <view v-if="loading" class="loading">
        <u-loading-icon mode="spinner"></u-loading-icon>
        <text>加载中...</text>
      </view>
      
      <view v-else-if="blogList.length === 0" class="empty">
        <u-empty mode="data" text="暂无博客"></u-empty>
      </view>
      
      <view v-else>
        <view
          v-for="blog in blogList"
          :key="blog.id"
          class="blog-item"
          @click="goToDetail(blog.id)"
        >
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
              <text class="blog-views">👁 {{ blog.views }}</text>
            </view>
            <view class="blog-tags">
              <u-tag
                v-for="tag in blog.tags"
                :key="tag.id"
                :text="tag.name"
                :bg-color="tag.color || '#409EFF'"
                type="info"
                size="mini"
                plain
              ></u-tag>
            </view>
          </view>
        </view>
        
        <!-- 加载更多 -->
        <view v-if="hasMore" class="load-more" @click="loadMore">
          <u-loadmore :status="loadMoreStatus" @loadmore="loadMore"></u-loadmore>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { blogApi, typeApi, tagApi } from '../../api/index.js'

// 下拉刷新
uni.onPullDownRefresh(() => {
  getBlogList(true)
  setTimeout(() => {
    uni.stopPullDownRefresh()
  }, 1000)
})

const themeColor = '#3c9cff'
const searchKeyword = ref('')
const filterTypeId = ref(null)
const filterTagId = ref(null)
const blogList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)
const loadMoreStatus = ref('loadmore')
const typeOptions = ref([{ label: '全部分类', value: null }])
const tagOptions = ref([{ label: '全部标签', value: null }])

// 获取分类列表
const getTypes = async () => {
  try {
    const res = await typeApi.getAll()
    if (res.code === 200 && res.data) {
      const types = res.data.map(type => ({
        label: type.name,
        value: type.id
      }))
      typeOptions.value = [{ label: '全部分类', value: null }, ...types]
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取标签列表
const getTags = async () => {
  try {
    const res = await tagApi.getAll()
    if (res.code === 200 && res.data) {
      const tags = res.data.map(tag => ({
        label: tag.name,
        value: tag.id
      }))
      tagOptions.value = [{ label: '全部标签', value: null }, ...tags]
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
      size: pageSize.value,
      published: 1 // 只显示已发布的
    }
    
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    if (filterTypeId.value) {
      params.typeId = filterTypeId.value
    }
    if (filterTagId.value) {
      params.tagId = filterTagId.value
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
    }
  } catch (error) {
    console.error('获取博客列表失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
    loadMoreStatus.value = 'loadmore'
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  getBlogList(true)
}

// 清空搜索
const handleClear = () => {
  searchKeyword.value = ''
  getBlogList(true)
}

// 筛选变化
const handleFilterChange = () => {
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

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
  
  return `${date.getMonth() + 1}-${date.getDate()}`
}

// 获取摘要
const getExcerpt = (content) => {
  if (!content) return ''
  // 移除Markdown标记
  const text = content.replace(/[#*`\[\]]/g, '').trim()
  return text.length > 100 ? text.substring(0, 100) + '...' : text
}

// 页面加载
onMounted(() => {
  getTypes()
  getTags()
  getBlogList(true)
})
</script>

<style lang="scss" scoped>
.container {
  padding: 20rpx;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.search-box {
  margin-bottom: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
  padding: 10rpx;
}

.filter-bar {
  margin-bottom: 20rpx;
  background-color: #fff;
  border-radius: 10rpx;
}

.blog-list {
  .loading, .empty {
    text-align: center;
    padding: 100rpx 0;
    color: #999;
  }
  
  .blog-item {
    background-color: #fff;
    border-radius: 10rpx;
    padding: 30rpx;
    margin-bottom: 20rpx;
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
    
    .blog-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;
      
      .blog-title {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
        flex: 1;
      }
      
      .blog-time {
        font-size: 24rpx;
        color: #999;
        margin-left: 20rpx;
      }
    }
    
    .blog-image {
      width: 100%;
      height: 300rpx;
      border-radius: 10rpx;
      overflow: hidden;
      margin-bottom: 20rpx;
      
      image {
        width: 100%;
        height: 100%;
      }
    }
    
    .blog-content {
      margin-bottom: 20rpx;
      
      .blog-excerpt {
        font-size: 28rpx;
        color: #666;
        line-height: 1.6;
      }
    }
    
    .blog-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .blog-meta {
        display: flex;
        align-items: center;
        gap: 20rpx;
        
        .blog-type {
          font-size: 24rpx;
          color: #3c9cff;
          background-color: #e8f4ff;
          padding: 4rpx 12rpx;
          border-radius: 4rpx;
        }
        
        .blog-views {
          font-size: 24rpx;
          color: #999;
        }
      }
      
      .blog-tags {
        display: flex;
        gap: 10rpx;
        flex-wrap: wrap;
      }
    }
  }
  
  .load-more {
    padding: 40rpx 0;
  }
}
</style>

