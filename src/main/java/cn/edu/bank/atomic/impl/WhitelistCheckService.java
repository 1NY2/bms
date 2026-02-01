package cn.edu.bank.atomic.impl;

import cn.edu.bank.enums.PurchaseErrorType;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原子服务3：白名单购买控制服务
 */
@Component("WHITELIST_CHECK")
public class WhitelistCheckService extends BaseAtomicService {
    
    // 模拟白名单数据
    private static final List<String> WHITELIST = Arrays.asList(
        "13800138000", "13900139000", "13700137000"
    );
    
    public WhitelistCheckService() {
        inputParams.put("userId", "用户ID");
        outputParams.put("inWhitelist", "是否在白名单");
        outputParams.put("checkMessage", "检查信息");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "WHITELIST_CHECK";
    }
    
    @Override
    public String getServiceName() {
        return "白名单购买控制";
    }
    
    @Override
    public String getServiceDescription() {
        return "检查用户是否在白名单中，控制购买权限";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行白名单检查");
        
        String userId = (String) getContextParam(context, "userId");
        Map<String, Object> result = new HashMap<>();
        
        boolean inWhitelist = WHITELIST.contains(userId);
        result.put("inWhitelist", inWhitelist);
        result.put("checkMessage", inWhitelist ? "用户在白名单中" : "用户不在白名单中");
        if (!inWhitelist) {
            result.put("message", "用户不在白名单中，无法购买此产品");
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.WHITELIST_DENIED.getCode());
        } else {
            result.put("message", "白名单检查通过");
            result.put("success", true);
        }
        
        log("白名单检查完成：" + inWhitelist);
        return result;
    }
}