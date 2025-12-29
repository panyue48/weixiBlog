<template>
  <div class="blog-list">
    <header class="header">
      <div class="container">
        <h1>个人博客</h1>
        <nav>
          <router-link to="/">首页</router-link>
          <router-link to="/admin/login" class="admin-link">后台管理</router-link>
        </nav>
      </div>
    </header>
    
    <main class="container">
      <div class="blog-cards">
        <div v-for="blog in blogs" :key="blog.id" class="blog-card card" @click="goToDetail(blog.id)">
          <h2>{{ blog.title }}</h2>
          <div class="meta">
            <span class="type">{{ blog.typeName }}</span>
            <span class="date">{{ formatDate(blog.createTime) }}</span>
            <span class="views">浏览 {{ blog.views }}</span>
          </div>
          <div class="tags" v-if="blog.tags && blog.tags.length > 0">
            <span v-for="tag in blog.tags" :key="tag.id" class="tag" :style="{ backgroundColor: tag.color }">
              {{ tag.name }}
            </span>
          </div>
          <p class="summary">{{ getSummary(blog.content) }}</p>
        </div>
      </div>
      
      <div class="pagination" v-if="total > pageSize">
        <button @click="prevPage" :disabled="currentPage === 1" class="btn">上一页</button>
        <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="btn">下一页</button>
      </div>
    </main>
  </div>
</template>

<script>
import { blogApi } from '../api'

export default {
  name: 'BlogList',
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
        const result = await blogApi.getList({
          current: this.currentPage,
          size: this.pageSize
        })
        this.blogs = result.records || []
        this.total = result.total || 0
      } catch (error) {
        console.error('加载博客列表失败', error)
      }
    },
    goToDetail(id) {
      this.$router.push(`/blog/${id}`)
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
      return new Date(date).toLocaleDateString('zh-CN')
    },
    getSummary(content) {
      if (!content) return ''
      // 移除Markdown标记
      const text = content.replace(/[#*`\[\]]/g, '').trim()
      return text.length > 150 ? text.substring(0, 150) + '...' : text
    }
  }
}
</script>

<style scoped>
.header {
  background: white;
  padding: 20px 0;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.header .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  font-size: 24px;
  color: #333;
}

.header nav {
  display: flex;
  gap: 20px;
}

.header nav a {
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.header nav a:hover {
  background-color: #f0f0f0;
}

.admin-link {
  background-color: #409EFF;
  color: white !important;
}

.admin-link:hover {
  background-color: #66b1ff;
}

.blog-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.blog-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.blog-card h2 {
  margin-bottom: 12px;
  color: #333;
}

.meta {
  display: flex;
  gap: 15px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #666;
}

.type {
  color: #409EFF;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
}

.summary {
  color: #666;
  line-height: 1.8;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>

