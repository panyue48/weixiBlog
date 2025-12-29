<template>
  <div class="home">
    <!-- 顶部导航栏 -->
    <header class="header">
      <div class="container">
        <h1 class="logo">Blog</h1>
        <nav class="main-nav">
          <router-link to="/" :class="{ active: $route.path === '/' }">首页</router-link>
          <router-link to="/types" :class="{ active: $route.path === '/types' }">分类</router-link>
          <router-link to="/tags" :class="{ active: $route.path === '/tags' }">标签</router-link>
          <router-link to="/archive" :class="{ active: $route.path === '/archive' }">归档</router-link>
          <router-link to="/about" :class="{ active: $route.path === '/about' }">关于我</router-link>
        </nav>
        <div class="search-box">
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="Search..." 
            @keyup.enter="handleSearch"
            @input="handleSearchInput"
          />
          <span class="search-icon" @click="handleSearch">🔍</span>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="container main-content">
      <!-- 左侧：博客列表 -->
      <div class="content-area">
        <div class="content-header">
          <h2>博客</h2>
          <span class="blog-count">共{{ total }}篇</span>
        </div>
        
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="blogs.length === 0" class="empty">暂无博客</div>
        <div v-else class="blog-cards">
          <div 
            v-for="blog in blogs" 
            :key="blog.id" 
            class="blog-card card" 
            @click="goToDetail(blog.id)"
          >
            <div class="blog-header">
              <h3 class="blog-title">{{ blog.title }}</h3>
              <div class="blog-meta">
                <span class="author">
                  <span class="avatar">👤</span>
                  {{ blog.userNickname || '管理员' }}
                </span>
                <span class="date">
                  <span class="icon">📅</span>
                  {{ formatDate(blog.createTime) }}
                </span>
                <span class="views">
                  <span class="icon">👁</span>
                  {{ blog.views || 0 }}
                </span>
              </div>
            </div>
            <div class="blog-content">
              <p class="summary">{{ getSummary(blog.content) }}</p>
              <div v-if="blog.firstPicture" class="blog-image">
                <img :src="blog.firstPicture" :alt="blog.title" />
              </div>
            </div>
            <div class="blog-footer">
              <div class="blog-tags" v-if="blog.tags && blog.tags.length > 0">
                <span 
                  v-for="tag in blog.tags" 
                  :key="tag.id" 
                  class="tag" 
                  :style="{ backgroundColor: tag.color }"
                  @click.stop="filterByTag(tag.id)"
                >
                  {{ tag.name }}
                </span>
              </div>
              <span 
                class="type-badge" 
                @click.stop="filterByType(blog.typeId)"
              >
                {{ blog.typeName }}
              </span>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > pageSize">
          <button @click="prevPage" :disabled="currentPage === 1" class="btn">上一页</button>
          <span>第 {{ currentPage }} / {{ totalPages }} 页</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="btn">下一页</button>
        </div>
      </div>

      <!-- 右侧：侧边栏 -->
      <aside class="sidebar">
        <!-- 分类 -->
        <div class="sidebar-section">
          <div class="section-header">
            <h3>分类</h3>
            <router-link to="/types" class="more-link">more >></router-link>
          </div>
          <ul class="category-list">
            <li 
              v-for="type in types" 
              :key="type.id"
              :class="{ active: activeTypeId === type.id }"
              @click="filterByType(type.id)"
            >
              <span class="category-name">{{ type.name }}</span>
              <span class="category-count">{{ type.blogCount || 0 }}</span>
            </li>
          </ul>
        </div>

        <!-- 标签 -->
        <div class="sidebar-section">
          <div class="section-header">
            <h3>标签</h3>
            <router-link to="/tags" class="more-link">more >></router-link>
          </div>
          <div class="tag-cloud">
            <span 
              v-for="tag in tags" 
              :key="tag.id"
              class="tag-item"
              :style="{ backgroundColor: tag.color }"
              :class="{ active: activeTagId === tag.id }"
              @click="filterByTag(tag.id)"
            >
              {{ tag.name }}
            </span>
          </div>
        </div>
      </aside>
    </main>
  </div>
</template>

<script>
import { blogApi, typeApi, tagApi } from '../api'

export default {
  name: 'Home',
  data() {
    return {
      blogs: [],
      types: [],
      tags: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      searchKeyword: '',
      activeTypeId: null,
      activeTagId: null
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  watch: {
    '$route'(to, from) {
      // 路由变化时从路由参数读取筛选条件
      if (to.path === '/') {
        this.loadFiltersFromRoute()
        this.loadBlogs()
      }
    }
  },
  mounted() {
    this.loadTypes()
    this.loadTags()
    
    // 先从路由参数获取筛选条件
    this.loadFiltersFromRoute()
    
    // 然后加载博客（此时筛选条件已设置）
    this.loadBlogs()
  },
  methods: {
    async loadBlogs() {
      this.loading = true
      try {
        const params = {
          current: this.currentPage,
          size: this.pageSize
        }
        
        if (this.searchKeyword) {
          params.keyword = this.searchKeyword
        }
        if (this.activeTypeId) {
          params.typeId = this.activeTypeId
        }
        if (this.activeTagId) {
          params.tagId = this.activeTagId
        }
        
        const result = await blogApi.getList(params)
        this.blogs = result.records || []
        this.total = result.total || 0
      } catch (error) {
        console.error('加载博客列表失败', error)
      } finally {
        this.loading = false
      }
    },
    async loadTypes() {
      try {
        const types = await typeApi.getList()
        this.types = types || []
        // 加载每个分类的博客数量
        for (let type of this.types) {
          const result = await blogApi.getList({ typeId: type.id, current: 1, size: 1 })
          type.blogCount = result.total || 0
        }
      } catch (error) {
        console.error('加载分类失败', error)
      }
    },
    async loadTags() {
      try {
        this.tags = await tagApi.getList() || []
      } catch (error) {
        console.error('加载标签失败', error)
      }
    },
    filterByType(typeId) {
      // 如果点击的是当前已激活的分类，则取消筛选；否则应用筛选
      if (this.activeTypeId === typeId) {
        this.activeTypeId = null
      } else {
        this.activeTypeId = typeId
      }
      this.activeTagId = null // 分类和标签互斥
      this.currentPage = 1
      this.updateRoute()
      this.loadBlogs()
    },
    filterByTag(tagId) {
      // 如果点击的是当前已激活的标签，则取消筛选；否则应用筛选
      if (this.activeTagId === tagId) {
        this.activeTagId = null
      } else {
        this.activeTagId = tagId
      }
      this.activeTypeId = null // 分类和标签互斥
      this.currentPage = 1
      this.updateRoute()
      this.loadBlogs()
    },
    handleSearch() {
      this.currentPage = 1
      this.updateRoute()
      this.loadBlogs()
    },
    handleSearchInput() {
      // 实时搜索（可选，也可以只在回车时搜索）
      // this.handleSearch()
    },
    updateRoute() {
      const query = {}
      if (this.searchKeyword) {
        query.keyword = this.searchKeyword
      }
      if (this.activeTypeId) {
        query.typeId = this.activeTypeId
      }
      if (this.activeTagId) {
        query.tagId = this.activeTagId
      }
      this.$router.push({ path: '/', query })
    },
    loadFiltersFromRoute() {
      // 从路由参数获取筛选条件
      const typeId = this.$route.query.typeId
      const tagId = this.$route.query.tagId
      const keyword = this.$route.query.keyword
      
      // 设置筛选条件
      this.activeTypeId = typeId ? parseInt(typeId) : null
      this.activeTagId = tagId ? parseInt(tagId) : null
      this.searchKeyword = keyword || ''
      this.currentPage = 1
    },
    resetFilters() {
      this.activeTypeId = null
      this.activeTagId = null
      this.searchKeyword = ''
      this.currentPage = 1
    },
    goToDetail(id) {
      this.$router.push(`/blog/${id}`)
    },
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--
        this.loadBlogs()
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages) {
        this.currentPage++
        this.loadBlogs()
        window.scrollTo({ top: 0, behavior: 'smooth' })
      }
    },
    formatDate(date) {
      if (!date) return ''
      const d = new Date(date)
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    },
    getSummary(content) {
      if (!content) return ''
      // 移除Markdown标记
      const text = content.replace(/[#*`\[\]()]/g, '').replace(/\n/g, ' ').trim()
      return text.length > 150 ? text.substring(0, 150) + '...' : text
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 顶部导航栏 */
.header {
  background: #2c3e50;
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header .container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  font-size: 24px;
  font-weight: bold;
  color: #17a2b8;
  margin: 0;
}

.main-nav {
  display: flex;
  gap: 30px;
  flex: 1;
  justify-content: center;
}

.main-nav a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.main-nav a:hover,
.main-nav a.active {
  color: white;
  background: rgba(255, 255, 255, 0.1);
}

.search-box {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 20px;
  padding: 5px 15px;
  gap: 10px;
}

.search-box input {
  background: transparent;
  border: none;
  outline: none;
  color: white;
  width: 200px;
  font-size: 14px;
}

.search-box input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.search-icon {
  cursor: pointer;
  font-size: 16px;
}

/* 主内容区 */
.main-content {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
  display: flex;
  gap: 30px;
}

.content-area {
  flex: 1;
  min-width: 0;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.content-header h2 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.blog-count {
  color: #666;
  font-size: 14px;
}

/* 博客卡片 */
.blog-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.blog-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.blog-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.blog-header {
  margin-bottom: 15px;
}

.blog-title {
  font-size: 20px;
  color: #333;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.blog-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #666;
  align-items: center;
}

.blog-meta .avatar,
.blog-meta .icon {
  margin-right: 5px;
}

.blog-content {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.blog-content .summary {
  flex: 1;
  color: #666;
  line-height: 1.8;
  margin: 0;
}

.blog-image {
  width: 150px;
  height: 100px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.blog-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.blog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.blog-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 10px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.tag:hover {
  opacity: 0.8;
}

.type-badge {
  padding: 4px 12px;
  background: #409EFF;
  color: white;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: background 0.3s;
}

.type-badge:hover {
  background: #66b1ff;
}

/* 侧边栏 */
.sidebar {
  width: 280px;
  flex-shrink: 0;
}

.sidebar-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.section-header h3 {
  font-size: 16px;
  color: #333;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.more-link {
  font-size: 12px;
  color: #409EFF;
  text-decoration: none;
}

.more-link:hover {
  text-decoration: underline;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  cursor: pointer;
  transition: color 0.3s;
  border-bottom: 1px solid #f5f5f5;
}

.category-list li:last-child {
  border-bottom: none;
}

.category-list li:hover,
.category-list li.active {
  color: #409EFF;
}

.category-name {
  flex: 1;
}

.category-count {
  background: #f5f5f5;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  padding: 4px 10px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
  cursor: pointer;
  transition: opacity 0.3s;
}

.tag-item:hover,
.tag-item.active {
  opacity: 0.8;
  transform: scale(1.05);
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
  padding: 20px;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.loading,
.empty {
  text-align: center;
  padding: 50px;
  color: #666;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .header .container {
    flex-wrap: wrap;
  }
  
  .main-nav {
    order: 3;
    width: 100%;
    margin-top: 10px;
    justify-content: flex-start;
    gap: 15px;
  }
  
  .search-box {
    width: 150px;
  }
  
  .search-box input {
    width: 100px;
  }
}
</style>

