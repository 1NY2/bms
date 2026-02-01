package cn.edu.bank.controller;

import cn.edu.bank.atomic.AtomicService;
import cn.edu.bank.atomic.AtomicServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 原子服务管理接口
 */
@RestController
@RequestMapping("/admin/atomic")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AtomicServiceController {

    @Autowired
    private AtomicServiceRegistry serviceRegistry;

    /**
     * 获取所有原子服务列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listServices() {
        try {
            List<Map<String, Object>> serviceList = new ArrayList<>();
            
            List<AtomicService> services = serviceRegistry.getAllServices();
            for (AtomicService service : services) {
                Map<String, Object> serviceInfo = new HashMap<>();
                serviceInfo.put("serviceCode", service.getServiceCode());
                serviceInfo.put("serviceName", service.getServiceName());
                serviceInfo.put("serviceDescription", service.getServiceDescription());
                serviceInfo.put("inputParams", service.getInputParams());
                serviceInfo.put("outputParams", service.getOutputParams());
                serviceList.add(serviceInfo);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", serviceList);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取服务列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取单个原子服务详情
     */
    @GetMapping("/detail/{serviceCode}")
    public ResponseEntity<Map<String, Object>> getServiceDetail(@PathVariable String serviceCode) {
        try {
            AtomicService service = serviceRegistry.getService(serviceCode);
            if (service == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "服务不存在");
                return ResponseEntity.ok(result);
            }
            
            Map<String, Object> serviceDetail = new HashMap<>();
            serviceDetail.put("serviceCode", service.getServiceCode());
            serviceDetail.put("serviceName", service.getServiceName());
            serviceDetail.put("serviceDescription", service.getServiceDescription());
            serviceDetail.put("inputParams", service.getInputParams());
            serviceDetail.put("outputParams", service.getOutputParams());
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", serviceDetail);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取服务详情失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 测试原子服务执行
     */
    @PostMapping("/test/{serviceCode}")
    public ResponseEntity<Map<String, Object>> testService(
            @PathVariable String serviceCode,
            @RequestBody Map<String, Object> context) {
        try {
            AtomicService service = serviceRegistry.getService(serviceCode);
            if (service == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "服务不存在");
                return ResponseEntity.ok(result);
            }
            
            Map<String, Object> executeResult = service.execute(context);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", executeResult);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "服务执行失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}
