package cn.edu.bank.atomic.impl;

import cn.edu.bank.enums.PurchaseErrorType;
import org.springframework.stereotype.Component;
import java.util.*;

/**
 * 原子服务5：用户标签控制服务
 */
@Component("TAG_CHECK")
public class TagCheckService extends BaseAtomicService {
    
    // 模拟用户标签数据
    private static final Map<String, List<String>> USER_TAGS = new HashMap<>();
    
    static {
        USER_TAGS.put("13800138000", Arrays.asList("VIP", "HIGH_VALUE", "TRUSTED"));
        USER_TAGS.put("13900139000", Arrays.asList("NORMAL", "ACTIVE"));
    }
    
    public TagCheckService() {
        inputParams.put("userId", "用户ID");
        inputParams.put("requiredTags", "需要的标签");
        outputParams.put("tagMatched", "标签是否匹配");
        outputParams.put("userTags", "用户标签列表");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "TAG_CHECK";
    }
    
    @Override
    public String getServiceName() {
        return "用户标签控制";
    }
    
    @Override
    public String getServiceDescription() {
        return "根据用户标签控制产品购买权限";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行用户标签检查");
        
        String userId = (String) getContextParam(context, "userId");
        String requiredTags = (String) getContextParam(context, "requiredTags");
        
        Map<String, Object> result = new HashMap<>();
        List<String> userTags = USER_TAGS.getOrDefault(userId, Collections.emptyList());
        
        boolean matched = true;
        if (requiredTags != null && !requiredTags.isEmpty()) {
            String[] required = requiredTags.split(",");
            matched = Arrays.stream(required).allMatch(userTags::contains);
        }
        
        result.put("tagMatched", matched);
        result.put("userTags", userTags);
        if (!matched) {
            result.put("message", "用户标签限制：不符合购买此产品的标签要求");
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.TAG_RESTRICTED.getCode());
        } else {
            result.put("message", "标签检查通过");
            result.put("success", true);
        }
        log("标签检查完成，匹配：" + matched);
        
        return result;
    }
}