<template>
  <div class="admin-profile">
    <div class="card">
      <h2>个人信息</h2>
      <form @submit.prevent="handleUpdateInfo">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="userInfo.username" type="text" disabled />
        </div>
        <div class="form-group">
          <label>昵称</label>
          <input v-model="form.nickname" type="text" placeholder="请输入昵称" />
        </div>
        <div class="form-group">
          <label>邮箱</label>
          <input v-model="form.email" type="email" placeholder="请输入邮箱" />
        </div>
        <div class="form-group">
          <label>头像URL</label>
          <input v-model="form.avatar" type="text" placeholder="请输入头像URL" />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="loading">
            {{ loading ? '保存中...' : '保存' }}
          </button>
        </div>
      </form>
    </div>
    
    <div class="card">
      <h2>修改密码</h2>
      <form @submit.prevent="handleChangePassword">
        <div class="form-group">
          <label>原密码 *</label>
          <input v-model="passwordForm.oldPassword" type="password" required placeholder="请输入原密码" />
        </div>
        <div class="form-group">
          <label>新密码 *</label>
          <input v-model="passwordForm.newPassword" type="password" required placeholder="请输入新密码" />
        </div>
        <div class="form-group">
          <label>确认新密码 *</label>
          <input v-model="passwordForm.confirmPassword" type="password" required placeholder="请再次输入新密码" />
        </div>
        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="passwordLoading">
            {{ passwordLoading ? '修改中...' : '修改密码' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import { userApi } from '../../api'

export default {
  name: 'AdminProfile',
  data() {
    return {
      userInfo: {},
      form: {
        nickname: '',
        email: '',
        avatar: ''
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      loading: false,
      passwordLoading: false
    }
  },
  mounted() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        this.userInfo = await userApi.getUserInfo()
        this.form = {
          nickname: this.userInfo.nickname || '',
          email: this.userInfo.email || '',
          avatar: this.userInfo.avatar || ''
        }
      } catch (error) {
        console.error('加载用户信息失败', error)
      }
    },
    async handleUpdateInfo() {
      this.loading = true
      try {
        await userApi.updateUserInfo(this.form)
        alert('保存成功')
        this.loadUserInfo()
      } catch (error) {
        alert('保存失败：' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    },
    async handleChangePassword() {
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }
      if (this.passwordForm.newPassword.length < 6) {
        alert('密码长度至少6位')
        return
      }
      this.passwordLoading = true
      try {
        await userApi.changePassword({
          oldPassword: this.passwordForm.oldPassword,
          newPassword: this.passwordForm.newPassword
        })
        alert('修改密码成功')
        this.passwordForm = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
      } catch (error) {
        alert('修改密码失败：' + (error.message || '未知错误'))
      } finally {
        this.passwordLoading = false
      }
    }
  }
}
</script>

<style scoped>
.admin-profile {
  max-width: 800px;
}

.admin-profile h2 {
  margin-bottom: 20px;
  color: #333;
}

.form-actions {
  margin-top: 20px;
}
</style>

