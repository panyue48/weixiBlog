package com.weixi.blog.controller;

import com.weixi.blog.common.Result;
import com.weixi.blog.dto.UserUpdateDTO;
import com.weixi.blog.service.UserService;
import com.weixi.blog.vo.UserVO;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class ApiUserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(HttpSession session) {
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
    
    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO userUpdateDTO, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            userService.updateUser(userId, userUpdateDTO);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody PasswordChangeDTO passwordDTO, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return Result.error("未登录");
            }
            userService.changePassword(userId, passwordDTO.getOldPassword(), passwordDTO.getNewPassword());
            return Result.success("修改密码成功", null);
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 密码修改DTO
     */
    @lombok.Data
    public static class PasswordChangeDTO {
        private String oldPassword;
        private String newPassword;
    }
}

