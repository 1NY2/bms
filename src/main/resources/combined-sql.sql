-- 插入产品数据
INSERT INTO deposit_product (product_code, product_name, duration, duration_desc, annual_rate, min_amount, increment_amount, person_limit, daily_limit, risk_level, start_date, interest_method, end_date, status, inventory, description) VALUES
    ('10620211122', '3个月定期存款', 90, '3个月', 1.85, 50.00, 50.00, 500000.00, 500000.00, '低', '2020-12-30', '到期付息', '2025-03-30', 'ACTIVE', 1000, '稳健理财，每日22:00前存入，当日起息（含节假日）'),
    ('10620211123', '6个月定期存款', 180, '6个月', 2.05, 50.00, 50.00, 500000.00, 500000.00, '低', '2020-12-30', '到期付息', '2025-06-30', 'ACTIVE', 800, '中期投资，收益更高'),
    ('10620211124', '一年定期存款', 365, '1年', 2.25, 50.00, 50.00, 500000.00, 500000.00, '低', '2020-12-30', '到期付息', '2025-12-30', 'ACTIVE', 500, '长期稳定，收益可观'),
    ('10620211125', '1天通知存款', 1, '1天', 1.55, 50.00, 50.00, 500000.00, 500000.00, '低', '2020-12-30', '到期付息', '2025-12-30', 'ACTIVE', 2000, '灵活存取，快速周转'),
    ('10620211126', '7天通知存款', 7, '7天', 2.10, 50.00, 50.00, 500000.00, 500000.00, '低', '2020-12-30', '到期付息', '2025-12-30', 'ACTIVE', 1500, '短期理财，收益稳定'),
    ('PROD001', '标准定期存款', 180, '6个月', 2.15, 100.00, 100.00, 1000000.00, 1000000.00, '低', '2020-12-30', '到期付息', '2025-12-30', 'ACTIVE', 2000, '标准定期存款产品'),
    ('PROD002', 'VIP专享存款', 365, '1年', 2.50, 10000.00, 1000.00, 5000000.00, 5000000.00, '低', '2020-12-30', '到期付息', '2025-12-30', 'ACTIVE', 500, 'VIP客户专享高收益产品'),
    ('PROD003', '活期理财', 1, '1天', 1.20, 100.00, 100.00, 500000.00, 500000.00, '低', '2020-12-30', '按日计息', '2025-12-30', 'ACTIVE', 10000, '活期理财产品，随时存取');

-- 插入账号数据（使用正确的BCrypt哈希值）
INSERT INTO account (username, password, real_name, phone, email, role, status, user_id, created_at) VALUES
    ('admin', '$2a\$10\$9aDsX1sef1o/mGLJS//fMOKtDlxGNykI8HQHcOFSVwf8qOrNrKmxa', '系统管理员', '13800000000', 'admin@bank.com', 'ADMIN', 'ACTIVE', 'ADMIN001', CURRENT_TIMESTAMP);

INSERT INTO account (username, password, real_name, phone, email, role, status, user_id, created_at) VALUES
    ('testuser', '$2a\$10$pRQLFBUt.mDiIVfy61CeN.VlZLbJFcfsVlaM8bhJx6bRxDMYwfuom', '测试用户', '13900000001', 'test@user.com', 'USER', 'ACTIVE', 'U001', CURRENT_TIMESTAMP);

-- 插入用户信息数据
INSERT INTO user_info (user_id, user_name, id_card, phone_number, email, balance, status, create_time, region, tags, whitelisted) VALUES
    ('ADMIN001', '系统管理员', '110101199003077654', '13800000000', 'admin@bank.com', 1000000.00, 'NORMAL', CURRENT_TIMESTAMP, '北京', '管理员', true);

INSERT INTO user_info (user_id, user_name, id_card, phone_number, email, balance, status, create_time, region, tags, whitelisted) VALUES
    ('U001', '测试用户', '110101199003077534', '13900000001', 'test@user.com', 10000.00, 'NORMAL', CURRENT_TIMESTAMP, '上海', '普通', false);

INSERT INTO user_info (user_id, user_name, id_card, phone_number, email, balance, status, create_time, region, tags, whitelisted) VALUES
    ('U002', 'VIP用户', '110101199003077535', '13900000002', 'vip@user.com', 500000.00, 'NORMAL', CURRENT_TIMESTAMP, '北京', 'VIP', true);

INSERT INTO user_info (user_id, user_name, id_card, phone_number, email, balance, status, create_time, region, tags, whitelisted) VALUES
    ('U003', '访客用户', '110101199003077536', '13900000003', 'guest@user.com', 5000.00, 'NORMAL', CURRENT_TIMESTAMP, '广州', '普通', false);

-- 插入购买订单数据
INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, create_time, remark) VALUES
    ('PO202312150001', 'U001', '10620211122', '3个月定期存款', 10000.00, 1.85, 90, 45.62, 'SUCCESS', CURRENT_TIMESTAMP, '首次购买');

INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, create_time, remark) VALUES
    ('PO202312150002', 'U001', '10620211124', '一年定期存款', 50000.00, 2.25, 365, 2760.27, 'PROCESSING', CURRENT_TIMESTAMP, '大额购买');

INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, create_time, remark) VALUES
    ('PO202312150003', 'U002', 'PROD002', 'VIP专享存款', 100000.00, 2.50, 365, 6164.38, 'SUCCESS', CURRENT_TIMESTAMP, 'VIP客户大额购买');

INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, create_time, remark) VALUES
    ('PO202312150004', 'U003', '10620211125', '1天通知存款', 5000.00, 1.55, 1, 0.21, 'SUCCESS', CURRENT_TIMESTAMP, '小额体验购买');

INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, create_time, remark) VALUES
    ('PO202312150005', 'U002', 'PROD001', '标准定期存款', 50000.00, 2.15, 180, 1298.63, 'FAILED', CURRENT_TIMESTAMP, '余额不足');

-- 综合演示数据SQL脚本
-- 包含个人中心、订单管理、资产概览等功能的测试数据

-- 1. 创建个人中心测试用户
INSERT INTO account (username, password, real_name, phone, email, role, status, user_id, created_at) VALUES
    ('personal_center_test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2X5F8F4v6SLmvgD7iqR0UJ2', '个人中心测试用户', '13987654321', 'pc_test@example.com', 'USER', 'ACTIVE', 'PC_TEST_001', NOW());

INSERT INTO user_info (`user_id`, `user_name`, `id_card`, `phone_number`, `email`, `balance`, `status`, `region`, `tags`, `whitelisted`, `create_time`) VALUES
    ('PC_TEST_001', '个人中心测试用户', '110101199004041237', '13987654321', 'pc_test@example.com', 350000.00, 'NORMAL', '深圳', '测试专用', TRUE, NOW());

-- 2. 创建订单管理测试用户
INSERT INTO account (username, password, real_name, phone, email, role, status, user_id, created_at) VALUES
    ('orders_test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2X5F8F4v6SLmvgD7iqR0UJ2', '订单测试用户', '13765432109', 'orders_test@example.com', 'USER', 'ACTIVE', 'ORDERS_TEST_001', NOW());

INSERT INTO user_info (`user_id`, `user_name`, `id_card`, `phone_number`, `email`, `balance`, `status`, `region`, `tags`, `whitelisted`, `create_time`) VALUES
    ('ORDERS_TEST_001', '订单测试用户', '110101199005051238', '13765432109', 'orders_test@example.com', 500000.00, 'NORMAL', '广州', '订单测试', FALSE, NOW());

-- 3. 创建资产概览测试用户
INSERT INTO account (username, password, real_name, phone, email, role, status, user_id, created_at) VALUES
    ('assets_test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2X5F8F4v6SLmvgD7iqR0UJ2', '资产测试用户', '13654321098', 'assets_test@example.com', 'USER', 'ACTIVE', 'ASSETS_TEST_001', NOW());

INSERT INTO user_info (`user_id`, `user_name`, `id_card`, `phone_number`, `email`, `balance`, `status`, `region`, `tags`, `whitelisted`, `create_time`) VALUES
    ('ASSETS_TEST_001', '资产测试用户', '110101199006061239', '13654321098', 'assets_test@example.com', 800000.00, 'NORMAL', '成都', '资产测试', TRUE, NOW());

-- 4. 创建综合测试用户
INSERT INTO account (username, password, real_name, phone, email, role, status, user_id, created_at) VALUES
    ('comprehensive_test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z2X5F8F4v6SLmvgD7iqR0UJ2', '综合测试用户', '13543210987', 'comprehensive_test@example.com', 'USER', 'ACTIVE', 'COMP_TEST_001', NOW());

INSERT INTO user_info (`user_id`, `user_name`, `id_card`, `phone_number`, `email`, `balance`, `status`, `region`, `tags`, `whitelisted`, `create_time`) VALUES
    ('COMP_TEST_001', '综合测试用户', '110101199007071240', '13543210987', 'comprehensive_test@example.com', 1000000.00, 'NORMAL', '武汉', '综合测试', TRUE, NOW());

-- 5. 创建个人中心测试产品
INSERT INTO deposit_product (product_code, product_name, duration, duration_desc, annual_rate, min_amount, increment_amount, person_limit, daily_limit, risk_level, start_date, interest_method, end_date, status, inventory, description) VALUES
('PC_PROD_001', '个人中心测试-定期存款A', 90, '3个月', 1.95, 1000.00, 100.00, 500000.00, 100000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 500, '个人中心测试用定期产品A'),
('PC_PROD_002', '个人中心测试-定期存款B', 180, '6个月', 2.25, 1000.00, 100.00, 500000.00, 100000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 300, '个人中心测试用定期产品B'),
('PC_PROD_003', '个人中心测试-定期存款C', 365, '1年', 2.60, 1000.00, 100.00, 1000000.00, 200000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 200, '个人中心测试用定期产品C'),
('PC_PROD_004', '个人中心测试-活期存款', 1, '1天', 0.32, 100.00, 100.00, 1000000.00, 500000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '按日计息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 999999, '个人中心测试用活期产品');

-- 6. 创建订单管理测试产品
INSERT INTO deposit_product (product_code, product_name, duration, duration_desc, annual_rate, min_amount, increment_amount, person_limit, daily_limit, risk_level, start_date, interest_method, end_date, status, inventory, description) VALUES
('ORD_PROD_001', '订单测试-短期理财A', 30, '1个月', 1.75, 1000.00, 100.00, 300000.00, 50000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 800, '订单测试用短期理财产品A'),
('ORD_PROD_002', '订单测试-中期理财B', 90, '3个月', 2.05, 1000.00, 100.00, 500000.00, 100000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 500, '订单测试用中期理财产品B'),
('ORD_PROD_003', '订单测试-长期理财C', 365, '1年', 2.75, 1000.00, 100.00, 1000000.00, 200000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 300, '订单测试用长期理财产品C'),
('ORD_PROD_004', '订单测试-活期理财D', 1, '1天', 0.35, 100.00, 100.00, 1000000.00, 500000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '按日计息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 999999, '订单测试用活期理财产品D');

-- 7. 创建资产概览测试产品
INSERT INTO deposit_product (product_code, product_name, duration, duration_desc, annual_rate, min_amount, increment_amount, person_limit, daily_limit, risk_level, start_date, interest_method, end_date, status, inventory, description) VALUES
('ASSETS_PROD_001', '资产测试-货币基金', 1, '1天', 2.10, 100.00, 100.00, 1000000.00, 500000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '按日计息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 999999, '资产测试用货币基金产品'),
('ASSETS_PROD_002', '资产测试-短期理财', 30, '1个月', 2.80, 1000.00, 100.00, 500000.00, 100000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 1000, '资产测试用短期理财产品'),
('ASSETS_PROD_003', '资产测试-中期理财', 180, '6个月', 3.20, 1000.00, 100.00, 500000.00, 100000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 800, '资产测试用中期理财产品'),
('ASSETS_PROD_004', '资产测试-长期理财', 365, '1年', 3.80, 1000.00, 100.00, 1000000.00, 200000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 500, '资产测试用长期理财产品'),
('ASSETS_PROD_005', '资产测试-结构性存款', 90, '3个月', 3.50, 10000.00, 1000.00, 2000000.00, 500000.00, 'MEDIUM', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 200, '资产测试用结构性存款产品');

-- 8. 创建综合测试产品
INSERT INTO deposit_product (product_code, product_name, duration, duration_desc, annual_rate, min_amount, increment_amount, person_limit, daily_limit, risk_level, start_date, interest_method, end_date, status, inventory, description) VALUES
('COMP_PROD_001', '综合测试-活期理财', 1, '1天', 0.40, 100.00, 100.00, 1000000.00, 500000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '按日计息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 999999, '综合测试用活期理财产品'),
('COMP_PROD_002', '综合测试-短期定存', 30, '1个月', 2.00, 1000.00, 100.00, 500000.00, 100000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 1000, '综合测试用短期定存产品'),
('COMP_PROD_003', '综合测试-中期定存', 90, '3个月', 2.50, 1000.00, 100.00, 500000.00, 100000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 800, '综合测试用中期定存产品'),
('COMP_PROD_004', '综合测试-长期定存', 365, '1年', 3.20, 1000.00, 100.00, 1000000.00, 200000.00, 'LOW', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 2 YEAR), 'ACTIVE', 500, '综合测试用长期定存产品'),
('COMP_PROD_005', '综合测试-高收益理财', 180, '6个月', 3.80, 10000.00, 1000.00, 2000000.00, 500000.00, 'MEDIUM', DATE_ADD(NOW(), INTERVAL 1 DAY), '到期一次性付息', DATE_ADD(NOW(), INTERVAL 1 YEAR), 'ACTIVE', 300, '综合测试用高收益理财产品');

-- 9. 创建个人中心测试订单
INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, start_date, end_date, create_time, update_time, remark) VALUES
('PC_ORD_001', 'PC_TEST_001', 'PC_PROD_001', '个人中心测试-定期存款A', 80000.00, 1.95, 90, 384.66, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), '3个月定期存款'),
('PC_ORD_002', 'PC_TEST_001', 'PC_PROD_002', '个人中心测试-定期存款B', 120000.00, 2.25, 180, 1331.51, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 181 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), '6个月定期存款'),
('PC_ORD_003', 'PC_TEST_001', 'PC_PROD_003', '个人中心测试-定期存款C', 60000.00, 2.60, 365, 1528.77, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 366 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), '1年期定期存款'),
('PC_ORD_004', 'PC_TEST_001', 'PC_PROD_004', '个人中心测试-活期存款', 15000.00, 0.32, 1, 1.32, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), '活期存款'),
('PC_ORD_005', 'PC_TEST_001', 'PC_PROD_001', '个人中心测试-定期存款A', 30000.00, 1.95, 90, 144.25, 'PROCESSING', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), NOW(), NOW(), '处理中的定期存款'),
('PC_ORD_006', 'PC_TEST_001', 'PC_PROD_004', '个人中心测试-活期存款', 20000.00, 0.32, 1, 1.76, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), '活期存款充值');

-- 10. 创建订单管理测试订单
INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, start_date, end_date, create_time, update_time, remark) VALUES
-- 成功订单
('ORD_TEST_001', 'ORDERS_TEST_001', 'ORD_PROD_001', '订单测试-短期理财A', 25000.00, 1.75, 30, 35.96, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY), NOW(), '成功的短期理财订单'),
('ORD_TEST_002', 'ORDERS_TEST_001', 'ORD_PROD_002', '订单测试-中期理财B', 50000.00, 2.05, 90, 252.40, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), '成功的中期理财订单'),
('ORD_TEST_003', 'ORDERS_TEST_001', 'ORD_PROD_003', '订单测试-长期理财C', 100000.00, 2.75, 365, 2750.00, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 366 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), '成功的长期理财订单'),
('ORD_TEST_004', 'ORDERS_TEST_001', 'ORD_PROD_004', '订单测试-活期理财D', 15000.00, 0.35, 1, 1.44, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), '成功的活期理财订单'),
('ORD_TEST_005', 'ORDERS_TEST_001', 'ORD_PROD_001', '订单测试-短期理财A', 30000.00, 1.75, 30, 43.15, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), NOW(), '另一笔成功的短期理财订单'),
-- 处理中订单
('ORD_TEST_006', 'ORDERS_TEST_001', 'ORD_PROD_002', '订单测试-中期理财B', 40000.00, 2.05, 90, 201.92, 'PROCESSING', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), '处理中的中期理财订单'),
('ORD_TEST_007', 'ORDERS_TEST_001', 'ORD_PROD_004', '订单测试-活期理财D', 20000.00, 0.35, 1, 1.92, 'PROCESSING', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), NOW(), '处理中的活期理财订单'),
-- 待支付订单
('ORD_TEST_008', 'ORDERS_TEST_001', 'ORD_PROD_003', '订单测试-长期理财C', 75000.00, 2.75, 365, 2062.50, 'PENDING', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 366 DAY), NOW(), NOW(), '待支付的长期理财订单'),
-- 已失败订单
('ORD_TEST_009', 'ORDERS_TEST_001', 'ORD_PROD_001', '订单测试-短期理财A', 35000.00, 1.75, 30, 50.38, 'FAILED', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NOW(), '支付失败的短期理财订单'),
-- 已取消订单
('ORD_TEST_010', 'ORDERS_TEST_001', 'ORD_PROD_002', '订单测试-中期理财B', 45000.00, 2.05, 90, 227.16, 'CANCELLED', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NOW(), '用户取消的中期理财订单');

-- 11. 创建资产概览测试订单
INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, start_date, end_date, create_time, update_time, remark) VALUES
-- 货币基金投资
('ASSETS_001', 'ASSETS_TEST_001', 'ASSETS_PROD_001', '资产测试-货币基金', 100000.00, 2.10, 1, 57.53, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 60 DAY), NOW(), '货币基金投资'),
('ASSETS_002', 'ASSETS_TEST_001', 'ASSETS_PROD_001', '资产测试-货币基金', 50000.00, 2.10, 1, 28.77, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), '追加货币基金投资'),
-- 短期理财投资
('ASSETS_003', 'ASSETS_TEST_001', 'ASSETS_PROD_002', '资产测试-短期理财', 80000.00, 2.80, 30, 184.11, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), NOW(), '短期理财投资'),
('ASSETS_004', 'ASSETS_TEST_001', 'ASSETS_PROD_002', '资产测试-短期理财', 60000.00, 2.80, 30, 138.08, 'PROCESSING', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), '处理中的短期理财'),
-- 中期理财投资
('ASSETS_005', 'ASSETS_TEST_001', 'ASSETS_PROD_003', '资产测试-中期理财', 150000.00, 3.20, 180, 2367.12, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 181 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY), NOW(), '中期理财投资'),
('ASSETS_006', 'ASSETS_TEST_001', 'ASSETS_PROD_003', '资产测试-中期理财', 100000.00, 3.20, 180, 1578.08, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 181 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), '另一笔中期理财投资'),
-- 长期理财投资
('ASSETS_007', 'ASSETS_TEST_001', 'ASSETS_PROD_004', '资产测试-长期理财', 200000.00, 3.80, 365, 7600.00, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 366 DAY), DATE_SUB(NOW(), INTERVAL 50 DAY), NOW(), '长期理财投资'),
-- 结构性存款投资
('ASSETS_008', 'ASSETS_TEST_001', 'ASSETS_PROD_005', '资产测试-结构性存款', 300000.00, 3.50, 90, 2589.04, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), '结构性存款投资');

-- 12. 创建综合测试订单
INSERT INTO purchase_order (order_no, user_id, product_code, product_name, amount, annual_rate, duration, expected_income, status, start_date, end_date, create_time, update_time, remark) VALUES
-- 活期理财订单
('COMP_ORD_001', 'COMP_TEST_001', 'COMP_PROD_001', '综合测试-活期理财', 150000.00, 0.40, 1, 16.44, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 60 DAY), NOW(), '活期理财投资'),
('COMP_ORD_002', 'COMP_TEST_001', 'COMP_PROD_001', '综合测试-活期理财', 80000.00, 0.40, 1, 8.77, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), NOW(), '追加活期理财投资'),
-- 短期定存订单
('COMP_ORD_003', 'COMP_TEST_001', 'COMP_PROD_002', '综合测试-短期定存', 100000.00, 2.00, 30, 164.38, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), NOW(), '短期定存投资'),
('COMP_ORD_004', 'COMP_TEST_001', 'COMP_PROD_002', '综合测试-短期定存', 75000.00, 2.00, 30, 123.29, 'PROCESSING', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 31 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), NOW(), '处理中的短期定存'),
-- 中期定存订单
('COMP_ORD_005', 'COMP_TEST_001', 'COMP_PROD_003', '综合测试-中期定存', 200000.00, 2.50, 90, 1232.88, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), DATE_SUB(NOW(), INTERVAL 40 DAY), NOW(), '中期定存投资'),
('COMP_ORD_006', 'COMP_TEST_001', 'COMP_PROD_003', '综合测试-中期定存', 150000.00, 2.50, 90, 924.66, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 91 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), NOW(), '另一笔中期定存投资'),
-- 长期定存订单
('COMP_ORD_007', 'COMP_TEST_001', 'COMP_PROD_004', '综合测试-长期定存', 300000.00, 3.20, 365, 9600.00, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 366 DAY), DATE_SUB(NOW(), INTERVAL 50 DAY), NOW(), '长期定存投资'),
-- 高收益理财订单
('COMP_ORD_008', 'COMP_TEST_001', 'COMP_PROD_005', '综合测试-高收益理财', 400000.00, 3.80, 180, 7463.01, 'SUCCESS', DATE_ADD(NOW(), INTERVAL 1 DAY), DATE_ADD(NOW(), INTERVAL 181 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), NOW(), '高收益理财投资');

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

-- 操作日志表测试数据
INSERT INTO operation_logs (log_type, user_id, product_code, order_no, action, content, status, create_time, request_params, response_result) VALUES
('USER_AUTH', 'U12345678', NULL, NULL, 'USER_LOGIN', '用户登录成功, username: testuser', 'SUCCESS', NOW(), '{"username":"testuser","password":"***"}', '登录成功'),
('USER_AUTH', 'U12345678', NULL, NULL, 'USER_LOGIN', '用户登录失败: 用户名或密码错误, username: wronguser', 'FAILED', NOW(), '{"username":"wronguser","password":"***"}', '用户名或密码错误'),
('PURCHASE', 'U12345678', '10620211122', 'ORD123456789', 'PURCHASE_PRODUCT', '购买产品成功', 'SUCCESS', NOW(), '{"userId":"U12345678","productCode":"10620211122","amount":"1000"}', '购买成功'),
('PURCHASE', 'U12345678', '10620211122', 'ORD123456790', 'PURCHASE_PRODUCT', '购买产品失败: 余额不足', 'FAILED', NOW(), '{"userId":"U12345678","productCode":"10620211122","amount":"100000"}', '余额不足'),
('ORDER_ACCESS', 'U12345678', '10620211122', 'ORD123456789', 'GET_ORDER_DETAIL', '获取订单详情成功, orderNo: ORD123456789', 'SUCCESS', NOW(), NULL, NULL),
('ORDER_MANAGE', 'U12345678', '10620211122', 'ORD123456789', 'UPDATE_ORDER_STATUS', '订单状态更新成功, orderNo: ORD123456789, status: SUCCESS', 'SUCCESS', NOW(), '{"status":"SUCCESS"}', '订单状态更新成功'),
('USER_INFO', 'U12345678', NULL, NULL, 'GET_USER_INFO', '获取用户信息成功, userId: U12345678', 'SUCCESS', NOW(), NULL, NULL),
('USER_INFO', 'U12345678', NULL, NULL, 'UPDATE_USER_INFO', '更新用户信息成功, userId: U12345678', 'SUCCESS', NOW(), '{"email":"test@example.com"}', '更新成功'),
('WORKFLOW', 'U12345678', '10620211122', 'ORD123456789', 'EXECUTE_WORKFLOW', '流程执行成功: PURCHASE_STANDARD', 'SUCCESS', NOW(), NULL, '流程执行成功'),
('ATOMIC_SERVICE', 'U12345678', '10620211122', 'ORD123456789', 'USER_VERIFY_SERVICE', '用户信息校验服务成功: 用户状态正常，状态为NORMAL', 'SUCCESS', NOW(), NULL, '用户信息校验通过');

-- 为现有产品添加工作流配置
-- 3个月定期存款
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, description, create_time) VALUES
    ('WF_3MONTH', '3个月定期存款购买流程', '10620211122', '{
  "workflowCode": "WF_3MONTH",
  "workflowName": "3个月定期存款购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult",
        "userStatus": "userStatus"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "limitCheck",
      "nodeType": "service",
      "serviceCode": "LIMIT_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "limitOk"
      }
    },
    {
      "nodeId": "limitCheckCondition",
      "nodeType": "condition",
      "expression": "#limitOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}",
        "remark": "${context.remark}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "PURCHASE"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "${context.message}",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "amountValidate", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "limitCheck", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "limitCheck", "to": "limitCheckCondition"},
    {"from": "limitCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "limitCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, '3个月定期存款购买流程', CURRENT_TIMESTAMP);

-- 6个月定期存款
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, description, create_time) VALUES
    ('WF_6MONTH', '6个月定期存款购买流程', '10620211123', '{
  "workflowCode": "WF_6MONTH",
  "workflowName": "6个月定期存款购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult",
        "userStatus": "userStatus"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "limitCheck",
      "nodeType": "service",
      "serviceCode": "LIMIT_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "limitOk"
      }
    },
    {
      "nodeId": "limitCheckCondition",
      "nodeType": "condition",
      "expression": "#limitOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}",
        "remark": "${context.remark}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "PURCHASE"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "${context.message}",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "amountValidate", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "limitCheck", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "limitCheck", "to": "limitCheckCondition"},
    {"from": "limitCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "limitCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, '6个月定期存款购买流程', CURRENT_TIMESTAMP);

-- 一年定期存款
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, description, create_time) VALUES
    ('WF_1YEAR', '一年定期存款购买流程', '10620211124', '{
  "workflowCode": "WF_1YEAR",
  "workflowName": "一年定期存款购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult",
        "userStatus": "userStatus"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "limitCheck",
      "nodeType": "service",
      "serviceCode": "LIMIT_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "limitOk"
      }
    },
    {
      "nodeId": "limitCheckCondition",
      "nodeType": "condition",
      "expression": "#limitOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}",
        "remark": "${context.remark}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "PURCHASE"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "${context.message}",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "amountValidate", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "limitCheck", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "limitCheck", "to": "limitCheckCondition"},
    {"from": "limitCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "limitCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, '一年定期存款购买流程', CURRENT_TIMESTAMP);

-- 1天通知存款
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, description, create_time) VALUES
    ('WF_1DAY', '1天通知存款购买流程', '10620211125', '{
  "workflowCode": "WF_1DAY",
  "workflowName": "1天通知存款购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult",
        "userStatus": "userStatus"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "limitCheck",
      "nodeType": "service",
      "serviceCode": "LIMIT_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "limitOk"
      }
    },
    {
      "nodeId": "limitCheckCondition",
      "nodeType": "condition",
      "expression": "#limitOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}",
        "remark": "${context.remark}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "PURCHASE"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "${context.message}",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "amountValidate", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "limitCheck", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "limitCheck", "to": "limitCheckCondition"},
    {"from": "limitCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "limitCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, '1天通知存款购买流程', CURRENT_TIMESTAMP);

-- 7天通知存款
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, description, create_time) VALUES
    ('WF_7DAY', '7天通知存款购买流程', '10620211126', '{
  "workflowCode": "WF_7DAY",
  "workflowName": "7天通知存款购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult",
        "userStatus": "userStatus"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "limitCheck",
      "nodeType": "service",
      "serviceCode": "LIMIT_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "limitOk"
      }
    },
    {
      "nodeId": "limitCheckCondition",
      "nodeType": "condition",
      "expression": "#limitOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}",
        "remark": "${context.remark}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "PURCHASE"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "${context.message}",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "amountValidate", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "limitCheck", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "limitCheck", "to": "limitCheckCondition"},
    {"from": "limitCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "limitCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, '7天通知存款购买流程', CURRENT_TIMESTAMP);

-- 插入3个预置工作流配置

-- 流程1：标准购买流程（适用于普通定期存款）
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, create_time, update_time, publish_time) VALUES
    ('FLOW_STANDARD', '标准购买流程', 'PROD001', '{
  "workflowCode": "FLOW_STANDARD",
  "workflowName": "标准购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult",
        "userStatus": "userStatus"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "limitCheck",
      "nodeType": "service",
      "serviceCode": "LIMIT_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "limitOk"
      }
    },
    {
      "nodeId": "limitCheckCondition",
      "nodeType": "condition",
      "expression": "#limitOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "PURCHASE"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "${context.message}",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "amountValidate", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "limitCheck", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "limitCheck", "to": "limitCheckCondition"},
    {"from": "limitCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "limitCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 流程2：VIP客户购买流程（适用于整存整取存款）
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, create_time, update_time, publish_time) VALUES
    ('FLOW_VIP', 'VIP客户购买流程', 'PROD002', '{
  "workflowCode": "FLOW_VIP",
  "workflowName": "VIP客户购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "tagCheck",
      "nodeType": "service",
      "serviceCode": "TAG_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "requiredTag": "VIP"
      },
      "outputMapping": {
        "hasTag": "isVip"
      }
    },
    {
      "nodeId": "vipCondition",
      "nodeType": "condition",
      "expression": "#isVip == true"
    },
    {
      "nodeId": "whitelistCheck",
      "nodeType": "service",
      "serviceCode": "WHITELIST_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "inWhitelist": "whitelisted"
      }
    },
    {
      "nodeId": "whitelistCheckCondition",
      "nodeType": "condition",
      "expression": "#whitelisted == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "VIP_PURCHASE"
      }
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "VIP检查失败",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "tagCheck", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "tagCheck", "to": "vipCondition"},
    {"from": "vipCondition", "to": "whitelistCheck", "condition": "true"},
    {"from": "vipCondition", "to": "rejectNode", "condition": "false"},
    {"from": "whitelistCheck", "to": "whitelistCheckCondition"},
    {"from": "whitelistCheckCondition", "to": "amountValidate", "condition": "true"},
    {"from": "whitelistCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 流程3：地域限定购买流程（适用于活期存款）
INSERT INTO workflow_config (workflow_code, workflow_name, product_code, flow_definition, status, version, create_time, update_time, publish_time) VALUES
    ('FLOW_REGION', '地域限定购买流程', 'PROD003', '{
  "workflowCode": "FLOW_REGION",
  "workflowName": "地域限定购买流程",
  "nodes": [
    {
      "nodeId": "start",
      "nodeType": "start"
    },
    {
      "nodeId": "idCheck",
      "nodeType": "service",
      "serviceCode": "ID_CHECK",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "idValid": "idValid"
      }
    },
    {
      "nodeId": "idCheckCondition",
      "nodeType": "condition",
      "expression": "#idValid == true"
    },
    {
      "nodeId": "userVerify",
      "nodeType": "service",
      "serviceCode": "USER_VERIFY",
      "inputMapping": {
        "userId": "${context.userId}"
      },
      "outputMapping": {
        "verifyResult": "verifyResult"
      }
    },
    {
      "nodeId": "userVerifyCondition",
      "nodeType": "condition",
      "expression": "#verifyResult == true"
    },
    {
      "nodeId": "regionCheck",
      "nodeType": "service",
      "serviceCode": "REGION_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "allowedRegions": "北京,上海,广州,深圳"
      },
      "outputMapping": {
        "inRegion": "regionAllowed"
      }
    },
    {
      "nodeId": "regionCondition",
      "nodeType": "condition",
      "expression": "#regionAllowed == true"
    },
    {
      "nodeId": "amountValidate",
      "nodeType": "service",
      "serviceCode": "AMOUNT_VALIDATE",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "validateResult": "amountValid"
      }
    },
    {
      "nodeId": "amountValidateCondition",
      "nodeType": "condition",
      "expression": "#amountValid == true"
    },
    {
      "nodeId": "balanceCheck",
      "nodeType": "service",
      "serviceCode": "BALANCE_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "balanceOk"
      }
    },
    {
      "nodeId": "balanceCheckCondition",
      "nodeType": "condition",
      "expression": "#balanceOk == true"
    },
    {
      "nodeId": "limitCheck",
      "nodeType": "service",
      "serviceCode": "LIMIT_CHECK",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "checkResult": "limitOk"
      }
    },
    {
      "nodeId": "limitCheckCondition",
      "nodeType": "condition",
      "expression": "#limitOk == true"
    },
    {
      "nodeId": "inventoryLock",
      "nodeType": "service",
      "serviceCode": "INVENTORY_LOCK",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "quantity": "${context.amount}"
      },
      "outputMapping": {
        "lockResult": "inventoryLocked"
      }
    },
    {
      "nodeId": "interestCalc",
      "nodeType": "service",
      "serviceCode": "INTEREST_CALC",
      "inputMapping": {
        "productCode": "${context.productCode}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "expectedIncome": "expectedIncome"
      }
    },
    {
      "nodeId": "orderCreate",
      "nodeType": "service",
      "serviceCode": "ORDER_CREATE",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "amount": "${context.amount}",
        "expectedIncome": "${context.expectedIncome}"
      },
      "outputMapping": {
        "orderNo": "orderNo",
        "success": "success"
      }
    },
    {
      "nodeId": "deductBalance",
      "nodeType": "service",
      "serviceCode": "DEDUCT_BALANCE",
      "inputMapping": {
        "userId": "${context.userId}",
        "amount": "${context.amount}"
      },
      "outputMapping": {
        "deductResult": "deductSuccess"
      }
    },
    {
      "nodeId": "logRecord",
      "nodeType": "service",
      "serviceCode": "LOG_RECORD",
      "inputMapping": {
        "userId": "${context.userId}",
        "productCode": "${context.productCode}",
        "orderNo": "${context.orderNo}",
        "action": "REGION_PURCHASE"
      }
    },
    {
      "nodeId": "rejectNode",
      "nodeType": "service",
      "serviceCode": "REJECT_SERVICE",
      "inputMapping": {
        "message": "地区限制",
        "errorType": "${context.errorType}"
      },
      "outputMapping": {
        "success": "success",
        "message": "message",
        "errorType": "errorType"
      }
    },
    {
      "nodeId": "end",
      "nodeType": "end"
    }
  ],
  "edges": [
    {"from": "start", "to": "idCheck"},
    {"from": "idCheck", "to": "idCheckCondition"},
    {"from": "idCheckCondition", "to": "userVerify", "condition": "true"},
    {"from": "idCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "userVerify", "to": "userVerifyCondition"},
    {"from": "userVerifyCondition", "to": "regionCheck", "condition": "true"},
    {"from": "userVerifyCondition", "to": "rejectNode", "condition": "false"},
    {"from": "regionCheck", "to": "regionCondition"},
    {"from": "regionCondition", "to": "amountValidate", "condition": "true"},
    {"from": "regionCondition", "to": "rejectNode", "condition": "false"},
    {"from": "amountValidate", "to": "amountValidateCondition"},
    {"from": "amountValidateCondition", "to": "balanceCheck", "condition": "true"},
    {"from": "amountValidateCondition", "to": "rejectNode", "condition": "false"},
    {"from": "balanceCheck", "to": "balanceCheckCondition"},
    {"from": "balanceCheckCondition", "to": "limitCheck", "condition": "true"},
    {"from": "balanceCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "limitCheck", "to": "limitCheckCondition"},
    {"from": "limitCheckCondition", "to": "inventoryLock", "condition": "true"},
    {"from": "limitCheckCondition", "to": "rejectNode", "condition": "false"},
    {"from": "inventoryLock", "to": "interestCalc"},
    {"from": "interestCalc", "to": "orderCreate"},
    {"from": "orderCreate", "to": "deductBalance"},
    {"from": "deductBalance", "to": "logRecord"},
    {"from": "logRecord", "to": "end"},
    {"from": "rejectNode", "to": "end"}
  ]
}', 'PUBLISHED', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
