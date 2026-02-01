import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// 引入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 配置 axios 基础路径
axios.defaults.baseURL = 'http://localhost:8080/api'

// 创建 Vue 应用实例
const app = createApp(App)

// 使用 Element Plus
app.use(ElementPlus)

// 全局配置 axios
app.config.globalProperties.$http = axios

// 挂载路由和应用
app.use(router).mount('#app')