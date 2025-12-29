<template>
  <div class="admin-tag-list">
    <div class="toolbar">
      <button @click="showAddDialog = true" class="btn btn-primary">+ 新建标签</button>
    </div>
    
    <div class="tag-list card">
      <div v-for="tag in tags" :key="tag.id" class="tag-item">
        <div class="tag-info">
          <span class="tag-badge" :style="{ backgroundColor: tag.color }">{{ tag.name }}</span>
        </div>
        <div class="tag-actions">
          <button @click="editTag(tag)" class="btn-link">编辑</button>
          <button @click="deleteTag(tag.id)" class="btn-link btn-danger-link">删除</button>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <div v-if="showAddDialog || editingTag" class="dialog-overlay" @click="closeDialog">
      <div class="dialog" @click.stop>
        <h3>{{ editingTag ? '编辑标签' : '新建标签' }}</h3>
        <form @submit.prevent="handleSave">
          <div class="form-group">
            <label>标签名称 *</label>
            <input v-model="form.name" type="text" required placeholder="请输入标签名称" />
          </div>
          <div class="form-group">
            <label>标签颜色 *</label>
            <input v-model="form.color" type="color" required />
            <input v-model="form.color" type="text" placeholder="#409EFF" style="margin-top: 10px;" />
          </div>
          <div class="form-actions">
            <button type="submit" class="btn btn-primary">保存</button>
            <button type="button" @click="closeDialog" class="btn">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { tagApi } from '../../api'

export default {
  name: 'AdminTagList',
  data() {
    return {
      tags: [],
      showAddDialog: false,
      editingTag: null,
      form: {
        name: '',
        color: '#409EFF'
      }
    }
  },
  mounted() {
    this.loadTags()
  },
  methods: {
    async loadTags() {
      try {
        this.tags = await tagApi.getAdminList()
      } catch (error) {
        console.error('加载标签列表失败', error)
      }
    },
    editTag(tag) {
      this.editingTag = tag
      this.form = {
        name: tag.name,
        color: tag.color || '#409EFF'
      }
    },
    async handleSave() {
      try {
        if (this.editingTag) {
          await tagApi.update(this.editingTag.id, this.form)
        } else {
          await tagApi.save(this.form)
        }
        this.closeDialog()
        this.loadTags()
      } catch (error) {
        alert('保存失败：' + (error.message || '未知错误'))
      }
    },
    async deleteTag(id) {
      if (!confirm('确定要删除这个标签吗？')) return
      try {
        await tagApi.delete(id)
        this.loadTags()
      } catch (error) {
        alert('删除失败：' + (error.message || '未知错误'))
      }
    },
    closeDialog() {
      this.showAddDialog = false
      this.editingTag = null
      this.form = {
        name: '',
        color: '#409EFF'
      }
    }
  }
}
</script>

<style scoped>
.tag-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.tag-item:last-child {
  border-bottom: none;
}

.tag-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: 4px;
  color: white;
  font-size: 14px;
}

.tag-actions {
  display: flex;
  gap: 10px;
}

.btn-link {
  color: #409EFF;
  cursor: pointer;
  background: none;
  border: none;
  padding: 0;
}

.btn-link:hover {
  text-decoration: underline;
}

.btn-danger-link {
  color: #F56C6C;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
}

.dialog h3 {
  margin-bottom: 20px;
  color: #333;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}
</style>

