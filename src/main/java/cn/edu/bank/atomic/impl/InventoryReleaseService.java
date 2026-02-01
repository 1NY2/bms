package cn.edu.bank.atomic.impl;

import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.repository.DepositProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 原子服务8：库存释放服务
 */
@Component("INVENTORY_RELEASE")
public class InventoryReleaseService extends BaseAtomicService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    public InventoryReleaseService() {
        inputParams.put("productCode", "产品编码");
        inputParams.put("quantity", "释放数量");
        outputParams.put("releaseResult", "释放结果");
    }
    
    @Override
    public String getServiceCode() {
        return "INVENTORY_RELEASE";
    }
    
    @Override
    public String getServiceName() {
        return "库存释放";
    }
    
    @Override
    public String getServiceDescription() {
        return "释放已锁定的产品库存";
    }
    
    @Override
    @Transactional
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行库存释放");
        
        String productCode = (String) getContextParam(context, "productCode");
        Integer quantity = getContextParam(context, "quantity") != null ? 
            Integer.valueOf(getContextParam(context, "quantity").toString()) : 1;
        
        Map<String, Object> result = new HashMap<>();
        
        Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
        
        if (productOpt.isPresent()) {
            DepositProduct product = productOpt.get();
            product.setInventory(product.getInventory() + quantity);
            productRepository.save(product);
            
            result.put("releaseResult", true);
            result.put("message", "库存释放成功");
            result.put("success", true);
            log("库存释放成功，当前库存：" + product.getInventory());
        } else {
            result.put("releaseResult", false);
            result.put("message", "产品不存在");
            result.put("success", false);
            result.put("errorType", "INVENTORY_RELEASE_FAILED");
            log("库存释放失败：产品不存在");
        }
        
        return result;
    }
}
