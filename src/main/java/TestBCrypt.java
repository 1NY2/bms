import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String rawPassword = "user123";
        
        // 生成BCrypt哈希
        String hashedPassword = encoder.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("BCrypt哈希: " + hashedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(rawPassword, hashedPassword);
        System.out.println("密码验证结果: " + matches);
        
        // 验证已知的testuser哈希
        String knownHash = "$2a$10$wJ6XQz9.WVu9RtyOBSF0iOqLqxEvAxxsK44aEtcBFYaBE0.QQyCc.";
        boolean knownMatches = encoder.matches(rawPassword, knownHash);
        System.out.println("与已知哈希匹配: " + knownMatches);
    }
}