**# 微信小程序开发需求文档**



**## 📋 项目概述**



**### 项目背景**

基于现有的个人博客系统（Spring Boot 3 + Vue 3），开发微信小程序端，使用 UniApp 框架实现多端开发，支持微信小程序、H5、App 等多平台。



**### 技术架构**

\- **开发框架**: UniApp (Vue 3 Composition API)

\- **UI组件库**: uView UI 或 uni-ui

\- **构建工具**: Vite

\- **后端接口**: 复用现有 Spring Boot API（端口 8083）

\- **认证方式**: Session 认证（小程序端需要适配）



\---



**## 🎯 功能需求**



**### 1. 用户认证模块**



**#### 1.1 登录功能**

\- **功能描述**: 用户通过账号密码登录

\- **接口**: `POST /api/login/login`

\- **请求参数**: 

 \```json

 {

  "username": "用户名",

  "password": "密码"

 }

 \```

\- **功能点**:

 \- 账号密码登录

 \- 登录状态保持（使用本地存储）

 \- 登录失败提示

 \- 自动填充上次登录账号（可选）



**#### 1.2 用户信息**

\- **功能描述**: 获取当前登录用户信息

\- **接口**: `GET /api/login/current`、`GET /api/user/info`

\- **功能点**:

 \- 显示用户昵称、头像

 \- 用户信息缓存



**#### 1.3 退出登录**

\- **功能描述**: 清除登录状态

\- **接口**: `POST /api/login/logout`

\- **功能点**:

 \- 清除本地存储的登录信息

 \- 返回登录页



\---



**### 2. 博客展示模块**



**#### 2.1 博客列表页（首页）**

\- **功能描述**: 分页展示已发布的博客列表

\- **接口**: `GET /api/blog/list`

\- **请求参数**:

 \- `current`: 当前页码（默认1）

 \- `size`: 每页数量（默认10）

 \- `keyword`: 搜索关键词（可选）

 \- `typeId`: 分类ID（可选）

 \- `tagId`: 标签ID（可选）

\- **功能点**:

 \- 博客卡片展示（标题、摘要、首图、作者、发布时间、浏览量）

 \- 下拉刷新

 \- 上拉加载更多（分页）

 \- 搜索功能（标题/内容模糊搜索）

 \- 分类筛选

 \- 标签筛选

 \- 空状态提示

 \- 加载状态提示

 \- 点击卡片跳转到详情页



**#### 2.2 博客详情页**

\- **功能描述**: 展示博客完整内容

\- **接口**: `GET /api/blog/{id}`

\- **功能点**:

 \- Markdown 内容渲染

 \- 代码高亮显示

 \- 文章信息展示（标题、作者、发布时间、浏览量、分类、标签）

 \- 标签点击筛选

 \- 分类点击筛选

 \- 分享功能（微信分享）

 \- 返回列表

 \- 作者可见的编辑/删除按钮（需判断是否为作者）



**#### 2.3 分类页面**

\- **功能描述**: 展示所有分类

\- **接口**: `GET /api/type/list`

\- **功能点**:

 \- 分类列表展示（网格布局）

 \- 显示每个分类下的博客数量

 \- 点击分类跳转到首页并自动筛选

 \- 空状态提示



**#### 2.4 标签页面**

\- **功能描述**: 展示所有标签

\- **接口**: `GET /api/tag/list`

\- **功能点**:

 \- 标签云展示（彩色标签）

 \- 点击标签跳转到首页并自动筛选

 \- 空状态提示



**#### 2.5 归档页面**

\- **功能描述**: 按时间顺序展示所有博客

\- **接口**: `GET /api/blog/list`（按时间排序）

\- **功能点**:

 \- 按年份分组展示

 \- 按月份分组展示

 \- 点击博客跳转到详情页



\---



**### 3. 博客管理模块（需登录）**



**#### 3.1 我的博客列表**

\- **功能描述**: 展示当前用户的所有博客（包含草稿）

\- **接口**: `GET /api/blog/admin/list`

\- **请求参数**:

 \- `current`: 当前页码

 \- `size`: 每页数量

 \- `keyword`: 搜索关键词

 \- `typeId`: 分类ID

 \- `tagId`: 标签ID

 \- `published`: 发布状态（1-已发布，0-草稿）

\- **功能点**:

 \- 博客列表展示（包含发布状态标识）

 \- 下拉刷新

 \- 上拉加载更多

 \- 搜索功能

 \- 筛选功能（按分类、标签、发布状态）

 \- 点击博客进入编辑页

 \- 删除博客功能

 \- 发布/草稿切换



**#### 3.2 博客编辑/发布**

\- **功能描述**: 创建或编辑博客

\- **接口**: 

 \- `POST /api/blog/save`（创建）

 \- `PUT /api/blog/{id}`（更新）

 \- `GET /api/blog/admin/{id}`（获取详情）

\- **功能点**:

 \- 标题输入

 \- Markdown 内容编辑（支持代码高亮预览）

 \- 分类选择（下拉选择器）

 \- 标签选择（多选）

 \- 首图设置（图片上传或URL输入）

 \- 发布状态选择（立即发布/保存草稿）

 \- 保存功能

 \- 取消功能

 \- 表单验证

 \- 图片上传功能（可选，需后端支持）



**#### 3.3 分类管理**

\- **功能描述**: 管理用户的分类

\- **接口**: 

 \- `GET /api/type/admin/list`（获取列表）

 \- `POST /api/type/save`（创建）

 \- `PUT /api/type/{id}`（更新）

 \- `DELETE /api/type/{id}`（删除）

\- **功能点**:

 \- 分类列表展示

 \- 创建分类（名称、描述）

 \- 编辑分类

 \- 删除分类（需确认）

 \- 空状态提示



**#### 3.4 标签管理**

\- **功能描述**: 管理用户的标签

\- **接口**: 

 \- `GET /api/tag/admin/list`（获取列表）

 \- `POST /api/tag/save`（创建）

 \- `PUT /api/tag/{id}`（更新）

 \- `DELETE /api/tag/{id}`（删除）

\- **功能点**:

 \- 标签列表展示（显示颜色）

 \- 创建标签（名称、颜色选择）

 \- 编辑标签

 \- 删除标签（需确认）

 \- 空状态提示



**#### 3.5 个人信息管理**

\- **功能描述**: 查看和修改个人信息

\- **接口**: 

 \- `GET /api/user/info`（获取信息）

 \- `PUT /api/user/info`（更新信息）

 \- `PUT /api/user/password`（修改密码）

\- **功能点**:

 \- 显示用户信息（用户名、昵称、头像）

 \- 修改昵称

 \- 修改头像（图片上传）

 \- 修改密码（旧密码、新密码、确认密码）

 \- 表单验证



\---



**## 🏗️ 技术架构设计**



**### 1. 项目结构**



\```

miniprogram/

├── api/           # API接口封装

│  └── index.js      # 统一API管理

├── pages/          # 页面文件

│  ├── index/       # 首页（博客列表）

│  │  └── index.vue

│  ├── detail/       # 博客详情页

│  │  └── detail.vue

│  ├── types/       # 分类页面

│  │  └── types.vue

│  ├── tags/        # 标签页面

│  │  └── tags.vue

│  ├── archive/      # 归档页面

│  │  └── archive.vue

│  ├── login/       # 登录页

│  │  └── login.vue

│  ├── my-blogs/      # 我的博客列表

│  │  └── my-blogs.vue

│  ├── blog-edit/     # 博客编辑页

│  │  └── blog-edit.vue

│  ├── type-manage/    # 分类管理

│  │  └── type-manage.vue

│  ├── tag-manage/     # 标签管理

│  │  └── tag-manage.vue

│  └── profile/      # 个人信息

│    └── profile.vue

├── components/       # 公共组件

│  ├── blog-card/     # 博客卡片组件

│  ├── markdown-viewer/  # Markdown渲染组件

│  ├── empty-state/    # 空状态组件

│  └── loading/      # 加载组件

├── utils/         # 工具函数

│  ├── request.js     # 网络请求封装

│  ├── storage.js     # 本地存储封装

│  ├── auth.js       # 认证相关工具

│  └── common.js      # 通用工具函数

├── store/         # 状态管理（可选，使用Pinia）

│  └── user.js       # 用户状态

├── static/         # 静态资源

│  ├── images/      # 图片资源

│  └── icons/       # 图标资源

├── styles/         # 样式文件

│  ├── common.scss    # 公共样式

│  └── variables.scss   # 变量定义

├── App.vue        # 应用入口

├── main.js        # 主入口文件

├── pages.json       # 页面配置

├── manifest.json     # 应用配置

├── uni.scss        # uni-app样式变量

└── package.json      # 依赖配置

\```



**### 2. 网络请求封装**



**#### 2.1 请求拦截器**

\- 统一添加 baseURL

\- 统一添加请求头（Content-Type）

\- 自动携带登录凭证（Cookie/Session）

\- 请求参数序列化

\- 请求超时处理



**#### 2.2 响应拦截器**

\- 统一处理响应数据格式

\- 统一错误处理

\- 登录状态失效自动跳转

\- 网络错误提示



**#### 2.3 请求示例**

\```javascript

// utils/request.js

const BASE_URL = 'http://your-domain.com:8083/api'



// 封装uni.request，支持Session认证

export const request = (options) => {

 return new Promise((resolve, reject) => {

  uni.request({

   url: BASE_URL + options.url,

   method: options.method || 'GET',

   data: options.data,

   header: {

​    'Content-Type': 'application/json',

​    ...options.header

   },

   // 小程序需要手动处理Cookie

   success: (res) => {

​    // 处理响应

   },

   fail: (err) => {

​    // 处理错误

   }

  })

 })

}

\```



**### 3. 认证方案**



**#### 3.1 Session 认证适配**

由于小程序不支持 Cookie，需要适配 Session 认证：



**方案一：使用 Token 认证（推荐）**

\- 后端改造：登录接口返回 Token

\- 前端存储：将 Token 存储在本地存储

\- 请求携带：每次请求在 Header 中携带 Token



**方案二：手动管理 Cookie**

\- 登录时从响应头获取 Set-Cookie

\- 将 Cookie 存储在本地

\- 每次请求手动在 Header 中携带 Cookie



**#### 3.2 登录状态管理**

\```javascript

// utils/auth.js

export const auth = {

 // 保存登录信息

 setLoginInfo(userInfo, token) {

  uni.setStorageSync('userInfo', userInfo)

  uni.setStorageSync('token', token)

 },

 

 // 获取登录信息

 getLoginInfo() {

  return {

   userInfo: uni.getStorageSync('userInfo'),

   token: uni.getStorageSync('token')

  }

 },

 

 // 判断是否登录

 isLoggedIn() {

  return !!uni.getStorageSync('token')

 },

 

 // 清除登录信息

 clearLoginInfo() {

  uni.removeStorageSync('userInfo')

  uni.removeStorageSync('token')

 }

}

\```



**### 4. 页面路由配置**



\```json

// pages.json

{

 "pages": [

  {

   "path": "pages/index/index",

   "style": {

​    "navigationBarTitleText": "博客首页"

   }

  },

  {

   "path": "pages/detail/detail",

   "style": {

​    "navigationBarTitleText": "博客详情"

   }

  },

  {

   "path": "pages/login/login",

   "style": {

​    "navigationBarTitleText": "登录"

   }

  }

  // ... 其他页面

 ],

 "tabBar": {

  "color": "#7A7E83",

  "selectedColor": "#3cc51f",

  "borderStyle": "black",

  "backgroundColor": "#ffffff",

  "list": [

   {

​    "pagePath": "pages/index/index",

​    "iconPath": "static/tabbar/home.png",

​    "selectedIconPath": "static/tabbar/home-active.png",

​    "text": "首页"

   },

   {

​    "pagePath": "pages/types/types",

​    "iconPath": "static/tabbar/types.png",

​    "selectedIconPath": "static/tabbar/types-active.png",

​    "text": "分类"

   },

   {

​    "pagePath": "pages/tags/tags",

​    "iconPath": "static/tabbar/tags.png",

​    "selectedIconPath": "static/tabbar/tags-active.png",

​    "text": "标签"

   },

   {

​    "pagePath": "pages/my-blogs/my-blogs",

​    "iconPath": "static/tabbar/my.png",

​    "selectedIconPath": "static/tabbar/my-active.png",

​    "text": "我的"

   }

  ]

 }

}

\```



**### 5. UI 组件库选择**



**#### 方案一：uView UI**

\- 功能丰富，组件完善

\- 支持 Vue 3

\- 文档详细

\- 安装：`npm install uview-plus`



**#### 方案二：uni-ui**

\- uni-app 官方组件库

\- 轻量级

\- 与 uni-app 深度集成



**#### 方案三：自定义组件**

\- 根据设计需求自定义组件

\- 更灵活，但开发量大



\---



**## 📱 页面设计规范**



**### 1. 设计原则**

\- **简洁明了**: 界面简洁，信息层次清晰

\- **易于操作**: 交互流畅，操作便捷

\- **响应式**: 适配不同屏幕尺寸

\- **一致性**: 保持设计风格统一



**### 2. 颜色规范**

\- **主色调**: 根据品牌色定义

\- **辅助色**: 用于标签、按钮等

\- **文字颜色**: 主文字、次要文字、提示文字

\- **背景色**: 页面背景、卡片背景



**### 3. 字体规范**

\- **标题**: 18-20px，加粗

\- **正文**: 14-16px，常规

\- **辅助文字**: 12-14px，常规

\- **行高**: 1.5-1.8



**### 4. 间距规范**

\- **页面边距**: 15-20rpx

\- **卡片间距**: 20-30rpx

\- **内容间距**: 10-15rpx



**### 5. 组件规范**

\- **按钮**: 统一圆角、高度、字体大小

\- **输入框**: 统一样式、占位符样式

\- **卡片**: 统一阴影、圆角、内边距

\- **加载状态**: 统一加载动画、提示文字



\---



**## 🔧 开发规范**



**### 1. 代码规范**

\- 使用 ESLint 进行代码检查

\- 遵循 Vue 3 Composition API 规范

\- 组件命名使用 PascalCase

\- 文件命名使用 kebab-case

\- 函数命名使用 camelCase



**### 2. 注释规范**

\- 组件顶部添加功能说明

\- 复杂逻辑添加注释

\- API 调用添加注释说明



**### 3. 提交规范**

\- 使用约定式提交（Conventional Commits）

\- 提交信息格式：`type(scope): subject`

\- 类型：feat、fix、docs、style、refactor、test、chore



**### 4. 性能优化**

\- 图片懒加载

\- 列表虚拟滚动（长列表）

\- 接口请求防抖/节流

\- 合理使用缓存

\- 减少不必要的渲染



\---



**## 🧪 测试需求**



**### 1. 功能测试**

\- 登录/退出功能

\- 博客列表展示

\- 博客详情展示

\- 搜索功能

\- 筛选功能

\- 博客编辑/发布

\- 分类/标签管理



**### 2. 兼容性测试**

\- 微信小程序版本兼容

\- 不同机型适配

\- 不同屏幕尺寸适配



**### 3. 性能测试**

\- 页面加载速度

\- 接口响应时间

\- 内存占用

\- 网络请求优化



**### 4. 用户体验测试**

\- 操作流畅性

\- 错误提示友好性

\- 加载状态提示

\- 空状态提示



\---



**## 📦 依赖清单**



**### 核心依赖**

\```json

{

 "dependencies": {

  "vue": "^3.0.0",

  "@dcloudio/uni-app": "^3.0.0",

  "@dcloudio/uni-mp-weixin": "^3.0.0"

 }

}

\```



**### UI 组件库（可选）**

\```json

{

 "dependencies": {

  "uview-plus": "^3.0.0"

 }

}

\```



**### Markdown 渲染（可选）**

\```json

{

 "dependencies": {

  "marked": "^4.0.0",

  "highlight.js": "^11.0.0"

 }

}

\```



**### 工具库**

\```json

{

 "dependencies": {

  "dayjs": "^1.11.0"

 }

}

\```



\---



**## 🚀 开发计划**



**### 第一阶段：基础功能（1-2周）**

- ✅ 项目初始化（UniApp + Vue 3）
- ✅ 网络请求封装（支持Session认证）
- ✅ 用户登录/退出功能
- ✅ 博客列表页（首页）
- ✅ 博客详情页（Markdown渲染）
- ✅ 基础UI组件开发

**### 第二阶段：核心功能（2-3周）**

- ✅ 分类页面
- ✅ 标签页面
- ✅ 归档页面
- ✅ 搜索功能
- ✅ 筛选功能
- ✅ 下拉刷新/上拉加载

**### 第三阶段：管理功能（2-3周）**

- ✅ 我的博客列表
- ✅ 博客编辑/发布
- ✅ 分类管理
- ✅ 标签管理
- ✅ 个人信息管理

**### 第四阶段：优化与测试（1-2周）**

- ✅ 性能优化
- ✅ UI/UX优化
- ✅ 兼容性测试
- ✅ 功能测试
- ✅ Bug修复

---

## 🔌 API接口对接说明

### 1. 接口基础信息

- **基础URL**: `http://your-domain.com:8083/api`
- **请求格式**: JSON
- **响应格式**: JSON
- **认证方式**: Session（需适配为Token或Cookie手动管理）

### 2. 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    // 具体数据
  }
}
```

### 3. 接口列表

#### 3.1 用户认证接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/login/login` | 用户登录 | 否 |
| POST | `/api/login/logout` | 用户注销 | 是 |
| GET | `/api/login/current` | 获取当前用户 | 否 |
| GET | `/api/user/info` | 获取用户信息 | 是 |
| PUT | `/api/user/info` | 更新用户信息 | 是 |
| PUT | `/api/user/password` | 修改密码 | 是 |

#### 3.2 博客接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/blog/list` | 获取博客列表（前台） | 否 |
| GET | `/api/blog/{id}` | 获取博客详情（前台） | 否 |
| GET | `/api/blog/admin/list` | 获取博客列表（后台） | 是 |
| GET | `/api/blog/admin/{id}` | 获取博客详情（后台） | 是 |
| POST | `/api/blog/save` | 创建博客 | 是 |
| PUT | `/api/blog/{id}` | 更新博客 | 是 |
| DELETE | `/api/blog/{id}` | 删除博客 | 是 |

#### 3.3 分类接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/type/list` | 获取分类列表（前台） | 否 |
| GET | `/api/type/admin/list` | 获取分类列表（后台） | 是 |
| POST | `/api/type/save` | 创建分类 | 是 |
| PUT | `/api/type/{id}` | 更新分类 | 是 |
| DELETE | `/api/type/{id}` | 删除分类 | 是 |

#### 3.4 标签接口

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| GET | `/api/tag/list` | 获取标签列表（前台） | 否 |
| GET | `/api/tag/admin/list` | 获取标签列表（后台） | 是 |
| POST | `/api/tag/save` | 创建标签 | 是 |
| PUT | `/api/tag/{id}` | 更新标签 | 是 |
| DELETE | `/api/tag/{id}` | 删除标签 | 是 |

### 4. 接口调用示例

```javascript
// api/index.js
import { request } from '@/utils/request'

// 用户登录
export const login = (data) => {
  return request({
    url: '/login/login',
    method: 'POST',
    data
  })
}

// 获取博客列表
export const getBlogList = (params) => {
  return request({
    url: '/blog/list',
    method: 'GET',
    data: params
  })
}

// 获取博客详情
export const getBlogDetail = (id) => {
  return request({
    url: `/blog/${id}`,
    method: 'GET'
  })
}
```

---

## 📱 小程序特殊配置

### 1. manifest.json 配置

```json
{
  "mp-weixin": {
    "appid": "your-appid",
    "setting": {
      "urlCheck": false,
      "es6": true,
      "enhance": true,
      "postcss": true,
      "minified": true
    },
    "usingComponents": true,
    "permission": {
      "scope.userLocation": {
        "desc": "你的位置信息将用于小程序位置接口的效果展示"
      }
    },
    "requiredPrivateInfos": []
  }
}
```

### 2. 网络请求配置

小程序需要在 `manifest.json` 中配置合法域名：

```json
{
  "mp-weixin": {
    "networkTimeout": {
      "request": 10000,
      "downloadFile": 10000
    }
  }
}
```

**注意**: 开发阶段需要在微信开发者工具中勾选"不校验合法域名"，生产环境需要在微信公众平台配置服务器域名。

### 3. Session认证适配方案

由于小程序不支持Cookie，需要采用以下方案之一：

#### 方案一：Token认证（推荐）

**后端改造**：
1. 登录接口返回Token
2. 创建Token验证拦截器
3. 请求头携带Token验证

**前端实现**：
```javascript
// utils/request.js
const BASE_URL = 'http://your-domain.com:8083/api'

export const request = (options) => {
  return new Promise((resolve, reject) => {
    // 获取Token
    const token = uni.getStorageSync('token')
    
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data.data)
          } else {
            // Token失效，清除登录信息
            if (res.data.code === 401) {
              uni.removeStorageSync('token')
              uni.removeStorageSync('userInfo')
              uni.reLaunch({
                url: '/pages/login/login'
              })
            }
            reject(new Error(res.data.message || '请求失败'))
          }
        } else {
          reject(new Error('网络请求失败'))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}
```

#### 方案二：手动管理Cookie

```javascript
// utils/request.js
let cookie = ''

export const request = (options) => {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        'Cookie': cookie,
        ...options.header
      },
      success: (res) => {
        // 从响应头获取Cookie
        const setCookie = res.header['Set-Cookie'] || res.header['set-cookie']
        if (setCookie) {
          cookie = setCookie
        }
        
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data.data)
          } else {
            reject(new Error(res.data.message || '请求失败'))
          }
        } else {
          reject(new Error('网络请求失败'))
        }
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}
```

### 4. 小程序权限配置

在 `manifest.json` 中配置所需权限：

```json
{
  "mp-weixin": {
    "permission": {
      "scope.userInfo": {
        "desc": "用于完善用户资料"
      },
      "scope.record": {
        "desc": "用于语音输入"
      }
    }
  }
}
```

---

## 🎨 UI/UX设计建议

### 1. 首页设计

- **顶部搜索栏**: 固定顶部，支持实时搜索
- **分类/标签筛选**: 横向滚动标签栏
- **博客卡片**: 
  - 首图（如有）
  - 标题（加粗，18px）
  - 摘要（2-3行，超出省略）
  - 底部信息栏：作者、时间、浏览量、分类
  - 标签展示（彩色小标签）
- **下拉刷新**: 使用uni-app原生下拉刷新
- **上拉加载**: 触底自动加载更多

### 2. 详情页设计

- **顶部**: 标题、作者信息、发布时间
- **内容区**: Markdown渲染区域
- **底部操作栏**: 
  - 分享按钮（微信分享）
  - 编辑/删除按钮（仅作者可见）
- **相关推荐**: 底部推荐相关博客

### 3. 编辑页设计

- **标题输入**: 单行输入框
- **内容编辑**: 
  - 支持Markdown语法
  - 实时预览（可选）
  - 工具栏（加粗、斜体、链接等）
- **分类选择**: 下拉选择器
- **标签选择**: 多选标签，支持创建新标签
- **首图设置**: 图片选择器或URL输入
- **发布选项**: 立即发布/保存草稿
- **底部操作栏**: 保存、取消按钮

### 4. 交互设计

- **加载状态**: 使用骨架屏或加载动画
- **空状态**: 友好的空状态提示和引导
- **错误提示**: Toast提示，统一错误处理
- **操作反馈**: 按钮点击反馈，操作成功提示
- **页面跳转**: 平滑过渡动画

---

## 🔐 安全考虑

### 1. 数据安全

- 敏感信息加密存储
- Token过期处理
- 请求参数校验
- XSS防护（Markdown内容过滤）

### 2. 接口安全

- 请求签名验证（可选）
- 频率限制
- 参数校验
- 错误信息不暴露敏感信息

### 3. 用户隐私

- 用户信息保护
- 权限申请说明
- 数据使用说明

---

## 📦 部署方案

### 1. 开发环境

```bash
# 安装依赖
npm install

# 开发运行（H5）
npm run dev:h5

# 开发运行（微信小程序）
npm run dev:mp-weixin
```

### 2. 生产环境

```bash
# 构建微信小程序
npm run build:mp-weixin

# 构建产物在 dist/build/mp-weixin 目录
# 使用微信开发者工具打开该目录
# 上传代码，提交审核
```

### 3. 版本管理

- 使用Git进行版本控制
- 遵循语义化版本号
- 每次发布记录变更日志

---

## 🐛 常见问题

### Q1: 小程序无法连接后端API？

**A**: 
1. 检查后端服务是否启动
2. 检查网络请求配置（BASE_URL）
3. 开发环境：在微信开发者工具中勾选"不校验合法域名"
4. 生产环境：在微信公众平台配置服务器域名

### Q2: Session认证在小程序中不生效？

**A**: 
小程序不支持Cookie，需要：
1. 方案一：后端改造为Token认证（推荐）
2. 方案二：手动管理Cookie，从响应头获取并存储

### Q3: Markdown渲染在小程序中显示异常？

**A**: 
1. 使用支持小程序的Markdown渲染库
2. 或使用rich-text组件渲染HTML
3. 注意样式适配

### Q4: 图片上传功能如何实现？

**A**: 
1. 使用uni.chooseImage选择图片
2. 使用uni.uploadFile上传到服务器
3. 或使用第三方图床服务（如七牛云、OSS）

### Q5: 小程序包体积过大？

**A**: 
1. 使用分包加载
2. 图片资源使用CDN
3. 移除未使用的依赖
4. 代码压缩优化

---

## 📚 参考资料

### 官方文档

- [UniApp官方文档](https://uniapp.dcloud.net.cn/)
- [微信小程序开发文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)
- [Vue 3官方文档](https://cn.vuejs.org/)

### 组件库

- [uView Plus](https://uviewui.com/)
- [uni-ui](https://uniapp.dcloud.net.cn/component/uniui/uni-ui.html)

### 工具库

- [Marked - Markdown解析](https://marked.js.org/)
- [Highlight.js - 代码高亮](https://highlightjs.org/)
- [Day.js - 日期处理](https://day.js.org/)

---

## ✅ 开发 checklist

### 项目初始化
- [ ] 创建UniApp项目
- [ ] 配置manifest.json
- [ ] 配置pages.json
- [ ] 安装依赖包
- [ ] 配置网络请求

### 基础功能
- [ ] 用户登录
- [ ] 用户退出
- [ ] 博客列表
- [ ] 博客详情
- [ ] Markdown渲染

### 核心功能
- [ ] 搜索功能
- [ ] 分类筛选
- [ ] 标签筛选
- [ ] 下拉刷新
- [ ] 上拉加载

### 管理功能
- [ ] 我的博客列表
- [ ] 博客编辑
- [ ] 博客发布
- [ ] 分类管理
- [ ] 标签管理
- [ ] 个人信息管理

### 优化与测试
- [ ] 性能优化
- [ ] UI优化
- [ ] 兼容性测试
- [ ] 功能测试
- [ ] Bug修复

---

## 📝 更新日志

### v1.0.0 (计划中)
- 初始版本
- 基础功能实现
- 博客展示和管理

---

**文档版本**: v1.0.0  
**最后更新**: 2024年  
**维护者**: 开发团队**