import axios from 'axios'

// 创建 axios 实例
const service = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端服务地址，包含/api前缀
  timeout: 5000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    console.log(error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    console.log('API响应拦截器 - 原始响应:', response)
    
    // 判断是否为管理端API请求（路径以/admin开头）
    const isAdminApi = response.config.url && response.config.url.includes('/admin/')
    
    // 特别处理：管理端订单相关的API，用于管理端获取所有订单、更新订单状态等
    const isManageOrdersApi = response.config.url && (
      response.config.url === '/purchase/orders'  // 仅管理端获取所有订单的API
      //api/purchase/order/[^/]+/(status|remark)$/.test(response.config.url) // 更新订单状态或备注
    )
    
    // 判断是否为购买API请求（需要保留完整的响应结构）
    const isPurchaseApi = response.config.url && response.config.url.includes('/purchase/execute') && !isManageOrdersApi
    
    // 如果是管理端API或管理订单API，返回完整response对象
    if (isAdminApi || isManageOrdersApi) {
      return response
    }
    
    // 如果是购买API，也返回完整response对象，因为需要处理错误类型
    if (isPurchaseApi) {
      return response
    }
    
    // 客户端API返回response.data
    return response.data
  },
  error => {
    // 对响应错误做点什么
    console.log('API响应错误:', error)
    if (error.response && error.response.status === 401) {
      // Token过期或无效，清除本地存储并跳转到登录页
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      window.location.href = '/#/login'
    }
    return Promise.reject(error)
  }
)

// 产品API
export const productApi = {
  // 获取产品列表
  getProductList() {
    return service.get('/admin/products/list')
  },
  
  // 获取产品详情
  getProductDetail(productCode) {
    return service.get(`/admin/products/detail/${productCode}`)
  },
  
  // 保存产品
  saveProduct(data) {
    return service.post('/admin/products/save', data)
  },
  
  // 更新产品状态
  updateProductStatus(productCode, status) {
    return service.post(`/admin/products/status/${productCode}?status=${status}`)
  },
  
  // 更新产品库存
  updateInventory(productCode, inventory) {
    return service.post(`/admin/products/inventory/${productCode}?inventory=${inventory}`)
  },
  
  // 删除产品
  deleteProduct(id) {
    return service.delete(`/admin/products/delete/${id}`)
  }
}

// 购买API
export const purchaseApi = {
  // 购买产品
  purchaseProduct(data) {
    return service.post('/purchase/execute', data)
  },
  
  // 获取用户订单列表
  getUserOrders(userId) {
    return service.get(`/purchase/orders/${userId}`)
  },
  
  // 获取所有订单列表（管理端）
  getAllOrders() {
    return service.get('/purchase/orders')
  },
  
  // 获取订单详情
  getOrderDetail(orderNo) {
    return service.get(`/purchase/order/${orderNo}`)
  },
  
  // 更新订单备注
  updateOrderRemark(orderNo, remark) {
    return service.put(`/purchase/order/${orderNo}/remark`, { remark })
  },
  
  // 更新订单状态（管理端）
  updateOrderStatus(orderNo, status) {
    return service.put(`/purchase/order/${orderNo}/status`, { status })
  },
  
  // 取消订单（客户端）
  cancelOrder(orderNo, userId) {
    return service.post(`/purchase/order/${orderNo}/cancel`, { userId })
  }
}

// 用户API
export const userApi = {
  // 获取用户信息
  getUserInfo(userId) {
    return service.get(`/user/info/${userId}`)
  },
  
  // 获取用户资产信息
  getUserAssets(userId) {
    return service.get(`/user/assets/${userId}`)
  },
  
  // 更新用户信息
  updateUserInfo(userId, userInfo) {
    return service.put(`/user/info/${userId}`, userInfo)
  }
}

// 管理端用户管理API
export const adminUserApi = {
  // 获取所有用户列表
  getAllUsers() {
    return service.get('/admin/users/list')
  },
  
  // 更新用户状态
  updateUserStatus(username, status) {
    return service.post('/admin/users/update-status', null, {
      params: { username, status }
    })
  },
  
  // 重置用户密码
  resetUserPassword(username, newPassword) {
    return service.post('/admin/users/reset-password', null, {
      params: { username, newPassword }
    })
  }
}

// 流程管理API
export const workflowApi = {
  // 获取流程列表
  listWorkflows() {
    return service.get('/admin/workflow/list')
  },
  
  // 保存流程
  saveWorkflow(data) {
    return service.post('/admin/workflow/save', data)
  },
  
  // 发布流程
  publishWorkflow(id) {
    return service.post(`/admin/workflow/publish/${id}`)
  },
  
  // 下线流程
  unpublishWorkflow(id) {
    return service.post(`/admin/workflow/unpublish/${id}`)
  },
  
  // 删除流程
  deleteWorkflow(id) {
    return service.delete(`/admin/workflow/delete/${id}`)
  },
  
  // 测试流程
  testWorkflow(workflowCode, context) {
    return service.post(`/admin/workflow/test/${workflowCode}`, context)
  }
}

// 原子服务管理API
export const atomicApi = {
  // 获取服务列表
  listServices() {
    return service.get('/admin/atomic/list')
  },
  
  // 获取服务详情
  getServiceDetail(serviceCode) {
    return service.get(`/admin/atomic/detail/${serviceCode}`)
  },
  
  // 测试服务
  testService(serviceCode, context) {
    return service.post(`/admin/atomic/test/${serviceCode}`, context)
  }
}

// 日志管理API
export const logApi = {
  // 获取所有日志
  getAllLogs() {
    return service.get('/admin/logs/all')
  },
  
  // 根据用户ID获取日志
  getLogsByUser(userId) {
    return service.get(`/admin/logs/by-user/${userId}`)
  },
  
  // 根据日志类型获取日志
  getLogsByType(logType) {
    return service.get(`/admin/logs/by-type/${logType}`)
  },
  
  // 根据操作类型获取日志
  getLogsByAction(action) {
    return service.get(`/admin/logs/by-action/${action}`)
  },
  
  // 根据产品编码获取日志
  getLogsByProduct(productCode) {
    return service.get(`/admin/logs/by-product/${productCode}`)
  },
  
  // 根据订单号获取日志
  getLogsByOrder(orderNo) {
    return service.get(`/admin/logs/by-order/${orderNo}`)
  },
  
  // 根据状态获取日志
  getLogsByStatus(status) {
    return service.get(`/admin/logs/by-status/${status}`)
  },
  
  // 根据时间范围获取日志
  getLogsByTimeRange(startTime, endTime) {
    return service.get(`/admin/logs/by-time-range?startTime=${startTime}&endTime=${endTime}`)
  },
  
  // 搜索日志
  searchLogs(keyword) {
    return service.get(`/admin/logs/search?keyword=${keyword}`)
  }
}

export default service