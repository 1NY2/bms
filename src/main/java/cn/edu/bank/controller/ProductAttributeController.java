package cn.edu.bank.controller;

import cn.edu.bank.entity.DepositProduct;
import cn.edu.bank.repository.DepositProductRepository;
import cn.edu.bank.service.DepositProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * 产品属性管理接口
 */
@RestController
@RequestMapping("/admin/products")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProductAttributeController {

    @Autowired
    private DepositProductRepository productRepository;

    @Autowired
    private DepositProductService productService;

    /**
     * 获取所有产品列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listProducts() {
        try {
            List<DepositProduct> products = productRepository.findAll();
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", products);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取产品列表失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 获取产品详情
     */
    @GetMapping("/detail/{productCode}")
    public ResponseEntity<Map<String, Object>> getProductDetail(@PathVariable String productCode) {
        try {
            Optional<DepositProduct> product = productRepository.findByProductCode(productCode);
            
            Map<String, Object> result = new HashMap<>();
            if (product.isPresent()) {
                result.put("success", true);
                result.put("data", product.get());
            } else {
                result.put("success", false);
                result.put("message", "产品不存在");
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "获取产品详情失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 保存或更新产品
     */
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveProduct(@RequestBody DepositProduct product) {
        try {
            // 如果是新产品，设置默认值
            if (product.getId() == null) {
                if (product.getStatus() == null) {
                    product.setStatus("ACTIVE");
                }
                if (product.getInventory() == null) {
                    product.setInventory(1000L);
                }
            }
            
            DepositProduct saved = productRepository.save(product);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "保存成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "保存失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 更新产品状态
     */
    @PostMapping("/status/{productCode}")
    public ResponseEntity<Map<String, Object>> updateProductStatus(
            @PathVariable String productCode,
            @RequestParam String status) {
        try {
            Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
            if (!productOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "产品不存在");
                return ResponseEntity.ok(result);
            }
            
            DepositProduct product = productOpt.get();
            product.setStatus(status);
            DepositProduct saved = productRepository.save(product);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "状态更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 更新产品库存
     */
    @PostMapping("/inventory/{productCode}")
    public ResponseEntity<Map<String, Object>> updateInventory(
            @PathVariable String productCode,
            @RequestParam Long inventory) {
        try {
            Optional<DepositProduct> productOpt = productRepository.findByProductCode(productCode);
            if (!productOpt.isPresent()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "产品不存在");
                return ResponseEntity.ok(result);
            }
            
            DepositProduct product = productOpt.get();
            product.setInventory(inventory);
            DepositProduct saved = productRepository.save(product);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", saved);
            result.put("message", "库存更新成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * 删除产品
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        try {
            productRepository.deleteById(id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}
