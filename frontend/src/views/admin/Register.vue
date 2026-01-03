<template>
  <div class="register-page">
    <div class="register-layout">
      <!-- 左侧图片区域 -->
      <div class="register-left">
        <div class="image-container">
          <img src="https://picsum.photos/800/600" alt="博客注册" class="register-image" />
          <div class="image-overlay">
            <p>加入我们，开始您的创作之旅。</p>
            <p>博客注册</p>
          </div>
        </div>
      </div>

      <!-- 右侧注册表单区域 -->
      <div class="register-right">
        <div class="register-container">
          <!-- Logo -->
          <div class="logo-section">
            <div class="logo-icon">💬</div>
            <h1 class="logo-text">BLOG PRESS</h1>
          </div>

          <!-- 注册表单 -->
          <form @submit.prevent="handleRegister" class="register-form">
            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">👤</span>
                <input 
                  v-model="form.username" 
                  type="text" 
                  required 
                  placeholder="用户名" 
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
                  minlength="6"
                />
                <span class="password-toggle" @click="togglePasswordVisibility">
                  {{ showPassword ? '👁️' : '👁️‍🗨️' }}
                </span>
              </div>
            </div>

            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">🔒</span>
                <input 
                  v-model="form.confirmPassword" 
                  type="password" 
                  required 
                  placeholder="确认密码" 
                  class="form-input"
                  minlength="6"
                />
              </div>
            </div>

            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">✏️</span>
                <input 
                  v-model="form.nickname" 
                  type="text" 
                  placeholder="昵称（可选）" 
                  class="form-input"
                />
              </div>
            </div>

            <div class="form-group">
              <div class="input-wrapper">
                <span class="input-icon">📧</span>
                <input 
                  v-model="form.email" 
                  type="email" 
                  placeholder="邮箱（可选）" 
                  class="form-input"
                />
              </div>
            </div>

            <div v-if="error" class="error-message">{{ error }}</div>

            <button type="submit" class="btn btn-register" :disabled="loading">
              {{ loading ? '注册中...' : '注册' }}
            </button>
          </form>

          <!-- 返回登录链接 -->
          <div class="back-to-login">
            <span>已有账号？</span>
            <router-link to="/admin/login">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { userApi } from '../../api'

export default {
  name: 'AdminRegister',
  data() {
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        email: ''
      },
      loading: false,
      error: '',
      showPassword: false
    }
  },
  methods: {
    async handleRegister() {
      // 验证密码
      if (this.form.password !== this.form.confirmPassword) {
        this.error = '两次输入的密码不一致'
        return
      }

      if (this.form.password.length < 6) {
        this.error = '密码长度至少为6位'
        return
      }

      this.loading = true
      this.error = ''
      this.registerSuccess = false
      try {
        const user = await userApi.register({
          username: this.form.username,
          password: this.form.password,
          nickname: this.form.nickname || null,
          email: this.form.email || null
        })
        sessionStorage.setItem('userId', user.id)
        sessionStorage.setItem('username', user.username)
        // 注册成功后直接跳转到首页
        this.$router.push('/')
      } catch (error) {
        this.error = error.message || '注册失败，请检查输入信息'
        this.registerSuccess = false
      } finally {
        this.loading = false
      }
    },
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword
      const passwordInputs = this.$el.querySelectorAll('input[type="password"]')
      passwordInputs.forEach(input => {
        if (this.showPassword) {
          input.type = 'text'
        } else {
          input.type = 'password'
        }
      })
    }
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  background-color: #fff;
  overflow: hidden;
}

.register-layout {
  display: flex;
  width: 100%;
  min-height: 100vh;
}

/* 左侧图片区域 */
.register-left {
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

.register-image {
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

/* 右侧注册表单区域 */
.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  padding: 40px 20px;
  min-height: 100vh;
}

.register-container {
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
.register-form {
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

.btn-register {
  width: 100%;
  padding: 14px;
  background-color: rgba(119, 175, 35, 1);
  color: white;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s;
  cursor: pointer;
  border: none;
}

.btn-register:hover:not(:disabled) {
  background-color: #555;
}

.btn-register:disabled {
  background-color: #999;
  cursor: not-allowed;
}

/* 注册成功后的操作区域 */
.success-actions {
  margin-top: 30px;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 8px;
  border: 1px solid #b3d8ff;
}

.success-message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
  font-size: 16px;
  color: #409EFF;
  font-weight: 500;
}

.success-icon {
  font-size: 20px;
}

.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
}

.btn-admin, .btn-home {
  padding: 12px 24px;
  border-radius: 6px;
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  display: inline-block;
}

.btn-admin {
  background: #409EFF;
  color: white;
}

.btn-admin:hover {
  background: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.btn-home {
  background: white;
  color: #409EFF;
  border: 1px solid #409EFF;
}

.btn-home:hover {
  background: #f0f9ff;
}

.back-to-login {
  margin-top: 30px;
  text-align: center;
  font-size: 14px;
  color: #606266;
}

.back-to-login a {
  color: #409EFF;
  text-decoration: none;
  margin-left: 5px;
  transition: color 0.3s;
}

.back-to-login a:hover {
  color: #66b1ff;
  text-decoration: underline;
}

/* 响应式设计 */
@media (min-width: 768px) {
  .register-left {
    display: block;
  }
  
  .register-right {
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
  .register-layout {
    flex-direction: column;
  }
  
  .register-left {
    display: block;
    height: 300px;
    flex: none;
  }
  
  .register-right {
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

