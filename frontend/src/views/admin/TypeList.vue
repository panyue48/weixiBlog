<template>
  <div class="admin-type-list">
    <div class="toolbar">
      <button @click="showAddDialog = true" class="btn btn-primary">+ 新建分类</button>
    </div>
    
    <div class="type-list card">
      <div v-for="type in types" :key="type.id" class="type-item">
        <div class="type-info">
          <h3>{{ type.name }}</h3>
          <p v-if="type.description">{{ type.description }}</p>
        </div>
        <div class="type-actions">
          <button @click="editType(type)" class="btn-link">编辑</button>
          <button @click="deleteType(type.id)" class="btn-link btn-danger-link">删除</button>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑对话框 -->
    <div v-if="showAddDialog || editingType" class="dialog-overlay" @click="closeDialog">
      <div class="dialog" @click.stop>
        <h3>{{ editingType ? '编辑分类' : '新建分类' }}</h3>
        <form @submit.prevent="handleSave">
          <div class="form-group">
            <label>分类名称 *</label>
            <input v-model="form.name" type="text" required placeholder="请输入分类名称" />
          </div>
          <div class="form-group">
            <label>分类描述</label>
            <textarea v-model="form.description" rows="3" placeholder="请输入分类描述"></textarea>
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
import { typeApi } from '../../api'

export default {
  name: 'AdminTypeList',
  data() {
    return {
      types: [],
      showAddDialog: false,
      editingType: null,
      form: {
        name: '',
        description: ''
      }
    }
  },
  mounted() {
    this.loadTypes()
  },
  methods: {
    async loadTypes() {
      try {
        this.types = await typeApi.getAdminList()
      } catch (error) {
        console.error('加载分类列表失败', error)
      }
    },
    editType(type) {
      this.editingType = type
      this.form = {
        name: type.name,
        description: type.description || ''
      }
    },
    async handleSave() {
      try {
        if (this.editingType) {
          await typeApi.update(this.editingType.id, this.form)
        } else {
          await typeApi.save(this.form)
        }
        this.closeDialog()
        this.loadTypes()
      } catch (error) {
        alert('保存失败：' + (error.message || '未知错误'))
      }
    },
    async deleteType(id) {
      if (!confirm('确定要删除这个分类吗？')) return
      try {
        await typeApi.delete(id)
        this.loadTypes()
      } catch (error) {
        alert('删除失败：' + (error.message || '未知错误'))
      }
    },
    closeDialog() {
      this.showAddDialog = false
      this.editingType = null
      this.form = {
        name: '',
        description: ''
      }
    }
  }
}
</script>

<style scoped>
.type-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.type-item:last-child {
  border-bottom: none;
}

.type-info h3 {
  margin-bottom: 5px;
  color: #333;
}

.type-info p {
  color: #666;
  font-size: 14px;
}

.type-actions {
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

