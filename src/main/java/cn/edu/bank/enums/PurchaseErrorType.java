package cn.edu.bank.enums;

/**
 * 购买流程错误类型枚举
 */
public enum PurchaseErrorType {
    ID_CHECK_FAILED("ID_CHECK_FAILED", "身份验证失败"),
    USER_VERIFY_FAILED("USER_VERIFY_FAILED", "用户信息校验失败"),
    AMOUNT_VALIDATE_FAILED("AMOUNT_VALIDATE_FAILED", "金额校验失败"),
    BALANCE_INSUFFICIENT("BALANCE_INSUFFICIENT", "余额不足"),
    LIMIT_EXCEEDED("LIMIT_EXCEEDED", "购买限额超限"),
    INVENTORY_INSUFFICIENT("INVENTORY_INSUFFICIENT", "库存不足"),
    REGION_RESTRICTED("REGION_RESTRICTED", "地域限制"),
    TAG_RESTRICTED("TAG_RESTRICTED", "用户标签限制"),
    WHITELIST_DENIED("WHITELIST_DENIED", "白名单限制"),
    UNKNOWN_ERROR("UNKNOWN_ERROR", "未知错误");

    private final String code;
    private final String message;

    PurchaseErrorType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据错误代码获取错误类型
     */
    public static PurchaseErrorType fromCode(String code) {
        for (PurchaseErrorType errorType : values()) {
            if (errorType.code.equals(code)) {
                return errorType;
            }
        }
        return UNKNOWN_ERROR;
    }
}