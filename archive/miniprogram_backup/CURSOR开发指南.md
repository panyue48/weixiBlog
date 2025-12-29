# Cursor + 微信开发者工具开发指南

## 📋 开发环境要求

### 必需工具
- ✅ **Cursor** - 代码编辑器（已安装）
- ✅ **Node.js** - 运行环境（需要安装，版本 >= 14）
- ✅ **微信开发者工具** - 小程序预览工具（已安装）
- ✅ **后端 Spring Boot** - 后端服务（已配置）

### 不需要的工具
- ❌ HBuilderX（不需要）
- ❌ 其他 IDE（不需要）

---

## 🚀 快速开始（5分钟）

### 步骤 1：安装 Node.js（如果还没安装）

1. **下载 Node.js**
   - 访问：https://nodejs.org/
   - 下载 LTS 版本（推荐 18.x 或 20.x）
   - 安装时选择"Add to PATH"

2. **验证安装**
   在 Cursor 终端执行：
   ```bash
   node -v    # 应该显示版本号，如 v18.17.0
   npm -v     # 应该显示版本号，如 9.6.7
   ```

### 步骤 2：安装前端依赖

在 Cursor 中打开终端（`Ctrl + ~` 或 `View → Terminal`），执行：

**⚠️ 重要：如果使用 PowerShell，请使用 `npm.cmd` 代替 `npm`**

```bash
# 进入前端项目目录
cd miniprogram

# 安装项目依赖（PowerShell 使用 npm.cmd）
npm.cmd install --legacy-peer-deps

# 安装 UI 组件库
npm.cmd install uview-ui@2.0.36 --legacy-peer-deps
```

**说明**：
- 项目使用 **Vite** 作为构建工具（不是 uni-cli）
- 使用 `--legacy-peer-deps` 参数可以解决依赖版本冲突问题
- 在 PowerShell 中必须使用 `npm.cmd` 代替 `npm` 命令

### 步骤 3：启动后端服务

在 Cursor 中运行 Spring Boot 项目：
- 打开 `BlogApplication.java`
- 点击运行按钮（或 `Ctrl + F5`）
- 确认服务启动在：http://localhost:8083/api

### 步骤 4：编译前端项目（使用 Vite）

**方式 A：开发模式（推荐用于日常开发）**

在 Cursor 终端执行：

```bash
# 确保在 miniprogram 目录
cd miniprogram

# 启动开发服务器（支持热更新）
npm.cmd run dev:mp-weixin
```

**开发模式说明**：
- ✅ **按需编译**：Vite 使用按需编译，首次访问页面时才会编译
- ✅ **热更新**：修改代码后自动重新编译
- ✅ **实时监听**：文件变化时自动触发重新编译
- ⚠️ **注意**：开发模式下 `dist/dev/mp-weixin` 目录可能不会立即生成，需要等待编译完成或访问页面后才会生成

**等待编译完成**：
- 保持终端运行，不要关闭编译进程
- 等待终端显示编译完成信息（可能需要几分钟）
- 编译完成后，`dist/dev/mp-weixin` 目录会自动生成

**方式 B：构建模式（用于生成完整输出）**

如果需要立即生成完整的 `dist` 目录：

```bash
# 确保在 miniprogram 目录
cd miniprogram

# 构建小程序代码（生产模式）
npm.cmd run build:mp-weixin
```

**构建模式说明**：
- ✅ **完整编译**：一次性编译所有文件
- ✅ **优化代码**：代码经过压缩和优化
- ✅ **输出目录**：生成到 `dist/build/mp-weixin` 目录
- ⚠️ **注意**：构建模式用于发布，开发时建议使用开发模式

### 步骤 5：在微信开发者工具中打开

**重要**：
- 如果使用**开发模式**：确保 `npm.cmd run dev:mp-weixin` 正在运行
- 如果使用**构建模式**：确保已执行 `npm.cmd run build:mp-weixin` 并完成

1. **检查编译输出目录**

   开发模式输出目录：
   ```
   miniprogram/dist/dev/mp-weixin
   ```
   
   构建模式输出目录：
   ```
   miniprogram/dist/build/mp-weixin
   ```

   **如果目录不存在**：
   - 开发模式：等待编译完成（可能需要几分钟）
   - 构建模式：重新运行 `npm.cmd run build:mp-weixin`

2. **打开微信开发者工具**
   - 启动微信开发者工具
   - 使用微信扫码登录（首次使用需要）

3. **新建项目**：
   - 点击左上角 **"+"** 或 **"项目"** → **"新建项目"**
   - **项目名称**：`weixi-blog`（任意名称）
   - **目录**：选择编译输出目录
     - 开发模式：`miniprogram/dist/dev/mp-weixin`
     - 构建模式：`miniprogram/dist/build/mp-weixin`
     ```
     例如：C:\cursorworkplace\weixiBlog\miniprogram\dist\dev\mp-weixin
     或：C:\cursorworkplace\weixiBlog\miniprogram\dist\build\mp-weixin
     ```
   - **AppID**：选择 **"测试号"**（开发测试用）
   - **开发模式**：选择 **"小程序"**
   - 点击 **"确定"**

4. **配置本地设置**（重要！）
   - 点击左侧 **"详情"** 标签
   - 找到 **"本地设置"**
   - ✅ 勾选 **"不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书"**
   - ✅ 勾选 **"启用自动预览"**（可选，自动刷新）

5. **开始预览**
   - 工具会自动编译并显示预览界面
   - 如果看到小程序页面，说明配置成功！
   - 可以开始测试功能了

**Vite + 微信开发者工具工作流程**：
```
Cursor 中修改代码
    ↓
Vite 自动检测变化并重新编译
    ↓
微信开发者工具自动刷新（如果开启了自动预览）
    ↓
查看效果和调试
```

---

## ⚡ Vite 构建说明

### 什么是 Vite？

本项目使用 **Vite** 作为构建工具，而不是传统的 uni-cli。Vite 具有以下优势：
- ⚡ **极速启动**：使用原生 ESM，启动速度更快
- 🔥 **热更新**：修改代码后自动重新编译
- 📦 **按需编译**：只编译修改的文件，提高效率

### Vite 开发模式 vs 生产模式

**开发模式**（`dev:mp-weixin`）：
- 启动开发服务器，持续监听文件变化
- 使用按需编译，首次访问页面时才会编译
- 自动重新编译修改的文件
- 输出到 `dist/dev/mp-weixin` 目录（可能需要等待编译完成）
- **保持终端运行，不要关闭**
- **适合日常开发使用**

**生产模式**（`build:mp-weixin`）：
- 一次性编译所有文件
- 代码经过压缩和优化
- 输出到 `dist/build/mp-weixin` 目录
- 用于正式发布或需要完整输出时

### Vite 工作流程

```
1. 启动开发服务器
   npm.cmd run dev:mp-weixin
   ↓
2. Vite 监听文件变化
   ↓
3. 修改代码并保存
   ↓
4. Vite 自动检测并重新编译
   ↓
5. 微信开发者工具自动刷新
   ↓
6. 查看效果
```

---

## 🎯 如何继续开发

### 当前项目状态

✅ **已完成**：
- 项目依赖已安装
- UI 组件库（uview-ui）已安装
- 项目结构已调整为 uni-app 标准结构（`src/` 目录）
- Vite 配置已就绪

### 下一步操作

**1. 启动后端服务**（如果还没启动）
```bash
# 在 Cursor 中运行 BlogApplication.java
# 或使用 Maven 命令
mvn spring-boot:run
```
确认后端服务运行在：http://localhost:8083/api

**2. 编译前端项目**

**选择开发模式（推荐）**：
```bash
cd miniprogram
npm.cmd run dev:mp-weixin
```
- 保持终端运行
- 等待编译完成（可能需要几分钟）
- 编译完成后，`dist/dev/mp-weixin` 目录会自动生成

**或使用构建模式（立即生成）**：
```bash
cd miniprogram
npm.cmd run build:mp-weixin
```
- 一次性编译完成
- 输出到 `dist/build/mp-weixin` 目录

**3. 在微信开发者工具中打开**

- 打开微信开发者工具
- 新建项目，选择编译输出目录：
  - 开发模式：`miniprogram/dist/dev/mp-weixin`
  - 构建模式：`miniprogram/dist/build/mp-weixin`
- 配置本地设置（勾选"不校验合法域名"）

**4. 开始开发**

- 在 Cursor 中编辑 `src/` 目录下的代码
- Vite 会自动检测变化并重新编译
- 在微信开发者工具中查看效果

### 开发模式 vs 构建模式选择

| 模式 | 命令 | 输出目录 | 适用场景 |
|------|------|---------|---------|
| **开发模式** | `npm.cmd run dev:mp-weixin` | `dist/dev/mp-weixin` | 日常开发，支持热更新 |
| **构建模式** | `npm.cmd run build:mp-weixin` | `dist/build/mp-weixin` | 需要完整输出，准备发布 |

**推荐**：日常开发使用开发模式，需要完整输出时使用构建模式。

---

## 🔄 日常开发流程

### 开发循环（使用 Vite 热更新）

**推荐方式：保持 Vite 开发服务器运行**

```
1. 在 Cursor 中修改代码
   └─> 编辑 .vue 文件、.js 文件等
   └─> 保存文件（Ctrl + S）

2. Vite 自动检测并重新编译
   └─> 终端会显示编译信息
   └─> 无需手动执行命令

3. 微信开发者工具自动刷新
   └─> 如果开启了"自动预览"，会自动刷新
   └─> 或手动点击"编译"按钮

4. 查看效果和调试
   └─> 在微信开发者工具中测试
   └─> 查看控制台和网络请求
```

**如果 Vite 服务器未运行**：
```bash
# 重新启动开发服务器
cd miniprogram
npm.cmd run dev:mp-weixin
```

**如果 dist 目录没有生成**：
```bash
# 方式 1：等待开发模式编译完成（推荐）
# 保持 npm.cmd run dev:mp-weixin 运行，等待编译完成

# 方式 2：使用构建模式立即生成
cd miniprogram
npm.cmd run build:mp-weixin
# 然后使用 dist/build/mp-weixin 目录
```

### 常用命令

在 `miniprogram` 目录下执行（PowerShell 使用 `npm.cmd`）：

```bash
# 启动开发服务器（开发模式，支持热更新）
npm.cmd run dev:mp-weixin

# 编译小程序代码（生产模式，用于发布）
npm.cmd run build:mp-weixin

# 启动 H5 开发服务器（浏览器预览）
npm.cmd run dev:h5

# 编译 H5 代码（生产模式）
npm.cmd run build:h5

# 安装新依赖（记得加 --legacy-peer-deps）
npm.cmd install 包名 --legacy-peer-deps

# 查看已安装的包
npm.cmd list
```

**命令说明**：
- `dev:*`：开发模式，启动开发服务器，支持热更新
- `build:*`：生产模式，生成优化后的代码，用于发布
- 开发时使用 `dev:mp-weixin`，保持终端运行即可

---

## 🐛 调试技巧

### 1. 查看控制台日志

**在微信开发者工具中**：
- 点击底部 **"控制台"** 标签
- 查看 `console.log()` 输出的日志
- 查看错误信息

**在 Cursor 终端中**：
- 查看编译错误
- 查看依赖安装问题

### 2. 查看网络请求

**在微信开发者工具中**：
- 点击底部 **"Network"** 标签
- 查看所有 API 请求
- 查看请求参数和响应数据

### 3. 代码调试

**在 Cursor 中**：
- 使用 `console.log()` 输出调试信息
- 使用断点调试（如果配置了调试环境）

**在微信开发者工具中**：
- 点击底部 **"调试器"** 标签
- 可以设置断点、查看变量值

### 4. 修改后端 API 地址

如果需要修改后端地址，编辑 `miniprogram/utils/request.js`：

```javascript
// 修改这里的地址
const BASE_URL = 'http://localhost:8083/api'
```

修改后需要重新编译：
```bash
npm run dev:mp-weixin
```

---

## 📁 项目结构说明

```
miniprogram/
├── src/              # 源代码目录（uni-app 标准结构）
│   ├── api/          # API 接口封装
│   │   └── index.js
│   ├── pages/         # 页面文件
│   │   ├── index/     # 首页
│   │   ├── detail/    # 详情页
│   │   ├── login/     # 登录页
│   │   └── write/     # 写博客页
│   ├── utils/         # 工具函数
│   │   └── request.js # 网络请求封装
│   ├── static/        # 静态资源
│   ├── App.vue        # 应用入口
│   ├── main.js        # 主入口文件
│   ├── pages.json     # 页面配置
│   ├── manifest.json  # 应用配置
│   └── uni.scss       # 全局样式
├── dist/              # 编译输出目录（自动生成）
│   ├── dev/           # 开发模式输出
│   │   └── mp-weixin/ # 小程序开发编译结果
│   └── build/         # 生产模式输出
│       └── mp-weixin/ # 小程序生产编译结果
├── package.json       # 项目依赖
└── vite.config.js     # Vite 配置
```

**重要说明**：
- ✅ 源代码在 `src/` 目录下（uni-app 标准结构）
- ✅ 编译输出在 `dist/` 目录下
- ✅ 开发模式输出到 `dist/dev/mp-weixin`
- ✅ 生产模式输出到 `dist/build/mp-weixin`

---

## ⚠️ 常见问题解决

### 问题 1：npm 命令找不到（PowerShell）

**错误**：`无法将"npm"项识别为 cmdlet、函数、脚本文件或可运行程序的名称`

**原因**：PowerShell 未识别到 npm 命令（环境变量未在当前会话中生效）

**解决方案（按顺序尝试）**：

**方法 A：重启 Cursor（推荐）** ⭐
1. 关闭 Cursor
2. 重新打开 Cursor
3. 打开终端，再次尝试 `npm install`

**方法 B：切换到 CMD 终端（推荐）** ⭐
1. 点击终端右上角的 `+` 或下拉箭头
2. 选择 `Command Prompt (cmd)`
3. 执行 `npm install`

**方法 C：刷新 PowerShell 环境变量**
```powershell
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
npm -v  # 验证
npm install
```

**方法 D：使用完整路径**
```powershell
"C:\Program Files\nodejs\npm.cmd" install
```

**验证 Node.js 是否在 PATH 中**：
```powershell
where.exe node
where.exe npm
```

### 问题 1.5：PowerShell 执行策略错误

**错误**：`无法加载文件 npm.ps1，因为在此系统上禁止运行脚本`

**原因**：PowerShell 执行策略限制了脚本运行

**解决方案**：

**方法 A：使用 npm.cmd（推荐）** ⭐
```powershell
# 所有 npm 命令都使用 npm.cmd
npm.cmd install --legacy-peer-deps
npm.cmd run dev:mp-weixin
```

**方法 B：修改执行策略**（需要管理员权限）
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

**方法 C：切换到 CMD 终端**
1. 点击终端右上角的 `+` 或下拉箭头
2. 选择 `Command Prompt (cmd)`
3. 在 CMD 中可以直接使用 `npm` 命令

### 问题 2：编译失败或依赖冲突

**错误**：`Cannot find module 'xxx'` 或 `ERESOLVE unable to resolve dependency tree`

**解决**：
```bash
cd miniprogram
# 使用 --legacy-peer-deps 解决依赖冲突
npm.cmd install --legacy-peer-deps
npm.cmd install uview-ui@2.0.36 --legacy-peer-deps
```

**如果还是失败**：
```bash
# 删除 node_modules 和 package-lock.json，重新安装
Remove-Item -Recurse -Force node_modules
Remove-Item package-lock.json
npm.cmd install --legacy-peer-deps
```

### 问题 3：网络请求失败

**错误**：`request:fail` 或 `域名不在合法列表中`

**解决**：
1. 在微信开发者工具中：**详情** → **本地设置** → ✅ **勾选"不校验合法域名"**
2. 确认后端服务已启动（http://localhost:8083/api）
3. 检查 `utils/request.js` 中的 `BASE_URL` 是否正确

### 问题 4：页面显示空白

**解决**：
1. 查看微信开发者工具控制台的错误信息
2. 确认后端服务已启动
3. 尝试清除缓存：**工具** → **清除缓存** → **清除全部**
4. 重新编译：`npm run dev:mp-weixin`

### 问题 5：修改代码后看不到效果

**解决**：
1. **确保 Vite 开发服务器正在运行**
   ```bash
   npm.cmd run dev:mp-weixin
   ```
   保持终端运行，不要关闭

2. **检查 Vite 是否检测到文件变化**
   - 修改文件后，终端应该显示编译信息
   - 如果没有任何输出，说明 Vite 可能没有运行

3. **在微信开发者工具中刷新**
   - 点击 **"编译"** 按钮手动刷新
   - 或开启 **"自动预览"** 自动刷新

4. **清除缓存**
   - 微信开发者工具：**工具** → **清除缓存** → **清除全部**
   - 重新编译：停止 Vite 服务器（Ctrl + C），然后重新运行

### 问题 6：uView UI 组件不显示

**解决**：
```bash
cd miniprogram
npm.cmd install uview-ui@2.0.36 --legacy-peer-deps
npm.cmd run dev:mp-weixin
```

### 问题 7：dist 目录没有生成或找不到 mp-weixin 目录

**原因**：
- 开发模式使用按需编译，可能不会立即生成 `dist` 目录
- 需要等待编译完成或访问页面后才会生成

**解决方案**：

**方法 A：等待开发模式编译完成**
```bash
# 保持 npm.cmd run dev:mp-weixin 运行
# 等待终端显示编译完成信息
# 检查 dist/dev/mp-weixin 目录是否生成
```

**方法 B：使用构建模式生成完整输出**
```bash
cd miniprogram
npm.cmd run build:mp-weixin
# 然后使用 dist/build/mp-weixin 目录
```

**方法 C：检查编译输出**
```bash
# 检查是否有编译错误
# 查看终端输出的错误信息
# 确认所有依赖都已正确安装
```

### 问题 8：Vite 编译后微信开发者工具显示空白

**解决**：
1. **确认编译输出目录正确**
   - 开发模式：`miniprogram/dist/dev/mp-weixin`
   - 构建模式：`miniprogram/dist/build/mp-weixin`
   - 检查该目录是否有 `app.json`、`pages` 等文件

2. **检查 Vite 编译是否成功**
   - 查看终端是否有错误信息
   - 确认编译完成（终端显示 ready 或编译完成）

3. **重新打开项目**
   - 在微信开发者工具中关闭当前项目
   - 重新打开正确的输出目录

4. **检查 manifest.json 配置**
   - 确认 `src/manifest.json` 中的 `mp-weixin` 配置正确
   - 确认项目结构正确（文件在 `src/` 目录下）

---

## 🎯 开发工作流最佳实践

### 1. 同时打开两个工具

```
Cursor（左侧）
├─> 编辑代码
├─> 查看文件结构
└─> 运行终端命令

微信开发者工具（右侧）
├─> 预览效果
├─> 查看控制台
└─> 调试功能
```

### 2. 开发步骤

1. **修改代码**（Cursor）
   - 编辑 `.vue` 文件
   - 编辑 `.js` 文件
   - 编辑样式文件

2. **编译代码**（Cursor 终端）
   ```bash
   npm run dev:mp-weixin
   ```

3. **查看效果**（微信开发者工具）
   - 自动刷新或手动点击"编译"
   - 测试功能
   - 查看控制台

4. **调试问题**（两个工具配合）
   - Cursor：查看代码逻辑
   - 微信开发者工具：查看运行结果和错误

### 3. 快捷键推荐

**Cursor**：
- `Ctrl + ~`：打开/关闭终端
- `Ctrl + P`：快速打开文件
- `Ctrl + F`：搜索代码
- `Ctrl + S`：保存文件

**微信开发者工具**：
- `Ctrl + R`：重新编译
- `Ctrl + Shift + C`：打开控制台

---

## 📝 测试功能清单

### ✅ 基础功能测试

- [ ] **博客列表**：首页显示博客列表
- [ ] **搜索功能**：搜索框可以搜索博客标题
- [ ] **筛选功能**：可以按分类/标签筛选
- [ ] **博客详情**：点击博客查看详情，Markdown 正确渲染
- [ ] **登录功能**：微信一键登录（需要配置 AppID）
- [ ] **发布博客**：填写标题、选择分类/标签、输入内容、发布
- [ ] **编辑博客**：修改已发布的博客
- [ ] **删除博客**：删除自己的博客

### ✅ 网络请求测试

- [ ] 查看 Network 面板，确认 API 请求正常
- [ ] 确认请求地址正确（http://localhost:8083/api）
- [ ] 确认返回数据格式正确

---

## 🔧 高级配置

### 修改编译端口

编辑 `vite.config.js`：

```javascript
export default defineConfig({
  plugins: [uni()],
  server: {
    port: 8081,  // 修改端口（如果 8080 被占用）
    open: true,
    cors: true
  }
})
```

### 修改后端地址

编辑 `miniprogram/utils/request.js`：

```javascript
// 开发环境
const BASE_URL = 'http://localhost:8083/api'

// 生产环境（部署后）
// const BASE_URL = 'https://your-domain.com/api'
```

### 添加新的依赖

```bash
cd miniprogram
npm install 包名
npm run dev:mp-weixin
```

---

## 📚 相关资源

- **Node.js 官网**：https://nodejs.org/
- **Uni-app 文档**：https://uniapp.dcloud.net.cn/
- **Vue 3 文档**：https://cn.vuejs.org/
- **uView UI 文档**：https://www.uviewui.com/
- **微信开发者工具**：https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html

---

## 🎉 快速检查清单

开始开发前，确保：

- [ ] Node.js 已安装（`node -v` 有输出）
- [ ] npm 已安装（`npm -v` 有输出）
- [ ] Node.js 已安装（`node -v` 有输出）
- [ ] npm 已安装（`npm -v` 有输出，PowerShell 使用 `npm.cmd -v`）
- [ ] 已执行 `npm.cmd install --legacy-peer-deps` 安装依赖
- [ ] 已执行 `npm.cmd install uview-ui@2.0.36 --legacy-peer-deps`
- [ ] 后端服务已启动（http://localhost:8083/api）
- [ ] 已执行 `npm.cmd run dev:mp-weixin` 启动开发服务器（保持运行）
   - 或已执行 `npm.cmd run build:mp-weixin` 生成构建输出
- [ ] 已确认编译输出目录存在：
   - 开发模式：`dist/dev/mp-weixin`
   - 构建模式：`dist/build/mp-weixin`
- [ ] 已在微信开发者工具中打开正确的输出目录
- [ ] 已勾选"不校验合法域名"选项
- [ ] 编译已完成（终端显示 ready 或编译完成信息）

---

## 💡 提示

1. **保持 Vite 开发服务器运行**：开发时保持 `npm.cmd run dev:mp-weixin` 终端运行，不要关闭
2. **PowerShell 使用 npm.cmd**：在 PowerShell 中所有 `npm` 命令都要改为 `npm.cmd`
3. **安装依赖加参数**：安装新包时记得加 `--legacy-peer-deps` 参数
4. **遇到问题先看控制台**：微信开发者工具的控制台会显示错误信息
5. **网络请求失败**：检查后端是否启动，检查"不校验合法域名"是否勾选
6. **保存代码**：Cursor 会自动保存，但建议重要修改后手动保存（`Ctrl + S`）
7. **Vite 自动编译**：修改代码后，Vite 会自动检测并重新编译，无需手动操作

---

## ❓ 常见问题

### Q: 必须用命令行吗？

**A**: 是的。因为不使用 HBuilderX，所以需要用命令行来：
- 安装依赖（`npm install`）
- 编译代码（`npm run dev:mp-weixin`）

### Q: 可以不用 Node.js 吗？

**A**: 不可以。Node.js 是必需的，用于：
- 运行 npm 命令
- 运行 Vite 构建工具
- 编译 Vue 代码

### Q: Vite 开发模式如何工作？

**A**: 
- 运行 `npm.cmd run dev:mp-weixin` 后，Vite 会启动开发服务器
- 修改代码后，Vite 会自动检测变化并重新编译
- 保持终端运行，无需手动重新执行命令
- 微信开发者工具会自动刷新（如果开启了自动预览）

### Q: 每次修改都要重新编译吗？

**A**: 
- **开发模式**：保持 `npm.cmd run dev:mp-weixin` 运行，Vite 会自动编译，无需手动操作
- **生产模式**：使用 `npm.cmd run build:mp-weixin` 生成最终代码

### Q: 可以热更新吗？

**A**: 
- ✅ **Vite 支持热更新**：修改代码后自动重新编译
- ✅ **微信开发者工具**：如果开启了"自动预览"，会自动刷新页面
- ⚠️ **注意**：需要保持 Vite 开发服务器运行

---

**祝你开发顺利！** 🚀

