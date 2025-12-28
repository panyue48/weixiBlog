package com.weixi.blog.controller;

import com.weixi.blog.common.Result;
import com.weixi.blog.dto.LoginDTO;
import com.weixi.blog.service.UserService;
import com.weixi.blog.vo.UserVO;
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
     * 微信登录/注册
     */
    @PostMapping("/wechat")
    public Result<UserVO> wechatLogin(@RequestBody LoginDTO loginDTO) {
        try {
            UserVO userVO = userService.login(
                loginDTO.getCode(),
                loginDTO.getNickname(),
                loginDTO.getAvatar()
            );
            return Result.success("登录成功", userVO);
        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }
}

