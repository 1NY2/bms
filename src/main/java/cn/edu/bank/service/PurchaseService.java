package cn.edu.bank.service;

import cn.edu.bank.atomic.AtomicServiceRegistry;
import cn.edu.bank.common.Result;
import cn.edu.bank.dto.PurchaseRequest;
import cn.edu.bank.dto.PurchaseResult;
import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.entity.OperationLog;
import cn.edu.bank.entity.PurchaseOrder;
import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.repository.DepositProductRepository;
import cn.edu.bank.repository.OperationLogRepository;
import cn.edu.bank.repository.PurchaseOrderRepository;
import cn.edu.bank.repository.UserInfoRepository;
import cn.edu.bank.workflow.engine.WorkflowEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 产品购买流程服务
 * 实现：用户信息校验 -> 库存锁定 -> 产品购买
 */
@Slf4j
@Service
public class PurchaseService {
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private DepositProductRepository productRepository;
    
    @Autowired
    private PurchaseOrderRepository orderRepository;
    
    @Autowired
    private WorkflowEngine workflowEngine;
    
    @Autowired
    private OperationLogRepository logRepository;
    
    /**
     * 获取用户订单列表
     */
    public List<PurchaseOrder> getUserOrders(String userId) {
        log.info("获取用户订单列表, userId: {}", userId);
        return orderRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    /**
     * 获取所有订单列表（管理端）
     */
    public List<PurchaseOrder> getAllOrders() {
        log.info("获取所有订单列表");
        return orderRepository.findAllByOrderByCreateTimeDesc();
    }
    
    /**
     * 根据订单号获取订单详情
     */
    public PurchaseOrder getOrderDetailByOrderNo(String orderNo) {
        log.info("获取订单详情, orderNo: {}", orderNo);
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
    }
    
    /**
     * 更新订单备注
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderRemark(String orderNo, String remark) {
        log.info("更新订单备注, orderNo: {}, remark: {}", orderNo, remark);
        
        // 记录关键操作日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("ORDER_MANAGE");
        logEntity.setAction("UPDATE_ORDER_REMARK");
        logEntity.setOrderNo(orderNo);
        logEntity.setUserId(getOrderUserId(orderNo)); // 获取订单所属用户ID
        logEntity.setContent("更新订单备注");
        logEntity.setStatus("PROCESSING");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        try {
            PurchaseOrder order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            order.setRemark(remark);
            order.setUpdateTime(LocalDateTime.now());
            
            orderRepository.save(order);
            log.info("订单备注更新成功");
            
            // 更新操作状态为成功
            logEntity.setStatus("SUCCESS");
            logEntity.setResponseResult("订单备注更新成功");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
        } catch (Exception e) {
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("更新订单备注失败: " + e.getMessage());
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            throw e; // 重新抛出异常
        }
    }
    
    /**
     * 根据订单号更新订单状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(String orderNo, String status) {
        log.info("更新订单状态, orderNo: {}, status: {}", orderNo, status);
        
        // 记录关键操作日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("ORDER_MANAGE");
        logEntity.setAction("UPDATE_ORDER_STATUS");
        logEntity.setOrderNo(orderNo);
        logEntity.setUserId(getOrderUserId(orderNo)); // 获取订单所属用户ID
        logEntity.setContent("更新订单状态");
        logEntity.setStatus("PROCESSING");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        try {
            PurchaseOrder order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            order.setStatus(status);
            order.setUpdateTime(LocalDateTime.now());
            
            orderRepository.save(order);
            log.info("订单状态更新成功");
            
            // 更新操作状态为成功
            logEntity.setStatus("SUCCESS");
            logEntity.setResponseResult("订单状态更新成功");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
        } catch (Exception e) {
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("更新订单状态失败: " + e.getMessage());
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            throw e; // 重新抛出异常
        }
    }
    
    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String orderNo, String userId) {
        log.info("取消订单, orderNo: {}, userId: {}", orderNo, userId);
        
        // 记录关键操作日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("ORDER_MANAGE");
        logEntity.setAction("CANCEL_ORDER");
        logEntity.setOrderNo(orderNo);
        logEntity.setUserId(userId);
        logEntity.setContent("用户取消订单");
        logEntity.setStatus("PROCESSING");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        try {
            PurchaseOrder order = orderRepository.findByOrderNo(orderNo)
                    .orElseThrow(() -> new RuntimeException("订单不存在"));
            
            // 验证订单是否属于该用户
            if (!order.getUserId().equals(userId)) {
                // 更新操作状态为失败
                logEntity.setStatus("FAILED");
                logEntity.setResponseResult("取消订单失败: 订单不属于当前用户");
                logEntity.setCreateTime(LocalDateTime.now());
                logRepository.save(logEntity);
                
                throw new RuntimeException("订单不属于当前用户");
            }
            
            // 检查订单状态，只有特定状态的订单才能被取消
            if ("CANCELLED".equals(order.getStatus()) || "FAILED".equals(order.getStatus())) {
                // 更新操作状态为失败
                logEntity.setStatus("FAILED");
                logEntity.setResponseResult("取消订单失败: 订单已处于终态，无法取消");
                logEntity.setCreateTime(LocalDateTime.now());
                logRepository.save(logEntity);
                
                throw new RuntimeException("订单已处于终态，无法取消");
            }
            
            order.setStatus("CANCELLED");
            order.setUpdateTime(LocalDateTime.now());
            
            orderRepository.save(order);
            log.info("订单取消成功, orderNo: {}", orderNo);
            
            // 更新操作状态为成功
            logEntity.setStatus("SUCCESS");
            logEntity.setResponseResult("订单取消成功");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
        } catch (Exception e) {
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("取消订单失败: " + e.getMessage());
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            throw e; // 重新抛出异常
        }
    }
    
    /**
     * 购买产品
     */
    @Transactional(rollbackFor = Exception.class)
    public Object purchaseProduct(PurchaseRequest request) {
        log.info("开始购买产品, userId: {}, productCode: {}, amount: {}, remark: {}", 
                request.getUserId(), request.getProductCode(), request.getAmount(), request.getRemark());
        
        // 记录购买操作日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("PURCHASE");
        logEntity.setAction("PURCHASE");
        logEntity.setUserId(request.getUserId());
        logEntity.setProductCode(request.getProductCode());
        logEntity.setContent("用户购买产品");
        logEntity.setStatus("PROCESSING");
        logEntity.setRequestParams(request.toString());
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        // 参数校验
        if (request.getUserId() == null || request.getUserId().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "用户ID不能为空");
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("用户ID不能为空");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            return errorResponse;
        }
        if (request.getProductCode() == null || request.getProductCode().trim().isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "产品编码不能为空");
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("产品编码不能为空");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            return errorResponse;
        }
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "购买金额必须大于0");
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("购买金额必须大于0");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            return errorResponse;
        }
        
        // 查询用户和产品信息
        Optional<UserInfo> userOpt = userInfoRepository.findByUserId(request.getUserId());
        if (!userOpt.isPresent()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "用户不存在");
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("用户不存在");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            return errorResponse;
        }
        
        Optional<DepositProduct> productOpt = productRepository.findByProductCode(request.getProductCode());
        if (!productOpt.isPresent()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "产品不存在");
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("产品不存在");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            return errorResponse;
        }
        
        UserInfo userInfo = userOpt.get();
        DepositProduct product = productOpt.get();
        
        // 构建流程上下文
        Map<String, Object> context = new HashMap<>();
        context.put("userId", request.getUserId());
        context.put("productCode", request.getProductCode());
        context.put("amount", request.getAmount().toString());
        context.put("remark", request.getRemark());
        
        // 执行流程引擎
        Map<String, Object> result;
        try {
            result = workflowEngine.executeByProduct(request.getProductCode(), context);
        } catch (Exception e) {
            log.error("[流程引擎] 执行购买流程失败", e);
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("流程执行失败: " + e.getMessage());
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "流程执行失败: " + e.getMessage());
            return errorResponse;
        }
        
        // 检查执行结果
        Boolean success = (Boolean) result.get("success");
        if (success == null || !success) {
            String message = (String) result.getOrDefault("message", "流程执行失败");
            String errorType = (String) result.get("errorType"); // 获取错误类型
            
            // 构建错误响应
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", message);
            if (errorType != null) {
                errorResponse.put("errorType", errorType);
            }
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult(message);
            if (errorType != null) {
                logEntity.setResponseResult(logEntity.getResponseResult() + ", 错误类型: " + errorType);
            }
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            return errorResponse; // 返回错误响应而不是抛出异常
        }
        
        // 从流程结果中获取订单信息
        Object orderInfoObj = result.get("orderInfo");
        if (orderInfoObj == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "流程执行成功但未返回订单信息");
            
            // 更新操作状态为失败
            logEntity.setStatus("FAILED");
            logEntity.setResponseResult("流程执行成功但未返回订单信息");
            logEntity.setCreateTime(LocalDateTime.now());
            logRepository.save(logEntity);
            
            return errorResponse;
        }
        
        // 创建订单
        PurchaseOrder order = createOrderFromInfo(orderInfoObj, userInfo, product);
        
        // 查询产品信息
        DepositProduct prod = productRepository.findByProductCode(request.getProductCode())
            .orElseThrow(() -> new RuntimeException("产品不存在"));
        
        // 构建购买结果
        PurchaseResult purchaseResult = buildPurchaseResult(order, prod);
        
        // 构建成功响应
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("success", true);
        successResponse.put("message", "购买成功");
        successResponse.put("data", purchaseResult);
        
        // 更新操作状态为成功
        logEntity.setStatus("SUCCESS");
        logEntity.setResponseResult("购买成功");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        return successResponse;
    }
    
    /**
     * 根据订单号获取用户ID
     */
    private String getOrderUserId(String orderNo) {
        Optional<PurchaseOrder> orderOpt = orderRepository.findByOrderNo(orderNo);
        return orderOpt.map(PurchaseOrder::getUserId).orElse(null);
    }
    
    /**
     * 根据订单信息创建订单
     */
    private PurchaseOrder createOrderFromInfo(Object orderInfoObj, UserInfo userInfo, DepositProduct product) {
        @SuppressWarnings("unchecked")
        Map<String, Object> orderInfo = (Map<String, Object>) orderInfoObj;
        
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNo((String) orderInfo.get("orderNo"));
        order.setUserId((String) orderInfo.get("userId"));
        order.setProductCode((String) orderInfo.get("productCode"));
        order.setProductName((String) orderInfo.get("productName"));
        order.setAmount((BigDecimal) orderInfo.get("amount"));
        order.setAnnualRate((BigDecimal) orderInfo.get("annualRate"));
        order.setDuration((Integer) orderInfo.get("duration"));
        order.setExpectedIncome((BigDecimal) orderInfo.get("expectedIncome"));
        order.setStatus("PROCESSING"); // 订单创建后应处于处理中状态，等待管理端审核
        order.setRemark((String) orderInfo.get("remark"));
        
        LocalDateTime createTime = (LocalDateTime) orderInfo.get("createTime");
        order.setCreateTime(createTime);
        order.setUpdateTime(createTime);
        order.setStartDate(createTime.plusDays(1)); // T+1起息
        order.setEndDate(createTime.plusDays(product.getDuration() + 1)); // 到期日
        
        return orderRepository.save(order);
    }
    
    /**
     * 构建购买结果
     */
    private PurchaseResult buildPurchaseResult(PurchaseOrder order, DepositProduct product) {
        PurchaseResult result = new PurchaseResult();
        result.setOrderNo(order.getOrderNo());
        result.setProductName(order.getProductName());
        result.setAmount(order.getAmount());
        result.setAnnualRate(order.getAnnualRate());
        result.setDuration(String.valueOf(order.getDuration()));
        result.setExpectedIncome(order.getExpectedIncome());
        result.setStartDate(order.getStartDate());
        result.setEndDate(order.getEndDate());
        return result;
    }
}