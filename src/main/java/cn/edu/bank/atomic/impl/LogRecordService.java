package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.OperationLog;
import cn.edu.bank.repository.OperationLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 原子服务10：日志录入服务
 */
@Component("LOG_RECORD")
public class LogRecordService extends BaseAtomicService {
    
    public LogRecordService() {
        inputParams.put("logType", "日志类型");
        inputParams.put("logContent", "日志内容");
        inputParams.put("userId", "用户ID");
        inputParams.put("productCode", "产品编码"); // 兼容流程定义
        inputParams.put("orderNo", "订单号"); // 兼容流程定义
        inputParams.put("action", "操作类型"); // 兼容流程定义
        inputParams.put("errorType", "错误类型"); // 兼容错误类型
        outputParams.put("logId", "日志ID");
        outputParams.put("recordTime", "记录时间");
    }
    
    @Override
    public String getServiceCode() {
        return "LOG_RECORD";
    }
    
    @Override
    public String getServiceName() {
        return "日志录入";
    }
    
    @Override
    public String getServiceDescription() {
        return "记录业务操作日志";
    }
    
    @Autowired
    private OperationLogRepository logRepository;
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行日志录入");
        
        // 尝试从logType参数获取，如果没有则从action参数获取
        String logType = (String) getContextParam(context, "logType");
        if (logType == null) {
            logType = (String) getContextParam(context, "action");
        }
        
        // 构造日志内容
        String logContent = (String) getContextParam(context, "logContent");
        if (logContent == null) {
            // 从其他参数构造日志内容
            String productCode = (String) getContextParam(context, "productCode");
            String orderNo = (String) getContextParam(context, "orderNo");
            String userId = (String) getContextParam(context, "userId");
            logContent = String.format("用户%s购买产品%s，订单号%s", userId, productCode, orderNo);
        }
        
        String userId = (String) getContextParam(context, "userId");
        String productCode = (String) getContextParam(context, "productCode");
        String orderNo = (String) getContextParam(context, "orderNo");
        String action = (String) getContextParam(context, "action");
        String errorType = (String) getContextParam(context, "errorType");
        String status = (String) getContextParam(context, "status");
        if (status == null) {
            status = "SUCCESS";
        }
        
        // 创建日志实体
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType(logType != null ? logType : "UNKNOWN");
        logEntity.setUserId(userId);
        logEntity.setProductCode(productCode);
        logEntity.setOrderNo(orderNo);
        logEntity.setAction(action != null ? action : "UNKNOWN");
        logEntity.setErrorType(errorType);
        logEntity.setContent(logContent);
        logEntity.setStatus(status);
        logEntity.setCreateTime(LocalDateTime.now());
        
        // 保存到数据库
        OperationLog savedLog = logRepository.save(logEntity);
        
        Map<String, Object> result = new HashMap<>();
        result.put("logId", savedLog.getId().toString());
        result.put("recordTime", savedLog.getCreateTime());
        result.put("message", "日志录入成功");
        result.put("success", true);
        
        log("日志录入完成，ID：" + savedLog.getId());
        return result;
    }
}
