package com.weixi.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客实体类
 */
@Data
public class Blog {
    private Long id;
    
    private String title;
    
    private String content; // Markdown格式内容
    
    private String firstPicture; // 首图URL
    
    private Integer views; // 浏览量
    
    private Long typeId; // 分类ID
    
    private Long userId; // 作者ID
    
    private Integer published; // 0-草稿，1-已发布
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
