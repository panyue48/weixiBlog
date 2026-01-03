package com.weixi.blog.service;

import com.weixi.blog.dto.UserUpdateDTO;
import com.weixi.blog.entity.User;
import com.weixi.blog.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 用户登录
     */
    UserVO login(String username, String password);
    
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);
    
    /**
     * 根据ID查询用户
     */
    UserVO findById(Long id);
    
    /**
     * 更新用户信息
     */
    void updateUser(Long userId, UserUpdateDTO userUpdateDTO);
    
    /**
     * 修改密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 用户注册
     */
    UserVO register(String username, String password, String nickname, String email);
}

