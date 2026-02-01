<template>
  <div class="help-center-page">
    <!-- 页面头部 -->
    <div class="header">
      <div class="back-btn" @click="$router.back()">
        <span>&lt;</span>
      </div>
      <h1 class="title">帮助中心</h1>
      <div class="placeholder"></div>
    </div>

    <!-- 搜索框 -->
    <div class="search-box">
      <input 
        type="text" 
        v-model="searchQuery" 
        placeholder="搜索问题关键词" 
        class="search-input"
      />
      <button class="search-btn" @click="search">搜索</button>
    </div>

    <!-- 常见问题分类 -->
    <div class="faq-categories">
      <div class="category-item" @click="selectCategory('account')">
        <div class="category-icon">👤</div>
        <div class="category-text">账户问题</div>
        <div class="category-arrow">›</div>
      </div>
      
      <div class="category-item" @click="selectCategory('product')">
        <div class="category-icon">💰</div>
        <div class="category-text">产品问题</div>
        <div class="category-arrow">›</div>
      </div>
      
      <div class="category-item" @click="selectCategory('transaction')">
        <div class="category-icon">💸</div>
        <div class="category-text">交易问题</div>
        <div class="category-arrow">›</div>
      </div>
      
      <div class="category-item" @click="selectCategory('security')">
        <div class="category-icon">🔒</div>
        <div class="category-text">安全问题</div>
        <div class="category-arrow">›</div>
      </div>
    </div>

    <!-- 热门问题 -->
    <div class="hot-questions">
      <div class="section-header">
        <h2 class="section-title">热门问题</h2>
      </div>
      
      <div class="questions-list">
        <div 
          v-for="question in hotQuestions" 
          :key="question.id" 
          class="question-item"
          @click="viewQuestion(question)"
        >
          <div class="question-text">{{ question.title }}</div>
          <div class="question-arrow">›</div>
        </div>
      </div>
    </div>
  </div>

  <!-- 问题详情对话框 -->
  <div class="dialog-overlay" v-if="showQuestionDialog" @click="showQuestionDialog = false">
    <div class="dialog" @click.stop>
      <div class="dialog-header">
        <h3>{{ selectedQuestion.title }}</h3>
        <div class="close-btn" @click="showQuestionDialog = false">×</div>
      </div>
      <div class="dialog-content">
        <div class="answer-content" v-html="selectedQuestion.answer"></div>
      </div>
      <div class="dialog-footer">
        <button class="confirm-btn" @click="showQuestionDialog = false">确定</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HelpCenter',
  data() {
    return {
      searchQuery: '',
      showQuestionDialog: false,
      selectedQuestion: {},
      hotQuestions: [
        {
          id: 1,
          title: '如何注册账户？',
          answer: '<p>注册账户非常简单，只需按照以下步骤操作：</p><ol><li>打开应用首页，点击"注册"按钮</li><li>填写手机号码并获取验证码</li><li>设置登录密码</li><li>完成实名认证</li><li>注册成功，可以开始使用</li></ol>'
        },
        {
          id: 2,
          title: '如何购买理财产品？',
          answer: '<p>购买理财产品流程如下：</p><ol><li>登录账户后进入"理财产品"页面</li><li>浏览并选择合适的理财产品</li><li>点击产品进入详情页，了解产品信息</li><li>点击"立即购买"按钮</li><li>输入购买金额并确认</li><li>完成支付后购买成功</li></ol>'
        },
        {
          id: 3,
          title: '如何提现资金？',
          answer: '<p>提现资金的操作步骤：</p><ol><li>进入"我的账户"页面</li><li>点击"提现"按钮</li><li>输入提现金额</li><li>选择提现银行卡</li><li>确认提现信息并提交</li><li>等待审核通过后资金到账</li></ol><p>注：提现可能需要1-3个工作日到账</p>'
        },
        {
          id: 4,
          title: '忘记密码怎么办？',
          answer: '<p>如果忘记密码，可以通过以下方式重置：</p><ol><li>在登录页面点击"忘记密码"</li><li>输入注册时绑定的手机号</li><li>获取短信验证码</li><li>设置新的登录密码</li><li>完成密码重置，使用新密码登录</li></ol>'
        },
        {
          id: 5,
          title: '如何联系客服？',
          answer: '<p>您可以通过以下方式联系我们的客服：</p><ul><li>客服热线：400-123-4567（工作日 9:00-18:00）</li><li>在线客服：在应用内点击"我的"-"联系客服"</li><li>微信客服：关注官方微信公众号"银行理财"</li><li>邮件咨询：service@bank.com</li></ul>'
        }
      ]
    }
  },
  methods: {
    search() {
      if (this.searchQuery.trim()) {
        alert(`搜索关键词: ${this.searchQuery}`);
        // 这里应该调用后端搜索接口
      } else {
        alert('请输入搜索关键词');
      }
    },
    
    selectCategory(category) {
      const categoryMap = {
        'account': '账户问题',
        'product': '产品问题',
        'transaction': '交易问题',
        'security': '安全问题'
      };
      
      alert(`查看${categoryMap[category]}`);
      // 这里应该跳转到对应分类的问题列表
    },
    
    viewQuestion(question) {
      this.selectedQuestion = question;
      this.showQuestionDialog = true;
    }
  }
}
</script>

<style scoped>
.help-center-page {
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

/* 搜索框 */
.search-box {
  padding: 16px;
  display: flex;
  gap: 8px;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
}

.search-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 0 20px;
  font-size: 16px;
  cursor: pointer;
}

/* 常见问题分类 */
.faq-categories {
  background: white;
  margin: 0 16px 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.category-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.3s;
}

.category-item:last-child {
  border-bottom: none;
}

.category-item:hover {
  background-color: #f8f9fa;
}

.category-icon {
  font-size: 20px;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.category-text {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.category-arrow {
  color: #ccc;
  font-size: 18px;
}

/* 热门问题 */
.hot-questions {
  background: white;
  margin: 16px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.questions-list {
  padding: 0 16px;
}

.question-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.question-item:last-child {
  border-bottom: none;
}

.question-text {
  flex: 1;
  font-size: 16px;
  color: #333;
}

.question-arrow {
  color: #ccc;
  font-size: 18px;
}

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dialog {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
}

.dialog-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.close-btn {
  font-size: 24px;
  cursor: pointer;
  padding: 4px;
}

.dialog-content {
  padding: 16px;
}

.answer-content {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}

.answer-content p {
  margin: 0 0 12px 0;
}

.answer-content ol, .answer-content ul {
  margin: 0 0 12px 20px;
  padding: 0;
}

.answer-content li {
  margin-bottom: 8px;
}

.dialog-footer {
  padding: 16px;
  border-top: 1px solid #eee;
  text-align: center;
}

.confirm-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 30px;
  font-size: 16px;
  cursor: pointer;
}
</style>