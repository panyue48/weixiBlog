<template>
  <view class="container">
    <view class="form-box">
      <!-- 标题输入 -->
      <view class="form-item">
        <text class="label">标题 *</text>
        <u-input
          v-model="formData.title"
          placeholder="请输入博客标题"
          border="surround"
          maxlength="200"
        ></u-input>
      </view>
      
      <!-- 分类选择 -->
      <view class="form-item">
        <text class="label">分类 *</text>
        <u-picker
          :show="showTypePicker"
          :columns="[typeOptions]"
          @confirm="handleTypeConfirm"
          @cancel="showTypePicker = false"
        ></u-picker>
        <u-input
          v-model="selectedTypeName"
          placeholder="请选择分类"
          border="surround"
          readonly
          @click="showTypePicker = true"
        ></u-input>
      </view>
      
      <!-- 标签选择 -->
      <view class="form-item">
        <text class="label">标签</text>
        <view class="tag-selector">
          <u-tag
            v-for="tag in allTags"
            :key="tag.id"
            :text="tag.name"
            :bg-color="tag.color || '#409EFF'"
            :type="isTagSelected(tag.id) ? 'primary' : 'info'"
            @click="toggleTag(tag.id)"
            custom-style="margin: 10rpx;"
          ></u-tag>
        </view>
      </view>
      
      <!-- 内容输入 -->
      <view class="form-item">
        <text class="label">内容 *</text>
        <textarea
          v-model="formData.content"
          placeholder="请输入博客内容（支持Markdown格式）"
          class="content-input"
          maxlength="50000"
        ></textarea>
      </view>
      
      <!-- 首图URL（可选） -->
      <view class="form-item">
        <text class="label">首图URL（可选）</text>
        <u-input
          v-model="formData.firstPicture"
          placeholder="请输入图片URL"
          border="surround"
        ></u-input>
      </view>
      
      <!-- 发布状态 -->
      <view class="form-item">
        <text class="label">发布状态</text>
        <u-radio-group v-model="formData.published">
          <u-radio name="1" label="立即发布"></u-radio>
          <u-radio name="0" label="保存草稿"></u-radio>
        </u-radio-group>
      </view>
      
      <!-- 操作按钮 -->
      <view class="action-buttons">
        <u-button
          type="primary"
          :loading="saving"
          @click="handleSave"
          custom-style="width: 100%; margin-bottom: 20rpx;"
        >
          {{ blogId ? '更新博客' : '发布博客' }}
        </u-button>
        
        <u-button
          v-if="blogId"
          type="error"
          :loading="deleting"
          @click="handleDelete"
          custom-style="width: 100%;"
        >
          删除博客
        </u-button>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { blogApi, typeApi, tagApi } from '../../api/index.js'

const blogId = ref(null)
const formData = ref({
  title: '',
  content: '',
  firstPicture: '',
  typeId: null,
  tagIds: [],
  published: '1'
})
const selectedTypeName = ref('')
const showTypePicker = ref(false)
const typeOptions = ref([])
const allTags = ref([])
const saving = ref(false)
const deleting = ref(false)

// 获取分类列表
const getTypes = async () => {
  try {
    const res = await typeApi.getAll()
    if (res.code === 200 && res.data) {
      typeOptions.value = res.data.map(type => ({
        label: type.name,
        value: type.id
      }))
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
      allTags.value = res.data
    }
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

// 分类选择确认
const handleTypeConfirm = (e) => {
  const selected = e.value[0]
  formData.value.typeId = selected.value
  selectedTypeName.value = selected.label
  showTypePicker.value = false
}

// 标签选择
const isTagSelected = (tagId) => {
  return formData.value.tagIds.includes(tagId)
}

const toggleTag = (tagId) => {
  const index = formData.value.tagIds.indexOf(tagId)
  if (index > -1) {
    formData.value.tagIds.splice(index, 1)
  } else {
    formData.value.tagIds.push(tagId)
  }
}

// 保存博客
const handleSave = async () => {
  // 验证
  if (!formData.value.title.trim()) {
    uni.showToast({
      title: '请输入标题',
      icon: 'none'
    })
    return
  }
  
  if (!formData.value.content.trim()) {
    uni.showToast({
      title: '请输入内容',
      icon: 'none'
    })
    return
  }
  
  if (!formData.value.typeId) {
    uni.showToast({
      title: '请选择分类',
      icon: 'none'
    })
    return
  }
  
  // 获取用户ID
  const userInfo = uni.getStorageSync('userInfo')
  if (!userInfo || !userInfo.id) {
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
  
  saving.value = true
  
  try {
    const data = {
      title: formData.value.title,
      content: formData.value.content,
      firstPicture: formData.value.firstPicture,
      typeId: formData.value.typeId,
      tagIds: formData.value.tagIds,
      published: parseInt(formData.value.published)
    }
    
    let res
    if (blogId.value) {
      // 更新
      res = await blogApi.update(blogId.value, data, userInfo.id)
    } else {
      // 新建
      res = await blogApi.save(data, userInfo.id)
    }
    
    if (res.code === 200) {
      uni.showToast({
        title: blogId.value ? '更新成功' : '发布成功',
        icon: 'success'
      })
      
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      throw new Error(res.message || '保存失败')
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

// 删除博客
const handleDelete = () => {
  uni.showModal({
    title: '确认删除',
    content: '确定要删除这篇博客吗？删除后无法恢复。',
    success: async (res) => {
      if (res.confirm) {
        const userInfo = uni.getStorageSync('userInfo')
        if (!userInfo || !userInfo.id) {
          uni.showToast({
            title: '请先登录',
            icon: 'none'
          })
          return
        }
        
        deleting.value = true
        
        try {
          const result = await blogApi.delete(blogId.value, userInfo.id)
          if (result.code === 200) {
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
            
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } else {
            throw new Error(result.message || '删除失败')
          }
        } catch (error) {
          console.error('删除失败:', error)
          uni.showToast({
            title: error.message || '删除失败',
            icon: 'none'
          })
        } finally {
          deleting.value = false
        }
      }
    }
  })
}

// 加载博客详情（编辑模式）
const loadBlogDetail = async () => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const id = currentPage.options?.id
  
  if (!id) return
  
  blogId.value = parseInt(id)
  
  try {
    const res = await blogApi.getDetail(blogId.value)
    if (res.code === 200 && res.data) {
      const blog = res.data
      formData.value = {
        title: blog.title,
        content: blog.content,
        firstPicture: blog.firstPicture || '',
        typeId: blog.typeId,
        tagIds: blog.tags ? blog.tags.map(t => t.id) : [],
        published: blog.published.toString()
      }
      
      // 设置分类名称
      const type = typeOptions.value.find(t => t.value === blog.typeId)
      if (type) {
        selectedTypeName.value = type.label
      }
    }
  } catch (error) {
    console.error('加载博客详情失败:', error)
  }
}

onMounted(() => {
  getTypes()
  getTags()
  loadBlogDetail()
})
</script>

<style lang="scss" scoped>
.container {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 20rpx;
}

.form-box {
  background-color: #fff;
  border-radius: 10rpx;
  padding: 30rpx;
}

.form-item {
  margin-bottom: 30rpx;
  
  .label {
    display: block;
    font-size: 28rpx;
    font-weight: bold;
    color: #333;
    margin-bottom: 15rpx;
  }
  
  .content-input {
    width: 100%;
    min-height: 400rpx;
    padding: 20rpx;
    border: 1rpx solid #dcdfe6;
    border-radius: 8rpx;
    font-size: 28rpx;
    line-height: 1.6;
    box-sizing: border-box;
  }
  
  .tag-selector {
    display: flex;
    flex-wrap: wrap;
    padding: 10rpx 0;
  }
}

.action-buttons {
  margin-top: 40rpx;
}
</style>

