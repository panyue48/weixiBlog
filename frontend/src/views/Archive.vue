<template>
  <div class="archive">
    <header class="header">
      <div class="container">
        <h1 class="logo">Blog</h1>
        <nav class="main-nav">
          <router-link to="/">首页</router-link>
          <router-link to="/types">分类</router-link>
          <router-link to="/tags">标签</router-link>
          <router-link to="/archive" class="active">归档</router-link>
          <router-link to="/about">关于我</router-link>
        </nav>
        <div class="search-box">
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="Search..." 
            @keyup.enter="handleSearch"
          />
          <span class="search-icon" @click="handleSearch">🔍</span>
        </div>
      </div>
    </header>

    <main class="container">
      <div class="archive-content">
        <h2>归档</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="archiveList.length === 0" class="empty">暂无博客</div>
        <div v-else class="archive-list">
          <div v-for="(group, yearMonth) in archiveList" :key="yearMonth" class="archive-group">
            <h3 class="archive-date">{{ yearMonth }}</h3>
            <div class="archive-items">
              <div 
                v-for="blog in group" 
                :key="blog.id" 
                class="archive-item"
                @click="goToDetail(blog.id)"
              >
                <span class="archive-item-date">{{ formatDay(blog.createTime) }}</span>
                <span class="archive-item-title">{{ blog.title }}</span>
                <span class="archive-item-views">👁 {{ blog.views || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { blogApi } from '../api'

export default {
  name: 'Archive',
  data() {
    return {
      blogs: [],
      loading: false,
      searchKeyword: ''
    }
  },
  computed: {
    archiveList() {
      // 按年月分组
      const groups = {}
      this.blogs.forEach(blog => {
        const date = new Date(blog.createTime)
        const yearMonth = `${date.getFullYear()}年${String(date.getMonth() + 1).padStart(2, '0')}月`
        
        if (!groups[yearMonth]) {
          groups[yearMonth] = []
        }
        groups[yearMonth].push(blog)
      })
      
      // 按日期排序（最新的在前）
      Object.keys(groups).forEach(key => {
        groups[key].sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      })
      
      // 按年月排序（最新的在前）
      const sortedGroups = {}
      Object.keys(groups).sort((a, b) => {
        const dateA = new Date(a.replace('年', '-').replace('月', ''))
        const dateB = new Date(b.replace('年', '-').replace('月', ''))
        return dateB - dateA
      }).forEach(key => {
        sortedGroups[key] = groups[key]
      })
      
      return sortedGroups
    }
  },
  mounted() {
    this.loadAllBlogs()
  },
  methods: {
    async loadAllBlogs() {
      this.loading = true
      try {
        // 获取所有已发布的博客（使用较大的pageSize）
        const result = await blogApi.getList({
          current: 1,
          size: 1000, // 获取足够多的数据
          keyword: this.searchKeyword || undefined
        })
        this.blogs = result.records || []
      } catch (error) {
        console.error('加载归档失败', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.loadAllBlogs()
    },
    goToDetail(id) {
      this.$router.push(`/blog/${id}`)
    },
    formatDay(date) {
      if (!date) return ''
      const d = new Date(date)
      return String(d.getDate()).padStart(2, '0')
    }
  }
}
</script>

<style scoped>
.archive {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #2c3e50;
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

.container {
  max-width: 1200px;
  margin: 30px auto;
  padding: 0 20px;
}

.archive-content h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
}

.archive-list {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.archive-group {
  margin-bottom: 40px;
}

.archive-group:last-child {
  margin-bottom: 0;
}

.archive-date {
  font-size: 20px;
  color: #409EFF;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #409EFF;
}

.archive-items {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.archive-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.3s;
}

.archive-item:hover {
  background: #f5f5f5;
}

.archive-item-date {
  width: 40px;
  text-align: center;
  color: #666;
  font-size: 14px;
}

.archive-item-title {
  flex: 1;
  color: #333;
  font-size: 15px;
}

.archive-item-title:hover {
  color: #409EFF;
}

.archive-item-views {
  color: #999;
  font-size: 13px;
}

.loading,
.empty {
  text-align: center;
  padding: 50px;
  color: #666;
}
</style>

