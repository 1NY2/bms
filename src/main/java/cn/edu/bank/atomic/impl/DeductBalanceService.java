package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务：余额扣减服务
 */
@Component("DEDUCT_BALANCE")
public class DeductBalanceService extends BaseAtomicService {
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    public DeductBalanceService() {
        inputParams.put("userId", "用户ID");
        inputParams.put("amount", "扣减金额");
        outputParams.put("deductResult", "扣减结果");
        outputParams.put("remainingBalance", "剩余余额");
    }
    
    @Override
    public String getServiceCode() {
        return "DEDUCT_BALANCE";
    }
    
    @Override
    public String getServiceName() {
        return "余额扣减";
    }
    
    @Override
    public String getServiceDescription() {
        return "扣减用户账户余额";
    }
    
    @Override
    @Transactional
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行余额扣减");
        
        String userId = (String) getContextParam(context, "userId");
        BigDecimal amount = new BigDecimal(getContextParam(context, "amount").toString());
        
        Map<String, Object> result = new HashMap<>();
        
        Optional<UserInfo> userOpt = userInfoRepository.findByUserId(userId);
        
        if (!userOpt.isPresent()) {
            result.put("deductResult", false);
            result.put("message", "用户不存在");
            result.put("success", false);
            result.put("errorType", "BALANCE_DEDUCT_FAILED");
            log("余额扣减失败：用户不存在");
            
            return result;
        }
        
        UserInfo user = userOpt.get();
        BigDecimal currentBalance = user.getBalance();
        
        // 检查余额是否充足
        if (currentBalance.compareTo(amount) < 0) {
            result.put("deductResult", false);
            result.put("message", "账户余额不足");
            result.put("success", false);
            result.put("errorType", "BALANCE_INSUFFICIENT");
            log("余额扣减失败：账户余额不足");
            
            return result;
        }
        
        // 扣减余额
        BigDecimal newBalance = currentBalance.subtract(amount);
        user.setBalance(newBalance);
        userInfoRepository.save(user);
        
        result.put("deductResult", true);
        result.put("remainingBalance", newBalance);
        result.put("message", "余额扣减成功");
        result.put("success", true);
        log("余额扣减成功，扣减金额：" + amount + "，剩余余额：" + newBalance);
        
        return result;
    }
}