<template>
  <div class="about">
    <header class="header">
      <div class="container">
        <h1 class="logo">Blog</h1>
        <nav class="main-nav">
          <router-link to="/">首页</router-link>
          <router-link to="/types">分类</router-link>
          <router-link to="/tags">标签</router-link>
          <router-link to="/archive">归档</router-link>
          <router-link to="/about" class="active">关于我</router-link>
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
      <div class="about-content">
        <h2>关于我</h2>
        <div class="about-card">
          <p>这是一个个人博客系统，使用 Vue 3 + Spring Boot 开发。</p>
          <p>欢迎访问我的博客，分享技术心得和生活感悟。</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'About',
  data() {
    return {
      searchKeyword: ''
    }
  },
  methods: {
    handleSearch() {
      this.$router.push({ path: '/', query: { keyword: this.searchKeyword } })
    }
  }
}
</script>

<style scoped>
.about {
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

.about-content h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 30px;
}

.about-card {
  background: white;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  line-height: 1.8;
  color: #666;
}

.about-card p {
  margin-bottom: 15px;
}
</style>

