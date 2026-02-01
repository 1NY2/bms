package cn.edu.bank.controller;

import cn.edu.bank.common.Result;
import cn.edu.bank.entity.OperationLog;
import cn.edu.bank.repository.OperationLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 日志查询控制器
 */
@Slf4j
@RestController
@RequestMapping("/admin/logs")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LogController {
    
    @Autowired
    private OperationLogRepository logRepository;
    
    /**
     * 根据用户ID查询操作日志
     */
    @GetMapping("/by-user/{userId}")
    public Result<List<OperationLog>> getLogsByUserId(@PathVariable String userId) {
        try {
            log.info("查询用户操作日志, userId: {}", userId);
            List<OperationLog> logs = logRepository.findByUserIdOrderByCreateTimeDesc(userId);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询用户操作日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据日志类型查询操作日志
     */
    @GetMapping("/by-type/{logType}")
    public Result<List<OperationLog>> getLogsByLogType(@PathVariable String logType) {
        try {
            log.info("查询日志类型操作日志, logType: {}", logType);
            List<OperationLog> logs = logRepository.findByLogTypeOrderByCreateTimeDesc(logType);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询日志类型操作日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据操作类型查询操作日志
     */
    @GetMapping("/by-action/{action}")
    public Result<List<OperationLog>> getLogsByAction(@PathVariable String action) {
        try {
            log.info("查询操作类型日志, action: {}", action);
            List<OperationLog> logs = logRepository.findByActionOrderByCreateTimeDesc(action);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询操作类型日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据产品编码查询操作日志
     */
    @GetMapping("/by-product/{productCode}")
    public Result<List<OperationLog>> getLogsByProductCode(@PathVariable String productCode) {
        try {
            log.info("查询产品操作日志, productCode: {}", productCode);
            List<OperationLog> logs = logRepository.findByProductCodeOrderByCreateTimeDesc(productCode);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询产品操作日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据订单号查询操作日志
     */
    @GetMapping("/by-order/{orderNo}")
    public Result<List<OperationLog>> getLogsByOrderNo(@PathVariable String orderNo) {
        try {
            log.info("查询订单操作日志, orderNo: {}", orderNo);
            List<OperationLog> logs = logRepository.findByOrderNoOrderByCreateTimeDesc(orderNo);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询订单操作日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据时间范围查询操作日志
     */
    @GetMapping("/by-time-range")
    public Result<List<OperationLog>> getLogsByTimeRange(
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            log.info("查询时间范围操作日志, startTime: {}, endTime: {}", startTime, endTime);
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            List<OperationLog> logs = logRepository.findByCreateTimeBetweenOrderByCreateTimeDesc(start, end);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询时间范围操作日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据状态查询操作日志
     */
    @GetMapping("/by-status/{status}")
    public Result<List<OperationLog>> getLogsByStatus(@PathVariable String status) {
        try {
            log.info("查询状态操作日志, status: {}", status);
            List<OperationLog> logs = logRepository.findByStatusOrderByCreateTimeDesc(status);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询状态操作日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 模糊搜索日志内容
     */
    @GetMapping("/search")
    public Result<List<OperationLog>> searchLogs(@RequestParam String keyword) {
        try {
            log.info("搜索日志内容, keyword: {}", keyword);
            List<OperationLog> logs = logRepository.findByContentContainingOrderByCreateTimeDesc(keyword);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("搜索日志内容失败", e);
            return Result.error("搜索日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取所有操作日志
     */
    @GetMapping("/all")
    public Result<List<OperationLog>> getAllLogs() {
        try {
            log.info("查询所有操作日志");
            List<OperationLog> logs = logRepository.findAll();
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询所有操作日志失败", e);
            return Result.error("查询日志失败：" + e.getMessage());
        }
    }
}