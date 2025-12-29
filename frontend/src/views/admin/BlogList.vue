<template>
  <div class="admin-blog-list">
    <div class="toolbar">
      <router-link to="/admin/blog/edit" class="btn btn-primary">+ 新建博客</router-link>
    </div>
    
    <div class="blog-table card">
      <table>
        <thead>
          <tr>
            <th>标题</th>
            <th>分类</th>
            <th>状态</th>
            <th>浏览量</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="blog in blogs" :key="blog.id">
            <td>{{ blog.title }}</td>
            <td>{{ blog.typeName }}</td>
            <td>
              <span :class="blog.published === 1 ? 'status-published' : 'status-draft'">
                {{ blog.published === 1 ? '已发布' : '草稿' }}
              </span>
            </td>
            <td>{{ blog.views }}</td>
            <td>{{ formatDate(blog.createTime) }}</td>
            <td>
              <router-link :to="`/admin/blog/edit/${blog.id}`" class="btn-link">编辑</router-link>
              <button @click="handleDelete(blog.id)" class="btn-link btn-danger-link">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <div class="pagination" v-if="total > pageSize">
      <button @click="prevPage" :disabled="currentPage === 1" class="btn">上一页</button>
      <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="btn">下一页</button>
    </div>
  </div>
</template>

<script>
import { blogApi } from '../../api'

export default {
  name: 'AdminBlogList',
  data() {
    return {
      blogs: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  mounted() {
    this.loadBlogs()
  },
  methods: {
    async loadBlogs() {
      try {
        const result = await blogApi.getAdminList({
          current: this.currentPage,
          size: this.pageSize
        })
        this.blogs = result.records || []
        this.total = result.total || 0
      } catch (error) {
        console.error('加载博客列表失败', error)
      }
    },
    async handleDelete(id) {
      if (!confirm('确定要删除这篇博客吗？')) return
      try {
        await blogApi.delete(id)
        this.loadBlogs()
      } catch (error) {
        alert('删除失败：' + (error.message || '未知错误'))
      }
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.loadBlogs()
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.loadBlogs()
      }
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString('zh-CN')
    }
  }
}
</script>

<style scoped>
.toolbar {
  margin-bottom: 20px;
}

.blog-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #f5f7fa;
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
}

th {
  font-weight: 600;
  color: #333;
}

.status-published {
  color: #67C23A;
}

.status-draft {
  color: #909399;
}

.btn-link {
  color: #409EFF;
  margin-right: 10px;
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

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}
</style>

