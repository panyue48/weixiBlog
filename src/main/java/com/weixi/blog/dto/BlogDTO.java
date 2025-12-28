package com.weixi.blog.dto;

import lombok.Data;

import java.util.List;

/**
 * 博客DTO - 用于接收前端数据
 */
@Data
public class BlogDTO {
    private Long id;
    private String title;
    private String content;
    private String firstPicture;
    private Long typeId;
    private List<Long> tagIds; // 标签ID列表
    private Integer published; // 0-草稿，1-已发布
}

