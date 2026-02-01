<template>
  <div class="message-center-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">消息通知</h1>
      <div class="placeholder"></div>
    </div>

    <!-- 消息列表 -->
    <div class="message-list">
      <div 
        v-for="message in messages" 
        :key="message.id" 
        class="message-item"
        :class="{ 'unread': !message.read }"
        @click="readMessage(message)"
      >
        <div class="message-icon">{{ message.icon }}</div>
        <div class="message-content">
          <div class="message-title">{{ message.title }}</div>
          <div class="message-summary">{{ message.summary }}</div>
          <div class="message-time">{{ formatTime(message.time) }}</div>
        </div>
        <div class="message-arrow">›</div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="messages.length === 0">
      <div class="empty-icon">📭</div>
      <div class="empty-text">暂无消息通知</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'MessageCenter',
  data() {
    return {
      messages: [
        {
          id: 1,
          icon: '💰',
          title: '理财产品收益到账',
          summary: '您购买的理财产品今日收益已到账，请查看',
          time: '2023-05-15 10:30',
          read: false
        },
        {
          id: 2,
          icon: '💳',
          title: '银行卡绑定成功',
          summary: '您的银行卡已成功绑定，可以开始投资理财了',
          time: '2023-05-14 15:45',
          read: true
        },
        {
          id: 3,
          icon: '🔒',
          title: '安全提醒',
          summary: '检测到您的账户在新设备上登录，请注意账户安全',
          time: '2023-05-12 09:20',
          read: true
        },
        {
          id: 4,
          icon: '🎉',
          title: '活动通知',
          summary: '新用户专享福利活动火热进行中，快来参与吧',
          time: '2023-05-10 14:15',
          read: true
        },
        {
          id: 5,
          icon: '📈',
          title: '市场动态',
          summary: '近期市场波动较大，请关注您的投资组合',
          time: '2023-05-08 11:30',
          read: true
        }
      ]
    }
  },
  methods: {
    readMessage(message) {
      // 标记消息为已读
      message.read = true;
      // 这里可以跳转到消息详情页面
      alert(`查看消息: ${message.title}`);
    },
    
    formatTime(timeStr) {
      return timeStr;
    }
  }
}
</script>

<style scoped>
.message-center-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 页面头部 */
.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.back-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 4px 8px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.placeholder {
  width: 24px;
}

/* 消息列表 */
.message-list {
  padding: 16px;
}

.message-item {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.message-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.message-item.unread {
  border-left: 4px solid #667eea;
}

.message-icon {
  font-size: 24px;
  margin-right: 12px;
}

.message-content {
  flex: 1;
}

.message-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.message-summary {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-arrow {
  color: #ccc;
  font-size: 18px;
  margin-left: 8px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
}
</style>