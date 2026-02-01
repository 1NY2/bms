package cn.edu.bank.atomic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原子服务注册器
 * 管理所有原子服务
 */
@Component
public class AtomicServiceRegistry {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    // 服务注册表：serviceCode -> AtomicService
    private Map<String, AtomicService> serviceMap = new HashMap<>();
    
    /**
     * 初始化：自动注册所有原子服务
     */
    @PostConstruct
    public void init() {
        // 获取所有AtomicService实现类的Bean
        Map<String, AtomicService> beans = applicationContext.getBeansOfType(AtomicService.class);
        
        for (AtomicService service : beans.values()) {
            registerService(service);
        }
        
        System.out.println("========================================");
        System.out.println("原子服务注册完成，共注册 " + serviceMap.size() + " 个服务：");
        serviceMap.forEach((code, service) -> {
            System.out.println("  - " + code + ": " + service.getServiceName());
        });
        System.out.println("========================================");
    }
    
    /**
     * 注册服务
     */
    public void registerService(AtomicService service) {
        serviceMap.put(service.getServiceCode(), service);
    }
    
    /**
     * 获取服务
     */
    public AtomicService getService(String serviceCode) {
        return serviceMap.get(serviceCode);
    }
    
    /**
     * 获取所有服务
     */
    public List<AtomicService> getAllServices() {
        return new ArrayList<>(serviceMap.values());
    }
    
    /**
     * 检查服务是否存在
     */
    public boolean hasService(String serviceCode) {
        return serviceMap.containsKey(serviceCode);
    }
}
