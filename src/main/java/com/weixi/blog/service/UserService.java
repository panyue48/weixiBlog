package com.weixi.blog.service;

import com.weixi.blog.entity.User;
import com.weixi.blog.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 微信登录/注册
     */
    UserVO login(String code, String nickname, String avatar);
    
    /**
     * 根据OpenID查询用户
     */
    User findByOpenid(String openid);
    
    /**
     * 根据ID查询用户
     */
    UserVO findById(Long id);
}

