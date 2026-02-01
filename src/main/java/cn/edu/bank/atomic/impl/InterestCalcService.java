package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.repository.DepositProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务6：利息计算服务
 */
@Component("INTEREST_CALC")
public class InterestCalcService extends BaseAtomicService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    public InterestCalcService() {
        inputParams.put("productCode", "产品编码"); // 添加产品编码参数
        inputParams.put("amount", "本金金额");
        inputParams.put("annualRate", "年化利率");
        inputParams.put("duration", "存款天数");
        outputParams.put("expectedIncome", "预期收益");
        outputParams.put("totalAmount", "到期总额");
    }
    
    @Override
    public String getServiceCode() {
        return "INTEREST_CALC";
    }
    
    @Override
    public String getServiceName() {
        return "利息计算";
    }
    
    @Override
    public String getServiceDescription() {
        return "计算存款产品的预期收益和到期总额";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行利息计算");
        
        Object amountObj = getContextParam(context, "amount");
        BigDecimal amount = new BigDecimal(amountObj.toString());
        
        // 尝试从参数中获取年化利率和存款天数，如果没有则从产品信息中获取
        Object rateObj = getContextParam(context, "annualRate");
        Object durationObj = getContextParam(context, "duration");
        
        BigDecimal annualRate;
        Integer duration;
        
        if (rateObj != null && durationObj != null) {
            // 直接使用传入的参数
            annualRate = new BigDecimal(rateObj.toString());
            duration = Integer.valueOf(durationObj.toString());
        } else {
            // 从产品信息中获取
            String productCode = (String) getContextParam(context, "productCode");
            if (productCode == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("expectedIncome", BigDecimal.ZERO);
                result.put("totalAmount", amount);
                result.put("message", "缺少必要的参数：productCode");
                result.put("success", false);
                result.put("errorType", "INTEREST_CALC_FAILED");
                log("利息计算失败：缺少必要的参数：productCode");
                return result;
            }
            
            Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
            if (!productOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("expectedIncome", BigDecimal.ZERO);
                result.put("totalAmount", amount);
                result.put("message", "产品不存在：" + productCode);
                result.put("success", false);
                result.put("errorType", "INTEREST_CALC_FAILED");
                log("利息计算失败：产品不存在：" + productCode);
                return result;
            }
            
            DepositProduct product = productOpt.get();
            annualRate = product.getAnnualRate();
            duration = product.getDuration();
        }
        
        // 计算预期收益：本金 × 年化利率 × 天数 / 365
        BigDecimal rate = annualRate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        BigDecimal days = new BigDecimal(duration);
        BigDecimal expectedIncome = amount.multiply(rate).multiply(days)
                .divide(new BigDecimal("365"), 2, RoundingMode.HALF_UP);
        BigDecimal totalAmount = amount.add(expectedIncome);
        
        Map<String, Object> result = new HashMap<>();
        result.put("expectedIncome", expectedIncome);
        result.put("totalAmount", totalAmount);
        result.put("message", "利息计算成功");
        result.put("success", true);
        
        log("利息计算完成，预期收益：" + expectedIncome);
        return result;
    }
}
