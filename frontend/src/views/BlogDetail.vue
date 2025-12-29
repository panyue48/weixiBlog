<template>
  <div class="blog-detail">
    <header class="header">
      <div class="container">
        <h1>个人博客</h1>
        <nav>
          <router-link to="/">返回首页</router-link>
        </nav>
      </div>
    </header>
    
    <main class="container">
      <div v-if="blog" class="blog-content card">
        <h1>{{ blog.title }}</h1>
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
        <div class="content" v-html="renderedContent"></div>
      </div>
      <div v-else class="loading">加载中...</div>
    </main>
  </div>
</template>

<script>
import { blogApi } from '../api'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

export default {
  name: 'BlogDetail',
  data() {
    return {
      blog: null
    }
  },
  computed: {
    renderedContent() {
      if (!this.blog || !this.blog.content) return ''
      return marked(this.blog.content)
    }
  },
  mounted() {
    this.loadBlog()
    // 配置marked
    marked.setOptions({
      highlight: function(code, lang) {
        if (lang && hljs.getLanguage(lang)) {
          return hljs.highlight(code, { language: lang }).value
        }
        return hljs.highlightAuto(code).value
      }
    })
  },
  methods: {
    async loadBlog() {
      try {
        const id = this.$route.params.id
        this.blog = await blogApi.getDetail(id)
      } catch (error) {
        console.error('加载博客详情失败', error)
        this.$router.push('/')
      }
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString('zh-CN')
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

.blog-content h1 {
  font-size: 32px;
  margin-bottom: 20px;
  color: #333;
}

.meta {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.type {
  color: #409EFF;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.tag {
  padding: 4px 8px;
  border-radius: 4px;
  color: white;
  font-size: 12px;
}

.content {
  line-height: 1.8;
  color: #333;
}

.content :deep(h1),
.content :deep(h2),
.content :deep(h3) {
  margin-top: 24px;
  margin-bottom: 16px;
}

.content :deep(p) {
  margin-bottom: 16px;
}

.content :deep(code) {
  background-color: #f4f4f4;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}

.content :deep(pre) {
  background-color: #f4f4f4;
  padding: 16px;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 16px;
}

.content :deep(pre code) {
  background-color: transparent;
  padding: 0;
}

.loading {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #666;
}
</style>

