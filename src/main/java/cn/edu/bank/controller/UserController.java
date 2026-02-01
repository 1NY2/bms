package cn.edu.bank.controller;

import cn.edu.bank.common.Result;
import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.service.UserService;
import cn.edu.bank.entity.Account;
import cn.edu.bank.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    /**
     * 获取用户详细信息
     */
    @GetMapping("/info/{userId}")
    public Result<UserInfo> getUserInfo(@PathVariable String userId) {
        try {
            UserInfo userInfo = userService.getUserInfo(userId);
            
            // 补充账号表中的邮箱信息
            Optional<Account> accountOpt = accountRepository.findByUserId(userId);
            if (accountOpt.isPresent() && userInfo != null) {
                userInfo.setEmail(accountOpt.get().getEmail());
            }
            
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户资产信息
     */
    @GetMapping("/assets/{userId}")
    public Result<UserInfo> getUserAssets(@PathVariable String userId) {
        try {
            UserInfo userInfo = userService.getUserInfo(userId);
            return Result.success(userInfo);
        } catch (Exception e) {
            return Result.error("获取用户资产信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info/{userId}")
    public Result<UserInfo> updateUserInfo(@PathVariable String userId, @RequestBody UserInfo userInfo) {
        try {
            UserInfo updatedUserInfo = userService.updateUserInfo(userId, userInfo);
            
            // 同步更新账号表中的邮箱信息
            Optional<Account> accountOpt = accountRepository.findByUserId(userId);
            if (accountOpt.isPresent() && userInfo.getEmail() != null) {
                Account account = accountOpt.get();
                account.setEmail(userInfo.getEmail());
                accountRepository.save(account);
            }
            
            return Result.success(updatedUserInfo);
        } catch (Exception e) {
            return Result.error("更新用户信息失败：" + e.getMessage());
        }
    }
}