package cn.edu.bank.workflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * 流程定义模型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlowDefinition {
    
    private String workflowCode;
    private String workflowName;
    private List<FlowNode> nodes;
    private List<FlowEdge> edges;
    
    // Getter and Setter
    
    public String getWorkflowCode() {
        return workflowCode;
    }
    
    public void setWorkflowCode(String workflowCode) {
        this.workflowCode = workflowCode;
    }
    
    public String getWorkflowName() {
        return workflowName;
    }
    
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }
    
    public List<FlowNode> getNodes() {
        return nodes;
    }
    
    public void setNodes(List<FlowNode> nodes) {
        this.nodes = nodes;
    }
    
    public List<FlowEdge> getEdges() {
        return edges;
    }
    
    public void setEdges(List<FlowEdge> edges) {
        this.edges = edges;
    }
    
    /**
     * 流程节点
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlowNode {
        private String nodeId;
        private String nodeType;          // start/service/condition/end
        private String serviceCode;       // 原子服务编码
        private String label;             // 节点标签
        private Map<String, String> inputMapping;   // 输入参数映射
        private Map<String, String> outputMapping;  // 输出参数映射
        private String expression;        // 条件表达式
        
        // Getter and Setter
        
        public String getNodeId() {
            return nodeId;
        }
        
        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }
        
        public String getNodeType() {
            return nodeType;
        }
        
        public void setNodeType(String nodeType) {
            this.nodeType = nodeType;
        }
        
        public String getServiceCode() {
            return serviceCode;
        }
        
        public void setServiceCode(String serviceCode) {
            this.serviceCode = serviceCode;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
        
        public Map<String, String> getInputMapping() {
            return inputMapping;
        }
        
        public void setInputMapping(Map<String, String> inputMapping) {
            this.inputMapping = inputMapping;
        }
        
        public Map<String, String> getOutputMapping() {
            return outputMapping;
        }
        
        public void setOutputMapping(Map<String, String> outputMapping) {
            this.outputMapping = outputMapping;
        }
        
        public String getExpression() {
            return expression;
        }
        
        public void setExpression(String expression) {
            this.expression = expression;
        }
    }
    
    /**
     * 流程连线
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FlowEdge {
        private String from;              // 起始节点ID
        private String to;                // 目标节点ID
        private String condition;         // 条件（true/false，用于条件节点）
        
        // Getter and Setter
        
        public String getFrom() {
            return from;
        }
        
        public void setFrom(String from) {
            this.from = from;
        }
        
        public String getTo() {
            return to;
        }
        
        public void setTo(String to) {
            this.to = to;
        }
        
        public String getCondition() {
            return condition;
        }
        
        public void setCondition(String condition) {
            this.condition = condition;
        }
    }
}