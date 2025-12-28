package com.weixi.blog.service;

import com.weixi.blog.entity.Tag;

import java.util.List;

/**
 * 标签服务接口
 */
public interface TagService {
    
    /**
     * 查询所有标签
     */
    List<Tag> getAllTags();
}

