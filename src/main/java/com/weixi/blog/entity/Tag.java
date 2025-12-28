package com.weixi.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 标签实体类
 */
@Data
public class Tag {
    private Long id;
    
    private Long userId; // 用户ID
    
    private String name;
    
    private String color; // 标签颜色，用于前端展示
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
