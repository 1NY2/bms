package cn.edu.bank.workflow.repository;

import cn.edu.bank.workflow.entity.WorkflowConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 流程配置Repository
 */
@Repository
public interface WorkflowConfigRepository extends JpaRepository<WorkflowConfig, Long> {
    
    Optional<WorkflowConfig> findByWorkflowCode(String workflowCode);
    
    List<WorkflowConfig> findByProductCode(String productCode);
    
    List<WorkflowConfig> findByStatus(String status);
    
    List<WorkflowConfig> findByProductCodeAndStatus(String productCode, String status);
}
