package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.enums.PurchaseErrorType;
import cn.edu.bank.repository.DepositProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务12：金额校验服务
 */
@Component("AMOUNT_VALIDATE")
public class AmountValidateService extends BaseAtomicService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    public AmountValidateService() {
        inputParams.put("productCode", "产品编码");
        inputParams.put("amount", "购买金额");
        outputParams.put("validateResult", "校验结果");
        outputParams.put("errorMessage", "错误信息");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "AMOUNT_VALIDATE";
    }
    
    @Override
    public String getServiceName() {
        return "金额校验";
    }
    
    @Override
    public String getServiceDescription() {
        return "校验购买金额是否符合产品要求（起存金额、递增金额）";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行金额校验");
        
        String productCode = (String) getContextParam(context, "productCode");
        BigDecimal amount = new BigDecimal(getContextParam(context, "amount").toString());
        
        Map<String, Object> result = new HashMap<>();
        
        Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
        
        if (!productOpt.isPresent()) {
            result.put("validateResult", false);
            result.put("errorMessage", "产品不存在");
            result.put("message", "产品不存在");
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.AMOUNT_VALIDATE_FAILED.getCode());
            log("金额校验失败：产品不存在");
            return result;
        }
        
        DepositProduct product = productOpt.get();
        
        // 校验起存金额
        if (amount.compareTo(product.getMinAmount()) < 0) {
            result.put("validateResult", false);
            String errorMessage = "购买金额低于起存金额：" + product.getMinAmount();
            result.put("errorMessage", errorMessage);
            result.put("message", errorMessage);
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.AMOUNT_VALIDATE_FAILED.getCode());
            log("金额校验失败：低于起存金额");
            return result;
        }
        
        // 校验递增金额
        BigDecimal diff = amount.subtract(product.getMinAmount());
        if (diff.compareTo(BigDecimal.ZERO) > 0 && 
            diff.remainder(product.getIncrementAmount()).compareTo(BigDecimal.ZERO) != 0) {
            result.put("validateResult", false);
            String errorMessage = "购买金额必须是" + product.getIncrementAmount() + "元的整数倍";
            result.put("errorMessage", errorMessage);
            result.put("message", errorMessage);
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.AMOUNT_VALIDATE_FAILED.getCode());
            log("金额校验失败：不符合递增要求");
            return result;
        }
        
        result.put("validateResult", true);
        result.put("message", "金额校验通过");
        log("金额校验通过");
        
        return result;
    }
}