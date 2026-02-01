package cn.edu.bank.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 产品购买请求DTO
 */
@Data
public class PurchaseRequest {
    
    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    /**
     * 产品编号
     */
    @NotBlank(message = "产品编号不能为空")
    private String productCode;
    
    /**
     * 购买金额
     */
    @NotNull(message = "购买金额不能为空")
    @DecimalMin(value = "0.01", message = "购买金额必须大于0")
    private BigDecimal amount;
    
    /**
     * 备注信息
     */
    private String remark;
}