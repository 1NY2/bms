public class IdCardValidationTest {
    public static void main(String[] args) {
        String idCard = "110101199007071240";
        System.out.println("验证身份证号: " + idCard);
        System.out.println("长度: " + idCard.length());
        
        // 验证前17位是否为数字
        String body = idCard.substring(0, 17);
        System.out.println("前17位: " + body);
        System.out.println("前17位是否为数字: " + body.matches("\\d{17}"));
        
        // 验证最后一位校验码
        String lastChar = idCard.substring(17, 18);
        System.out.println("最后一位: " + lastChar);
        System.out.println("最后一位是否为数字或X: " + lastChar.matches("[0-9Xx]"));
        
        // 计算校验码
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        String checkCodes = "10X98765432";
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * weights[i];
        }
        System.out.println("加权和: " + sum);
        
        int checkIndex = sum % 11;
        char expectedCheckCode = checkCodes.charAt(checkIndex);
        System.out.println("计算得到的校验码索引: " + checkIndex);
        System.out.println("期望的校验码: " + expectedCheckCode);
        System.out.println("实际的校验码: " + lastChar.charAt(0));
        System.out.println("校验是否通过: " + (Character.toUpperCase(lastChar.charAt(0)) == expectedCheckCode));
        
        // 生成正确的校验码
        String correctIdCard = body + expectedCheckCode;
        System.out.println("正确的身份证号应该是: " + correctIdCard);
    }
}