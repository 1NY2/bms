package cn.edu.bank.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Entity
@Table(name = "operation_logs")
public class OperationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "log_type", length = 50)
    private String logType;  // 日志类型：PURCHASE, LOGIN, ORDER, etc.
    
    @Column(name = "user_id", length = 50)
    private String userId;  // 操作用户ID
    
    @Column(name = "product_code", length = 50)
    private String productCode;  // 产品编码
    
    @Column(name = "order_no", length = 100)
    private String orderNo;  // 订单号
    
    @Column(name = "action", length = 100)
    private String action;  // 操作类型
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;  // 日志内容
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;  // 操作IP地址
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;  // 用户代理
    
    @Column(name = "status", length = 20)
    private String status;  // 操作状态：SUCCESS, FAILED, etc.
    
    @Column(name = "create_time")
    private LocalDateTime createTime;  // 创建时间
    
    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;  // 请求参数
    
    @Column(name = "response_result", columnDefinition = "TEXT")
    private String responseResult;  // 响应结果

    @Column(name = "error_type", length = 50)
    private String errorType;  // 错误类型

    // 构造函数
    public OperationLog() {
        this.createTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(String responseResult) {
        this.responseResult = responseResult;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}