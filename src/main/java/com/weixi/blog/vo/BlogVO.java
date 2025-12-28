package com.weixi.blog.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客VO - 用于返回给前端
 */
@Data
public class BlogVO {
    private Long id;
    private String title;
    private String content;
    private String firstPicture;
    private Integer views;
    private Long typeId;
    private String typeName; // 分类名称
    private Long userId;
    private String userNickname; // 作者昵称
    private String userAvatar; // 作者头像
    private Integer published;
    private List<TagVO> tags; // 标签列表
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

