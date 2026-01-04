<template>
  <view class="container">
    <!-- 顶部操作栏 -->
    <view class="toolbar">
      <view class="add-btn" @click="showAddDialog = true">
        <text>+ 新建标签</text>
      </view>
    </view>

    <!-- 标签列表 -->
    <view class="tag-list">
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>
      
      <view v-else-if="tags.length === 0" class="empty">
        <empty-state icon="🏷️" text="暂无标签"></empty-state>
      </view>
      
      <view v-else>
        <view
          v-for="tag in tags"
          :key="tag.id"
          class="tag-item"
        >
          <view class="tag-info">
            <view class="tag-preview" :style="{ backgroundColor: tag.color || '#2563EB' }">
              <text>{{ tag.name }}</text>
            </view>
            <text class="tag-color">{{ tag.color || '#2563EB' }}</text>
          </view>
          <view class="tag-actions">
            <view class="action-btn edit-btn" @click="handleEdit(tag)">
              <text>编辑</text>
            </view>
            <view class="action-btn delete-btn" @click="handleDelete(tag.id)">
              <text>删除</text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 添加/编辑对话框 -->
    <view v-if="showAddDialog || showEditDialog" class="dialog-mask" @click="closeDialog">
      <view class="dialog-content" @click.stop>
        <view class="dialog-header">
          <text>{{ showEditDialog ? '编辑标签' : '新建标签' }}</text>
          <text class="dialog-close" @click="closeDialog">✕</text>
        </view>
        <view class="dialog-body">
          <view class="form-item">
            <text class="label">名称 *</text>
            <input
              v-model="form.name"
              class="input"
              placeholder="请输入标签名称"
              placeholder-style="color: #999"
            />
          </view>
          <view class="form-item">
            <text class="label">颜色</text>
            <view class="color-selector">
              <view
                v-for="color in colorList"
                :key="color"
                class="color-item"
                :class="{ active: form.color === color }"
                :style="{ backgroundColor: color }"
                @click="form.color = color"
              ></view>
            </view>
            <view class="color-preview">
              <view class="preview-tag" :style="{ backgroundColor: form.color || colorList[0] }">
                <text>{{ form.name || '标签预览' }}</text>
              </view>
            </view>
          </view>
        </view>
        <view class="dialog-footer">
          <button class="cancel-btn" @click="closeDialog">取消</button>
          <button class="confirm-btn" :loading="saving" @click="handleSave">
            {{ saving ? '保存中...' : '保存' }}
          </button>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { tagApi } from '../../api/index.js'
import { isLoggedIn } from '../../utils/auth.js'
import EmptyState from '../../components/empty-state/empty-state.vue'

const tags = ref([])
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const editingId = ref(null)

const form = ref({
  name: '',
  color: '#2563EB'
})

// 预设颜色列表
const colorList = [
  '#2563EB', '#22C55E', '#F59E0B', '#EF4444', '#8B5CF6',
  '#EC4899', '#06B6D4', '#84CC16', '#F97316', '#6366F1',
  '#14B8A6', '#F43F5E', '#A855F7', '#3B82F6', '#10B981'
]

// 检查登录状态
onLoad(() => {
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
  
  loadTags()
})

// 加载标签列表
const loadTags = async () => {
  loading.value = true
  try {
    const res = await tagApi.getAdminList()
    if (res.code === 200 && res.data) {
      tags.value = res.data || []
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

// 编辑标签
const handleEdit = (tag) => {
  form.value = {
    name: tag.name || '',
    color: tag.color || '#2563EB'
  }
  editingId.value = tag.id
  showEditDialog.value = true
}

// 删除标签
const handleDelete = (id) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个标签吗？删除后使用该标签的博客将无法显示标签信息。',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await tagApi.delete(id)
          if (result.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            loadTags()
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

// 保存
const handleSave = async () => {
  if (!form.value.name || !form.value.name.trim()) {
    uni.showToast({
      title: '请输入标签名称',
      icon: 'none'
    })
    return
  }
  
  saving.value = true
  try {
    let result
    if (editingId.value) {
      // 更新
      result = await tagApi.update(editingId.value, form.value)
    } else {
      // 创建
      result = await tagApi.save(form.value)
    }
    
    if (result.code === 200) {
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      closeDialog()
      loadTags()
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
    saving.value = false
  }
}

// 关闭对话框
const closeDialog = () => {
  showAddDialog.value = false
  showEditDialog.value = false
  editingId.value = null
  form.value = { name: '', color: '#2563EB' }
}
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #FAFAFA;
  padding: 24rpx;
}

.toolbar {
  margin-bottom: 24rpx;
  
  .add-btn {
    width: 100%;
    padding: 24rpx;
    background-color: #2563EB;
    color: #fff;
    border-radius: 12rpx;
    text-align: center;
    font-size: 32rpx;
    font-weight: 500;
    box-shadow: 0 4rpx 12rpx rgba(37, 99, 235, 0.3);
    
    &:active {
      transform: scale(0.98);
      opacity: 0.9;
    }
  }
}

.tag-list {
  .loading, .empty {
    text-align: center;
    padding: 120rpx 0;
    color: #71717A;
    font-size: 28rpx;
  }
  
  .tag-item {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
    border: 1rpx solid #F4F4F5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .tag-info {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 20rpx;
      
      .tag-preview {
        padding: 12rpx 24rpx;
        border-radius: 8rpx;
        font-size: 26rpx;
        font-weight: 500;
        
        text {
          color: #fff;
        }
      }
      
      .tag-color {
        font-size: 24rpx;
        color: #71717A;
        font-family: monospace;
      }
    }
    
    .tag-actions {
      display: flex;
      gap: 16rpx;
      
      .action-btn {
        padding: 12rpx 24rpx;
        border-radius: 8rpx;
        font-size: 26rpx;
        font-weight: 500;
        
        text {
          color: #fff;
        }
        
        &.edit-btn {
          background-color: #2563EB;
        }
        
        &.delete-btn {
          background-color: #EF4444;
        }
        
        &:active {
          transform: scale(0.95);
          opacity: 0.9;
        }
      }
    }
  }
}

.dialog-mask {
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

.dialog-content {
  width: 85%;
  max-width: 600rpx;
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

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #E4E4E7;
  font-size: 32rpx;
  font-weight: 600;
  color: #09090B;
  
  .dialog-close {
    font-size: 40rpx;
    color: #71717A;
    padding: 8rpx;
    
    &:active {
      opacity: 0.6;
    }
  }
}

.dialog-body {
  padding: 32rpx;
  
  .form-item {
    margin-bottom: 32rpx;
    
    .label {
      display: block;
      font-size: 28rpx;
      font-weight: 500;
      color: #09090B;
      margin-bottom: 16rpx;
    }
    
    .input {
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
    
    .color-selector {
      display: flex;
      flex-wrap: wrap;
      gap: 16rpx;
      margin-bottom: 24rpx;
      
      .color-item {
        width: 60rpx;
        height: 60rpx;
        border-radius: 12rpx;
        border: 3rpx solid transparent;
        transition: all 0.2s ease;
        
        &.active {
          border-color: #09090B;
          transform: scale(1.1);
        }
        
        &:active {
          transform: scale(0.95);
        }
      }
    }
    
    .color-preview {
      padding: 24rpx;
      background-color: #F4F4F5;
      border-radius: 12rpx;
      text-align: center;
      
      .preview-tag {
        display: inline-block;
        padding: 12rpx 24rpx;
        border-radius: 8rpx;
        font-size: 28rpx;
        font-weight: 500;
        
        text {
          color: #fff;
        }
      }
    }
  }
}

.dialog-footer {
  display: flex;
  gap: 20rpx;
  padding: 24rpx 32rpx;
  border-top: 1rpx solid #E4E4E7;
  
  .cancel-btn, .confirm-btn {
    flex: 1;
    height: 80rpx;
    border-radius: 12rpx;
    font-size: 30rpx;
    font-weight: 500;
    border: none;
    
    &::after {
      border: none;
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
  
  .confirm-btn {
    background-color: #2563EB;
    color: #fff;
    
    &:active {
      background-color: #1D4ED8;
      transform: scale(0.98);
    }
  }
}
</style>

