package com.weixi.blog.controller;

import com.weixi.blog.common.Result;
import com.weixi.blog.dto.LoginDTO;
import com.weixi.blog.dto.RegisterDTO;
import com.weixi.blog.service.UserService;
import com.weixi.blog.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器
 */
@Slf4j
@RestController
@RequestMapping("/login")
public class ApiLoginController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterDTO registerDTO, HttpSession session) {
        try {
            UserVO userVO = userService.register(
                registerDTO.getUsername(),
                registerDTO.getPassword(),
                registerDTO.getNickname(),
                registerDTO.getEmail()
            );
            
            // 注册成功后自动登录，将用户信息存入Session
            session.setAttribute("userId", userVO.getId());
            session.setAttribute("username", userVO.getUsername());
            
            return Result.success("注册成功", userVO);
        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error("注册失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        try {
            UserVO userVO = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            
            // 将用户信息存入Session
            session.setAttribute("userId", userVO.getId());
            session.setAttribute("username", userVO.getUsername());
            
            return Result.success("登录成功", userVO);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 用户注销
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpSession session) {
        try {
            session.invalidate();
            return Result.success("注销成功", null);
        } catch (Exception e) {
            log.error("注销失败", e);
            return Result.error("注销失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    public Result<UserVO> getCurrentUser(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            UserVO userVO = userService.findById(userId);
            return Result.success(userVO);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }
}
