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
     * 查询所有分类
     */
    List<Type> selectAll();
}
