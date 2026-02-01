package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.enums.PurchaseErrorType;
import cn.edu.bank.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务2：用户信息校验服务
 */
@Component("USER_VERIFY")
public class UserVerifyService extends BaseAtomicService {
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    public UserVerifyService() {
        inputParams.put("userId", "用户ID");
        outputParams.put("verifyResult", "校验结果");
        outputParams.put("userStatus", "用户状态");
        outputParams.put("userName", "用户姓名");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "USER_VERIFY";
    }
    
    @Override
    public String getServiceName() {
        return "用户信息校验";
    }
    
    @Override
    public String getServiceDescription() {
        return "校验用户状态和基本信息的有效性";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行用户信息校验");
        
        String userId = (String) getContextParam(context, "userId");
        Map<String, Object> result = new HashMap<>();
        
        Optional<UserInfo> userOpt = userInfoRepository.findByUserId(userId);
        
        if (!userOpt.isPresent()) {
            result.put("verifyResult", false);
            result.put("userStatus", "NOT_FOUND");
            result.put("message", "用户不存在");
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.USER_VERIFY_FAILED.getCode());
            log("用户不存在");
        } else {
            UserInfo user = userOpt.get();
            result.put("verifyResult", "NORMAL".equals(user.getStatus()));
            result.put("userStatus", user.getStatus());
            result.put("userName", user.getUserName());
            if (!"NORMAL".equals(user.getStatus())) {
                result.put("message", "用户状态异常：" + user.getStatus());
                result.put("success", false);
                result.put("errorType", PurchaseErrorType.USER_VERIFY_FAILED.getCode());
            } else {
                result.put("message", "用户信息校验通过");
                result.put("success", true);
            }
            log("用户校验完成，状态：" + user.getStatus());
        }
        
        return result;
    }
}