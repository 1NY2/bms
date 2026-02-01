package cn.edu.bank.workflow.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 流程配置实体
 */
@Entity
@Table(name = "workflow_config")
public class WorkflowConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String workflowCode;          // 流程编码
    
    @Column(nullable = false, length = 100)
    private String workflowName;          // 流程名称
    
    @Column(length = 50)
    private String productCode;           // 关联产品编码
    
    @Column(columnDefinition = "TEXT")
    private String flowDefinition;        // 流程定义JSON
    
    @Column(length = 20)
    private String status;                // 状态：DRAFT(草稿)、PUBLISHED(已发布)
    
    private Integer version;              // 版本号
    
    @Column(columnDefinition = "TEXT")
    private String description;           // 流程描述
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private LocalDateTime publishTime;    // 发布时间
    
    // Getter and Setter (简化起见，实际应用中使用Lombok)
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public String getProductCode() {
        return productCode;
    }
    
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getFlowDefinition() {
        return flowDefinition;
    }
    
    public void setFlowDefinition(String flowDefinition) {
        this.flowDefinition = flowDefinition;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public LocalDateTime getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
}
