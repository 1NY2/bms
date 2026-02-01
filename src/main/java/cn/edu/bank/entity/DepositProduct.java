package cn.edu.bank.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 银行存款产品实体类
 */
@Data
@Entity
@Table(name = "deposit_product")
public class DepositProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 产品编号
     */
    @Column(nullable = false, unique = true, length = 50)
    private String productCode;
    
    /**
     * 产品名称
     */
    @Column(nullable = false, length = 100)
    private String productName;
    
    /**
     * 产品期限（天数）
     */
    @Column(nullable = false)
    private Integer duration;
    
    /**
     * 期限单位说明（如：180天、3个月、1年）
     */
    @Column(length = 50)
    private String durationDesc;
    
    /**
     * 年化利率（%）
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal annualRate;
    
    /**
     * 起存金额（元）
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minAmount;
    
    /**
     * 递增金额（元）
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal incrementAmount;
    
    /**
     * 单人限额（元）
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal personLimit;
    
    /**
     * 单日限额（元）
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal dailyLimit;
    
    /**
     * 风险等级
     */
    @Column(length = 20)
    private String riskLevel;
    
    /**
     * 起息日期
     */
    @Column(nullable = false)
    private LocalDate startDate;
    
    /**
     * 结息方式
     */
    @Column(length = 50)
    private String interestMethod;
    
    /**
     * 到期日期
     */
    @Column(nullable = false)
    private LocalDate endDate;
    
    /**
     * 产品状态（ACTIVE-在售, PAUSED-暂停, EXPIRED-已过期）
     */
    @Column(length = 20)
    private String status;
    
    /**
     * 产品库存（剩余可购买份额）
     */
    @Column(nullable = false)
    private Long inventory;
    
    /**
     * 产品描述
     */
    @Column(columnDefinition = "TEXT")
    private String description;
}
