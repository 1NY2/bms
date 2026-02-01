package cn.edu.bank.repository;

import cn.edu.bank.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 购买订单Repository
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    
    /**
     * 根据订单号查询订单
     */
    Optional<PurchaseOrder> findByOrderNo(String orderNo);
    
    /**
     * 查询用户的所有订单
     */
    List<PurchaseOrder> findByUserIdOrderByCreateTimeDesc(String userId);
    
    /**
     * 查询所有订单，按创建时间倒序排列
     */
    List<PurchaseOrder> findAllByOrderByCreateTimeDesc();
    
    /**
     * 查询用户今日购买某产品的总金额
     */
    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM PurchaseOrder o " +
           "WHERE o.userId = :userId AND o.productCode = :productCode " +
           "AND o.createTime >= :startTime AND o.status = 'SUCCESS'")
    BigDecimal sumTodayPurchaseAmount(@Param("userId") String userId, 
                                      @Param("productCode") String productCode,
                                      @Param("startTime") LocalDateTime startTime);
    
    /**
     * 查询用户购买某产品的总金额
     */
    @Query("SELECT COALESCE(SUM(o.amount), 0) FROM PurchaseOrder o " +
           "WHERE o.userId = :userId AND o.productCode = :productCode AND o.status = 'SUCCESS'")
    BigDecimal sumUserPurchaseAmount(@Param("userId") String userId, 
                                     @Param("productCode") String productCode);
}