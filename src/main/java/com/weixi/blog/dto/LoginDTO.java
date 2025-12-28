package com.weixi.blog.dto;

import lombok.Data;

/**
 * 微信登录DTO
 */
@Data
public class LoginDTO {
    private String code; // 微信登录code
    private String nickname; // 用户昵称（可选）
    private String avatar; // 用户头像（可选）
}

