package com.weixi.blog.service;

import com.weixi.blog.common.PageResult;
import com.weixi.blog.dto.BlogDTO;
import com.weixi.blog.vo.BlogVO;

/**
 * 博客服务接口
 */
public interface BlogService {
    
    /**
     * 分页查询博客列表
     */
    PageResult<BlogVO> getBlogPage(Integer current, Integer size, String keyword, Long typeId, Long tagId, Integer published);
    
    /**
     * 根据用户ID分页查询博客列表
     */
    PageResult<BlogVO> getBlogPageByUserId(Long userId, Integer current, Integer size, String keyword, Long typeId, Long tagId, Integer published);
    
    /**
     * 根据ID查询博客详情
     */
    BlogVO getBlogDetail(Long id);
    
    /**
     * 发布/保存博客
     */
    Long saveBlog(BlogDTO blogDTO, Long userId);
    
    /**
     * 更新博客
     */
    void updateBlog(Long id, BlogDTO blogDTO, Long userId);
    
    /**
     * 删除博客
     */
    void deleteBlog(Long id, Long userId);
    
    /**
     * 增加浏览量
     */
    void incrementViews(Long id);
}
