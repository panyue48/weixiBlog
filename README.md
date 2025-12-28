# 个人博客系统

一个基于 Spring Boot + Vue 3 的个人博客系统，支持博客文章的展示和管理。

## 项目结构

```
weixiBlog/
├── src/                    # 后端代码（Spring Boot）
│   └── main/
│       ├── java/           # Java源代码
│       └── resources/      # 配置文件
├── frontend/               # 前端代码（Vue 3）
├── archive/                # 已隔离的小程序代码
├── blog_db.sql            # 数据库脚本
└── pom.xml                # Maven配置
```

## 技术栈

### 后端
- Spring Boot 3.1.4
- MyBatis
- MySQL 8.0
- Spring Security (BCrypt密码加密)

### 前端
- Vue 3
- Vue Router
- Axios
- Vite
- Marked (Markdown渲染)
- Highlight.js (代码高亮)

## 快速开始

### 1. 数据库设置

执行 `blog_db.sql` 创建数据库和表结构。

默认管理员账号：
- 用户名：admin
- 密码：admin123

**注意**：首次使用前，请使用BCrypt工具生成新密码的哈希值并更新数据库。

### 2. 后端配置

1. 修改 `src/main/resources/application.yml` 中的数据库连接信息
2. 运行 `BlogApplication.java` 启动后端服务
3. 后端服务运行在：http://localhost:8083/api

### 3. 前端配置

```bash
cd frontend
npm install
npm run dev
```

前端服务运行在：http://localhost:3000

## 功能特性

### 前台功能
- ✅ 博客列表展示
- ✅ 博客详情查看（支持Markdown渲染和代码高亮）
- ✅ 分类和标签筛选

### 后台管理
- ✅ 用户登录/注销（Session认证）
- ✅ 个人信息修改
- ✅ 博客的增删改查（每个用户管理自己的博客）
- ✅ 分类的增删改查（每个用户管理自己的分类）
- ✅ 标签的增删改查（每个用户管理自己的标签）

## API接口

### 前台接口（无需认证）
- `GET /api/blog/list` - 获取博客列表
- `GET /api/blog/{id}` - 获取博客详情

### 后台接口（需要登录）
- `POST /api/login/login` - 用户登录
- `POST /api/login/logout` - 用户注销
- `GET /api/login/current` - 获取当前用户信息

- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/info` - 更新用户信息
- `PUT /api/user/password` - 修改密码

- `GET /api/blog/admin/list` - 获取博客列表（后台）
- `GET /api/blog/admin/{id}` - 获取博客详情（后台）
- `POST /api/blog/save` - 创建博客
- `PUT /api/blog/{id}` - 更新博客
- `DELETE /api/blog/{id}` - 删除博客

- `GET /api/type/admin/list` - 获取分类列表
- `POST /api/type/save` - 创建分类
- `PUT /api/type/{id}` - 更新分类
- `DELETE /api/type/{id}` - 删除分类

- `GET /api/tag/admin/list` - 获取标签列表
- `POST /api/tag/save` - 创建标签
- `PUT /api/tag/{id}` - 更新标签
- `DELETE /api/tag/{id}` - 删除标签

## 数据库设计

- `t_user` - 用户表
- `t_blog` - 博客表
- `t_type` - 分类表（关联用户）
- `t_tag` - 标签表（关联用户）
- `t_blog_tags` - 博客标签关联表

每个用户只能管理自己的博客、分类和标签。

## 开发文档

📖 **详细开发指南请查看：[DEVELOPMENT_GUIDE.md](./DEVELOPMENT_GUIDE.md)**

开发指南包含：
- 项目架构说明（前后端分离）
- 启动顺序和操作流程
- 前后端交互原理
- 增删改查开发示例
- 日常开发操作指南
- 常见问题解答

## 注意事项

1. 小程序相关代码已移动到 `archive/miniprogram_backup` 目录
2. 使用Session进行用户认证，前端需要设置 `withCredentials: true`
3. 密码使用BCrypt加密存储
4. **必须先启动后端，再启动前端**
