package com.weixi.blog.controller;

import com.weixi.blog.common.Result;
import com.weixi.blog.entity.Tag;
import com.weixi.blog.service.TagService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@Slf4j
@RestController
@RequestMapping("/tag")
public class ApiTagController {
    
    @Autowired
    private TagService tagService;
    
    /**
     * 前台：查询所有标签（已废弃，保留兼容性）
     */
    @GetMapping("/list")
    public Result<List<Tag>> getAllTags() {
        try {
            List<Tag> tags = tagService.getAllTags();
            return Result.success(tags);
        } catch (Exception e) {
            log.error("查询标签列表失败", e);
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
     * 后台：根据当前用户查询所有标签
     */
    @GetMapping("/admin/list")
    public Result<List<Tag>> getTagsByUserId(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            List<Tag> tags = tagService.getTagsByUserId(userId);
            return Result.success(tags);
        } catch (Exception e) {
            log.error("查询标签列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：创建标签
     */
    @PostMapping("/save")
    public Result<Long> createTag(@RequestBody Tag tag, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            Long tagId = tagService.createTag(userId, tag);
            return Result.success("创建成功", tagId);
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：更新标签
     */
    @PutMapping("/{id}")
    public Result<Void> updateTag(@PathVariable Long id, @RequestBody Tag tag, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            tagService.updateTag(id, userId, tag);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：删除标签
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            tagService.deleteTag(id, userId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
