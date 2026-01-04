<template>
  <view class="container">
    <view class="form">
      <!-- 标题 -->
      <view class="form-item">
        <text class="label">标题 *</text>
        <input
          v-model="form.title"
          class="input"
          placeholder="请输入博客标题"
          placeholder-style="color: #999"
        />
      </view>
      
      <!-- 分类 -->
      <view class="form-item">
        <text class="label">分类 *</text>
        <view class="picker-btn" @click="showTypePicker = true">
          <text>{{ selectedTypeName || '请选择分类' }}</text>
          <text class="arrow">▼</text>
        </view>
      </view>
      
      <!-- 标签 -->
      <view class="form-item">
        <text class="label">标签</text>
        <view class="tag-selector">
          <view
            v-for="tag in tags"
            :key="tag.id"
            class="tag-item"
            :class="{ selected: form.tagIds.includes(tag.id) }"
            :style="{ backgroundColor: form.tagIds.includes(tag.id) ? tag.color : '#F4F4F5' }"
            @click="toggleTag(tag.id)"
          >
            <text :style="{ color: form.tagIds.includes(tag.id) ? '#fff' : '#09090B' }">
              {{ tag.name }}
            </text>
          </view>
        </view>
      </view>
      
      <!-- 首图 -->
      <view class="form-item">
        <text class="label">首图URL</text>
        <input
          v-model="form.firstPicture"
          class="input"
          placeholder="请输入图片URL"
          placeholder-style="color: #999"
        />
        <view v-if="form.firstPicture" class="image-preview">
          <image :src="form.firstPicture" mode="aspectFill" @error="handleImageError"></image>
        </view>
      </view>
      
      <!-- 内容 -->
      <view class="form-item">
        <text class="label">内容 *</text>
        <textarea
          v-model="form.content"
          class="textarea"
          placeholder="支持Markdown格式..."
          placeholder-style="color: #999"
          :maxlength="50000"
        ></textarea>
        <view class="char-count">{{ form.content.length }}/50000</view>
      </view>
      
      <!-- 发布状态 -->
      <view class="form-item">
        <view class="checkbox-item" @click="form.published = form.published === 1 ? 0 : 1">
          <view class="checkbox" :class="{ checked: form.published === 1 }">
            <text v-if="form.published === 1">✓</text>
          </view>
          <text class="checkbox-label">立即发布</text>
        </view>
      </view>
      
      <!-- 操作按钮 -->
      <view class="form-actions">
        <button class="save-btn" :loading="loading" @click="handleSave">
          {{ loading ? '保存中...' : '保存' }}
        </button>
        <button class="cancel-btn" @click="handleCancel">取消</button>
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
            v-for="(type, index) in types"
            :key="index"
            class="picker-item"
            :class="{ active: form.typeId === type.id }"
            @click="handleTypeSelect(type)"
          >
            <text>{{ type.name }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { blogApi, typeApi, tagApi } from '../../api/index.js'
import { isLoggedIn } from '../../utils/auth.js'

const form = ref({
  title: '',
  content: '',
  typeId: '',
  tagIds: [],
  firstPicture: '',
  published: 1
})

const types = ref([])
const tags = ref([])
const loading = ref(false)
const showTypePicker = ref(false)
const selectedTypeName = ref('')

// 检查登录状态
onLoad((options) => {
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
  
  loadTypes()
  loadTags()
  
  // 如果是编辑模式，加载博客详情
  if (options.id) {
    loadBlog(parseInt(options.id))
  }
})

// 加载分类列表
const loadTypes = async () => {
  try {
    const res = await typeApi.getAdminList()
    if (res.code === 200 && res.data) {
      types.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

// 加载标签列表
const loadTags = async () => {
  try {
    const res = await tagApi.getAdminList()
    if (res.code === 200 && res.data) {
      tags.value = res.data || []
    }
  } catch (error) {
    console.error('加载标签失败:', error)
  }
}

// 加载博客详情
const loadBlog = async (id) => {
  try {
    const res = await blogApi.getAdminDetail(id)
    if (res.code === 200 && res.data) {
      const blog = res.data
      form.value = {
        title: blog.title || '',
        content: blog.content || '',
        typeId: blog.typeId || '',
        tagIds: blog.tags ? blog.tags.map(t => t.id) : [],
        firstPicture: blog.firstPicture || '',
        published: blog.published || 1
      }
      
      // 设置分类名称
      const type = types.value.find(t => t.id === blog.typeId)
      if (type) {
        selectedTypeName.value = type.name
      }
    }
  } catch (error) {
    console.error('加载博客失败:', error)
    uni.showToast({
      title: '加载失败',
      icon: 'none'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  }
}

// 选择分类
const handleTypeSelect = (type) => {
  form.value.typeId = type.id
  selectedTypeName.value = type.name
  showTypePicker.value = false
}

// 切换标签
const toggleTag = (tagId) => {
  const index = form.value.tagIds.indexOf(tagId)
  if (index > -1) {
    form.value.tagIds.splice(index, 1)
  } else {
    form.value.tagIds.push(tagId)
  }
}

// 图片加载错误
const handleImageError = () => {
  uni.showToast({
    title: '图片加载失败',
    icon: 'none'
  })
}

// 保存
const handleSave = async () => {
  // 表单验证
  if (!form.value.title || !form.value.title.trim()) {
    uni.showToast({
      title: '请输入标题',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.content || !form.value.content.trim()) {
    uni.showToast({
      title: '请输入内容',
      icon: 'none'
    })
    return
  }
  
  if (!form.value.typeId) {
    uni.showToast({
      title: '请选择分类',
      icon: 'none'
    })
    return
  }
  
  loading.value = true
  
  try {
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const options = currentPage.options || {}
    const id = options.id
    
    let result
    if (id) {
      // 更新
      result = await blogApi.update(parseInt(id), form.value)
    } else {
      // 创建
      result = await blogApi.save(form.value)
    }
    
    if (result.code === 200) {
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      throw new Error(result.message || '保存失败')
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

// 取消
const handleCancel = () => {
  uni.showModal({
    title: '提示',
    content: '确定要取消吗？未保存的内容将丢失。',
    success: (res) => {
      if (res.confirm) {
        uni.navigateBack()
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #FAFAFA;
  padding: 24rpx;
}

.form {
  background-color: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  
  .form-item {
    margin-bottom: 32rpx;
    
    .label {
      display: block;
      font-size: 28rpx;
      font-weight: 500;
      color: #09090B;
      margin-bottom: 16rpx;
    }
    
    .input, .textarea {
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
    }
    
    .textarea {
      min-height: 400rpx;
      line-height: 1.6;
    }
    
    .char-count {
      text-align: right;
      font-size: 24rpx;
      color: #71717A;
      margin-top: 8rpx;
    }
    
    .picker-btn {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 20rpx;
      background-color: #F4F4F5;
      border-radius: 12rpx;
      font-size: 28rpx;
      color: #09090B;
      
      .arrow {
        font-size: 20rpx;
        color: #71717A;
      }
    }
    
    .tag-selector {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;
      
      .tag-item {
        padding: 12rpx 24rpx;
        border-radius: 8rpx;
        font-size: 26rpx;
        font-weight: 500;
        transition: all 0.2s ease;
        
        &:active {
          transform: scale(0.95);
        }
      }
    }
    
    .image-preview {
      width: 100%;
      height: 300rpx;
      border-radius: 12rpx;
      overflow: hidden;
      margin-top: 16rpx;
      background-color: #F4F4F5;
      
      image {
        width: 100%;
        height: 100%;
      }
    }
    
    .checkbox-item {
      display: flex;
      align-items: center;
      gap: 16rpx;
      
      .checkbox {
        width: 40rpx;
        height: 40rpx;
        border: 2rpx solid #E4E4E7;
        border-radius: 8rpx;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: #fff;
        
        &.checked {
          background-color: #2563EB;
          border-color: #2563EB;
          
          text {
            color: #fff;
            font-size: 24rpx;
            font-weight: bold;
          }
        }
      }
      
      .checkbox-label {
        font-size: 28rpx;
        color: #09090B;
      }
    }
  }
  
  .form-actions {
    display: flex;
    gap: 20rpx;
    margin-top: 48rpx;
    padding-top: 32rpx;
    border-top: 1rpx solid #F4F4F5;
    
    .save-btn, .cancel-btn {
      flex: 1;
      height: 88rpx;
      border-radius: 12rpx;
      font-size: 32rpx;
      font-weight: 500;
      border: none;
      
      &::after {
        border: none;
      }
    }
    
    .save-btn {
      background-color: #2563EB;
      color: #fff;
      
      &:active {
        background-color: #1D4ED8;
        transform: scale(0.98);
      }
    }
    
    .cancel-btn {
      background-color: #F4F4F5;
      color: #09090B;
      
      &:active {
        background-color: #E4E4E7;
        transform: scale(0.98);
      }
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
  background-color: #fff;
  border-radius: 24rpx;
  overflow: hidden;
  box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.12);
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
  color: #09090B;
  
  .picker-close {
    font-size: 40rpx;
    color: #71717A;
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
    border-bottom: 1rpx solid #F4F4F5;
    font-size: 28rpx;
    color: #09090B;
    transition: all 0.2s ease;
    
    &:active {
      background-color: #F4F4F5;
    }
    
    &.active {
      background-color: #EFF6FF;
      color: #2563EB;
      font-weight: 500;
    }
  }
}
</style>

