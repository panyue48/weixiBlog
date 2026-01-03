package com.weixi.blog.service.impl;

import com.weixi.blog.dto.UserUpdateDTO;
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
    public UserVO login(String username, String password) {
        User user = findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码（直接字符串比较，不使用哈希）
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 转换为VO返回（不包含密码）
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
    
    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
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
    
    @Override
    @Transactional
    public void updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (userUpdateDTO.getNickname() != null) {
            user.setNickname(userUpdateDTO.getNickname());
        }
        if (userUpdateDTO.getAvatar() != null) {
            user.setAvatar(userUpdateDTO.getAvatar());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证旧密码（直接字符串比较，不使用哈希）
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 直接存储新密码（不加密）
        user.setPassword(newPassword);
        userMapper.updateById(user);
    }
    
    @Override
    @Transactional
    public UserVO register(String username, String password, String nickname, String email) {
        // 检查用户名是否已存在
        User existingUser = findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 直接存储密码（不加密，与现有逻辑保持一致）
        user.setNickname(nickname != null && !nickname.trim().isEmpty() ? nickname : username);
        user.setEmail(email);
        
        // 插入用户
        userMapper.insert(user);
        
        // 转换为VO返回（不包含密码）
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
}
