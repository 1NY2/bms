package cn.edu.bank.atomic;

import java.util.Map;

/**
 * 原子服务接口
 * 所有原子服务都必须实现此接口
 */
public interface AtomicService {
    
    /**
     * 获取服务编码
     */
    String getServiceCode();
    
    /**
     * 获取服务名称
     */
    String getServiceName();
    
    /**
     * 获取服务描述
     */
    String getServiceDescription();
    
    /**
     * 获取输入参数定义
     */
    Map<String, String> getInputParams();
    
    /**
     * 获取输出参数定义
     */
    Map<String, String> getOutputParams();
    
    /**
     * 执行服务
     * @param context 上下文参数
     * @return 执行结果
     */
    Map<String, Object> execute(Map<String, Object> context) throws Exception;
}
