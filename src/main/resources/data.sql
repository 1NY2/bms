-- 创建数据库
CREATE DATABASE IF NOT EXISTS bankdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE bankdb;

-- 删除已存在的表（按依赖关系倒序）
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS deposit_product;
DROP TABLE IF EXISTS user_info;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS workflow_config;

-- 创建账号表
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录用）',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密后）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role VARCHAR(20) NOT NULL COMMENT '角色：USER-普通用户, ADMIN-管理员',
    status VARCHAR(20) NOT NULL COMMENT '账号状态：ACTIVE-激活, LOCKED-锁定, DELETED-已删除',
    user_id VARCHAR(50) COMMENT '关联的用户ID（用于业务系统）',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    last_login_at DATETIME COMMENT '最后登录时间',
    INDEX idx_username (username),
    INDEX idx_user_id (user_id)
) COMMENT='账号信息表';

-- 创建用户信息表
CREATE TABLE user_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL UNIQUE COMMENT '用户ID（手机号或账号）',
    user_name VARCHAR(50) NOT NULL COMMENT '用户姓名',
    id_card VARCHAR(18) NOT NULL UNIQUE COMMENT '身份证号',
    phone_number VARCHAR(11) NOT NULL COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    balance DECIMAL(15,2) NOT NULL COMMENT '账户余额',
    status VARCHAR(20) COMMENT '用户状态（NORMAL-正常, LOCKED-锁定, FROZEN-冻结）',
    region VARCHAR(50) COMMENT '地区',
    tags VARCHAR(100) COMMENT '标签',
    whitelisted BOOLEAN COMMENT '是否白名单用户',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_id_card (id_card)
) COMMENT='用户信息表';

-- 创建存款产品表
CREATE TABLE deposit_product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_code VARCHAR(50) NOT NULL UNIQUE COMMENT '产品编号',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    duration INT NOT NULL COMMENT '产品期限（天数）',
    duration_desc VARCHAR(50) COMMENT '期限单位说明（如：180天、3个月、1年）',
    annual_rate DECIMAL(5,2) NOT NULL COMMENT '年化利率（%）',
    min_amount DECIMAL(15,2) NOT NULL COMMENT '起存金额（元）',
    increment_amount DECIMAL(15,2) NOT NULL COMMENT '递增金额（元）',
    person_limit DECIMAL(15,2) NOT NULL COMMENT '单人限额（元）',
    daily_limit DECIMAL(15,2) NOT NULL COMMENT '单日限额（元）',
    risk_level VARCHAR(20) COMMENT '风险等级',
    start_date DATE NOT NULL COMMENT '起息日期',
    interest_method VARCHAR(50) COMMENT '结息方式',
    end_date DATE NOT NULL COMMENT '到期日期',
    status VARCHAR(20) COMMENT '产品状态（ACTIVE-在售, PAUSED-暂停, EXPIRED-已过期）',
    inventory BIGINT NOT NULL COMMENT '产品库存（剩余可购买份额）',
    description TEXT COMMENT '产品描述',
    INDEX idx_product_code (product_code)
) COMMENT='银行存款产品表';

-- 创建购买订单表
CREATE TABLE purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id VARCHAR(50) NOT NULL COMMENT '用户ID',
    product_code VARCHAR(50) NOT NULL COMMENT '产品编号',
    product_name VARCHAR(100) NOT NULL COMMENT '产品名称',
    amount DECIMAL(15,2) NOT NULL COMMENT '购买金额',
    annual_rate DECIMAL(5,2) NOT NULL COMMENT '年化利率',
    duration INT NOT NULL COMMENT '产品期限（天数）',
    expected_income DECIMAL(15,2) NOT NULL COMMENT '预期收益',
    status VARCHAR(20) NOT NULL COMMENT '订单状态（PENDING-待支付, PROCESSING-处理中, SUCCESS-成功, FAILED-失败, CANCELLED-已取消）',
    start_date DATETIME COMMENT '起息日期',
    end_date DATETIME COMMENT '到期日期',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    remark TEXT COMMENT '备注信息',
    INDEX idx_order_no (order_no),
    INDEX idx_user_id (user_id),
    INDEX idx_product_code (product_code)
) COMMENT='购买订单表';

-- 创建工作流配置表
CREATE TABLE workflow_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    workflow_code VARCHAR(50) NOT NULL UNIQUE COMMENT '流程编码',
    workflow_name VARCHAR(100) NOT NULL COMMENT '流程名称',
    product_code VARCHAR(50) COMMENT '关联产品编码',
    flow_definition TEXT COMMENT '流程定义JSON',
    status VARCHAR(20) COMMENT '状态：DRAFT(草稿)、PUBLISHED(已发布)',
    version INT COMMENT '版本号',
    description TEXT COMMENT '流程描述',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    publish_time DATETIME COMMENT '发布时间',
    INDEX idx_workflow_code (workflow_code),
    INDEX idx_product_code (product_code)
) COMMENT='工作流配置表';

-- 创建操作日志表
CREATE TABLE operation_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    log_type VARCHAR(50) COMMENT '日志类型',
    user_id VARCHAR(50) COMMENT '用户ID',
    product_code VARCHAR(50) COMMENT '产品编码',
    order_no VARCHAR(100) COMMENT '订单号',
    action VARCHAR(100) COMMENT '操作类型',
    content TEXT COMMENT '日志内容',
    ip_address VARCHAR(45) COMMENT '操作IP地址',
    user_agent TEXT COMMENT '用户代理',
    status VARCHAR(20) COMMENT '操作状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    INDEX idx_user_id (user_id),
    INDEX idx_log_type (log_type),
    INDEX idx_action (action),
    INDEX idx_product_code (product_code),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) COMMENT='操作日志表';