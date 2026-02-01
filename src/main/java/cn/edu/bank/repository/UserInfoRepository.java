package cn.edu.bank.repository;

import cn.edu.bank.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户信息Repository
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    
    /**
     * 根据用户ID查询用户信息
     */
    Optional<UserInfo> findByUserId(String userId);
    
    /**
     * 根据身份证号查询用户信息
     */
    Optional<UserInfo> findByIdCard(String idCard);
}
