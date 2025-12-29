# 前后端分离开发与部署说明

## 开发环境（前后端分离运行）

### 启动步骤

进入到 `frontend` 目录中，在命令行依次输入如下命令：

```bash
# 安装依赖
npm install

# 在 localhost:3000 启动前端开发服务器
npm run dev
```

由于我在 `frontend/vite.config.js` 项目中已经配置了端口转发，将数据转发到 Spring Boot 上，因此项目启动之后，在浏览器中输入 `http://localhost:3000` 就可以访问我们的前端项目了，所有的请求通过端口转发将数据传到 Spring Boot 中（注意此时不要关闭 Spring Boot 项目）。

### 为什么可以直接访问 localhost:3000？

在 `frontend/vite.config.js` 中配置了代理：

```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8083',
      changeOrigin: true
    }
  }
}
```

这意味着：

1. **前端页面服务**：Vite 开发服务器在 3000 端口提供前端页面（HTML、CSS、JS 等静态资源）
2. **API 请求代理**：所有以 `/api` 开头的请求会被自动转发到 `http://localhost:8083`
3. **前端代码配置**：前端代码中的 `axios` 配置了 `baseURL: '/api'`，所以所有 API 请求都会通过代理转发到后端

因此：

- 访问 `http://localhost:3000` 看到的是前端页面（由 Vite 提供）
- 前端发起的 API 请求（如 `/api/blog/list`）会被 Vite 代理转发到 `http://localhost:8083/api/blog/list`
- 后端（Spring Boot）运行在 8083 端口，处理这些 API 请求

### 开发流程

1. **启动后端**：运行 Spring Boot 项目（8083 端口）
2. **启动前端**：在 `frontend` 目录执行 `npm run dev`（3000 端口）
3. **访问前端**：浏览器打开 `http://localhost:3000`
4. **开发调试**：修改前端代码会自动热更新，无需重启

### 开发优势

- **热更新**：修改前端代码后，浏览器自动刷新，无需手动刷新
- **独立开发**：前后端可以独立开发和调试
- **跨域解决**：通过代理自动解决跨域问题
- **开发效率**：前端开发时不需要重新编译后端代码

## 生产环境（前后端合并部署）

### 打包步骤

开发完成后，进入到 `frontend` 目录，然后执行如下命令：

```bash
npm run build
```

该命令执行成功之后，`frontend` 目录下生成一个 `dist` 文件夹，将该文件夹中的所有文件（`index.html` 和 `static` 文件夹）拷贝到 Spring Boot 项目中 `src/main/resources/static/` 目录下。

### 部署访问

然后就可以像开发环境那样直接访问了：

1. **启动 Spring Boot 项目**（8083 端口）
2. **访问应用**：浏览器打开 `http://localhost:8083` 即可看到前端页面
3. **统一端口**：此时前后端通过同一个端口提供服务，不再需要代理

### 生产环境特点

- **单一端口**：前后端统一通过 8083 端口访问
- **静态资源**：前端打包后的静态文件由 Spring Boot 自动提供
- **无需代理**：生产环境不需要 Vite 开发服务器，所有请求直接到 Spring Boot
- **部署简单**：只需要部署一个 Spring Boot 应用即可

## 总结

### 开发环境

- **前端端口**：3000（Vite 开发服务器）
- **后端端口**：8083（Spring Boot）
- **访问方式**：`http://localhost:3000`
- **API 请求**：通过 Vite 代理转发到 8083 端口

### 生产环境

- **统一端口**：8083（Spring Boot）
- **访问方式**：`http://localhost:8083`
- **API 请求**：直接由 Spring Boot 处理，无需代理

## 注意事项

1. **开发时**：必须同时启动前端（3000）和后端（8083）两个服务
2. **生产时**：只需要启动后端（8083）服务，前端文件已经打包到 `static` 目录
3. **端口配置**：如果修改了后端端口，需要同步修改 `vite.config.js` 中的 `target` 配置
4. **跨域问题**：开发环境通过代理解决，生产环境不需要考虑跨域

## 关于前后端分离部署

如果不想将前端打包到 Spring Boot 中，也可以使用 Nginx 等 Web 服务器单独部署前端，然后通过 Nginx 配置反向代理将 API 请求转发到 Spring Boot。这种方式更适合大型项目的生产环境部署。

