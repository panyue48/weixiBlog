<template>
  <view class="markdown-viewer">
    <rich-text :nodes="formatContent(content)"></rich-text>
  </view>
</template>

<script setup>
const props = defineProps({
  content: {
    type: String,
    default: ''
  }
})

// 格式化内容（简单的Markdown转HTML）
const formatContent = (content) => {
  if (!content) return ''
  
  // 简单的Markdown处理
  let html = content
    // 标题
    .replace(/^### (.*$)/gim, '<h3>$1</h3>')
    .replace(/^## (.*$)/gim, '<h2>$1</h2>')
    .replace(/^# (.*$)/gim, '<h1>$1</h1>')
    // 粗体
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    // 斜体
    .replace(/\*(.*?)\*/g, '<em>$1</em>')
    // 代码块
    .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
    // 行内代码
    .replace(/`(.*?)`/g, '<code>$1</code>')
    // 链接
    .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2">$1</a>')
    // 换行
    .replace(/\n/g, '<br/>')
  
  return html
}
</script>

<style lang="scss" scoped>
.markdown-viewer {
  :deep(h1) {
    font-size: 36rpx;
    font-weight: bold;
    margin: 20rpx 0;
    color: #333;
  }
  
  :deep(h2) {
    font-size: 32rpx;
    font-weight: bold;
    margin: 18rpx 0;
    color: #333;
  }
  
  :deep(h3) {
    font-size: 30rpx;
    font-weight: bold;
    margin: 16rpx 0;
    color: #333;
  }
  
  :deep(p) {
    margin: 10rpx 0;
    line-height: 1.8;
  }
  
  :deep(code) {
    background-color: #f5f5f5;
    padding: 2rpx 6rpx;
    border-radius: 4rpx;
    font-family: monospace;
    font-size: 26rpx;
  }
  
  :deep(pre) {
    background-color: #f5f5f5;
    padding: 20rpx;
    border-radius: 8rpx;
    overflow-x: auto;
    margin: 20rpx 0;
    
    code {
      background-color: transparent;
      padding: 0;
    }
  }
  
  :deep(strong) {
    font-weight: bold;
  }
  
  :deep(em) {
    font-style: italic;
  }
  
  :deep(a) {
    color: #3c9cff;
    text-decoration: underline;
  }
  
  :deep(ul), :deep(ol) {
    margin: 10rpx 0;
    padding-left: 40rpx;
  }
  
  :deep(li) {
    margin: 5rpx 0;
  }
  
  :deep(blockquote) {
    border-left: 4rpx solid #ddd;
    padding-left: 20rpx;
    margin: 10rpx 0;
    color: #666;
  }
  
  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: 8rpx;
    margin: 10rpx 0;
  }
}
</style>

