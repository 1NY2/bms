package cn.edu.bank.atomic.impl;

import cn.edu.bank.enums.PurchaseErrorType;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * 原子服务：拒绝服务
 * 当流程中的条件不满足时，此服务用于终止流程并返回失败信息
 */
@Component("REJECT_SERVICE")
public class RejectService extends BaseAtomicService {
    
    public RejectService() {
        inputParams.put("message", "拒绝原因");
        inputParams.put("errorType", "错误类型");
        outputParams.put("success", "执行结果");
        outputParams.put("message", "失败信息");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "REJECT_SERVICE";
    }
    
    @Override
    public String getServiceName() {
        return "拒绝服务";
    }
    
    @Override
    public String getServiceDescription() {
        return "当流程条件不满足时终止流程并返回失败信息";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("执行拒绝服务");
        
        String message = (String) getContextParam(context, "message");
        String errorTypeCode = (String) getContextParam(context, "errorType");
        
        // 如果没有指定错误类型，则使用默认错误类型
        PurchaseErrorType errorType = PurchaseErrorType.UNKNOWN_ERROR;
        if (errorTypeCode != null) {
            errorType = PurchaseErrorType.fromCode(errorTypeCode);
        }
        
        // 如果消息为null，尝试从上下文中直接获取
        if (message == null) {
            message = (String) context.get("message");
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        result.put("errorType", errorType.getCode());
        log("拒绝服务执行完成：" + message + ", 错误类型：" + errorType.getCode());
        
        return result;
    }
}