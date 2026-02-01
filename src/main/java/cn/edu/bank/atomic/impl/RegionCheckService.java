package cn.edu.bank.atomic.impl;

import cn.edu.bank.enums.PurchaseErrorType;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 原子服务4：地域购买控制服务
 */
@Component("REGION_CHECK")
public class RegionCheckService extends BaseAtomicService {
    
    // 允许购买的地区列表（通过身份证号前6位判断）
    private static final List<String> ALLOWED_REGIONS = Arrays.asList(
        "430000", "440000", "110000"  // 湖南、广东、北京
    );
    
    public RegionCheckService() {
        inputParams.put("idCard", "身份证号");
        outputParams.put("regionAllowed", "地域是否允许");
        outputParams.put("regionCode", "地区编码");
        outputParams.put("errorType", "错误类型");
    }
    
    @Override
    public String getServiceCode() {
        return "REGION_CHECK";
    }
    
    @Override
    public String getServiceName() {
        return "地域购买控制";
    }
    
    @Override
    public String getServiceDescription() {
        return "根据用户所在地域控制产品购买权限";
    }
    
    @Override
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行地域检查");
        
        String idCard = (String) getContextParam(context, "idCard");
        Map<String, Object> result = new HashMap<>();
        
        if (idCard != null && idCard.length() >= 6) {
            String regionCode = idCard.substring(0, 6);
            boolean allowed = ALLOWED_REGIONS.stream()
                .anyMatch(r -> regionCode.startsWith(r.substring(0, 2)));
            
            result.put("regionAllowed", allowed);
            result.put("regionCode", regionCode);
            if (!allowed) {
                result.put("message", "地域限制：用户所在地区不允许购买此产品");
                result.put("success", false);
                result.put("errorType", PurchaseErrorType.REGION_RESTRICTED.getCode());
            } else {
                result.put("message", "地域检查通过");
                result.put("success", true);
            }
            log("地域检查完成，允许：" + allowed);
        } else {
            result.put("regionAllowed", false);
            result.put("regionCode", "UNKNOWN");
            result.put("message", "身份证号无效，地域检查失败");
            result.put("success", false);
            result.put("errorType", PurchaseErrorType.REGION_RESTRICTED.getCode());
            log("身份证号无效，地域检查失败");
        }
        
        return result;
    }
}