package com.weixi.blog.controller;

import com.weixi.blog.common.PageResult;
import com.weixi.blog.common.Result;
import com.weixi.blog.dto.BlogDTO;
import com.weixi.blog.service.BlogService;
import com.weixi.blog.vo.BlogVO;
import jakarta.servlet.http.HttpSession;
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
     * 分页查询博客列表（前台）
     * @param current 当前页，默认1
     * @param size 每页数量，默认10
     * @param keyword 搜索关键词（标题/内容）
     * @param typeId 分类ID
     * @param tagId 标签ID
     * @param session HTTP会话，用于获取当前登录用户信息
     */
    @GetMapping("/list")
    public Result<PageResult<BlogVO>> getBlogList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long tagId,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            PageResult<BlogVO> page;
            if (userId != null) {
                // 已登录：只显示自己的已发布博客
                page = blogService.getBlogPageByUserId(userId, current, size, keyword, typeId, tagId, 1);
            } else {
                // 未登录：返回空列表，不显示任何博客
                page = new PageResult<>(new java.util.ArrayList<>(), 0L, current, size);
            }
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询博客列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 查询博客详情（前台）
     * @param id 博客ID
     * @param session HTTP会话，用于获取当前登录用户信息
     */
    @GetMapping("/{id}")
    public Result<BlogVO> getBlogDetail(@PathVariable Long id, HttpSession session) {
        try {
            BlogVO blogVO = blogService.getBlogDetail(id);
            if (blogVO == null) {
                return Result.error("博客不存在");
            }
            
            Long userId = (Long) session.getAttribute("userId");
            if (userId != null) {
                // 已登录：只能查看自己的博客（包括草稿和已发布的）
                if (!blogVO.getUserId().equals(userId)) {
                    return Result.error("无权访问此博客");
                }
            } else {
                // 未登录：只能查看已发布的博客
                if (blogVO.getPublished() == null || blogVO.getPublished() != 1) {
                    return Result.error("博客不存在");
                }
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
     * 后台：分页查询博客列表（包含草稿）
     */
    @GetMapping("/admin/list")
    public Result<PageResult<BlogVO>> getAdminBlogList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) Integer published,
            HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            // 后台只查询当前用户的博客
            PageResult<BlogVO> page = blogService.getBlogPageByUserId(userId, current, size, keyword, typeId, tagId, published);
            return Result.success(page);
        } catch (Exception e) {
            log.error("查询博客列表失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：发布/保存博客
     */
    @PostMapping("/save")
    public Result<Long> saveBlog(@RequestBody BlogDTO blogDTO, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            Long blogId = blogService.saveBlog(blogDTO, userId);
            return Result.success("保存成功", blogId);
        } catch (Exception e) {
            log.error("保存博客失败", e);
            return Result.error("保存失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：更新博客
     */
    @PutMapping("/{id}")
    public Result<Void> updateBlog(@PathVariable Long id, @RequestBody BlogDTO blogDTO, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            blogService.updateBlog(id, blogDTO, userId);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新博客失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：删除博客
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteBlog(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            blogService.deleteBlog(id, userId);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            log.error("删除博客失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 后台：获取博客详情（包含草稿）
     */
    @GetMapping("/admin/{id}")
    public Result<BlogVO> getAdminBlogDetail(@PathVariable Long id, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            BlogVO blogVO = blogService.getBlogDetail(id);
            if (blogVO == null) {
                return Result.error("博客不存在");
            }
            // 检查权限
            if (!blogVO.getUserId().equals(userId)) {
                return Result.error("无权限访问");
            }
            return Result.success(blogVO);
        } catch (Exception e) {
            log.error("查询博客详情失败", e);
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}
