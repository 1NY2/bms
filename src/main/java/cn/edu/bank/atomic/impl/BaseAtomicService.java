package cn.edu.bank.atomic.impl;

import cn.edu.bank.atomic.AtomicService;

import java.util.HashMap;
import java.util.Map;

/**
 * 原子服务基类
 */
public abstract class BaseAtomicService implements AtomicService {
    
    protected Map<String, String> inputParams = new HashMap<>();
    protected Map<String, String> outputParams = new HashMap<>();
    
    /**
     * 记录执行日志
     */
    protected void log(String message) {
        System.out.println("[" + getServiceCode() + "] " + message);
    }
    
    /**
     * 获取上下文参数
     */
    protected Object getContextParam(Map<String, Object> context, String key) {
        return context.get(key);
    }
    
    /**
     * 设置上下文参数
     */
    protected void setContextParam(Map<String, Object> context, String key, Object value) {
        context.put(key, value);
    }
    
    @Override
    public Map<String, String> getInputParams() {
        return inputParams;
    }
    
    @Override
    public Map<String, String> getOutputParams() {
        return outputParams;
    }
}
