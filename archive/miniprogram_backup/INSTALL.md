# 安装和运行指南

## 前置要求

1. Node.js 16+ 
2. npm 或 yarn
3. 微信开发者工具（用于小程序预览）
4. 后端服务已启动（端口8083）

## 快速开始

### 1. 安装依赖

```bash
cd miniprogram
npm install
```

### 2. 安装 uView UI

uView UI 需要通过 npm 安装：

```bash
npm install uview-ui
```

如果安装失败，可以尝试：

```bash
npm install uview-ui@2.0.36
```

### 3. 配置后端地址

编辑 `miniprogram/utils/request.js`，确保 `BASE_URL` 指向你的后端服务：

```javascript
const BASE_URL = 'http://localhost:8083/api'  // 根据实际情况修改
```

### 4. 运行项目

#### 方式一：H5 预览（推荐用于开发）

```bash
npm run dev:h5
```

然后在浏览器访问 http://localhost:8080

**优点**：
- 快速预览，无需微信开发者工具
- 支持热更新
- 方便调试

**注意**：H5环境不支持微信登录，会自动使用模拟登录

#### 方式二：微信小程序预览

```bash
npm run dev:mp-weixin
```

然后在微信开发者工具中：
1. 打开微信开发者工具
2. 选择"导入项目"
3. 项目目录选择：`miniprogram/dist/dev/mp-weixin`
4. AppID 可以填写测试号或你的小程序AppID

## 常见问题

### Q1: npm install 失败？

**解决方案**：
1. 清除缓存：`npm cache clean --force`
2. 使用国内镜像：
   ```bash
   npm config set registry https://registry.npmmirror.com
   ```
3. 删除 node_modules 和 package-lock.json 后重新安装

### Q2: uView UI 组件不显示？

**解决方案**：
1. 确认 `main.js` 中已正确引入 uView：
   ```javascript
   import uView from 'uview-ui'
   app.use(uView)
   ```
2. 确认 `pages.json` 中已配置 easycom：
   ```json
   "easycom": {
     "autoscan": true,
     "custom": {
       "^u-(.*)": "uview-ui/components/u-$1/u-$1.vue"
     }
   }
   ```

### Q3: 无法连接后端API？

**解决方案**：
1. 确认后端服务已启动（检查 http://localhost:8083/api）
2. 检查 `utils/request.js` 中的 `BASE_URL`
3. 确认后端已配置CORS（已包含 `CorsConfig.java`）
4. 如果是H5预览，检查浏览器控制台的网络请求

### Q4: H5预览时登录功能不可用？

**原因**：H5环境不支持微信登录API

**解决方案**：
- 代码已自动处理，会使用模拟登录（测试模式）
- 如需真实微信登录，请在微信开发者工具中预览

### Q5: 页面显示空白？

**解决方案**：
1. 检查浏览器/开发者工具控制台是否有错误
2. 确认所有依赖已正确安装
3. 尝试清除缓存重新编译

## 开发建议

1. **优先使用H5预览**：开发阶段建议使用 `npm run dev:h5`，速度快，调试方便
2. **定期测试小程序**：重要功能开发完成后，在微信开发者工具中测试
3. **检查控制台**：遇到问题先查看控制台错误信息
4. **网络请求调试**：使用浏览器开发者工具的Network面板查看API请求

## 下一步

- 配置小程序AppID（在 `manifest.json` 中）
- 集成图片上传功能
- 优化Markdown渲染
- 添加更多交互功能

