package com.weixi.blog.mapper;

import com.weixi.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 标签Mapper - 使用XML映射
 */
@Mapper
public interface TagMapper {
    
    /**
     * 查询所有标签
     */
    List<Tag> selectAll();
}
