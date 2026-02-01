import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 测试密码
        String rawPassword = "user123";
        
        // 生成BCrypt哈希
        String hashedPassword = encoder.encode(rawPassword);
        
        System.out.println("Original password: " + rawPassword);
        System.out.println("BCrypt hash: " + hashedPassword);
        
        // 验证密码
        boolean matches = encoder.matches(rawPassword, hashedPassword);
        System.out.println("Password verification result: " + matches);
        
        // 验证已知的testuser哈希
        String knownHash = "$2a$10$wJ6XQz9.WVu9RtyOBSF0iOqLqxEvAxxsK44aEtcBFYaBE0.QQyCc.";
        boolean knownMatches = encoder.matches(rawPassword, knownHash);
        System.out.println("Matches known hash: " + knownMatches);
    }
}