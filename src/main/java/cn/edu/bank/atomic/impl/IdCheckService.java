package cn.edu.bank.atomic.impl;

import cn.edu.bank.enums.PurchaseErrorType;
import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务1：证件审查服务
 */
@Component("ID_CHECK")
public class IdCheckService extends BaseAtomicService {
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    public IdCheckService() {
        inputParams.put("userId", "用户ID");
        inputParams.put("idCard", "身份证号");
        inputParams.put("userName", "用户姓名");
        outputParams.put("idValid", "身份证验证结果");
        outputParams.put("checkMessage", "审查信息");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "ID_CHECK";
    }
    
    @Override
    public String getServiceName() {
        return "证件审查";
    }
    
    @Override
    public String getServiceDescription() {
        return "验证用户身份证信息的合法性和真实性";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行证件审查");
        
        String idCard = (String) getContextParam(context, "idCard");
        String userName = (String) getContextParam(context, "userName");
        String userId = (String) getContextParam(context, "userId");
        
        Map<String, Object> result = new HashMap<>();
        
        // 如果身份证号未从输入参数中提供，则从用户信息中获取
        if (idCard == null || idCard.trim().isEmpty()) {
            if (userId != null && !userId.trim().isEmpty()) {
                Optional<UserInfo> userOpt = userInfoRepository.findByUserId(userId);
                if (userOpt.isPresent()) {
                    idCard = userOpt.get().getIdCard();
                    // 如果userName未提供，也从用户信息中获取
                    if (userName == null || userName.trim().isEmpty()) {
                        userName = userOpt.get().getUserName();
                    }
                } else {
                    result.put("checkResult", false);
                    result.put("checkMessage", "用户不存在，无法获取身份证信息");
                    result.put("message", "用户不存在，无法获取身份证信息");
                    result.put("errorType", PurchaseErrorType.ID_CHECK_FAILED.getCode());
                    result.put("success", false);
                    log("证件审查失败：用户不存在");
                    return result;
                }
            } else {
                result.put("checkResult", false);
                result.put("checkMessage", "缺少身份证号和用户ID信息");
                result.put("message", "缺少身份证号和用户ID信息");
                result.put("errorType", PurchaseErrorType.ID_CHECK_FAILED.getCode());
                result.put("success", false);
                log("证件审查失败：缺少身份证号和用户ID信息");
                return result;
            }
        }
        
        // 完整的身份证校验逻辑
        if (idCard == null || idCard.length() != 18) {
            result.put("idValid", false);
            result.put("checkResult", false);
            result.put("checkMessage", "身份证号格式不正确");
            result.put("message", "身份证号格式不正确");
            result.put("errorType", PurchaseErrorType.ID_CHECK_FAILED.getCode());
            result.put("success", false);
            log("证件审查失败：身份证号格式不正确");
        } else if (!isValidIdCard(idCard)) {
            result.put("idValid", false);
            result.put("checkResult", false);
            result.put("checkMessage", "身份证号校验失败");
            result.put("message", "身份证号校验失败");
            result.put("errorType", PurchaseErrorType.ID_CHECK_FAILED.getCode());
            result.put("success", false);
            log("证件审查失败：身份证号校验失败");
        } else {
            result.put("idValid", true);
            result.put("checkResult", true);
            result.put("checkMessage", "证件审查通过");
            result.put("message", "证件审查通过");
            result.put("success", true);
            log("证件审查通过");
        }
        
        return result;
    }
    
    /**
     * 验证身份证号码的合法性
     * @param idCard 身份证号码
     * @return 是否合法
     */
    private boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }
        
        // 验证前17位是否为数字
        String body = idCard.substring(0, 17);
        if (!body.matches("\\d{17}")) {
            return false;
        }
        
        // 验证最后一位校验码
        String lastChar = idCard.substring(17, 18);
        if (!lastChar.matches("[0-9Xx]")) {
            return false;
        }
        
        // 计算校验码
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        String checkCodes = "10X98765432";
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * weights[i];
        }
        
        int checkIndex = sum % 11;
        char expectedCheckCode = checkCodes.charAt(checkIndex);
        
        return Character.toUpperCase(lastChar.charAt(0)) == expectedCheckCode;
    }
    
    /**
     * 生成身份证号的校验码
     * @param body 身份证前17位
     * @return 校验码
     */
    public static String generateCheckCode(String body) {
        if (body == null || body.length() != 17 || !body.matches("\\d{17}")) {
            return null;
        }
        
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        String checkCodes = "10X98765432";
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (body.charAt(i) - '0') * weights[i];
        }
        
        int checkIndex = sum % 11;
        return String.valueOf(checkCodes.charAt(checkIndex));
    }
}