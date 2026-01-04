/**
 * 简单的事件总线
 * 用于页面间通信
 */

const events = {}

export default {
  // 监听事件
  on(event, callback) {
    if (!events[event]) {
      events[event] = []
    }
    events[event].push(callback)
  },
  
  // 触发事件
  emit(event, data) {
    if (events[event]) {
      events[event].forEach(callback => {
        callback(data)
      })
    }
  },
  
  // 移除监听
  off(event, callback) {
    if (events[event]) {
      const index = events[event].indexOf(callback)
      if (index > -1) {
        events[event].splice(index, 1)
      }
    }
  }
}

