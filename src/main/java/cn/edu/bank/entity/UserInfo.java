package cn.edu.bank.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户信息实体类
 */
@Data
@Entity
@Table(name = "user_info")
public class UserInfo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID（手机号或账号）
     */
    @Column(nullable = false, unique = true, length = 50)
    private String userId;
    
    /**
     * 用户姓名
     */
    @Column(nullable = false, length = 50)
    private String userName;
    
    /**
     * 身份证号
     */
    @Column(unique = true, length = 18)
    private String idCard;
    
    /**
     * 手机号
     */
    @Column(nullable = false, length = 11)
    private String phoneNumber;
    
    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;
    
    /**
     * 账户余额
     */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance;
    
    /**
     * 用户状态（NORMAL-正常, LOCKED-锁定, FROZEN-冻结）
     */
    @Column(length = 20)
    private String status;
    
    /**
     * 地区
     */
    @Column(length = 50)
    private String region;
    
    /**
     * 标签
     */
    @Column(length = 100)
    private String tags;
    
    /**
     * 是否白名单用户
     */
    @Column
    private Boolean whitelisted;
    
    /**
     * 创建时间
     */
    @Column(nullable = false)
    private LocalDateTime createTime;
}