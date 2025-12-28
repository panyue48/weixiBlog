package com.weixi.blog.service;

import com.weixi.blog.entity.Type;

import java.util.List;

/**
 * 分类服务接口
 */
public interface TypeService {
    
    /**
     * 根据用户ID查询所有分类
     */
    List<Type> getTypesByUserId(Long userId);
    
    /**
     * 查询所有分类（已废弃，保留兼容性）
     */
    List<Type> getAllTypes();
    
    /**
     * 创建分类
     */
    Long createType(Long userId, Type type);
    
    /**
     * 更新分类
     */
    void updateType(Long id, Long userId, Type type);
    
    /**
     * 删除分类
     */
    void deleteType(Long id, Long userId);
}

