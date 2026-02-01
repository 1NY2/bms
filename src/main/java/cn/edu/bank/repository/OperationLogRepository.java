package cn.edu.bank.repository;

import cn.edu.bank.entity.OperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志数据访问层
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    
    /**
     * 根据用户ID查询操作日志
     */
    List<OperationLog> findByUserIdOrderByCreateTimeDesc(String userId);
    
    /**
     * 根据日志类型查询操作日志
     */
    List<OperationLog> findByLogTypeOrderByCreateTimeDesc(String logType);
    
    /**
     * 根据操作类型查询操作日志
     */
    List<OperationLog> findByActionOrderByCreateTimeDesc(String action);
    
    /**
     * 根据产品编码查询操作日志
     */
    List<OperationLog> findByProductCodeOrderByCreateTimeDesc(String productCode);
    
    /**
     * 根据订单号查询操作日志
     */
    List<OperationLog> findByOrderNoOrderByCreateTimeDesc(String orderNo);
    
    /**
     * 根据时间范围查询操作日志
     */
    List<OperationLog> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和时间范围查询操作日志
     */
    List<OperationLog> findByUserIdAndCreateTimeBetweenOrderByCreateTimeDesc(String userId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据日志类型和时间范围查询操作日志
     */
    List<OperationLog> findByLogTypeAndCreateTimeBetweenOrderByCreateTimeDesc(String logType, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据用户ID和日志类型查询操作日志
     */
    List<OperationLog> findByUserIdAndLogTypeOrderByCreateTimeDesc(String userId, String logType);
    
    /**
     * 根据状态查询操作日志
     */
    List<OperationLog> findByStatusOrderByCreateTimeDesc(String status);
    
    /**
     * 模糊搜索日志内容
     */
    @Query("SELECT log FROM OperationLog log WHERE log.content LIKE %:keyword% ORDER BY log.createTime DESC")
    List<OperationLog> findByContentContainingOrderByCreateTimeDesc(@Param("keyword") String keyword);
}