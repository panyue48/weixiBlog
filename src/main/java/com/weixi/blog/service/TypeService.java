package com.weixi.blog.service;

import com.weixi.blog.entity.Type;

import java.util.List;

/**
 * 分类服务接口
 */
public interface TypeService {
    
    /**
     * 查询所有分类
     */
    List<Type> getAllTypes();
}

