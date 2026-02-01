package cn.edu.bank.service;

import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.repository.DepositProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 存款产品服务
 */
@Slf4j
@Service
public class DepositProductService {
    
    @Autowired
    private DepositProductRepository productRepository;
    
    /**
     * 查询所有在售产品
     */
    public List<DepositProduct> getAvailableProducts() {
        log.info("查询所有在售产品");
        return productRepository.findAvailableProducts("ACTIVE");
    }
    
    /**
     * 根据产品编号查询产品详情
     */
    public DepositProduct getProductByCode(String productCode) {
        log.info("查询产品详情, productCode: {}", productCode);
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new RuntimeException("产品不存在"));
    }
}
