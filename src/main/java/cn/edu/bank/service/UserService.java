package cn.edu.bank.service;

import cn.edu.bank.entity.UserInfo;
import cn.edu.bank.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 用户信息服务
 */
@Service
public class UserService {
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    /**
     * 获取用户信息
     */
    public UserInfo getUserInfo(String userId) {
        Optional<UserInfo> userOpt = userInfoRepository.findByUserId(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        return userOpt.get();
    }
    
    /**
     * 更新用户信息
     */
    public UserInfo updateUserInfo(String userId, UserInfo updatedUserInfo) {
        Optional<UserInfo> userOpt = userInfoRepository.findByUserId(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("用户不存在");
        }
        
        UserInfo userInfo = userOpt.get();
        
        // 更新用户信息字段（不允许更新真实姓名和身份证号，这些字段通过实名认证设置）
        if (updatedUserInfo.getPhoneNumber() != null) {
            userInfo.setPhoneNumber(updatedUserInfo.getPhoneNumber());
        }
        
        if (updatedUserInfo.getEmail() != null) {
            userInfo.setEmail(updatedUserInfo.getEmail());
        }
        
        if (updatedUserInfo.getRegion() != null) {
            userInfo.setRegion(updatedUserInfo.getRegion());
        }
        
        // 保存更新后的用户信息
        return userInfoRepository.save(userInfo);
    }
}