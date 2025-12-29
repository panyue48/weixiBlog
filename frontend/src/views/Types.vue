<template>
  <div class="types">
    <header class="header">
      <div class="container">
        <h1 class="logo">Blog</h1>
        <nav class="main-nav">
          <router-link to="/">首页</router-link>
          <router-link to="/types" class="active">分类</router-link>
          <router-link to="/tags">标签</router-link>
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
      <div class="types-content">
        <h2>分类</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="types.length === 0" class="empty">暂无分类</div>
        <div v-else class="type-grid">
          <div 
            v-for="type in types" 
            :key="type.id" 
            class="type-card"
            @click="goToType(type.id)"
          >
            <h3>{{ type.name }}</h3>
            <p class="type-count">{{ type.blogCount || 0 }} 篇博客</p>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { typeApi, blogApi } from '../api'

export default {
  name: 'Types',
  data() {
    return {
      types: [],
      loading: false,
      searchKeyword: ''
    }
  },
  mounted() {
    this.loadTypes()
  },
  methods: {
    async loadTypes() {
      this.loading = true
      try {
        const types = await typeApi.getList()
        this.types = types || []
        
        // 加载每个分类的博客数量
        for (let type of this.types) {
          try {
            const result = await blogApi.getList({ typeId: type.id, current: 1, size: 1 })
            type.blogCount = result.total || 0
          } catch (error) {
            type.blogCount = 0
          }
        }
      } catch (error) {
        console.error('加载分类失败', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.$router.push({ path: '/', query: { keyword: this.searchKeyword } })
    },
    goToType(typeId) {
      this.$router.push({ path: '/', query: { typeId } })
    }
  }
}
</script>

<style scoped>
.types {
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

.types-content h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
}

.type-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.type-card {
  background: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.type-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.type-card h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 10px 0;
}

.type-count {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.loading,
.empty {
  text-align: center;
  padding: 50px;
  color: #666;
}
</style>

