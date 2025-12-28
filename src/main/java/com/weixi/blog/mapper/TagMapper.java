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
     * 根据用户ID查询所有标签
     */
    List<Tag> selectByUserId(Long userId);
    
    /**
     * 查询所有标签（已废弃，保留兼容性）
     */
    List<Tag> selectAll();
    
    /**
     * 插入标签
     */
    int insert(Tag tag);
    
    /**
     * 更新标签
     */
    int updateById(Tag tag);
    
    /**
     * 删除标签
     */
    int deleteById(Long id, Long userId);
    
    /**
     * 根据ID和用户ID查询标签
     */
    Tag selectByIdAndUserId(Long id, Long userId);
}
