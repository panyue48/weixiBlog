package com.weixi.blog.controller;

import com.weixi.blog.common.PageResult;
import com.weixi.blog.common.Result;
import com.weixi.blog.dto.BlogDTO;
import com.weixi.blog.service.BlogService;
import com.weixi.blog.vo.BlogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 博客控制器
 */
@Slf4j
@RestController
@RequestMapping("/blog")
public class ApiBlogController {
    
    @Autowired
    private BlogService blogService;
    
    /**
     * 分页查询博客列表
     * @param current 当前页，默认1
     * @param size 每页数量，默认10
     * @param keyword 搜索关键词（标题/内容）
     * @param typeId 分类ID
     * @param tagId 标签ID
     * @param published 是否发布：1-已发布，0-草稿，null-全部
     */
    @GetMapping("/list")
    public Result<PageResult<BlogVO>> getBlogList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Integer published) {
        try {
            PageResult<BlogVO> page = blogService.getBlogPage(current, size, keyword, typeId, tagId, published);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询博客列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询博客详情
     */
    @GetMapping("/{id}")
    public Result<BlogVO> getBlogDetail(@PathVariable Long id) {
        try {
            BlogVO blogVO = blogService.getBlogDetail(id);
            if (blogVO == null) {
                return Result.error("博客不存在");
            }
            // 增加浏览量
            blogService.incrementViews(id);
            return Result.success(blogVO);
        } catch (Exception e) {
            log.error("查询博客详情失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 发布/保存博客
     * @param userId 用户ID（实际应该从token中获取，这里简化处理）
     */
    @PostMapping("/save")
    public Result<Long> saveBlog(@RequestBody BlogDTO blogDTO, @RequestParam Long userId) {
        try {
            Long blogId = blogService.saveBlog(blogDTO, userId);
            return Result.success("保存成功", blogId);
        } catch (Exception e) {
            log.error("保存博客失败", e);
            return Result.error("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新博客
     */
    @PutMapping("/{id}")
    public Result<Void> updateBlog(@PathVariable Long id, @RequestBody BlogDTO blogDTO, @RequestParam Long userId) {
        try {
            blogService.updateBlog(id, blogDTO, userId);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新博客失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除博客
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBlog(@PathVariable Long id, @RequestParam Long userId) {
        try {
            blogService.deleteBlog(id, userId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除博客失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
