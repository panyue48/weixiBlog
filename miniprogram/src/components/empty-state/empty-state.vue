<template>
  <view class="empty-state">
    <text class="icon">{{ icon }}</text>
    <text class="text">{{ text }}</text>
  </view>
</template>

<script setup>
const props = defineProps({
  icon: {
    type: String,
    default: '📭'
  },
  text: {
    type: String,
    default: '暂无数据'
  }
})
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
  
  .icon {
    font-size: 120rpx;
    margin-bottom: 20rpx;
  }
  
  .text {
    font-size: 28rpx;
    color: #999;
  }
}
</style>

