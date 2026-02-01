package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.repository.DepositProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 原子服务14：订单创建服务
 * 注意：此服务现在只准备订单信息，不实际创建订单，订单将在整个流程成功后创建
 * 如果上下文中存在失败标志，则直接返回失败结果
 */
@Component("ORDER_CREATE")
public class OrderCreateService extends BaseAtomicService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    public OrderCreateService() {
        inputParams.put("userId", "用户ID");
        inputParams.put("productCode", "产品编码");
        inputParams.put("amount", "购买金额");
        inputParams.put("expectedIncome", "预期收益");
        inputParams.put("remark", "备注信息"); // 添加备注信息参数
        outputParams.put("orderInfo", "订单信息");
        outputParams.put("orderNo", "订单号");
    }
    
    @Override
    public String getServiceCode() {
        return "ORDER_CREATE";
    }
    
    @Override
    public String getServiceName() {
        return "订单信息准备";
    }
    
    @Override
    public String getServiceDescription() {
        return "准备购买订单信息，但不实际创建订单（订单将在流程成功后创建）";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始准备订单信息");
        
        // 检查上下文是否包含失败标志，如果有则直接返回失败结果
        if (context.containsKey("success") && context.get("success").equals(false)) {
            log("检测到失败标志，跳过订单创建");
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", context.getOrDefault("message", "流程执行失败，无法创建订单"));
            result.put("errorType", "ORDER_CREATE_FAILED");
            return result;
        }
        
        String userId = (String) getContextParam(context, "userId");
        String productCode = (String) getContextParam(context, "productCode");
        BigDecimal amount = new BigDecimal(getContextParam(context, "amount").toString());
        BigDecimal expectedIncome = context.containsKey("expectedIncome") ? 
            new BigDecimal(getContextParam(context, "expectedIncome").toString()) : BigDecimal.ZERO;
        // 获取备注信息
        String remark = (String) getContextParam(context, "remark");
        if (remark == null) {
            remark = ""; // 设置默认空字符串，避免null值
        }
        
        Map<String, Object> result = new HashMap<>();
        
        Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
        
        if (!productOpt.isPresent()) {
            result.put("createResult", false);
            result.put("message", "产品不存在");
            result.put("success", false);
            result.put("errorType", "ORDER_CREATE_FAILED");
            return result;
        }
        
        DepositProduct product = productOpt.get();
        
        // 生成订单号但不保存到数据库
        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // 创建订单信息对象
        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderNo", orderNo);
        orderInfo.put("userId", userId);
        orderInfo.put("productCode", productCode);
        orderInfo.put("productName", product.getProductName());
        orderInfo.put("amount", amount);
        orderInfo.put("annualRate", product.getAnnualRate());
        orderInfo.put("duration", product.getDuration());
        orderInfo.put("expectedIncome", expectedIncome);
        orderInfo.put("remark", remark);
        orderInfo.put("createTime", LocalDateTime.now());
        
        result.put("orderNo", orderNo);
        result.put("orderInfo", orderInfo);
        result.put("success", true); // 添加success字段以匹配流程定义
        result.put("message", "订单信息准备成功");
        log("订单信息准备成功：" + orderNo);
        
        return result;
    }
}