package com.weixi.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
public class User {
    private Long id;
    
    private String openid;
    
    private String nickname;
    
    private String avatar;
    
    private Integer gender; // 0-未知，1-男，2-女
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
