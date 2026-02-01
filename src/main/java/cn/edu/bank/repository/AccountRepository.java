package cn.edu.bank.repository;

import cn.edu.bank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 账号Repository
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * 根据用户名查询账号
     */
    Optional<Account> findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 根据用户ID查询账号
     */
    Optional<Account> findByUserId(String userId);
}
