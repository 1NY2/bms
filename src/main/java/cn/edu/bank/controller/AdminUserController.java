package cn.edu.bank.controller;

import cn.edu.bank.entity.Account;
import cn.edu.bank.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理端用户管理控制器
 */
@RestController
@RequestMapping("/admin/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AdminUserController {

    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取所有用户列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<Account> users = accountRepository.findAll();
            
            // 处理敏感信息，不返回密码
            for (Account user : users) {
                user.setPassword(null);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", users);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取用户列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 更新用户状态
     */
    @PostMapping("/update-status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @RequestParam String username,
            @RequestParam String status) {
        try {
            Optional<Account> accountOpt = accountRepository.findByUsername(username);
            if (!accountOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.ok(result);
            }

            Account account = accountOpt.get();
            account.setStatus(status);
            accountRepository.save(account);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "用户状态更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "更新用户状态失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam String username,
            @RequestParam(required = false) String newPassword) {
        try {
            Optional<Account> accountOpt = accountRepository.findByUsername(username);
            if (!accountOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.ok(result);
            }

            Account account = accountOpt.get();
            // 如果没有提供新密码，则使用默认密码: 123456
            String password = newPassword != null && !newPassword.isEmpty() ? newPassword : "123456";
            // 使用PasswordEncoder进行加密
            account.setPassword(passwordEncoder.encode(password));
            accountRepository.save(account);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "密码重置成功，默认密码为: 123456");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "重置密码失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}