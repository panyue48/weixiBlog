package com.weixi.blog.controller;

import com.weixi.blog.common.Result;
import com.weixi.blog.entity.Tag;
import com.weixi.blog.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * 查询所有标签
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
}

