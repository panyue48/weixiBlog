<template>
  <div class="tags">
    <header class="header">
      <div class="container">
        <h1 class="logo">Blog</h1>
        <nav class="main-nav">
          <router-link to="/">首页</router-link>
          <router-link to="/types">分类</router-link>
          <router-link to="/tags" class="active">标签</router-link>
          <router-link to="/archive">归档</router-link>
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
      <div class="tags-content">
        <h2>标签</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="tags.length === 0" class="empty">暂无标签</div>
        <div v-else class="tag-cloud">
          <span 
            v-for="tag in tags" 
            :key="tag.id" 
            class="tag-item"
            :style="{ backgroundColor: tag.color }"
            @click="goToTag(tag.id)"
          >
            {{ tag.name }}
          </span>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { tagApi } from '../api'

export default {
  name: 'Tags',
  data() {
    return {
      tags: [],
      loading: false,
      searchKeyword: ''
    }
  },
  mounted() {
    this.loadTags()
  },
  methods: {
    async loadTags() {
      this.loading = true
      try {
        this.tags = await tagApi.getList() || []
      } catch (error) {
        console.error('加载标签失败', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.$router.push({ path: '/', query: { keyword: this.searchKeyword } })
    },
    goToTag(tagId) {
      this.$router.push({ path: '/', query: { tagId } })
    }
  }
}
</script>

<style scoped>
.tags {
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

.tags-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tags-content h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
}

.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.tag-item {
  padding: 10px 20px;
  border-radius: 20px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tag-item:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.loading,
.empty {
  text-align: center;
  padding: 50px;
  color: #666;
}
</style>

