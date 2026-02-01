package cn.edu.bank.controller;

import cn.edu.bank.workflow.entity.WorkflowConfig;
import cn.edu.bank.workflow.repository.WorkflowConfigRepository;
import cn.edu.bank.workflow.engine.WorkflowEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 流程配置管理接口
 */
@RestController
@RequestMapping("/admin/workflow")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WorkflowController {

    @Autowired
    private WorkflowConfigRepository workflowRepository;
    
    @Autowired
    private WorkflowEngine workflowEngine;

    /**
     * 获取所有流程列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listWorkflows() {
        try {
            List<WorkflowConfig> workflows = workflowRepository.findAll();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", workflows);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取流程列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 根据产品获取流程
     */
    @GetMapping("/product/{productCode}")
    public ResponseEntity<Map<String, Object>> getWorkflowByProduct(@PathVariable String productCode) {
        try {
            List<WorkflowConfig> workflows = workflowRepository.findByProductCode(productCode);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", workflows);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取流程失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取流程详情
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<Map<String, Object>> getWorkflowDetail(@PathVariable Long id) {
        try {
            Optional<WorkflowConfig> workflow = workflowRepository.findById(id);
            
            Map<String, Object> result = new HashMap<>();
            if (workflow.isPresent()) {
                result.put("success", true);
                result.put("data", workflow.get());
            } else {
                result.put("success", false);
                result.put("message", "流程不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取流程详情失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 保存或更新流程
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveWorkflow(@RequestBody WorkflowConfig workflow) {
        try {
            if (workflow.getId() == null) {
                workflow.setCreateTime(LocalDateTime.now());
                workflow.setStatus("DRAFT");
            }
            workflow.setUpdateTime(LocalDateTime.now());
            
            WorkflowConfig saved = workflowRepository.save(workflow);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "保存成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 发布流程
     */
    @PostMapping("/publish/{id}")
    public ResponseEntity<Map<String, Object>> publishWorkflow(@PathVariable Long id) {
        try {
            Optional<WorkflowConfig> workflowOpt = workflowRepository.findById(id);
            if (!workflowOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "流程不存在");
                return ResponseEntity.ok(result);
            }
            
            WorkflowConfig workflow = workflowOpt.get();
            
            // 将同产品的其他已发布流程设为未发布
            List<WorkflowConfig> publishedWorkflows = workflowRepository.findByProductCodeAndStatus(
                    workflow.getProductCode(), "PUBLISHED");
            for (WorkflowConfig published : publishedWorkflows) {
                published.setStatus("DRAFT");
                workflowRepository.save(published);
            }
            
            // 发布当前流程
            workflow.setStatus("PUBLISHED");
            workflow.setUpdateTime(LocalDateTime.now());
            WorkflowConfig saved = workflowRepository.save(workflow);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "发布成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "发布失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 下线流程
     */
    @PostMapping("/unpublish/{id}")
    public ResponseEntity<Map<String, Object>> unpublishWorkflow(@PathVariable Long id) {
        try {
            Optional<WorkflowConfig> workflowOpt = workflowRepository.findById(id);
            if (!workflowOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "流程不存在");
                return ResponseEntity.ok(result);
            }
            
            WorkflowConfig workflow = workflowOpt.get();
            workflow.setStatus("DRAFT");
            workflow.setUpdateTime(LocalDateTime.now());
            WorkflowConfig saved = workflowRepository.save(workflow);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "下线成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "下线失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 删除流程
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteWorkflow(@PathVariable Long id) {
        try {
            workflowRepository.deleteById(id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 测试流程执行
     */
    @PostMapping("/test/{workflowCode}")
    public ResponseEntity<Map<String, Object>> testWorkflow(
            @PathVariable String workflowCode,
            @RequestBody Map<String, Object> context) {
        try {
            Map<String, Object> result = workflowEngine.execute(workflowCode, context);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "执行失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
