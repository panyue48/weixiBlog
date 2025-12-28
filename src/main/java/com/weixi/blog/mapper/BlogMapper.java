package com.weixi.blog.mapper;

import com.weixi.blog.entity.Blog;
import com.weixi.blog.vo.BlogVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客Mapper - 使用XML映射
 */
@Mapper
public interface BlogMapper {
    
    /**
     * 插入博客
     */
    int insert(Blog blog);
    
    /**
     * 更新博客
     */
    int updateById(Blog blog);
    
    /**
     * 删除博客
     */
    int deleteById(Long id);
    
    /**
     * 根据ID查询博客
     */
    Blog selectById(Long id);
    
    /**
     * 分页查询博客列表（带关联信息）
     */
    List<BlogVO> selectBlogPage(@Param("keyword") String keyword,
                                @Param("typeId") Long typeId,
                                @Param("tagId") Long tagId,
                                @Param("published") Integer published,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);
    
    /**
     * 根据用户ID分页查询博客列表（带关联信息）
     */
    List<BlogVO> selectBlogPageByUserId(@Param("userId") Long userId,
                                        @Param("keyword") String keyword,
                                        @Param("typeId") Long typeId,
                                        @Param("tagId") Long tagId,
                                        @Param("published") Integer published,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);
    
    /**
     * 查询总数
     */
    int countBlogs(@Param("keyword") String keyword,
                   @Param("typeId") Long typeId,
                   @Param("tagId") Long tagId,
                   @Param("published") Integer published);
    
    /**
     * 根据用户ID查询总数
     */
    int countBlogsByUserId(@Param("userId") Long userId,
                          @Param("keyword") String keyword,
                          @Param("typeId") Long typeId,
                          @Param("tagId") Long tagId,
                          @Param("published") Integer published);
    
    /**
     * 根据ID查询博客详情（带关联信息）
     */
    BlogVO selectBlogDetailById(@Param("id") Long id);
    
    /**
     * 增加浏览量
     */
    void incrementViews(@Param("id") Long id);
}
