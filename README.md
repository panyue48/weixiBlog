# 微信小程序个人博客系统

一个完整的微信小程序个人博客系统，包含后端 API 和前端小程序。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.0
- **语言**: Java 17+
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **构建工具**: Maven

### 前端
- **框架**: Uni-app (Vue 3 Composition API)
- **UI组件库**: uView UI 2.0
- **构建工具**: Vite

## 项目结构

```
weixiBlog/
├── blog_db.sql              # 数据库建表脚本
├── pom.xml                  # Maven配置
├── src/                     # 后端源码
│   └── main/
│       ├── java/           # Java源码
│       └── resources/     # 配置文件
│           ├── application.yml
│           └── mapper/     # MyBatis XML映射
├── miniprogram/            # 前端小程序
│   ├── api/               # API接口封装
│   ├── pages/             # 页面文件
│   ├── utils/             # 工具函数
│   ├── package.json
│   └── ...
└── README.md
```

## 快速开始

### 1. 数据库初始化

执行 `blog_db.sql` 创建数据库和表：

```bash
mysql -u root -p < blog_db.sql
```

或使用数据库管理工具（如 Navicat、DBeaver）执行 SQL 脚本。

### 2. 配置后端

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/blog_db?...
    username: root
    password: your_password  # 修改为你的MySQL密码
```

### 3. 启动后端

```bash
# 使用Maven
mvn spring-boot:run

# 或使用IDE直接运行 BlogApplication.java
```

后端服务将运行在：http://localhost:8083/api

### 4. 配置并启动前端

```bash
cd miniprogram

# 安装依赖
npm install
npm install uview-ui@2.0.36

# 配置后端地址（如需要）
# 编辑 miniprogram/utils/request.js
# const BASE_URL = 'http://localhost:8083/api'

# 启动H5预览（推荐）
npm run dev:h5

# 或启动小程序预览
npm run dev:mp-weixin
```

访问 http://localhost:8080 即可在浏览器中预览。

## 功能特性

### ✅ 已实现功能

1. **用户系统**
   - 微信授权登录
   - 自动注册
   - 用户信息管理

2. **博客管理**
   - 发布博客（支持Markdown）
   - 编辑博客
   - 删除博客
   - 草稿/发布状态

3. **分类与标签**
   - 博客分类管理
   - 多标签支持
   - 分类/标签筛选

4. **浏览与搜索**
   - 首页博客列表
   - 博客详情页
   - 标题/内容搜索
   - 分类筛选
   - 标签筛选
   - 分页加载

## API 接口

### 用户相关
- `POST /api/login/wechat` - 微信登录

### 博客相关
- `GET /api/blog/list` - 获取博客列表（支持分页、搜索、筛选）
- `GET /api/blog/{id}` - 获取博客详情
- `POST /api/blog/save` - 发布博客
- `PUT /api/blog/{id}` - 更新博客
- `DELETE /api/blog/{id}` - 删除博客

### 分类相关
- `GET /api/type/list` - 获取所有分类

### 标签相关
- `GET /api/tag/list` - 获取所有标签

## 开发说明

### 后端开发

- 统一响应格式：`Result<T>`
- 实体类使用 MyBatis-Plus 注解
- Service 层处理业务逻辑
- Controller 层提供 RESTful API

### 前端开发

- 使用 Vue 3 Composition API
- 网络请求统一封装在 `utils/request.js`
- API 接口封装在 `api/index.js`
- 使用 uView UI 组件库

## 注意事项

1. **跨域配置**：后端已包含 `CorsConfig.java`，支持跨域请求
2. **微信登录**：H5环境会自动使用模拟登录，小程序环境需要配置真实 AppID
3. **图片上传**：当前版本使用URL方式，后续可集成图片上传功能
4. **Markdown渲染**：当前使用简单正则替换，建议后续集成专业库

## 常见问题

### Q: 数据库连接失败？
A: 检查 `application.yml` 中的数据库配置，确保MySQL服务已启动。

### Q: 前端无法连接后端？
A: 
1. 确认后端服务已启动（http://localhost:8083/api）
2. 检查 `miniprogram/utils/request.js` 中的 BASE_URL
3. 确认后端已配置CORS

### Q: uView UI 组件不显示？
A: 确认已安装 uview-ui：`npm install uview-ui@2.0.36`

### Q: H5预览时登录功能不可用？
A: H5环境不支持微信登录，代码已自动处理，会使用模拟登录。

## 文档

- [前端快速启动指南](miniprogram/QUICK_START.md)
- [前端安装说明](miniprogram/INSTALL.md)
- [前端README](miniprogram/README.md)

## 下一步计划

- [ ] 集成专业的 Markdown 渲染库
- [ ] 添加图片上传功能
- [ ] 优化移动端体验
- [ ] 添加评论功能
- [ ] 添加点赞/收藏功能

## 许可证

MIT License

