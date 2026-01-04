<template>
  <view class="container">
    <!-- 顶部工具栏 -->
    <view class="toolbar">
      <view class="search-box">
        <input
          v-model="searchKeyword"
          class="search-input"
          placeholder="搜索博客标题或内容"
          placeholder-style="color: #999"
          @confirm="handleSearch"
          @input="handleSearchInput"
        />
        <view class="search-btn" @click="handleSearch">搜索</view>
      </view>
      
      <view class="filter-bar">
        <view class="filter-item" @click="showTypePicker = true">
          <text>{{ selectedTypeName || '全部分类' }}</text>
          <text class="arrow">▼</text>
        </view>
        <view class="filter-item" @click="showStatusPicker = true">
          <text>{{ selectedStatusName || '全部状态' }}</text>
          <text class="arrow">▼</text>
        </view>
      </view>
      
      <view class="action-bar">
        <view class="add-btn" @click="goToEdit">
          <text>+ 新建博客</text>
        </view>
      </view>
    </view>

    <!-- 博客列表 -->
    <view class="blog-list">
      <view v-if="loading && blogList.length === 0" class="loading">
        <text>加载中...</text>
      </view>
      
      <view v-else-if="blogList.length === 0" class="empty">
        <empty-state icon="📝" text="暂无博客"></empty-state>
      </view>
      
      <view v-else>
        <view
          v-for="blog in blogList"
          :key="blog.id"
          class="blog-item"
          @click="goToEdit(blog.id)"
        >
          <view class="blog-header">
            <text class="blog-title">{{ blog.title }}</text>
            <view class="blog-status" :class="blog.published === 1 ? 'published' : 'draft'">
              {{ blog.published === 1 ? '已发布' : '草稿' }}
            </view>
          </view>
          
          <view class="blog-meta">
            <text class="blog-type">{{ blog.typeName }}</text>
            <text class="blog-views">👁 {{ blog.views || 0 }}</text>
            <text class="blog-time">{{ formatTime(blog.createTime) }}</text>
          </view>
          
          <view class="blog-actions">
            <view class="action-btn edit-btn" @click.stop="goToEdit(blog.id)">
              <text>编辑</text>
            </view>
            <view class="action-btn delete-btn" @click.stop="handleDelete(blog.id)">
              <text>删除</text>
            </view>
            <view 
              v-if="blog.published === 1"
              class="action-btn draft-btn"
              @click.stop="handleToggleStatus(blog.id, 0)"
            >
              <text>转为草稿</text>
            </view>
            <view 
              v-else
              class="action-btn publish-btn"
              @click.stop="handleToggleStatus(blog.id, 1)"
            >
              <text>发布</text>
            </view>
          </view>
        </view>
        
        <!-- 加载更多 -->
        <view v-if="hasMore" class="load-more" @click="loadMore">
          <text>{{ loadMoreStatus === 'loading' ? '加载中...' : '加载更多' }}</text>
        </view>
        <view v-else-if="blogList.length > 0" class="no-more">
          <text>没有更多了</text>
        </view>
      </view>
    </view>
    
    <!-- 分类选择器 -->
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
    
    <!-- 状态选择器 -->
    <view v-if="showStatusPicker" class="picker-mask" @click="showStatusPicker = false">
      <view class="picker-content" @click.stop>
        <view class="picker-header">
          <text>选择状态</text>
          <text class="picker-close" @click="showStatusPicker = false">✕</text>
        </view>
        <view class="picker-list">
          <view
            v-for="(option, index) in statusOptions"
            :key="index"
            class="picker-item"
            :class="{ active: selectedStatus === option.value }"
            @click="handleStatusSelect(option)"
          >
            <text>{{ option.label }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad, onShow, onPullDownRefresh } from '@dcloudio/uni-app'
import { blogApi, typeApi } from '../../api/index.js'
import { formatTime, debounce } from '../../utils/common.js'
import { isLoggedIn } from '../../utils/auth.js'
import EmptyState from '../../components/empty-state/empty-state.vue'

// 下拉刷新处理
onPullDownRefresh(async () => {
  await getBlogList(true)
  uni.stopPullDownRefresh()
})

const searchKeyword = ref('')
const selectedTypeId = ref(null)
const selectedTypeName = ref('')
const selectedStatus = ref(null)
const selectedStatusName = ref('')
const blogList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)
const loadMoreStatus = ref('loadmore')
const typeOptions = ref([{ label: '全部分类', value: null }])
const statusOptions = ref([
  { label: '全部状态', value: null },
  { label: '已发布', value: 1 },
  { label: '草稿', value: 0 }
])
const showTypePicker = ref(false)
const showStatusPicker = ref(false)

// 检查登录状态
onLoad(() => {
  if (!isLoggedIn()) {
    uni.showToast({
      title: '请先登录',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateTo({
        url: '/pages/login/login'
      })
    }, 1500)
    return
  }
  
  loadTypes()
  getBlogList(true)
})

onShow(() => {
  // 从编辑页返回时刷新列表
  if (isLoggedIn()) {
    getBlogList(true)
  }
})

// 获取分类列表
const loadTypes = async () => {
  try {
    const res = await typeApi.getAdminList()
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
    if (selectedStatus.value !== null) {
      params.published = selectedStatus.value
    }
    
    const res = await blogApi.getAdminList(params)
    
    if (res.code === 200 && res.data) {
      const { records, pages } = res.data
      
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

// 状态选择
const handleStatusSelect = (option) => {
  selectedStatus.value = option.value
  selectedStatusName.value = option.value !== null ? option.label : ''
  showStatusPicker.value = false
  getBlogList(true)
}

// 加载更多
const loadMore = () => {
  if (!hasMore.value || loading.value) return
  currentPage.value++
  getBlogList(false)
}

// 跳转到编辑页
const goToEdit = (id) => {
  if (id) {
    uni.navigateTo({
      url: `/pages/blog-edit/blog-edit?id=${id}`
    })
  } else {
    uni.navigateTo({
      url: '/pages/blog-edit/blog-edit'
    })
  }
}

// 删除博客
const handleDelete = (id) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这篇博客吗？删除后无法恢复。',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await blogApi.delete(id)
          if (result.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            getBlogList(true)
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

// 切换发布状态
const handleToggleStatus = async (id, published) => {
  try {
    // 先获取博客详情
    const detailRes = await blogApi.getAdminDetail(id)
    if (detailRes.code !== 200 || !detailRes.data) {
      throw new Error('获取博客详情失败')
    }
    
    const blog = detailRes.data
    const updateData = {
      title: blog.title,
      content: blog.content,
      typeId: blog.typeId,
      tagIds: blog.tags ? blog.tags.map(t => t.id) : [],
      firstPicture: blog.firstPicture || '',
      published: published
    }
    
    const result = await blogApi.update(id, updateData)
    if (result.code === 200) {
      uni.showToast({
        title: published === 1 ? '发布成功' : '已转为草稿',
        icon: 'success'
      })
      getBlogList(true)
    } else {
      throw new Error(result.message || '操作失败')
    }
  } catch (error) {
    console.error('切换状态失败:', error)
    uni.showToast({
      title: error.message || '操作失败',
      icon: 'none'
    })
  }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background: #FAF5F0;
  padding: 24rpx;
}

.toolbar {
  margin-bottom: 24rpx;
  
  .search-box {
    display: flex;
    align-items: center;
    background: #FFF8F0;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 16rpx;
    box-shadow: 0 2rpx 8rpx rgba(139, 111, 71, 0.08);
    border: 1rpx solid rgba(139, 111, 71, 0.1);
    
    .search-input {
      flex: 1;
      height: 64rpx;
      font-size: 28rpx;
      color: #3D2817;
    }
    
    .search-btn {
      margin-left: 20rpx;
      padding: 12rpx 32rpx;
      background: #8B6F47;
      color: #fff;
      border-radius: 12rpx;
      font-size: 28rpx;
      font-weight: 500;
      
      &:active {
        background: #6B5B47;
      }
    }
  }
  
  .filter-bar {
    display: flex;
    gap: 16rpx;
    margin-bottom: 16rpx;
    
    .filter-item {
      flex: 1;
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx 24rpx;
      background: #FFF8F0;
      border-radius: 12rpx;
      font-size: 28rpx;
      color: #3D2817;
      box-shadow: 0 2rpx 8rpx rgba(139, 111, 71, 0.08);
      border: 1rpx solid rgba(139, 111, 71, 0.1);
      
      &:active {
        background: #F5E6D3;
      }
      
      .arrow {
        font-size: 20rpx;
        color: #6B5B47;
      }
    }
  }
  
  .action-bar {
    .add-btn {
      width: 100%;
      padding: 24rpx;
      background: #8B6F47;
      color: #fff;
      border-radius: 12rpx;
      text-align: center;
      font-size: 32rpx;
      font-weight: 500;
      box-shadow: 0 4rpx 12rpx rgba(139, 111, 71, 0.3);
      
      &:active {
        transform: scale(0.98);
        background: #6B5B47;
      }
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
  
  .blog-item {
    background: #FFF8F0;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(139, 111, 71, 0.1);
    border: 1rpx solid rgba(139, 111, 71, 0.1);
    
    .blog-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20rpx;
      
      .blog-title {
        flex: 1;
        font-size: 32rpx;
        font-weight: 600;
        color: #3D2817;
      }
      
      .blog-status {
        padding: 6rpx 16rpx;
        border-radius: 8rpx;
        font-size: 24rpx;
        font-weight: 500;
        
        &.published {
          background: rgba(139, 111, 71, 0.15);
          color: #8B6F47;
        }
        
        &.draft {
          background: rgba(107, 91, 71, 0.1);
          color: #6B5B47;
        }
      }
    }
    
    .blog-meta {
      display: flex;
      align-items: center;
      gap: 20rpx;
      margin-bottom: 20rpx;
      font-size: 24rpx;
      color: #6B5B47;
      
      .blog-type {
        color: #8B6F47;
        background: rgba(139, 111, 71, 0.15);
        padding: 4rpx 12rpx;
        border-radius: 6rpx;
      }
    }
    
    .blog-actions {
      display: flex;
      gap: 16rpx;
      padding-top: 20rpx;
      border-top: 1rpx solid rgba(139, 111, 71, 0.1);
      
      .action-btn {
        flex: 1;
        padding: 16rpx;
        border-radius: 8rpx;
        text-align: center;
        font-size: 26rpx;
        font-weight: 500;
        
        text {
          color: #fff;
        }
        
        &.edit-btn {
          background: #8B6F47;
        }
        
        &.delete-btn {
          background: #D4A574;
        }
        
        &.publish-btn {
          background: #8B6F47;
        }
        
        &.draft-btn {
          background: #6B5B47;
        }
        
        &:active {
          transform: scale(0.98);
          opacity: 0.9;
        }
      }
    }
  }
  
  .load-more, .no-more {
    text-align: center;
    padding: 48rpx 0;
    color: #A1A1AA;
    font-size: 24rpx;
  }
  
  .load-more {
    color: #8B6F47;
    font-weight: 500;
    
    &:active {
      opacity: 0.7;
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
  from { opacity: 0; }
  to { opacity: 1; }
}

.picker-content {
  width: 80%;
  max-height: 60%;
  background: #FFF8F0;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(139, 111, 71, 0.15);
  border: 1rpx solid rgba(139, 111, 71, 0.1);
  animation: slideUp 0.3s ease;
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
      background: #F5E6D3;
    }
    
    &.active {
      background: rgba(139, 111, 71, 0.15);
      color: #8B6F47;
      font-weight: 500;
    }
  }
}
</style>

