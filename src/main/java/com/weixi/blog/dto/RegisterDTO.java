package com.weixi.blog.dto;

import lombok.Data;

/**
 * 注册DTO
 */
@Data
public class RegisterDTO {
    private String username; // 用户名
    private String password; // 密码
    private String nickname; // 昵称（可选）
    private String email; // 邮箱（可选）
}

