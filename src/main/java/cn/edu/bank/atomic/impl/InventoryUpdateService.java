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
 * 原子服务9：库存更新服务
 */
@Component("INVENTORY_UPDATE")
public class InventoryUpdateService extends BaseAtomicService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    public InventoryUpdateService() {
        inputParams.put("productCode", "产品编码");
        inputParams.put("newInventory", "新库存数量");
        outputParams.put("updateResult", "更新结果");
    }
    
    @Override
    public String getServiceCode() {
        return "INVENTORY_UPDATE";
    }
    
    @Override
    public String getServiceName() {
        return "库存更新";
    }
    
    @Override
    public String getServiceDescription() {
        return "更新产品库存数量";
    }
    
    @Override
    @Transactional
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行库存更新");
        
        String productCode = (String) getContextParam(context, "productCode");
        Long newInventory = Long.valueOf(getContextParam(context, "newInventory").toString());
        
        Map<String, Object> result = new HashMap<>();
        
        Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
        
        if (productOpt.isPresent()) {
            DepositProduct product = productOpt.get();
            Long oldInventory = product.getInventory();
            product.setInventory(newInventory);
            productRepository.save(product);
            
            result.put("updateResult", true);
            result.put("oldInventory", oldInventory);
            result.put("newInventory", newInventory);
            result.put("message", "库存更新成功");
            result.put("success", true);
            log("库存更新成功：" + oldInventory + " -> " + newInventory);
        } else {
            result.put("updateResult", false);
            result.put("message", "产品不存在");
            result.put("success", false);
            result.put("errorType", "INVENTORY_UPDATE_FAILED");
            log("库存更新失败：产品不存在");
        }
        
        return result;
    }
}
