<template>
  <div class="admin-dashboard">
    <aside class="sidebar">
      <div class="logo">
        <h2>博客管理</h2>
      </div>
      <nav class="nav">
        <router-link to="/admin/blogs" class="nav-item">
          <span>📝</span> 博客管理
        </router-link>
        <router-link to="/admin/types" class="nav-item">
          <span>📂</span> 分类管理
        </router-link>
        <router-link to="/admin/tags" class="nav-item">
          <span>🏷️</span> 标签管理
        </router-link>
        <router-link to="/admin/profile" class="nav-item">
          <span>👤</span> 个人信息
        </router-link>
      </nav>
      <div class="logout">
        <button @click="handleLogout" class="btn btn-danger">退出登录</button>
      </div>
    </aside>
    
    <main class="main-content">
      <header class="header">
        <h3>{{ currentPageTitle }}</h3>
        <div class="user-info">
          <span>欢迎，{{ username }}</span>
        </div>
      </header>
      <div class="content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script>
import { userApi } from '../../api'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      username: sessionStorage.getItem('username') || ''
    }
  },
  computed: {
    currentPageTitle() {
      const route = this.$route.name
      const titles = {
        'AdminBlogList': '博客管理',
        'AdminBlogEdit': '编辑博客',
        'AdminTypeList': '分类管理',
        'AdminTagList': '标签管理',
        'AdminProfile': '个人信息'
      }
      return titles[route] || '后台管理'
    }
  },
  methods: {
    async handleLogout() {
      try {
        await userApi.logout()
        sessionStorage.clear()
        this.$router.push('/admin/login')
      } catch (error) {
        console.error('退出登录失败', error)
        sessionStorage.clear()
        this.$router.push('/admin/login')
      }
    }
  }
}
</script>

<style scoped>
.admin-dashboard {
  display: flex;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.sidebar {
  width: 250px;
  background: white;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.logo {
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.logo h2 {
  color: #333;
  font-size: 20px;
}

.nav {
  flex: 1;
  padding: 20px 0;
}

.nav-item {
  display: block;
  padding: 12px 20px;
  color: #666;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.nav-item:hover {
  background-color: #f0f0f0;
  color: #409EFF;
}

.nav-item.router-link-active {
  background-color: #ecf5ff;
  color: #409EFF;
  border-left-color: #409EFF;
}

.nav-item span {
  margin-right: 8px;
}

.logout {
  padding: 20px;
  border-top: 1px solid #eee;
}

.logout .btn {
  width: 100%;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: white;
  padding: 20px 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h3 {
  color: #333;
  font-size: 18px;
}

.user-info {
  color: #666;
  font-size: 14px;
}

.content {
  flex: 1;
  padding: 30px;
  overflow-y: auto;
}
</style>

