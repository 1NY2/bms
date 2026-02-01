package cn.edu.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购买结果DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResult {
    
    /**
     * 订单号
     */
    private String orderNo;
    
    /**
     * 产品名称
     */
    private String productName;
    
    /**
     * 购买金额
     */
    private BigDecimal amount;
    
    /**
     * 年化利率
     */
    private BigDecimal annualRate;
    
    /**
     * 产品期限
     */
    private String duration;
    
    /**
     * 预期收益
     */
    private BigDecimal expectedIncome;
    
    /**
     * 起息日期
     */
    private LocalDateTime startDate;
    
    /**
     * 到期日期
     */
    private LocalDateTime endDate;
    
    /**
     * 购买时间
     */
    private LocalDateTime purchaseTime;
}
