package cn.edu.bank.controller;

import cn.edu.bank.common.Result;
import cn.edu.bank.dto.PurchaseRequest;
import cn.edu.bank.dto.PurchaseResult;
import cn.edu.bank.entity.PurchaseOrder;
import cn.edu.bank.service.PurchaseService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购买流程控制器
 */
@Slf4j
@RestController
@RequestMapping("/purchase")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PurchaseController {
    
    @Autowired
    private PurchaseService purchaseService;
    
    /**
     * 购买产品
     */
    @PostMapping("/execute")
    public Result<Object> purchaseProduct(@Validated @RequestBody PurchaseRequest request) {
        log.info("请求购买产品, userId: {}, productCode: {}, amount: {}, remark: {}", 
                 request.getUserId(), request.getProductCode(), request.getAmount(), request.getRemark());
        
        try {
            Object result = purchaseService.purchaseProduct(request);
            
            // 检查是否为错误响应
            if (result instanceof Map) {
                Map<String, Object> response = (Map<String, Object>) result;
                Boolean success = (Boolean) response.get("success");
                
                if (success != null && !success) {
                    // 返回错误信息，包含错误类型
                    String message = (String) response.get("message");
                    Object data = response.get("data");
                    String errorType = (String) response.get("errorType");
                    
                    // 构建包含错误类型的响应
                    Map<String, Object> errorResponse = new java.util.HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", message);
                    if (errorType != null) {
                        errorResponse.put("errorType", errorType);
                    }
                    if (data != null) {
                        errorResponse.put("data", data);
                    }
                    
                    return Result.error("购买失败", errorResponse); // 使用带数据的错误方法
                } else {
                    // 成功响应
                    String message = (String) response.get("message");
                    Object data = response.get("data");
                    
                    return Result.success(message, data);
                }
            }
            
            // 如果返回的是PurchaseResult对象（正常情况下的成功响应）
            return Result.success("购买成功", result);
        } catch (Exception e) {
            log.error("购买产品失败", e);
            return Result.error("购买失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户订单列表
     */
    @GetMapping("/orders/{userId}")
    public Result<List<PurchaseOrder>> getUserOrders(@PathVariable String userId) {
        log.info("请求用户订单列表, userId: {}", userId);
        
        try {
            List<PurchaseOrder> orders = purchaseService.getUserOrders(userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("获取用户订单列表失败", e);
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有订单列表（管理端）
     */
    @GetMapping("/orders")
    public Result<List<PurchaseOrder>> getAllOrders() {
        log.info("请求所有订单列表");
        
        try {
            List<PurchaseOrder> orders = purchaseService.getAllOrders();
            return Result.success(orders);
        } catch (Exception e) {
            log.error("获取所有订单列表失败", e);
            return Result.error("获取订单列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据订单号获取订单详情
     */
    @GetMapping("/order/{orderNo}")
    public Result<PurchaseOrder> getOrderDetail(@PathVariable String orderNo) {
        log.info("请求订单详情, orderNo: {}", orderNo);
        
        try {
            PurchaseOrder order = purchaseService.getOrderDetailByOrderNo(orderNo);
            return Result.success(order);
        } catch (Exception e) {
            log.error("获取订单详情失败", e);
            return Result.error("获取订单详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新订单备注
     */
    @PutMapping("/order/{orderNo}/remark")
    public Result<String> updateOrderRemark(@PathVariable String orderNo, @RequestBody UpdateRemarkRequest request) {
        log.info("更新订单备注, orderNo: {}, remark: {}", orderNo, request.getRemark());
        
        try {
            purchaseService.updateOrderRemark(orderNo, request.getRemark());
            return Result.success("备注更新成功");
        } catch (Exception e) {
            log.error("更新订单备注失败", e);
            return Result.error("更新备注失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新订单状态（管理端）
     */
    @PutMapping("/order/{orderNo}/status")
    public Result<String> updateOrderStatus(@PathVariable String orderNo, @RequestBody UpdateStatusRequest request) {
        log.info("更新订单状态, orderNo: {}, status: {}", orderNo, request.getStatus());
        
        try {
            purchaseService.updateOrderStatus(orderNo, request.getStatus());
            return Result.success("订单状态更新成功");
        } catch (Exception e) {
            log.error("更新订单状态失败", e);
            return Result.error("更新订单状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消订单（客户端）
     */
    @PostMapping("/order/{orderNo}/cancel")
    public Result<String> cancelOrder(@PathVariable String orderNo, @RequestBody CancelOrderRequest request) {
        log.info("取消订单, orderNo: {}, userId: {}", orderNo, request.getUserId());
        
        try {
            purchaseService.cancelOrder(orderNo, request.getUserId());
            return Result.success("订单取消成功");
        } catch (Exception e) {
            log.error("取消订单失败", e);
            return Result.error("取消订单失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新备注请求DTO
     */
    @Data
    public static class UpdateRemarkRequest {
        private String remark;
    }
    
    /**
     * 更新状态请求DTO
     */
    @Data
    public static class UpdateStatusRequest {
        private String status;
    }
    
    /**
     * 取消订单请求DTO
     */
    @Data
    public static class CancelOrderRequest {
        private String userId;
    }
}