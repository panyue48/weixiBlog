package com.weixi.blog.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 博客标签关联Mapper
 */
@Mapper
public interface BlogTagsMapper {
    
    /**
     * 批量插入博客标签关联
     */
    int insertBatch(@Param("blogId") Long blogId, @Param("tagIds") List<Long> tagIds);
    
    /**
     * 根据博客ID删除所有标签关联
     */
    int deleteByBlogId(Long blogId);
}
