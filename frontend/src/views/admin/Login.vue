<template>
  <div class="login-page">
    <div class="login-layout">
      <!-- 左侧图片区域 -->
      <div class="login-left">
        <div class="image-container">
          <img src="https://picsum.photos/800/600" alt="博客管理" class="login-image" />
          <div class="image-overlay">
            <p>创作您的故事，分享您的世界。</p>
            <p>博客管理</p>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单区域 -->
      <div class="login-right">
        <div class="login-container">
          <!-- Logo -->
          <div class="logo-section">
            <div class="logo-icon">💬</div>
            <h1 class="logo-text">BLOG PRESS</h1>
          </div>

          <!-- 登录表单 -->
          <form @submit.prevent="handleLogin" class="login-form">
            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">👤</span>
                <input 
                  v-model="form.username" 
                  type="text" 
                  required 
                  placeholder="用户名/邮箱" 
                  class="form-input"
                />
              </div>
            </div>
            
            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <input 
                  v-model="form.password" 
                  type="password" 
                  required 
                  placeholder="密码" 
                  class="form-input"
                />
                <span class="password-toggle" @click="togglePasswordVisibility">
                  {{ showPassword ? '👁️' : '👁️‍🗨️' }}
                </span>
              </div>
            </div>

            <div v-if="error" class="error-message">{{ error }}</div>

            <div class="form-options">
              <label class="remember-me">
                <input type="checkbox" v-model="form.rememberMe" />
                <span>记住我</span>
              </label>
              <a href="#" class="forgot-password" @click.prevent="handleForgotPassword">
                忘记密码?
              </a>
            </div>

            <button type="submit" class="btn btn-login" :disabled="loading">
              {{ loading ? '登录中...' : '登录' }}
            </button>
          </form>

          <!-- 返回博客链接 -->
          <div class="back-to-blog">
            <a href="/" @click.prevent="$router.push('/')">返回博客</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { userApi } from '../../api'

export default {
  name: 'AdminLogin',
  data() {
    return {
      form: {
        username: '',
        password: '',
        rememberMe: false
      },
      loading: false,
      error: '',
      showPassword: false
    }
  },
  methods: {
    async handleLogin() {
      this.loading = true
      this.error = ''
      try {
        const user = await userApi.login(this.form)
        sessionStorage.setItem('userId', user.id)
        sessionStorage.setItem('username', user.username)
        if (this.form.rememberMe) {
          localStorage.setItem('rememberMe', 'true')
        }
        this.$router.push('/admin')
      } catch (error) {
        this.error = error.message || '登录失败，请检查用户名和密码'
      } finally {
        this.loading = false
      }
    },
    togglePasswordVisibility() {
      const passwordInput = this.$el.querySelector('input[type="password"]')
      if (passwordInput) {
        if (passwordInput.type === 'password') {
          passwordInput.type = 'text'
          this.showPassword = true
        } else {
          passwordInput.type = 'password'
          this.showPassword = false
        }
      }
    },
    handleForgotPassword() {
      alert('忘记密码功能暂未开放')
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  background-color: #fff;
  overflow: hidden;
}

.login-layout {
  display: flex;
  width: 100%;
  min-height: 100vh;
}

/* 左侧图片区域 */
.login-left {
  flex: 1;
  position: relative;
  display: none;
}

.image-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.login-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(60, 60, 60, 0.9);
  color: white;
  padding: 30px 40px;
  font-size: 18px;
  line-height: 1.8;
}

.image-overlay p {
  margin: 0;
  font-weight: 400;
}

.image-overlay p:first-child {
  margin-bottom: 5px;
}

/* 右侧登录表单区域 */
.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  padding: 40px 20px;
  min-height: 100vh;
}

.login-container {
  width: 100%;
  max-width: 420px;
}

/* Logo区域 */
.logo-section {
  display: flex;
  align-items: center;
  margin-bottom: 50px;
}

.logo-icon {
  font-size: 32px;
  margin-right: 12px;
  background: #409EFF;
  color: white;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.logo-text {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0;
  letter-spacing: 1px;
}

/* 表单样式 */
.login-form {
  width: 100%;
}

.form-group {
  margin-bottom: 24px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 16px;
  font-size: 18px;
  z-index: 1;
  color: #909399;
}

.form-input {
  width: 100%;
  padding: 14px 16px 14px 48px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 15px;
  transition: all 0.3s;
  background-color: #fff;
}

.form-input:focus {
  outline: none;
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.form-input::placeholder {
  color: #c0c4cc;
}

.password-toggle {
  position: absolute;
  right: 16px;
  cursor: pointer;
  font-size: 18px;
  z-index: 1;
  user-select: none;
}

.error-message {
  color: #F56C6C;
  margin-bottom: 20px;
  font-size: 14px;
  padding: 10px;
  background-color: #fef0f0;
  border-radius: 4px;
  border-left: 3px solid #F56C6C;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.remember-me {
  display: flex;
  align-items: center;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
}

.remember-me input {
  margin-right: 8px;
  cursor: pointer;
  width: 16px;
  height: 16px;
}

.forgot-password {
  color: #409EFF;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s;
}

.forgot-password:hover {
  color: #66b1ff;
}

.btn-login {
  width: 100%;
  padding: 14px;
  background-color: #333;
  color: white;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
  cursor: pointer;
}

.btn-login:hover:not(:disabled) {
  background-color: #555;
}

.btn-login:disabled {
  background-color: #999;
  cursor: not-allowed;
}

.back-to-blog {
  margin-top: 30px;
  text-align: center;
}

.back-to-blog a {
  color: #606266;
  font-size: 14px;
  text-decoration: none;
  transition: color 0.3s;
}

.back-to-blog a:hover {
  color: #409EFF;
}

/* 响应式设计 */
@media (min-width: 768px) {
  .login-left {
    display: block;
  }
  
  .login-right {
    padding: 60px 40px;
  }
}

@media (min-width: 1024px) {
  .image-overlay {
    padding: 40px 50px;
    font-size: 20px;
  }
}

@media (max-width: 767px) {
  .login-layout {
    flex-direction: column;
  }
  
  .login-left {
    display: block;
    height: 300px;
    flex: none;
  }
  
  .login-right {
    flex: 1;
    min-height: auto;
  }
  
  .logo-section {
    margin-bottom: 40px;
  }
  
  .logo-text {
    font-size: 24px;
  }
}
</style>

