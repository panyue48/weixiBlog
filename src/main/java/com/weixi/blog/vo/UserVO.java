package com.weixi.blog.vo;

import lombok.Data;

/**
 * 用户VO - 用于返回给前端（不包含敏感信息）
 */
@Data
public class UserVO {
    private Long id;
    private String nickname;
    private String avatar;
    private Integer gender;
}

