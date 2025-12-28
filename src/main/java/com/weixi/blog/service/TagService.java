package com.weixi.blog.service;

import com.weixi.blog.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {
    
    /**
     * 根据用户ID查询所有标签
     */
    List<Tag> getTagsByUserId(Long userId);
    
    /**
     * 查询所有标签（已废弃，保留兼容性）
     */
    List<Tag> getAllTags();
    
    /**
     * 创建标签
     */
    Long createTag(Long userId, Tag tag);
    
    /**
     * 更新标签
     */
    void updateTag(Long id, Long userId, Tag tag);
    
    /**
     * 删除标签
     */
    void deleteTag(Long id, Long userId);
}

