# 微信小程序个人博客系统 - 前端

## 项目说明

这是基于 Uni-app + Vue 3 + uView UI 开发的微信小程序个人博客系统前端。

## 技术栈

- **框架**: Uni-app (Vue 3 Composition API)
- **UI组件库**: uView UI
- **构建工具**: Vite

## 安装依赖

```bash
cd miniprogram
npm install
```

## 开发运行

### H5 预览（推荐用于开发调试）

```bash
npm run dev:h5
```

访问 http://localhost:8080 即可在浏览器中预览。

### 微信小程序预览

```bash
npm run dev:mp-weixin
```

然后在微信开发者工具中导入 `miniprogram/dist/dev/mp-weixin` 目录。

## 配置说明

### 修改后端API地址

编辑 `utils/request.js` 文件，修改 `BASE_URL` 变量：

```javascript
const BASE_URL = 'http://localhost:8083/api'  // 改为你的后端地址
```

### 微信小程序配置

1. 在微信公众平台注册小程序，获取 AppID
2. 编辑 `manifest.json`，在 `mp-weixin.appid` 中填入你的 AppID

## 项目结构

```
miniprogram/
├── api/              # API接口封装
├── pages/            # 页面文件
│   ├── index/        # 首页
│   ├── login/        # 登录页
│   ├── detail/       # 详情页
│   └── write/       # 撰写页
├── utils/             # 工具函数
│   └── request.js    # 网络请求封装
├── static/           # 静态资源
├── App.vue           # 应用入口
├── main.js           # 主入口文件
├── pages.json        # 页面配置
├── manifest.json     # 应用配置
└── package.json      # 依赖配置
```

## 功能说明

### 1. 首页 (pages/index/index.vue)
- 博客列表展示
- 搜索功能（标题/内容）
- 分类筛选
- 标签筛选
- 下拉刷新
- 上拉加载更多

### 2. 登录页 (pages/login/login.vue)
- 微信一键登录
- 用户信息展示
- 退出登录

### 3. 撰写页 (pages/write/write.vue)
- 发布新博客
- 编辑已有博客
- 分类选择
- 标签多选
- 支持Markdown格式
- 保存草稿/立即发布

### 4. 详情页 (pages/detail/detail.vue)
- 博客详情展示
- Markdown内容渲染
- 编辑/删除功能（仅作者可见）

## 注意事项

1. **跨域问题**: 开发环境需要在后端配置CORS，已包含 `CorsConfig.java`
2. **微信登录**: 真实环境需要配置小程序AppID和Secret
3. **图片上传**: 当前版本使用URL方式，后续可集成图片上传功能
4. **Markdown渲染**: 当前使用简单的正则替换，建议后续集成专业的Markdown渲染库

## 常见问题

### Q: H5预览时无法连接后端？
A: 检查后端是否启动，以及 `utils/request.js` 中的 `BASE_URL` 是否正确。

### Q: 微信开发者工具中无法预览？
A: 确保已正确配置 `manifest.json` 中的 AppID，或者使用测试号。

### Q: 登录功能在H5中无法使用？
A: H5环境不支持微信登录，会自动使用模拟登录（测试模式）。

