# 个人博客系统 - 开发指南

## 📋 目录

1. [项目架构说明](#项目架构说明)
2. [启动顺序](#启动顺序)
3. [前后端分离说明](#前后端分离说明)
4. [前后端交互原理](#前后端交互原理)
5. [开发流程](#开发流程)
6. [增删改查开发示例](#增删改查开发示例)
7. [常见问题](#常见问题)

---

## 🏗️ 项目架构说明

### 架构模式：前后端分离

本项目采用**前后端分离**架构：

```
┌─────────────────┐         HTTP/JSON         ┌─────────────────┐
│                 │  ←──────────────────────→  │                 │
│   Vue 3 前端     │         API 请求            │  Spring Boot    │
│   (Port 3000)   │                            │   后端          │
│                 │                            │  (Port 8083)    │
└─────────────────┘                            └─────────────────┘
```

### 技术栈

**前端（Frontend）**
- Vue 3 - 前端框架
- Vue Router - 路由管理
- Axios - HTTP 请求库
- Vite - 构建工具
- Marked - Markdown 渲染
- Highlight.js - 代码高亮

**后端（Backend）**
- Spring Boot 3.1.4 - Java 后端框架
- MyBatis - ORM 框架
- MySQL 8.0 - 数据库
- Spring Security - 密码加密（BCrypt）
- Session - 用户认证

---

## 🚀 启动顺序

### ✅ 正确的启动顺序

**必须先启动后端，再启动前端！**

```
步骤 1: 启动后端 Spring Boot
  ↓
步骤 2: 启动前端 Vue (npm run dev)
  ↓
步骤 3: 访问前端页面
```

### 详细步骤

#### 1. 启动后端（必须先启动）

```bash
# 方式一：在 IDE 中运行
# 打开 BlogApplication.java，点击运行按钮

# 方式二：使用 Maven 命令
mvn spring-boot:run

# 方式三：打包后运行
mvn clean package
java -jar target/weixi-blog-1.0.0.jar
```

**验证后端是否启动成功：**
- 查看控制台输出，应该看到：`Tomcat started on port(s): 8083`
- 访问：`http://localhost:8083/api/login/current`（会返回未登录，但说明后端已启动）

#### 2. 启动前端（后端启动后再启动）

```bash
# 进入前端目录
cd frontend

# 首次运行需要安装依赖（只需一次）
npm install

# 启动开发服务器
npm run dev
```

**验证前端是否启动成功：**
- 控制台显示：`Local: http://localhost:3000/`
- 浏览器访问：`http://localhost:3000/`

---

## 🔄 前后端分离说明

### 什么是前后端分离？

**前后端分离**是指：
- **前端**：负责页面展示、用户交互、UI 渲染
- **后端**：负责业务逻辑、数据处理、数据库操作
- **通信**：通过 HTTP API（JSON 格式）进行数据交换

### 本项目的前后端分离特点

1. **独立部署**
   - 前端运行在 3000 端口
   - 后端运行在 8083 端口
   - 可以分别部署到不同的服务器

2. **独立开发**
   - 前端开发者只需关注 Vue 代码
   - 后端开发者只需关注 Java 代码
   - 通过 API 接口约定进行协作

3. **技术栈独立**
   - 前端使用 JavaScript/Vue
   - 后端使用 Java/Spring Boot
   - 互不干扰

---

## 🔌 前后端交互原理

### 数据流向

```
用户操作前端页面
    ↓
前端发送 HTTP 请求（Axios）
    ↓
Vite 代理转发到后端（vite.config.js）
    ↓
后端处理请求（Spring Boot Controller）
    ↓
后端查询数据库（MyBatis）
    ↓
后端返回 JSON 数据
    ↓
前端接收数据并更新页面（Vue）
```

### 代理配置

前端通过 **Vite 代理** 转发请求到后端：

**文件：`frontend/vite.config.js`**
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8083',  // 后端地址
      changeOrigin: true
    }
  }
}
```

**工作原理：**
- 前端请求：`http://localhost:3000/api/blog/list`
- Vite 代理转发到：`http://localhost:8083/api/blog/list`
- 后端处理并返回数据
- 前端接收数据并渲染

### API 调用示例

**前端代码：`frontend/src/api/index.js`**
```javascript
// 前端调用
export const blogApi = {
  getList: (params) => api.get('/blog/list', { params })
}
```

**后端接口：`src/main/java/com/weixi/blog/controller/ApiBlogController.java`**
```java
@GetMapping("/list")
public Result<PageResult<BlogVO>> getBlogList(...) {
    // 处理业务逻辑
    return Result.success(page);
}
```

**数据格式：**
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "records": [...],
    "total": 100
  }
}
```

---

## 💻 开发流程

### 完整开发流程

```
1. 需求分析
   ↓
2. 数据库设计（如需要）
   ↓
3. 后端开发
   ├── Entity（实体类）
   ├── Mapper（数据访问层）
   ├── Service（业务逻辑层）
   └── Controller（控制器层）
   ↓
4. 测试后端 API（使用 Postman 或浏览器）
   ↓
5. 前端开发
   ├── API 封装（api/index.js）
   ├── 页面组件（views/）
   └── 路由配置（router/index.js）
   ↓
6. 前后端联调
   ↓
7. 测试和优化
```

### 开发顺序建议

**推荐顺序：后端 → 前端**

1. **先开发后端 API**
   - 确保接口能正常返回数据
   - 使用 Postman 测试接口

2. **再开发前端页面**
   - 调用后端 API
   - 渲染数据到页面

---

## 📝 增删改查开发示例

### 示例：开发"标签管理"功能

#### 步骤 1：数据库设计（已完成）

表结构已在 `blog_db.sql` 中定义：
```sql
CREATE TABLE `t_tag` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `color` VARCHAR(20) DEFAULT '#409EFF',
  ...
)
```

#### 步骤 2：后端开发

**2.1 实体类（Entity）**
```java
// src/main/java/com/weixi/blog/entity/Tag.java
@Data
public class Tag {
    private Long id;
    private Long userId;
    private String name;
    private String color;
    // ...
}
```

**2.2 Mapper 接口**
```java
// src/main/java/com/weixi/blog/mapper/TagMapper.java
@Mapper
public interface TagMapper {
    List<Tag> selectByUserId(Long userId);
    int insert(Tag tag);
    int updateById(Tag tag);
    int deleteById(Long id, Long userId);
}
```

**2.3 Mapper XML**
```xml
<!-- src/main/resources/mapper/TagMapper.xml -->
<select id="selectByUserId" resultMap="TagMap">
    SELECT * FROM t_tag WHERE user_id = #{userId}
</select>
```

**2.4 Service 接口**
```java
// src/main/java/com/weixi/blog/service/TagService.java
public interface TagService {
    List<Tag> getTagsByUserId(Long userId);
    Long createTag(Long userId, Tag tag);
    void updateTag(Long id, Long userId, Tag tag);
    void deleteTag(Long id, Long userId);
}
```

**2.5 Service 实现**
```java
// src/main/java/com/weixi/blog/service/impl/TagServiceImpl.java
@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;
    
    @Override
    public List<Tag> getTagsByUserId(Long userId) {
        return tagMapper.selectByUserId(userId);
    }
    
    // ... 其他方法实现
}
```

**2.6 Controller**
```java
// src/main/java/com/weixi/blog/controller/ApiTagController.java
@RestController
@RequestMapping("/tag")
public class ApiTagController {
    @Autowired
    private TagService tagService;
    
    @GetMapping("/admin/list")
    public Result<List<Tag>> getTagsByUserId(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        List<Tag> tags = tagService.getTagsByUserId(userId);
        return Result.success(tags);
    }
    
    @PostMapping("/save")
    public Result<Long> createTag(@RequestBody Tag tag, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        Long tagId = tagService.createTag(userId, tag);
        return Result.success("创建成功", tagId);
    }
    
    // ... 其他接口
}
```

**2.7 测试后端 API**

使用 Postman 或浏览器测试：
```
GET http://localhost:8083/api/tag/admin/list
POST http://localhost:8083/api/tag/save
PUT http://localhost:8083/api/tag/{id}
DELETE http://localhost:8083/api/tag/{id}
```

#### 步骤 3：前端开发

**3.1 API 封装**
```javascript
// frontend/src/api/index.js
export const tagApi = {
  getAdminList: () => api.get('/tag/admin/list'),
  save: (data) => api.post('/tag/save', data),
  update: (id, data) => api.put(`/tag/${id}`, data),
  delete: (id) => api.delete(`/tag/${id}`)
}
```

**3.2 页面组件**
```vue
<!-- frontend/src/views/admin/TagList.vue -->
<template>
  <div class="admin-tag-list">
    <button @click="loadTags">加载标签</button>
    <div v-for="tag in tags" :key="tag.id">
      {{ tag.name }}
      <button @click="deleteTag(tag.id)">删除</button>
    </div>
  </div>
</template>

<script>
import { tagApi } from '../../api'

export default {
  data() {
    return {
      tags: []
    }
  },
  mounted() {
    this.loadTags()
  },
  methods: {
    async loadTags() {
      this.tags = await tagApi.getAdminList()
    },
    async deleteTag(id) {
      await tagApi.delete(id)
      this.loadTags()
    }
  }
}
</script>
```

**3.3 路由配置（如需要新页面）**
```javascript
// frontend/src/router/index.js
{
  path: '/admin/tags',
  name: 'AdminTagList',
  component: AdminTagList
}
```

---

## 🛠️ 日常开发操作指南

### 场景 1：添加新的业务功能

**例如：添加"评论"功能**

1. **数据库**
   ```sql
   CREATE TABLE t_comment (...)
   ```

2. **后端**
   - 创建 `Comment.java` 实体类
   - 创建 `CommentMapper.java` 和 XML
   - 创建 `CommentService.java` 和实现类
   - 创建 `ApiCommentController.java`

3. **前端**
   - 在 `api/index.js` 中添加 `commentApi`
   - 创建 `CommentList.vue` 组件
   - 在路由中添加路径

### 场景 2：修改现有功能

**例如：修改博客列表的查询条件**

1. **后端修改**
   - 修改 `BlogService.java` 中的查询逻辑
   - 修改 `BlogMapper.xml` 中的 SQL

2. **前端修改**
   - 修改 `BlogList.vue` 中的查询参数
   - 更新页面显示逻辑

3. **测试**
   - 重启后端（如果修改了 Java 代码）
   - 刷新前端页面（前端支持热更新）

### 场景 3：调试技巧

**后端调试：**
- 查看控制台日志
- 使用 `System.out.println()` 或 `log.debug()`
- 使用 IDE 断点调试

**前端调试：**
- 浏览器开发者工具（F12）
- Console 查看 API 请求和响应
- Network 标签查看 HTTP 请求详情

**前后端联调：**
- 确保后端已启动
- 确保前端代理配置正确
- 检查 CORS 配置（已配置在 `CorsConfig.java`）

---

## ❓ 常见问题

### Q1: 前端访问后端 API 报 404？

**原因：**
- 后端未启动
- 后端端口不是 8083
- API 路径错误

**解决：**
1. 确认后端已启动（查看控制台）
2. 检查 `application.yml` 中的端口
3. 检查 API 路径是否正确

### Q2: 前端显示"未登录"？

**原因：**
- Session 未设置
- 拦截器拦截了请求

**解决：**
1. 先访问 `/admin/login` 登录
2. 检查 `AuthInterceptor.java` 的排除路径配置

### Q3: 修改后端代码后不生效？

**解决：**
- 重启 Spring Boot 应用
- 使用 IDE 的自动重启功能（DevTools）

### Q4: 修改前端代码后不生效？

**解决：**
- Vite 支持热更新，通常自动刷新
- 如果不行，手动刷新浏览器（F5）
- 检查浏览器控制台是否有错误

### Q5: 数据库连接失败？

**解决：**
1. 检查 MySQL 是否启动
2. 检查 `application.yml` 中的数据库配置
3. 确认数据库 `blog_db` 已创建

---

## 📚 项目文件结构说明

```
weixiBlog/
├── src/main/java/com/weixi/blog/
│   ├── entity/          # 实体类（对应数据库表）
│   ├── mapper/          # 数据访问层接口
│   ├── service/         # 业务逻辑层
│   │   └── impl/        # Service 实现类
│   ├── controller/      # 控制器（API 接口）
│   ├── dto/             # 数据传输对象
│   ├── vo/              # 视图对象
│   └── config/          # 配置类
│
├── src/main/resources/
│   ├── mapper/          # MyBatis XML 映射文件
│   └── application.yml  # 应用配置
│
├── frontend/
│   ├── src/
│   │   ├── api/         # API 封装
│   │   ├── router/      # 路由配置
│   │   ├── views/       # 页面组件
│   │   └── App.vue      # 根组件
│   └── vite.config.js   # Vite 配置
│
└── blog_db.sql          # 数据库脚本
```

---

## 🎯 开发最佳实践

1. **先设计数据库表结构**
2. **先开发后端 API，测试通过后再开发前端**
3. **使用统一的返回格式**（`Result<T>`）
4. **前后端约定好 API 接口文档**
5. **代码提交前进行充分测试**
6. **遵循 RESTful API 设计规范**

---

## 📞 需要帮助？

如果遇到问题，请检查：
1. 后端日志（控制台输出）
2. 前端控制台（浏览器 F12）
3. 网络请求（Network 标签）

祝开发顺利！🚀

