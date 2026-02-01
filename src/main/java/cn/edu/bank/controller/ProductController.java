package cn.edu.bank.controller;

import cn.edu.bank.common.Result;
import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.service.DepositProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品控制器
 */
@Slf4j
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductController {
    
    @Autowired
    private DepositProductService productService;
    
    /**
     * 查询所有在售产品列表
     */
    @GetMapping("/list")
    public Result<List<DepositProduct>> getProductList() {
        log.info("请求产品列表");
        
        try {
            List<DepositProduct> products = productService.getAvailableProducts();
            return Result.success(products);
        } catch (Exception e) {
            log.error("查询产品列表失败", e);
            return Result.error("查询产品列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据产品编号查询产品详情
     */
    @GetMapping("/detail/{productCode}")
    public Result<DepositProduct> getProductDetail(@PathVariable String productCode) {
        log.info("请求产品详情, productCode: {}", productCode);
        
        try {
            DepositProduct product = productService.getProductByCode(productCode);
            return Result.success(product);
        } catch (Exception e) {
            log.error("查询产品详情失败", e);
            return Result.error("查询产品详情失败：" + e.getMessage());
        }
    }
}