-- ============================================
-- 个人博客系统 - 数据库设计
-- Database: blog_db
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `blog_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `blog_db`;

-- 临时禁用外键检查，以便删除表时不受外键约束影响
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================
-- 1. 用户表 (t_user)
-- ============================================
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，唯一标识',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  `nickname` VARCHAR(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入测试用户（密码：admin123，实际应该使用BCrypt加密）
-- 默认密码为 admin123，实际使用时需要加密
INSERT INTO `t_user` (`username`, `password`, `nickname`, `email`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJ1a', '管理员', 'admin@example.com');

-- ============================================
-- 2. 分类表 (t_type) - 每个用户管理自己的分类
-- ============================================
DROP TABLE IF EXISTS `t_type`;
CREATE TABLE `t_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_name` (`name`),
  UNIQUE KEY `uk_user_name` (`user_id`, `name`),
  CONSTRAINT `fk_type_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 插入默认分类（关联到admin用户）
INSERT INTO `t_type` (`user_id`, `name`, `description`) VALUES
(1, '技术分享', '技术相关的文章'),
(1, '生活随笔', '日常生活感悟'),
(1, '读书笔记', '读书心得和笔记'),
(1, '其他', '其他类型文章');

-- ============================================
-- 3. 标签表 (t_tag) - 每个用户管理自己的标签
-- ============================================
DROP TABLE IF EXISTS `t_tag`;
CREATE TABLE `t_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `color` VARCHAR(20) DEFAULT '#409EFF' COMMENT '标签颜色（用于前端展示）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_name` (`name`),
  UNIQUE KEY `uk_user_name` (`user_id`, `name`),
  CONSTRAINT `fk_tag_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 插入默认标签（关联到admin用户）
INSERT INTO `t_tag` (`user_id`, `name`, `color`) VALUES
(1, 'Java', '#FF6B6B'),
(1, 'Spring Boot', '#4ECDC4'),
(1, 'Vue', '#45B7D1'),
(1, 'MySQL', '#FFEAA7'),
(1, '前端', '#DDA0DD'),
(1, '后端', '#98D8C8');

-- ============================================
-- 4. 博客表 (t_blog)
-- ============================================
DROP TABLE IF EXISTS `t_blog`;
CREATE TABLE `t_blog` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '博客ID',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `content` LONGTEXT NOT NULL COMMENT '内容（Markdown格式）',
  `first_picture` VARCHAR(500) DEFAULT NULL COMMENT '首图URL',
  `views` INT(11) DEFAULT 0 COMMENT '浏览量',
  `type_id` BIGINT(20) NOT NULL COMMENT '分类ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '作者ID',
  `published` TINYINT(1) DEFAULT 1 COMMENT '是否发布：0-草稿，1-已发布',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_published` (`published`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_title` (`title`),
  CONSTRAINT `fk_blog_type` FOREIGN KEY (`type_id`) REFERENCES `t_type` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_blog_user` FOREIGN KEY (`user_id`) REFERENCES `t_user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客表';

-- ============================================
-- 5. 博客标签关联表 (t_blog_tags)
-- ============================================
DROP TABLE IF EXISTS `t_blog_tags`;
CREATE TABLE `t_blog_tags` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `blog_id` BIGINT(20) NOT NULL COMMENT '博客ID',
  `tag_id` BIGINT(20) NOT NULL COMMENT '标签ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_blog_tag` (`blog_id`, `tag_id`),
  KEY `idx_blog_id` (`blog_id`),
  KEY `idx_tag_id` (`tag_id`),
  CONSTRAINT `fk_blog_tags_blog` FOREIGN KEY (`blog_id`) REFERENCES `t_blog` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_blog_tags_tag` FOREIGN KEY (`tag_id`) REFERENCES `t_tag` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客标签关联表';

-- 重新启用外键检查
SET FOREIGN_KEY_CHECKS = 1;
