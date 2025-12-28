package com.weixi.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 分类实体类
 */
@Data
public class Type {
    private Long id;
    
    private Long userId; // 用户ID
    
    private String name;
    
    private String description;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
