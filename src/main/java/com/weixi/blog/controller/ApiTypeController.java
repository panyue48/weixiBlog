package com.weixi.blog.controller;

import com.weixi.blog.common.Result;
import com.weixi.blog.entity.Type;
import com.weixi.blog.service.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器
 */
@Slf4j
@RestController
@RequestMapping("/type")
public class ApiTypeController {
    
    @Autowired
    private TypeService typeService;
    
    /**
     * 查询所有分类
     */
    @GetMapping("/list")
    public Result<List<Type>> getAllTypes() {
        try {
            List<Type> types = typeService.getAllTypes();
            return Result.success(types);
        } catch (Exception e) {
            log.error("查询分类列表失败", e);
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isEmpty()) {
                errorMsg = e.getClass().getSimpleName() + ": " + e.getCause();
                if (errorMsg == null || errorMsg.isEmpty()) {
                    errorMsg = "未知错误，请查看服务器日志";
                }
            }
            return Result.error("查询失败: " + errorMsg);
        }
    }
}

