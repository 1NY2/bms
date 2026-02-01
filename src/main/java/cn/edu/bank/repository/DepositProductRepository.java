package cn.edu.bank.repository;

import cn.edu.bank.entity.DepositProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

/**
 * 存款产品Repository
 */
@Repository
public interface DepositProductRepository extends JpaRepository<DepositProduct, Long> {
    
    /**
     * 根据产品编号查询产品
     */
    Optional<DepositProduct> findByProductCode(String productCode);
    
    /**
     * 根据产品编号查询产品（悲观锁）
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM DepositProduct p WHERE p.productCode = :productCode")
    Optional<DepositProduct> findByProductCodeWithLock(@Param("productCode") String productCode);
    
    /**
     * 查询所有在售产品
     */
    List<DepositProduct> findByStatusOrderByAnnualRateDesc(String status);
    
    /**
     * 查询库存大于0的在售产品
     */
    @Query("SELECT p FROM DepositProduct p WHERE p.status = :status AND p.inventory > 0 ORDER BY p.annualRate DESC")
    List<DepositProduct> findAvailableProducts(@Param("status") String status);
}
