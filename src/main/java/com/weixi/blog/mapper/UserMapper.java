package com.weixi.blog.mapper;

import com.weixi.blog.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper - 使用XML映射
 */
@Mapper
public interface UserMapper {
    
    /**
     * 插入用户
     */
    int insert(User user);
    
    /**
     * 更新用户
     */
    int updateById(User user);
    
    /**
     * 根据ID查询用户
     */
    User selectById(Long id);
    
    /**
     * 根据openid查询用户
     */
    User selectByOpenid(String openid);
}
