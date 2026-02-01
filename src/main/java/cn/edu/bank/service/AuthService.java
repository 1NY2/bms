package cn.edu.bank.service;

import cn.edu.bank.dto.LoginRequest;
import cn.edu.bank.dto.LoginResponse;
import cn.edu.bank.dto.RegisterRequest;
import cn.edu.bank.entity.Account;
import cn.edu.bank.entity.OperationLog;
import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.repository.AccountRepository;
import cn.edu.bank.repository.OperationLogRepository;
import cn.edu.bank.repository.UserInfoRepository;
import cn.edu.bank.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 认证服务
 */
@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private OperationLogRepository logRepository;
    
    /**
     * 用户注册
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 记录服务调用日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("USER_AUTH");
        logEntity.setAction("USER_REGISTER_SERVICE");
        logEntity.setUserId(request.getUsername());
        logEntity.setContent("用户注册, username: " + request.getUsername());
        logEntity.setStatus("PROCESSING");
        logEntity.setRequestParams(request.toString());
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        try {
            // 检查用户名是否已存在
            if (accountRepository.existsByUsername(request.getUsername())) {
                // 记录失败日志
                OperationLog errorLog = new OperationLog();
                errorLog.setLogType("USER_AUTH");
                errorLog.setAction("USER_REGISTER_SERVICE");
                errorLog.setUserId(request.getUsername());
                errorLog.setContent("用户注册失败: 用户名已存在, username: " + request.getUsername());
                errorLog.setStatus("FAILED");
                errorLog.setRequestParams(request.toString());
                errorLog.setResponseResult("用户名已存在");
                errorLog.setCreateTime(LocalDateTime.now());
                logRepository.save(errorLog);
                
                throw new RuntimeException("用户名已存在");
            }
            
            // 创建账号
            Account account = new Account();
            account.setUsername(request.getUsername());
            account.setPassword(passwordEncoder.encode(request.getPassword()));
            account.setPhone(request.getPhone());
            account.setEmail(request.getEmail());
            account.setRole("USER"); // 默认为普通用户
            account.setStatus("ACTIVE");
            account.setCreatedAt(LocalDateTime.now());
            
            // 生成用户ID
            String userId = "U" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            account.setUserId(userId);
            
            accountRepository.save(account);
            
            // 创建对应的UserInfo（用于业务系统）
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setUserName(""); // 注册时不填写真实姓名
            userInfo.setIdCard(""); // 注册时不填写身份证号
            userInfo.setPhoneNumber(request.getPhone());
            userInfo.setEmail(request.getEmail()); // 设置邮箱
            userInfo.setBalance(new BigDecimal("10000")); // 默认余额10000元
            userInfo.setStatus("NORMAL");
            userInfo.setRegion("未设置");
            userInfo.setTags("普通");
            userInfo.setWhitelisted(false);
            userInfo.setCreateTime(LocalDateTime.now());
            
            userInfoRepository.save(userInfo);
            
            // 生成Token
            String token = jwtUtil.generateToken(account.getUsername(), account.getRole(), userId);
            
            // 记录成功日志
            OperationLog successLog = new OperationLog();
            successLog.setLogType("USER_AUTH");
            successLog.setAction("USER_REGISTER_SERVICE");
            successLog.setUserId(request.getUsername());
            successLog.setContent("用户注册成功, username: " + request.getUsername() + ", userId: " + userId);
            successLog.setStatus("SUCCESS");
            successLog.setRequestParams(request.toString());
            successLog.setResponseResult("注册成功");
            successLog.setCreateTime(LocalDateTime.now());
            logRepository.save(successLog);
            
            return new LoginResponse(token, account.getUsername(), "", 
                                    account.getRole(), userId);
        } catch (Exception e) {
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("USER_AUTH");
            errorLog.setAction("USER_REGISTER_SERVICE");
            errorLog.setUserId(request.getUsername());
            errorLog.setContent("用户注册失败, username: " + request.getUsername() + ", 错误: " + e.getMessage());
            errorLog.setStatus("FAILED");
            errorLog.setRequestParams(request.toString());
            errorLog.setResponseResult(e.getMessage());
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw e; // 重新抛出异常
        }
    }
    
    /**
     * 用户登录
     */
    public LoginResponse login(LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());
        
        // 记录服务调用日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("USER_AUTH");
        logEntity.setAction("USER_LOGIN_SERVICE");
        logEntity.setUserId(request.getUsername());
        logEntity.setContent("用户登录, username: " + request.getUsername());
        logEntity.setStatus("PROCESSING");
        logEntity.setRequestParams(request.toString());
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        // 查找账号
        Optional<Account> accountOpt = accountRepository.findByUsername(request.getUsername());
        if (!accountOpt.isPresent()) {
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("USER_AUTH");
            errorLog.setAction("USER_LOGIN_SERVICE");
            errorLog.setUserId(request.getUsername());
            errorLog.setContent("用户登录失败: 用户名或密码错误, username: " + request.getUsername());
            errorLog.setStatus("FAILED");
            errorLog.setRequestParams(request.toString());
            errorLog.setResponseResult("用户名或密码错误");
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw new RuntimeException("用户名或密码错误");
        }
        
        Account account = accountOpt.get();
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("USER_AUTH");
            errorLog.setAction("USER_LOGIN_SERVICE");
            errorLog.setUserId(request.getUsername());
            errorLog.setContent("用户登录失败: 用户名或密码错误, username: " + request.getUsername());
            errorLog.setStatus("FAILED");
            errorLog.setRequestParams(request.toString());
            errorLog.setResponseResult("用户名或密码错误");
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 检查账号状态
        if (!"ACTIVE".equals(account.getStatus())) {
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("USER_AUTH");
            errorLog.setAction("USER_LOGIN_SERVICE");
            errorLog.setUserId(request.getUsername());
            errorLog.setContent("用户登录失败: 账号状态异常，请联系管理员, username: " + request.getUsername());
            errorLog.setStatus("FAILED");
            errorLog.setRequestParams(request.toString());
            errorLog.setResponseResult("账号状态异常，请联系管理员");
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw new RuntimeException("账号状态异常，请联系管理员");
        }
        
        // 更新最后登录时间
        account.setLastLoginAt(LocalDateTime.now());
        accountRepository.save(account);
        
        // 获取用户信息中的真实姓名
        String realName = "";
        Optional<UserInfo> userInfoOpt = userInfoRepository.findByUserId(account.getUserId());
        if (userInfoOpt.isPresent()) {
            realName = userInfoOpt.get().getUserName();
        }
        
        // 生成Token
        String token = jwtUtil.generateToken(account.getUsername(), account.getRole(), account.getUserId());
        
        logger.info("Login successful for username: {}", request.getUsername());
        
        // 记录成功日志
        OperationLog successLog = new OperationLog();
        successLog.setLogType("USER_AUTH");
        successLog.setAction("USER_LOGIN_SERVICE");
        successLog.setUserId(request.getUsername());
        successLog.setContent("用户登录成功, username: " + request.getUsername() + ", userId: " + account.getUserId());
        successLog.setStatus("SUCCESS");
        successLog.setRequestParams(request.toString());
        successLog.setResponseResult("登录成功");
        successLog.setCreateTime(LocalDateTime.now());
        logRepository.save(successLog);
        
        return new LoginResponse(token, account.getUsername(), realName, 
                               account.getRole(), account.getUserId());
    }
    
    /**
     * 验证Token
     */
    public LoginResponse validateToken(String token) {
        // 记录服务调用日志
        OperationLog logEntity = new OperationLog();
        logEntity.setLogType("USER_AUTH");
        logEntity.setAction("VALIDATE_TOKEN_SERVICE");
        logEntity.setContent("验证Token");
        logEntity.setStatus("PROCESSING");
        logEntity.setRequestParams(token);
        logEntity.setCreateTime(LocalDateTime.now());
        logRepository.save(logEntity);
        
        try {
            // 解析Token获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            String userId = jwtUtil.getUserIdFromToken(token);
            
            // 查找账号
            Optional<Account> accountOpt = accountRepository.findByUsername(username);
            if (!accountOpt.isPresent()) {
                // 记录失败日志
                OperationLog errorLog = new OperationLog();
                errorLog.setLogType("USER_AUTH");
                errorLog.setAction("VALIDATE_TOKEN_SERVICE");
                errorLog.setUserId(username);
                errorLog.setContent("Token验证失败: 用户不存在, username: " + username);
                errorLog.setStatus("FAILED");
                errorLog.setRequestParams(token);
                errorLog.setResponseResult("用户不存在");
                errorLog.setCreateTime(LocalDateTime.now());
                logRepository.save(errorLog);
                
                throw new RuntimeException("用户不存在");
            }
            
            Account account = accountOpt.get();
            
            // 检查账号状态
            if (!"ACTIVE".equals(account.getStatus())) {
                // 记录失败日志
                OperationLog errorLog = new OperationLog();
                errorLog.setLogType("USER_AUTH");
                errorLog.setAction("VALIDATE_TOKEN_SERVICE");
                errorLog.setUserId(username);
                errorLog.setContent("Token验证失败: 账号状态异常，请联系管理员, username: " + username);
                errorLog.setStatus("FAILED");
                errorLog.setRequestParams(token);
                errorLog.setResponseResult("账号状态异常，请联系管理员");
                errorLog.setCreateTime(LocalDateTime.now());
                logRepository.save(errorLog);
                
                throw new RuntimeException("账号状态异常，请联系管理员");
            }
            
            // 获取用户信息中的真实姓名
            String realName = "";
            Optional<UserInfo> userInfoOpt = userInfoRepository.findByUserId(account.getUserId());
            if (userInfoOpt.isPresent()) {
                realName = userInfoOpt.get().getUserName();
            }
            
            // 记录成功日志
            OperationLog successLog = new OperationLog();
            successLog.setLogType("USER_AUTH");
            successLog.setAction("VALIDATE_TOKEN_SERVICE");
            successLog.setUserId(username);
            successLog.setContent("Token验证成功, username: " + username + ", userId: " + userId);
            successLog.setStatus("SUCCESS");
            successLog.setRequestParams(token);
            successLog.setResponseResult("验证成功");
            successLog.setCreateTime(LocalDateTime.now());
            logRepository.save(successLog);
            
            return new LoginResponse(token, username, realName, role, userId);
        } catch (Exception e) {
            // 记录失败日志
            OperationLog errorLog = new OperationLog();
            errorLog.setLogType("USER_AUTH");
            errorLog.setAction("VALIDATE_TOKEN_SERVICE");
            errorLog.setContent("Token验证失败: " + e.getMessage());
            errorLog.setStatus("FAILED");
            errorLog.setRequestParams(token);
            errorLog.setResponseResult("Token验证失败：" + e.getMessage());
            errorLog.setCreateTime(LocalDateTime.now());
            logRepository.save(errorLog);
            
            throw new RuntimeException("Token验证失败：" + e.getMessage());
        }
    }
}