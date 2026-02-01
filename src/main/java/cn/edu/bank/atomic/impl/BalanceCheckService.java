package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.enums.PurchaseErrorType;
import cn.edu.bank.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务11：余额校验服务
 */
@Component("BALANCE_CHECK")
public class BalanceCheckService extends BaseAtomicService {
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    public BalanceCheckService() {
        inputParams.put("userId", "用户ID");
        inputParams.put("requiredAmount", "需要金额");
        inputParams.put("amount", "购买金额"); // 兼容流程定义中的amount参数
        outputParams.put("balanceSufficient", "余额是否充足");
        outputParams.put("currentBalance", "当前余额");
        outputParams.put("checkResult", "校验结果");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "BALANCE_CHECK";
    }
    
    @Override
    public String getServiceName() {
        return "余额校验";
    }
    
    @Override
    public String getServiceDescription() {
        return "检查用户账户余额是否充足";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行余额校验");
        
        String userId = (String) getContextParam(context, "userId");
        // 尝试从requiredAmount参数获取，如果没有则从amount参数获取（兼容不同流程定义）
        Object amountObj = getContextParam(context, "requiredAmount");
        if (amountObj == null) {
            amountObj = getContextParam(context, "amount");
        }
        BigDecimal requiredAmount = new BigDecimal(amountObj.toString());
        
        Map<String, Object> result = new HashMap<>();
        
        Optional<UserInfo> userOpt = userInfoRepository.findByUserId(userId);
        
        if (userOpt.isPresent()) {
            UserInfo user = userOpt.get();
            BigDecimal balance = user.getBalance();
            boolean sufficient = balance.compareTo(requiredAmount) >= 0;
            
            result.put("balanceSufficient", sufficient);
            result.put("checkResult", sufficient); // 添加checkResult字段以匹配流程定义
            result.put("currentBalance", balance);
            String message = sufficient ? "余额充足" : "余额不足";
            result.put("message", message);
            result.put("success", sufficient);
            if (!sufficient) {
                result.put("errorType", PurchaseErrorType.BALANCE_INSUFFICIENT.getCode());
            }
            log("余额校验完成：" + sufficient);
        } else {
            result.put("balanceSufficient", false);
            result.put("checkResult", false);
            result.put("message", "用户不存在");
            result.put("errorType", PurchaseErrorType.USER_VERIFY_FAILED.getCode());
            log("余额校验失败：用户不存在");
        }
        
        return result;
    }
}