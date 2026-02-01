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
 * 原子服务7：库存锁定服务
 */
@Component("INVENTORY_LOCK")
public class InventoryLockService extends BaseAtomicService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    public InventoryLockService() {
        inputParams.put("productCode", "产品编码");
        inputParams.put("quantity", "锁定数量");
        outputParams.put("lockResult", "锁定结果");
        outputParams.put("remainInventory", "剩余库存");
    }
    
    @Override
    public String getServiceCode() {
        return "INVENTORY_LOCK";
    }
    
    @Override
    public String getServiceName() {
        return "库存锁定";
    }
    
    @Override
    public String getServiceDescription() {
        return "锁定产品库存，防止超卖";
    }
    
    @Override
    @Transactional
    public Map<String, Object> execute(Map<String, Object> context) throws Exception {
        log("开始执行库存锁定");
        
        String productCode = (String) getContextParam(context, "productCode");
        // 尝试从quantity参数获取，如果没有则从amount参数获取（兼容前端传参）
        Integer quantity = 1;
        if (getContextParam(context, "quantity") != null) {
            quantity = Integer.valueOf(getContextParam(context, "quantity").toString());
        } else if (getContextParam(context, "amount") != null) {
            // 如果amount参数存在且为数字，则使用1作为默认quantity
            try {
                Double amountValue = Double.valueOf(getContextParam(context, "amount").toString());
                // 这里假设每次购买都是1个单位
                quantity = 1;
            } catch (NumberFormatException e) {
                // 如果转换失败，保持默认值1
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        
        // 使用悲观锁查询产品
        Optional<DepositProduct> productOpt = productRepository.findByProductCodeWithLock(productCode);
        
        if (!productOpt.isPresent()) {
            result.put("lockResult", false);
            result.put("message", "产品不存在");
            result.put("success", false);
            result.put("errorType", "INVENTORY_INSUFFICIENT");
            log("库存锁定失败：产品不存在");
        } else {
            DepositProduct product = productOpt.get();
            
            if (product.getInventory() < quantity) {
                result.put("lockResult", false);
                result.put("remainInventory", product.getInventory());
                result.put("message", "库存不足");
                result.put("success", false);
                result.put("errorType", "INVENTORY_INSUFFICIENT");
                log("库存锁定失败：库存不足");
            } else {
                // 扣减库存
                product.setInventory(product.getInventory() - quantity);
                productRepository.save(product);
                
                result.put("lockResult", true);
                result.put("remainInventory", product.getInventory());
                result.put("message", "库存锁定成功");
                result.put("success", true);
                log("库存锁定成功，剩余：" + product.getInventory());
            }
        }
        
        return result;
    }
}
