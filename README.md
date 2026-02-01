# 银行存款产品服务编排系统

银行存款产品服务编排系统是一个基于Spring Boot和Vue.js的全栈应用程序，旨在实现银行存款产品的动态配置、服务流程编排和购买管理。

## 项目概述

本项目实现了以下核心功能：
- **动态流程引擎**：支持条件分支和可视化编排的存款产品购买流程
- **原子服务管理**：提供14个标准化的原子服务组件
- **产品管理系统**：支持存款产品的属性配置和管理
- **订单处理系统**：完整的订单生命周期管理
- **用户管理系统**：包含客户端和管理端两种角色
- **操作日志记录**：完整的操作审计跟踪

## 技术架构

### 后端技术栈
- **框架**: Spring Boot 2.7.14
- **数据访问**: Spring Data JPA
- **数据库**: MySQL 8.0
- **安全认证**: JWT Token + Spring Security
- **序列化**: Jackson
- **表达式语言**: Spring EL (SpEL)
- **代码简化**: Lombok

### 前端技术栈
- **框架**: Vue 3
- **构建工具**: Vite
- **路由管理**: Vue Router
- **HTTP客户端**: Axios
- **UI组件**: Element Plus
- **图表库**: Chart.js

## 核心特性

### 1. 动态流程引擎
- 支持图形化流程编排
- 条件分支逻辑处理
- 原子服务动态调用
- 流程执行监控和日志记录

### 2. 原子服务设计
系统包含14个标准原子服务：
- 用户验证服务 (USER_VERIFY)
- 身份校验服务 (ID_CHECK)  
- 地区校验服务 (REGION_CHECK)
- 白名单校验服务 (WHITELIST_CHECK)
- 标签校验服务 (TAG_CHECK)
- 限额校验服务 (LIMIT_CHECK)
- 余额校验服务 (BALANCE_CHECK)
- 金额校验服务 (AMOUNT_VALIDATE)
- 库存锁定服务 (INVENTORY_LOCK)
- 订单创建服务 (ORDER_CREATE)
- 利息计算服务 (INTEREST_CALC)
- 扣款服务 (DEDUCT_BALANCE)
- 库存更新服务 (INVENTORY_UPDATE)
- 拒绝服务 (REJECT_SERVICE)

### 3. 安全机制
- JWT Token认证
- 密码BCrypt加密
- 角色权限控制
- CORS跨域支持

### 4. 数据管理
- 完整的用户信息管理
- 存款产品配置管理
- 购买订单全生命周期
- 操作日志追踪

## 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── cn/edu/bank/
│   │       ├── atomic/          # 原子服务实现
│   │       ├── common/          # 通用组件
│   │       ├── config/          # 配置类
│   │       ├── controller/      # 控制器层
│   │       ├── dto/             # 数据传输对象
│   │       ├── entity/          # 实体类
│   │       ├── enums/           # 枚举类
│   │       ├── repository/      # 数据访问层
│   │       ├── service/         # 业务逻辑层
│   │       ├── util/            # 工具类
│   │       └── workflow/        # 工作流引擎
│   └── resources/
└── frontend/                    # 前端项目
    ├── src/
    │   ├── api/                 # API接口
    │   ├── components/          # 组件
    │   ├── views/               # 页面视图
    │   │   ├── admin/           # 管理端页面
    │   │   └── user/            # 客户端页面
    │   └── router/              # 路由配置
    └── public/
```

## 环境要求

- Java 8+
- Node.js 16+
- MySQL 8.0
- Maven 3.6+

## 快速开始

### 1. 后端启动

```bash
# 克隆项目
git clone <repository-url>

# 进入项目目录
cd bank-deposit-system

# 构建并启动后端服务
mvn clean spring-boot:run
```

后端服务将在 `http://localhost:8080` 启动

### 2. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端开发服务器将在 `http://localhost:5173` 启动

### 3. 默认账户

- **管理员**: admin / admin123
- **普通用户**: testuser / user123

## API 接口

### 用户认证
- `POST /auth/login` - 用户登录
- `POST /auth/register` - 用户注册

### 产品管理
- `GET /products` - 获取产品列表
- `GET /products/{code}` - 获取产品详情
- `POST /products` - 创建产品
- `PUT /products/{code}` - 更新产品

### 购买流程
- `POST /purchase/execute` - 执行购买
- `GET /purchase/orders/{userId}` - 获取用户订单
- `GET /purchase/order/{orderNo}` - 获取订单详情

### 原子服务
- `GET /atomic-services` - 获取原子服务列表
- `POST /atomic-services/test` - 测试原子服务

## 管理功能

### 产品管理
- 存款产品创建和编辑
- 产品属性配置（利率、期限、起存金额等）
- 产品状态管理

### 流程编排
- 可视化工作流设计
- 原子服务拖拽组合
- 条件分支配置
- 流程发布和版本管理

### 订单管理
- 订单查询和筛选
- 订单状态更新
- 订单备注管理

### 用户管理
- 用户信息维护
- 权限角色分配
- 账户状态管理

### 日志管理
- 操作日志查询
- 异常日志分析
- 流程执行记录

## 部署说明

### 生产部署
1. 修改 application.yml 中的数据库连接信息
2. 执行 `mvn clean package` 打包
3. 运行 `java -jar target/bank-deposit-system-1.0-SNAPSHOT.jar`
4. 构建前端生产包：`npm run build`

## 开发指南

### 添加新原子服务
1. 实现 AtomicService 接口
2. 继承 BaseAtomicService 类
3. 在 AtomicServiceRegistry 中注册服务

### 工作流配置
- 工作流定义使用JSON格式存储
- 支持开始节点、服务节点、条件节点、结束节点和拒绝节点
- 条件节点支持SpEL表达式

## 安全注意事项

1. JWT Token有效期为2小时
2. 密码使用BCrypt算法加密存储
3. 所有API接口都需要认证（除登录注册外）
4. 敏感操作需要二次确认

## 测试数据

系统预置了以下测试数据：
- 多种类型的存款产品
- 不同权限的用户账户
- 示例工作流配置
- 模拟订单数据

## 故障排除

### 常见问题
1. **数据库连接失败**: 检查MySQL服务是否启动，连接参数是否正确
2. **前端无法访问后端**: 确认CORS配置是否正确
3. **流程执行失败**: 检查工作流配置和原子服务实现
4. **登录失败**: 验证用户名密码是否正确

### 日志位置
- 应用日志: logs/app.log
- 操作日志: 数据库operation_log表
- 流程日志: 数据库operation_log表

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。在贡献代码前，请确保：
- 代码风格遵循项目规范
- 单元测试通过
- 相关文档已更新
- 代码逻辑清晰易懂

## 许可证

本项目仅供学习和研究使用。