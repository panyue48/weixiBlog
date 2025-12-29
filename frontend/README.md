# 个人博客系统 - 前端

## 技术栈
- Vue 3
- Vue Router
- Axios
- Vite
- Marked (Markdown渲染)
- Highlight.js (代码高亮)

## 安装依赖

```bash
cd frontend
npm install
```

## 开发

```bash
npm run dev
```

前端服务将运行在 http://localhost:3000

## 构建

```bash
npm run build
```

## 项目结构

```
frontend/
├── src/
│   ├── api/          # API接口
│   ├── router/       # 路由配置
│   ├── views/        # 页面组件
│   │   ├── BlogList.vue      # 前台博客列表
│   │   ├── BlogDetail.vue    # 前台博客详情
│   │   └── admin/            # 后台管理页面
│   │       ├── Login.vue     # 登录页
│   │       ├── Dashboard.vue # 后台主框架
│   │       ├── BlogList.vue  # 博客管理
│   │       ├── BlogEdit.vue  # 博客编辑
│   │       ├── TypeList.vue  # 分类管理
│   │       ├── TagList.vue   # 标签管理
│   │       └── Profile.vue  # 个人信息
│   ├── App.vue       # 根组件
│   ├── main.js       # 入口文件
│   └── style.css     # 全局样式
├── index.html
├── package.json
└── vite.config.js
```

## 功能说明

### 前台
- 博客列表展示
- 博客详情查看（支持Markdown渲染）
- 分类和标签筛选

### 后台管理
- 用户登录/注销
- 个人信息修改
- 博客的增删改查
- 分类的增删改查
- 标签的增删改查

## 注意事项

1. 确保后端服务运行在 http://localhost:8083
2. 前端通过代理访问后端API（配置在 vite.config.js）
3. 使用Session进行用户认证

