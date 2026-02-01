package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.enums.PurchaseErrorType;
import cn.edu.bank.repository.DepositProductRepository;
import cn.edu.bank.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务13：限额校验服务
 */
@Component("LIMIT_CHECK")
public class LimitCheckService extends BaseAtomicService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    @Autowired
    private PurchaseOrderRepository orderRepository;
    
    public LimitCheckService() {
        inputParams.put("userId", "用户ID");
        inputParams.put("productCode", "产品编码");
        inputParams.put("amount", "购买金额");
        outputParams.put("limitCheckResult", "限额校验结果");
        outputParams.put("checkResult", "校验结果");
        outputParams.put("errorMessage", "错误信息");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "LIMIT_CHECK";
    }
    
    @Override
    public String getServiceName() {
        return "限额校验";
    }
    
    @Override
    public String getServiceDescription() {
        return "检查购买金额是否超过单人限额和单日限额";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行限额校验");
        
        String userId = (String) getContextParam(context, "userId");
        String productCode = (String) getContextParam(context, "productCode");
        BigDecimal amount = new BigDecimal(getContextParam(context, "amount").toString());
        
        Map<String, Object> result = new HashMap<>();
        
        Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
        
        if (!productOpt.isPresent()) {
            result.put("limitCheckResult", false);
            result.put("errorMessage", "产品不存在");
            result.put("message", "产品不存在");
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.LIMIT_EXCEEDED.getCode());
            return result;
        }
        
        DepositProduct product = productOpt.get();
        
        // 查询用户今日购买金额
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        BigDecimal todayAmount = orderRepository.sumTodayPurchaseAmount(userId, productCode, todayStart);
        
        // 校验单日限额
        if (todayAmount.add(amount).compareTo(product.getDailyLimit()) > 0) {
            result.put("limitCheckResult", false);
            result.put("checkResult", false); // 添加checkResult字段以匹配流程定义
            String errorMessage = "超过单日购买限额：" + product.getDailyLimit();
            result.put("errorMessage", errorMessage);
            result.put("message", errorMessage);
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.LIMIT_EXCEEDED.getCode());
            log("限额校验失败：超过单日限额");
            return result;
        }
        
        // 查询用户总购买金额
        BigDecimal totalAmount = orderRepository.sumUserPurchaseAmount(userId, productCode);
        
        // 校验单人限额
        if (totalAmount.add(amount).compareTo(product.getPersonLimit()) > 0) {
            result.put("limitCheckResult", false);
            result.put("checkResult", false); // 添加checkResult字段以匹配流程定义
            String errorMessage = "超过单人购买限额：" + product.getPersonLimit();
            result.put("errorMessage", errorMessage);
            result.put("message", errorMessage);
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.LIMIT_EXCEEDED.getCode());
            log("限额校验失败：超过单人限额");
            return result;
        }
        
        result.put("limitCheckResult", true);
        result.put("checkResult", true); // 添加checkResult字段以匹配流程定义
        result.put("message", "限额校验通过");
        log("限额校验通过");
        
        return result;
    }
}