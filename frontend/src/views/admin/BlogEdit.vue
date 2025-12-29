<template>
  <div class="admin-blog-edit">
    <form @submit.prevent="handleSave" class="card">
      <div class="form-group">
        <label>标题 *</label>
        <input v-model="form.title" type="text" required placeholder="请输入博客标题" />
      </div>
      
      <div class="form-group">
        <label>分类 *</label>
        <select v-model="form.typeId" required>
          <option value="">请选择分类</option>
          <option v-for="type in types" :key="type.id" :value="type.id">{{ type.name }}</option>
        </select>
      </div>
      
      <div class="form-group">
        <label>标签</label>
        <div class="tag-selector">
          <div v-for="tag in tags" :key="tag.id" class="tag-item">
            <input type="checkbox" :value="tag.id" v-model="form.tagIds" :id="`tag-${tag.id}`" />
            <label :for="`tag-${tag.id}`" :style="{ backgroundColor: tag.color }">{{ tag.name }}</label>
          </div>
        </div>
      </div>
      
      <div class="form-group">
        <label>首图URL</label>
        <input v-model="form.firstPicture" type="text" placeholder="请输入图片URL" />
      </div>
      
      <div class="form-group">
        <label>内容 *</label>
        <div class="markdown-editor">
          <div class="editor-pane">
            <div class="pane-header">编辑</div>
            <textarea 
              v-model="form.content" 
              class="markdown-input" 
              required 
              placeholder="支持Markdown格式，右侧实时预览..."
              @input="handleInput"
            ></textarea>
          </div>
          <div class="preview-pane">
            <div class="pane-header">预览</div>
            <div class="markdown-preview" v-html="renderedContent"></div>
          </div>
        </div>
      </div>
      
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="form.published" :true-value="1" :false-value="0" />
          立即发布
        </label>
      </div>
      
      <div class="form-actions">
        <button type="submit" class="btn btn-primary" :disabled="loading">
          {{ loading ? '保存中...' : '保存' }}
        </button>
        <router-link to="/admin/blogs" class="btn">取消</router-link>
      </div>
    </form>
  </div>
</template>

<script>
import { blogApi, typeApi, tagApi } from '../../api'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

export default {
  name: 'AdminBlogEdit',
  data() {
    return {
      form: {
        title: '',
        content: '',
        typeId: '',
        tagIds: [],
        firstPicture: '',
        published: 1
      },
      types: [],
      tags: [],
      loading: false
    }
  },
  computed: {
    renderedContent() {
      if (!this.form.content) return '<p class="empty-preview">输入Markdown内容，右侧将实时显示预览效果...</p>'
      return marked(this.form.content)
    }
  },
  mounted() {
    // 配置marked
    marked.setOptions({
      highlight: function(code, lang) {
        if (lang && hljs.getLanguage(lang)) {
          try {
            return hljs.highlight(code, { language: lang }).value
          } catch (err) {
            return hljs.highlightAuto(code).value
          }
        }
        return hljs.highlightAuto(code).value
      },
      breaks: true, // 支持换行
      gfm: true // 支持GitHub风格的Markdown
    })
    
    this.loadTypes()
    this.loadTags()
    if (this.$route.params.id) {
      this.loadBlog()
    }
  },
  methods: {
    async loadTypes() {
      try {
        this.types = await typeApi.getAdminList()
      } catch (error) {
        console.error('加载分类失败', error)
      }
    },
    async loadTags() {
      try {
        this.tags = await tagApi.getAdminList()
      } catch (error) {
        console.error('加载标签失败', error)
      }
    },
    async loadBlog() {
      try {
        const blog = await blogApi.getAdminDetail(this.$route.params.id)
        this.form = {
          title: blog.title,
          content: blog.content,
          typeId: blog.typeId,
          tagIds: blog.tags ? blog.tags.map(t => t.id) : [],
          firstPicture: blog.firstPicture || '',
          published: blog.published || 1
        }
      } catch (error) {
        console.error('加载博客失败', error)
        this.$router.push('/admin/blogs')
      }
    },
    handleInput() {
      // 实时更新预览，computed属性会自动处理
    },
    async handleSave() {
      this.loading = true
      try {
        const id = this.$route.params.id
        if (id) {
          await blogApi.update(id, this.form)
        } else {
          await blogApi.save(this.form)
        }
        this.$router.push('/admin/blogs')
      } catch (error) {
        alert('保存失败：' + (error.message || '未知错误'))
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style scoped>
.tag-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  display: flex;
  align-items: center;
}

.tag-item input[type="checkbox"] {
  display: none;
}

.tag-item label {
  padding: 6px 12px;
  border-radius: 4px;
  color: white;
  cursor: pointer;
  opacity: 0.6;
  transition: opacity 0.3s;
}

.tag-item input[type="checkbox"]:checked + label {
  opacity: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

/* Markdown编辑器样式 */
.markdown-editor {
  display: flex;
  gap: 1px;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f5f5;
  min-height: 600px;
}

.editor-pane,
.preview-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
}

.pane-header {
  padding: 10px 15px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  font-weight: 600;
  font-size: 14px;
  color: #495057;
}

.markdown-input {
  flex: 1;
  width: 100%;
  padding: 15px;
  border: none;
  outline: none;
  resize: none;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
  font-size: 14px;
  line-height: 1.6;
  color: #333;
  background: #fff;
}

.markdown-input:focus {
  background: #fafafa;
}

.markdown-preview {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  line-height: 1.8;
  color: #333;
  background: #fff;
}

.empty-preview {
  color: #999;
  text-align: center;
  padding: 50px 20px;
  font-style: italic;
}

/* Markdown预览样式 */
.markdown-preview :deep(h1),
.markdown-preview :deep(h2),
.markdown-preview :deep(h3),
.markdown-preview :deep(h4),
.markdown-preview :deep(h5),
.markdown-preview :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-preview :deep(h1) {
  font-size: 2em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-preview :deep(h2) {
  font-size: 1.5em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-preview :deep(h3) {
  font-size: 1.25em;
}

.markdown-preview :deep(p) {
  margin-bottom: 16px;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  margin-bottom: 16px;
  padding-left: 30px;
}

.markdown-preview :deep(li) {
  margin-bottom: 8px;
}

.markdown-preview :deep(blockquote) {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin-bottom: 16px;
}

.markdown-preview :deep(code) {
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  font-size: 85%;
  padding: 0.2em 0.4em;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', 'Consolas', 'source-code-pro', monospace;
}

.markdown-preview :deep(pre) {
  background-color: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  overflow: auto;
  margin-bottom: 16px;
  line-height: 1.45;
}

.markdown-preview :deep(pre code) {
  background-color: transparent;
  padding: 0;
  font-size: 100%;
  word-break: normal;
  white-space: pre;
}

.markdown-preview :deep(a) {
  color: #0366d6;
  text-decoration: none;
}

.markdown-preview :deep(a:hover) {
  text-decoration: underline;
}

.markdown-preview :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 16px 0;
}

.markdown-preview :deep(table) {
  border-collapse: collapse;
  margin-bottom: 16px;
  width: 100%;
}

.markdown-preview :deep(table th),
.markdown-preview :deep(table td) {
  border: 1px solid #dfe2e5;
  padding: 6px 13px;
}

.markdown-preview :deep(table th) {
  background-color: #f6f8fa;
  font-weight: 600;
}

.markdown-preview :deep(hr) {
  height: 0.25em;
  padding: 0;
  margin: 24px 0;
  background-color: #e1e4e8;
  border: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .markdown-editor {
    flex-direction: column;
    min-height: 400px;
  }
  
  .editor-pane,
  .preview-pane {
    min-height: 300px;
  }
}
</style>

