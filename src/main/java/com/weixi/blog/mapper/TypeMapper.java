package com.weixi.blog.mapper;

import com.weixi.blog.entity.Type;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分类Mapper - 使用XML映射
 */
@Mapper
public interface TypeMapper {
    
    /**
     * 根据用户ID查询所有分类
     */
    List<Type> selectByUserId(Long userId);
    
    /**
     * 查询所有分类（已废弃，保留兼容性）
     */
    List<Type> selectAll();
    
    /**
     * 插入分类
     */
    int insert(Type type);
    
    /**
     * 更新分类
     */
    int updateById(Type type);
    
    /**
     * 删除分类
     */
    int deleteById(Long id, Long userId);
    
    /**
     * 根据ID和用户ID查询分类
     */
    Type selectByIdAndUserId(Long id, Long userId);
}
