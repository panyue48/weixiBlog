package com.weixi.blog.dto;

import lombok.Data;

/**
 * 用户信息更新DTO
 */
@Data
public class UserUpdateDTO {
    private String nickname; // 昵称
    private String avatar; // 头像URL
    private String email; // 邮箱
}

