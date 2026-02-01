package cn.edu.bank.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购买订单实体类
 */
@Data
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 订单号
     */
    @Column(nullable = false, unique = true, length = 50)
    private String orderNo;
    
    /**
     * 用户ID
     */
    @Column(nullable = false, length = 50)
    private String userId;
    
    /**
     * 产品编号
     */
    @Column(nullable = false, length = 50)
    private String productCode;
    
    /**
     * 产品名称
     */
    @Column(nullable = false, length = 100)
    private String productName;
    
    /**
     * 购买金额
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
    
    /**
     * 年化利率
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualRate;
    
    /**
     * 产品期限（天数）
     */
    @Column(nullable = false)
    private Integer duration;
    
    /**
     * 预期收益
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal expectedIncome;
    
    /**
     * 订单状态（PENDING-待支付, PROCESSING-处理中, SUCCESS-成功, FAILED-失败, CANCELLED-已取消）
     */
    @Column(nullable = false, length = 20)
    private String status;
    
    /**
     * 起息日期
     */
    private LocalDateTime startDate;
    
    /**
     * 到期日期
     */
    private LocalDateTime endDate;
    
    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 备注信息
     */
    @Column(columnDefinition = "TEXT")
    private String remark;
}
