package com.weixi.blog.service.impl;

import com.weixi.blog.entity.User;
import com.weixi.blog.mapper.UserMapper;
import com.weixi.blog.service.UserService;
import com.weixi.blog.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public UserVO login(String code, String nickname, String avatar) {
        String openid = null;
        
        // 测试环境：使用临时openid（如果不需要真实微信登录）
        // 如果需要真实微信登录，请取消注释微信SDK相关代码
        if (code != null && !code.isEmpty() && !code.startsWith("test_")) {
            // 如果有真实微信code，可以在这里调用微信API
            // 当前使用测试模式，生成临时openid
            openid = "test_openid_" + System.currentTimeMillis();
        } else {
            // 测试环境：如果没有配置微信信息，使用临时openid
            openid = "test_openid_" + System.currentTimeMillis();
        }
        
        // 查询用户是否存在
        User user = findByOpenid(openid);
        
        if (user == null) {
            // 新用户，自动注册
            user = new User();
            user.setOpenid(openid);
            user.setNickname(nickname != null ? nickname : "微信用户");
            user.setAvatar(avatar);
            userMapper.insert(user);
        } else {
            // 老用户，更新昵称和头像（如果提供了）
            if (nickname != null && !nickname.isEmpty()) {
                user.setNickname(nickname);
            }
            if (avatar != null && !avatar.isEmpty()) {
                user.setAvatar(avatar);
            }
            userMapper.updateById(user);
        }
        
        // 转换为VO返回
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
    
    @Override
    public User findByOpenid(String openid) {
        return userMapper.selectByOpenid(openid);
    }
    
    @Override
    public UserVO findById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
