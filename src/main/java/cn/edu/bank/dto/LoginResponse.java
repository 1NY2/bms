package cn.edu.bank.dto;

/**
 * 登录响应DTO
 */
public class LoginResponse {
    
    private String token;
    private String username;
    private String realName;
    private String role;
    private String userId;

    public LoginResponse() {
    }

    public LoginResponse(String token, String username, String realName, String role, String userId) {
        this.token = token;
        this.username = username;
        this.realName = realName;
        this.role = role;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
