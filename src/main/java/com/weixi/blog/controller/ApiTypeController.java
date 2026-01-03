package com.weixi.blog.controller;

import com.weixi.blog.common.Result;
import com.weixi.blog.entity.Type;
import com.weixi.blog.service.TypeService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 前台：查询分类列表
     * @param session HTTP会话，用于获取当前登录用户信息
     */
    @GetMapping("/list")
    public Result<List<Type>> getAllTypes(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            List<Type> types;
            if (userId != null) {
                // 已登录：只返回自己的分类
                types = typeService.getTypesByUserId(userId);
            } else {
                // 未登录：返回所有分类
                types = typeService.getAllTypes();
            }
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
    
    /**
     * 后台：根据当前用户查询所有分类
     */
    @GetMapping("/admin/list")
    public Result<List<Type>> getTypesByUserId(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            List<Type> types = typeService.getTypesByUserId(userId);
            return Result.success(types);
        } catch (Exception e) {
            log.error("查询分类列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：创建分类
     */
    @PostMapping("/save")
    public Result<Long> createType(@RequestBody Type type, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            Long typeId = typeService.createType(userId, type);
            return Result.success("创建成功", typeId);
        } catch (Exception e) {
            log.error("创建分类失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：更新分类
     */
    @PutMapping("/{id}")
    public Result<Void> updateType(@PathVariable Long id, @RequestBody Type type, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            typeService.updateType(id, userId, type);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：删除分类
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteType(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            typeService.deleteType(id, userId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
