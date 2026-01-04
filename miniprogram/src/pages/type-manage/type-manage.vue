<template>
  <view class="container">
    <!-- 顶部操作栏 -->
    <view class="toolbar">
      <view class="add-btn" @click="openAddDialog">
        <text>+ 新建分类</text>
      </view>
    </view>

    <!-- 分类列表 -->
    <view class="type-list">
      <view v-if="loading" class="loading">
        <text>加载中...</text>
      </view>
      
      <view v-else-if="types.length === 0" class="empty">
        <empty-state icon="📂" text="暂无分类"></empty-state>
      </view>
      
      <view v-else>
        <view
          v-for="type in types"
          :key="type.id"
          class="type-item"
        >
          <view class="type-info">
            <text class="type-name">{{ type.name }}</text>
            <text v-if="type.description" class="type-desc">{{ type.description }}</text>
          </view>
          <view class="type-actions">
            <view class="action-btn edit-btn" @click="handleEdit(type)">
              <text>编辑</text>
            </view>
            <view class="action-btn delete-btn" @click="handleDelete(type.id)">
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
          <text>{{ showEditDialog ? '编辑分类' : '新建分类' }}</text>
          <text class="dialog-close" @click="closeDialog">✕</text>
        </view>
        <view class="dialog-body">
          <view class="form-item">
            <text class="label">名称 *</text>
            <input
              v-model="form.name"
              class="input"
              placeholder="请输入分类名称"
              placeholder-style="color: #999"
            />
          </view>
          <view class="form-item">
            <text class="label">描述</text>
            <textarea
              v-model="form.description"
              class="textarea"
              placeholder="请输入分类描述（可选）"
              placeholder-style="color: #999"
            ></textarea>
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
import { typeApi } from '../../api/index.js'
import { isLoggedIn } from '../../utils/auth.js'
import EmptyState from '../../components/empty-state/empty-state.vue'

const types = ref([])
const loading = ref(false)
const saving = ref(false)
const showAddDialog = ref(false)
const showEditDialog = ref(false)
const editingId = ref(null)

const form = ref({
  name: '',
  description: ''
})

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
  
  loadTypes()
})

// 加载分类列表
const loadTypes = async () => {
  loading.value = true
  try {
    const res = await typeApi.getAdminList()
    if (res.code === 200 && res.data) {
      types.value = res.data || []
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

// 打开添加对话框
const openAddDialog = () => {
  form.value = { name: '', description: '' }
  showAddDialog.value = true
}

// 编辑分类
const handleEdit = (type) => {
  form.value = {
    name: type.name || '',
    description: type.description || ''
  }
  editingId.value = type.id
  showEditDialog.value = true
}

// 删除分类
const handleDelete = (id) => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这个分类吗？删除后该分类下的博客将无法显示分类信息。',
    success: async (res) => {
      if (res.confirm) {
        try {
          const result = await typeApi.delete(id)
          if (result.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            loadTypes()
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
      title: '请输入分类名称',
      icon: 'none'
    })
    return
  }
  
  saving.value = true
  try {
    let result
    if (editingId.value) {
      // 更新
      result = await typeApi.update(editingId.value, form.value)
    } else {
      // 创建
      result = await typeApi.save(form.value)
    }
    
    if (result.code === 200) {
      uni.showToast({
        title: '保存成功',
        icon: 'success'
      })
      closeDialog()
      loadTypes()
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
  form.value = { name: '', description: '' }
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

.type-list {
  .loading, .empty {
    text-align: center;
    padding: 120rpx 0;
    color: #71717A;
    font-size: 28rpx;
  }
  
  .type-item {
    background-color: #fff;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
    border: 1rpx solid #F4F4F5;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .type-info {
      flex: 1;
      
      .type-name {
        display: block;
        font-size: 32rpx;
        font-weight: 600;
        color: #09090B;
        margin-bottom: 8rpx;
      }
      
      .type-desc {
        display: block;
        font-size: 24rpx;
        color: #71717A;
      }
    }
    
    .type-actions {
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
      min-height: 120rpx;
      line-height: 1.6;
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

