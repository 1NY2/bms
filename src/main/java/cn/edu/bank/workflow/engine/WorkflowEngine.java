package cn.edu.bank.workflow.engine;

import cn.edu.bank.atomic.AtomicService;
import cn.edu.bank.atomic.AtomicServiceRegistry;
import cn.edu.bank.entity.OperationLog;
import cn.edu.bank.repository.OperationLogRepository;
import cn.edu.bank.workflow.entity.WorkflowConfig;
import cn.edu.bank.workflow.model.FlowDefinition;
import cn.edu.bank.workflow.repository.WorkflowConfigRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 流程引擎 - 动态执行编排好的流程
 */
@Service
public class WorkflowEngine {
    
    @Autowired
    private WorkflowConfigRepository workflowConfigRepository;
    
    @Autowired
    private AtomicServiceRegistry serviceRegistry;
    
    @Autowired
    private OperationLogRepository logRepository;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    public WorkflowEngine() {
        // 配置ObjectMapper忽略未知属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置ObjectMapper处理其他JSON格式问题
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        // 额外配置：允许缺少引号的字段名和控制字符
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        // 允许转义字符
        objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
    }
    
    /**
     * 根据产品编码执行流程
     */
    public Map<String, Object> executeByProduct(String productCode, Map<String, Object> context) throws Exception {
        // 1. 查找产品关联的流程
        List<WorkflowConfig> workflows = workflowConfigRepository.findByProductCode(productCode);
        
        if (workflows.isEmpty()) {
            throw new RuntimeException("未找到产品关联的流程配置");
        }
        
        // 使用已发布的流程
        WorkflowConfig workflow = workflows.stream()
            .filter(w -> "PUBLISHED".equals(w.getStatus()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("未找到已发布的流程"));
        
        return execute(workflow.getWorkflowCode(), context);
    }
    
    /**
     * 根据流程编码执行流程
     */
    public Map<String, Object> execute(String workflowCode, Map<String, Object> context) throws Exception {
        System.out.println("========== 开始执行流程：" + workflowCode + " ==========");
        
        // 记录流程执行日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("WORKFLOW");
        logEntity.setAction("EXECUTE_WORKFLOW");
        logEntity.setUserId((String) context.get("userId"));
        logEntity.setProductCode((String) context.get("productCode"));
        logEntity.setContent("开始执行流程: " + workflowCode);
        logEntity.setStatus("PROCESSING");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        // 1. 加载流程配置
        WorkflowConfig config = workflowConfigRepository.findByWorkflowCode(workflowCode)
            .orElseThrow(() -> new RuntimeException("流程不存在：" + workflowCode));
        
        // 2. 解析流程定义
        FlowDefinition definition;
        try {
            // 预处理JSON字符串，确保格式正确
            String jsonContent = preprocessJson(config.getFlowDefinition());
            definition = objectMapper.readValue(jsonContent, FlowDefinition.class);
        } catch (Exception e) {
            System.err.println("JSON解析失败，流程定义内容：");
            System.err.println(config.getFlowDefinition());
            System.err.println("错误详情：" + e.getMessage());
            
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("WORKFLOW");
            errorLog.setAction("EXECUTE_WORKFLOW");
            errorLog.setUserId((String) context.get("userId"));
            errorLog.setProductCode((String) context.get("productCode"));
            errorLog.setContent("JSON解析失败: " + e.getMessage());
            errorLog.setStatus("FAILED");
            errorLog.setResponseResult("流程定义JSON格式错误: " + e.getMessage());
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw new RuntimeException("流程定义JSON格式错误: " + e.getMessage(), e);
        }
        
        // 3. 执行流程
        try {
            executeFlow(definition, context);
            
            // 记录成功日志
            OperationLog successLog = new OperationLog();
            successLog.setLogType("WORKFLOW");
            successLog.setAction("EXECUTE_WORKFLOW");
            successLog.setUserId((String) context.get("userId"));
            successLog.setProductCode((String) context.get("productCode"));
            successLog.setContent("流程执行成功: " + workflowCode);
            successLog.setStatus("SUCCESS");
            successLog.setResponseResult("流程执行成功");
            successLog.setCreateTime(LocalDateTime.now());
            logRepository.save(successLog);
        } catch (Exception e) {
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("WORKFLOW");
            errorLog.setAction("EXECUTE_WORKFLOW");
            errorLog.setUserId((String) context.get("userId"));
            errorLog.setProductCode((String) context.get("productCode"));
            errorLog.setContent("流程执行失败: " + e.getMessage());
            errorLog.setStatus("FAILED");
            errorLog.setResponseResult("流程执行失败: " + e.getMessage());
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw e; // 重新抛出异常
        }
        
        System.out.println("========== 流程执行完成 ==========");
        return context;
    }
    
    /**
     * 预处理JSON字符串，修复可能的格式问题
     */
    private String preprocessJson(String jsonContent) {
        // 移除可能的控制字符和不可见字符
        jsonContent = jsonContent.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        return jsonContent;
    }
    
    /**
     * 执行流程
     */
    private void executeFlow(FlowDefinition definition, Map<String, Object> context) throws Exception {
        // 构建节点映射
        Map<String, FlowDefinition.FlowNode> nodeMap = new HashMap<>();
        for (FlowDefinition.FlowNode node : definition.getNodes()) {
            nodeMap.put(node.getNodeId(), node);
        }
        
        // 构建邻接表（图结构）
        Map<String, List<FlowDefinition.FlowEdge>> edgeMap = new HashMap<>();
        for (FlowDefinition.FlowEdge edge : definition.getEdges()) {
            edgeMap.computeIfAbsent(edge.getFrom(), k -> new ArrayList<>()).add(edge);
        }
        
        // 找到开始节点
        FlowDefinition.FlowNode startNode = definition.getNodes().stream()
            .filter(n -> "start".equals(n.getNodeType()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("未找到开始节点"));
        
        // 从开始节点执行
        executeNode(startNode, nodeMap, edgeMap, context);
    }
    
    /**
     * 执行节点（递归）
     */
    private void executeNode(
        FlowDefinition.FlowNode node,
        Map<String, FlowDefinition.FlowNode> nodeMap,
        Map<String, List<FlowDefinition.FlowEdge>> edgeMap,
        Map<String, Object> context
    ) throws Exception {
        
        System.out.println("  -> 执行节点：" + node.getLabel() + " (" + node.getNodeType() + ")");
        
        // 记录节点执行日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("WORKFLOW");
        logEntity.setAction("EXECUTE_NODE");
        logEntity.setUserId((String) context.get("userId"));
        logEntity.setProductCode((String) context.get("productCode"));
        logEntity.setContent("开始执行节点: " + node.getLabel() + " (" + node.getNodeType() + ")");
        logEntity.setStatus("PROCESSING");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        // 检查上下文是否包含失败标志，如果有则停止执行后续节点
        if (context.containsKey("success") && context.get("success").equals(false)) {
            System.out.println("  -> 检测到失败标志，停止执行后续节点");
            
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("WORKFLOW");
            errorLog.setAction("EXECUTE_NODE");
            errorLog.setUserId((String) context.get("userId"));
            errorLog.setProductCode((String) context.get("productCode"));
            errorLog.setContent("检测到失败标志，停止执行后续节点");
            errorLog.setStatus("FAILED");
            errorLog.setResponseResult(context.get("message") != null ? context.get("message").toString() : "流程执行失败");
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            return;
        }
        
        switch (node.getNodeType()) {
            case "start":
                // 开始节点，直接执行下一个
                
                // 记录开始节点日志
                OperationLog startLog = new OperationLog();
                startLog.setLogType("WORKFLOW");
                startLog.setAction("EXECUTE_START_NODE");
                startLog.setUserId((String) context.get("userId"));
                startLog.setProductCode((String) context.get("productCode"));
                startLog.setContent("开始节点执行");
                startLog.setStatus("SUCCESS");
                startLog.setCreateTime(LocalDateTime.now());
                logRepository.save(startLog);
                break;
                
            case "service":
                // 执行原子服务
                executeServiceNode(node, context);
                break;
                
            case "condition":
                // 条件判断节点
                executeConditionNode(node, nodeMap, edgeMap, context);
                return; // 条件节点自己处理后续流转
                
            case "end":
                // 结束节点
                System.out.println("  -> 流程结束");
                
                // 记录结束节点日志
                OperationLog endLog = new OperationLog();
                endLog.setLogType("WORKFLOW");
                endLog.setAction("EXECUTE_END_NODE");
                endLog.setUserId((String) context.get("userId"));
                endLog.setProductCode((String) context.get("productCode"));
                endLog.setContent("流程结束节点执行");
                endLog.setStatus("SUCCESS");
                endLog.setCreateTime(LocalDateTime.now());
                logRepository.save(endLog);
                return;
                
            // 特殊处理拒绝节点
            case "rejectNode":
            case "REJECT":
                // 设置失败标志，停止流程执行
                context.put("success", false);
                // 保留之前设置的错误类型和消息，避免覆盖具体错误信息
                if (!context.containsKey("message")) {
                    context.put("message", "流程条件不满足，购买被拒绝");
                }
                // 只有当没有错误类型时才设置默认值，避免覆盖原子服务已设置的具体错误类型
                if (!context.containsKey("errorType")) {
                    context.put("errorType", "UNKNOWN_ERROR");
                }
                System.out.println("  -> 检测到拒绝节点，停止流程执行");
                
                // 记录拒绝节点日志
                OperationLog rejectLog = new OperationLog();
                rejectLog.setLogType("WORKFLOW");
                rejectLog.setAction("EXECUTE_REJECT_NODE");
                rejectLog.setUserId((String) context.get("userId"));
                rejectLog.setProductCode((String) context.get("productCode"));
                rejectLog.setContent("拒绝节点执行，流程停止");
                rejectLog.setStatus("FAILED");
                rejectLog.setResponseResult(context.get("message") != null ? context.get("message").toString() : "流程条件不满足，购买被拒绝");
                rejectLog.setCreateTime(LocalDateTime.now());
                logRepository.save(rejectLog);
                
                return;
        }
        
        // 执行下一个节点
        List<FlowDefinition.FlowEdge> nextEdges = edgeMap.get(node.getNodeId());
        if (nextEdges != null && !nextEdges.isEmpty()) {
            FlowDefinition.FlowEdge nextEdge = nextEdges.get(0);
            FlowDefinition.FlowNode nextNode = nodeMap.get(nextEdge.getTo());
            if (nextNode != null) {
                executeNode(nextNode, nodeMap, edgeMap, context);
            }
        }
    }
    
    /**
     * 执行服务节点
     */
    private void executeServiceNode(FlowDefinition.FlowNode node, Map<String, Object> context) throws Exception {
        // 获取原子服务
        AtomicService service = serviceRegistry.getService(node.getServiceCode());
        if (service == null) {
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("WORKFLOW");
            errorLog.setAction("EXECUTE_SERVICE_NODE");
            errorLog.setUserId((String) context.get("userId"));
            errorLog.setProductCode((String) context.get("productCode"));
            errorLog.setContent("执行服务失败: 服务不存在 " + node.getServiceCode());
            errorLog.setStatus("FAILED");
            errorLog.setResponseResult("服务不存在：" + node.getServiceCode());
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw new RuntimeException("服务不存在：" + node.getServiceCode());
        }
        
        // 记录服务节点执行日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("WORKFLOW");
        logEntity.setAction("EXECUTE_SERVICE_NODE");
        logEntity.setUserId((String) context.get("userId"));
        logEntity.setProductCode((String) context.get("productCode"));
        logEntity.setContent("开始执行服务节点: " + node.getLabel() + " (" + node.getServiceCode() + ")");
        logEntity.setStatus("PROCESSING");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        // 准备输入参数（参数映射）
        Map<String, Object> serviceContext = prepareServiceInput(node, context);
        
        // 执行服务
        Map<String, Object> result;
        try {
            result = service.execute(serviceContext);
        } catch (Exception e) {
            System.err.println("执行服务失败：" + node.getServiceCode() + ", 错误: " + e.getMessage());
            e.printStackTrace();
            
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("WORKFLOW");
            errorLog.setAction("EXECUTE_SERVICE_NODE");
            errorLog.setUserId((String) context.get("userId"));
            errorLog.setProductCode((String) context.get("productCode"));
            errorLog.setContent("执行服务失败: " + node.getServiceCode() + ", 错误: " + e.getMessage());
            errorLog.setStatus("FAILED");
            errorLog.setResponseResult("执行服务失败：" + node.getServiceCode() + ", 错误: " + e.getMessage());
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw new RuntimeException("执行服务失败：" + node.getServiceCode() + ", 错误: " + e.getMessage(), e);
        }
        
        // 检查服务执行结果是否包含失败标志
        if (result.containsKey("success") && result.get("success").equals(false)) {
            // 将失败信息和错误类型传递到上下文，以便提前终止流程
            context.put("success", false);
            if (result.containsKey("message")) {
                context.put("message", result.get("message"));
            }
            // 传递错误类型
            if (result.containsKey("errorType")) {
                context.put("errorType", result.get("errorType"));
            }
            System.out.println("  -> 服务执行失败，停止后续节点执行");
            
            // 记录服务失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("WORKFLOW");
            errorLog.setAction("EXECUTE_SERVICE_NODE");
            errorLog.setUserId((String) context.get("userId"));
            errorLog.setProductCode((String) context.get("productCode"));
            errorLog.setContent("服务执行失败: " + node.getLabel() + " (" + node.getServiceCode() + "), 原因: " + result.get("message"));
            errorLog.setStatus("FAILED");
            errorLog.setResponseResult(result.get("message").toString());
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            return;
        }
        
        // 记录服务成功日志
        OperationLog successLog = new OperationLog();
        successLog.setLogType("WORKFLOW");
        successLog.setAction("EXECUTE_SERVICE_NODE");
        successLog.setUserId((String) context.get("userId"));
        successLog.setProductCode((String) context.get("productCode"));
        successLog.setContent("服务执行成功: " + node.getLabel() + " (" + node.getServiceCode() + ")");
        successLog.setStatus("SUCCESS");
        successLog.setResponseResult("服务执行成功");
        successLog.setCreateTime(LocalDateTime.now());
        logRepository.save(successLog);
        
        // 处理输出参数映射 - 只有在服务成功时才进行输出映射
        mapServiceOutput(node, result, context);
    }
    
    /**
     * 执行条件节点
     */
    private void executeConditionNode(
        FlowDefinition.FlowNode node,
        Map<String, FlowDefinition.FlowNode> nodeMap,
        Map<String, List<FlowDefinition.FlowEdge>> edgeMap,
        Map<String, Object> context
    ) throws Exception {
        
        // 记录条件节点执行日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("WORKFLOW");
        logEntity.setAction("EXECUTE_CONDITION_NODE");
        logEntity.setUserId((String) context.get("userId"));
        logEntity.setProductCode((String) context.get("productCode"));
        logEntity.setContent("开始执行条件节点: " + node.getLabel() + ", 表达式: " + node.getExpression());
        logEntity.setStatus("PROCESSING");
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        // 计算条件表达式
        boolean result = evaluateExpression(node.getExpression(), context);
        System.out.println("    条件判断结果：" + result);
        
        // 记录条件判断结果日志
        OperationLog resultLog = new OperationLog();
        resultLog.setLogType("WORKFLOW");
        resultLog.setAction("EXECUTE_CONDITION_NODE");
        resultLog.setUserId((String) context.get("userId"));
        resultLog.setProductCode((String) context.get("productCode"));
        resultLog.setContent("条件判断完成: " + node.getLabel() + ", 表达式: " + node.getExpression() + ", 结果: " + result);
        resultLog.setStatus("SUCCESS");
        resultLog.setResponseResult(String.valueOf(result));
        resultLog.setCreateTime(LocalDateTime.now());
        logRepository.save(resultLog);
        
        // 根据条件选择分支
        List<FlowDefinition.FlowEdge> nextEdges = edgeMap.get(node.getNodeId());
        if (nextEdges != null) {
            for (FlowDefinition.FlowEdge edge : nextEdges) {
                String condition = edge.getCondition();
                if ((result && "true".equals(condition)) || 
                    (!result && "false".equals(condition))) {
                    FlowDefinition.FlowNode nextNode = nodeMap.get(edge.getTo());
                    if (nextNode != null) {
                        executeNode(nextNode, nodeMap, edgeMap, context);
                    }
                    break;
                }
            }
        }
    }
    
    /**
     * 准备服务输入参数
     */
    private Map<String, Object> prepareServiceInput(
        FlowDefinition.FlowNode node, 
        Map<String, Object> context
    ) {
        Map<String, Object> input = new HashMap<>(context);
        
        // 处理输入参数映射
        if (node.getInputMapping() != null) {
            for (Map.Entry<String, String> entry : node.getInputMapping().entrySet()) {
                String paramName = entry.getKey();
                String expression = entry.getValue();
                
                // 解析表达式：${context.xxx}
                Object value = resolveExpression(expression, context);
                input.put(paramName, value);
            }
        }
        
        // 特殊处理拒绝服务：如果输入映射中没有包含message或errorType，但上下文中有这些值，则自动添加
        if ("REJECT_SERVICE".equals(node.getServiceCode())) {
            if (!input.containsKey("message") && context.containsKey("message")) {
                input.put("message", context.get("message"));
            }
            if (!input.containsKey("errorType") && context.containsKey("errorType")) {
                input.put("errorType", context.get("errorType"));
            }
        }
        
        return input;
    }
    
    /**
     * 映射服务输出
     */
    private void mapServiceOutput(
        FlowDefinition.FlowNode node,
        Map<String, Object> result,
        Map<String, Object> context
    ) {
        // 如果上下文已标记为失败，避免覆盖错误类型和消息
        boolean isFailed = context.containsKey("success") && !context.get("success").equals(true);
        
        if (node.getOutputMapping() != null) {
            for (Map.Entry<String, String> entry : node.getOutputMapping().entrySet()) {
                String resultKey = entry.getKey();      // 服务输出的key
                String contextKey = entry.getValue();    // 存入context的key
                
                if (result.containsKey(resultKey)) {
                    // 如果流程已失败，避免覆盖错误类型和消息
                    if (isFailed && ("errorType".equals(contextKey) || "message".equals(contextKey) || "success".equals(contextKey))) {
                        continue; // 跳过可能覆盖错误信息的映射
                    }
                    context.put(contextKey, result.get(resultKey));
                }
            }
        } else {
            // 如果没有配置映射，只在流程未失败时合并所有结果
            if (!isFailed) {
                // 流程未失败时才合并所有结果
                context.putAll(result);
            } else {
                // 流程已失败时，只添加非关键信息，避免覆盖错误类型和消息
                for (Map.Entry<String, Object> entry : result.entrySet()) {
                    String key = entry.getKey();
                    // 避免覆盖关键的错误信息
                    if (!"errorType".equals(key) && !"message".equals(key) && !"success".equals(key)) {
                        context.put(key, entry.getValue());
                    }
                }
            }
        }
    }
    
    /**
     * 解析表达式：${context.userId} -> context中的userId值
     */
    private Object resolveExpression(String expression, Map<String, Object> context) {
        if (expression == null) {
            return null;
        }
        
        // 简单实现：${context.xxx}
        if (expression.startsWith("${") && expression.endsWith("}")) {
            String key = expression.substring(2, expression.length() - 1);
            if (key.startsWith("context.")) {
                key = key.substring(8); // 去掉"context."
            }
            return context.get(key);
        }
        
        return expression;
    }
    
    /**
     * 计算条件表达式
     */
    private boolean evaluateExpression(String expression, Map<String, Object> context) {
        try {
            // 使用SpEL表达式引擎
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            
            // 创建上下文
            StandardEvaluationContext evalContext = new StandardEvaluationContext();
            context.forEach(evalContext::setVariable);
            
            // 计算表达式
            Boolean result = exp.getValue(evalContext, Boolean.class);
            return result != null && result;
        } catch (Exception e) {
            System.err.println("表达式计算失败：" + expression);
            e.printStackTrace();
            return false;
        }
    }
}