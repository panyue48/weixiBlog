package com.weixi.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 博客标签关联实体类
 */
@Data
public class BlogTags {
    private Long id;
    
    private Long blogId;
    
    private Long tagId;
    
    private LocalDateTime createTime;
}
