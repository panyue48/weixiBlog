# API 测试指南 - Postman & cURL

## 📋 快速测试流程

### 1. 确保后端已启动
```bash
# 检查后端是否运行在 8083 端口
# 访问：http://localhost:8083/api/blog/list
```

### 2. 使用 cURL 测试（命令行）

#### 基础测试：获取博客列表
```bash
curl -X GET "http://localhost:8083/api/blog/list" -H "Content-Type: application/json"
```

#### 带分页参数
```bash
curl -X GET "http://localhost:8083/api/blog/list?current=1&size=10" -H "Content-Type: application/json"
```

#### 模糊搜索测试
```bash
curl -X GET "http://localhost:8083/api/blog/list?keyword=Java" -H "Content-Type: application/json"
```

#### 按分类筛选
```bash
curl -X GET "http://localhost:8083/api/blog/list?typeId=1" -H "Content-Type: application/json"
```

#### 按标签筛选
```bash
curl -X GET "http://localhost:8083/api/blog/list?tagId=1" -H "Content-Type: application/json"
```

#### 组合查询（搜索 + 分类）
```bash
curl -X GET "http://localhost:8083/api/blog/list?keyword=Spring&typeId=1&current=1&size=5" -H "Content-Type: application/json"
```

#### 获取博客详情
```bash
curl -X GET "http://localhost:8083/api/blog/1" -H "Content-Type: application/json"
```

#### 获取分类列表
```bash
curl -X GET "http://localhost:8083/api/type/list" -H "Content-Type: application/json"
```

#### 获取标签列表
```bash
curl -X GET "http://localhost:8083/api/tag/list" -H "Content-Type: application/json"
```

---

## 🚀 Postman 测试流程

### 步骤 1：创建新请求
1. 打开 Postman
2. 点击 **"New"** → **"HTTP Request"**
3. 选择请求方法：**GET**

### 步骤 2：设置请求 URL
```
http://localhost:8083/api/blog/list
```

### 步骤 3：添加请求参数（可选）
在 **Params** 标签页添加：
- `current`: 1
- `size`: 10
- `keyword`: Java（测试搜索）
- `typeId`: 1（测试分类筛选）
- `tagId`: 1（测试标签筛选）

### 步骤 4：设置请求头
在 **Headers** 标签页添加：
- `Content-Type`: `application/json`

### 步骤 5：发送请求
点击 **"Send"** 按钮

### 步骤 6：查看响应
- **Status**: 应该显示 `200 OK`
- **Body**: 查看返回的 JSON 数据

---

## 📊 预期响应格式

### 成功响应示例
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "id": 1,
        "title": "博客标题",
        "content": "博客内容...",
        "typeId": 1,
        "typeName": "技术分享",
        "tags": [
          {
            "id": 1,
            "name": "Java",
            "color": "#ff0000"
          }
        ],
        "views": 100,
        "createTime": "2024-01-01 12:00:00"
      }
    ],
    "total": 10,
    "current": 1,
    "size": 10
  }
}
```

### 错误响应示例
```json
{
  "code": 500,
  "message": "查询失败: 错误信息",
  "data": null
}
```

---

## 🧪 测试场景清单

- [ ] 基础列表查询（无参数）
- [ ] 分页查询（current, size）
- [ ] 模糊搜索（keyword）
- [ ] 按分类筛选（typeId）
- [ ] 按标签筛选（tagId）
- [ ] 组合查询（多个参数）
- [ ] 博客详情查询（/{id}）
- [ ] 分类列表查询
- [ ] 标签列表查询

---

## 💡 常见问题

### 1. 连接失败
- 检查后端是否启动（端口 8083）
- 检查防火墙设置

### 2. 404 错误
- 确认 URL 路径正确：`/api/blog/list`
- 检查 context-path 配置

### 3. 500 错误
- 检查数据库连接
- 查看后端日志

### 4. 空数据
- 确认数据库中有数据
- 检查 published 字段是否为 1（已发布）

---

## 📝 Windows PowerShell 测试命令

如果使用 PowerShell，需要对 URL 中的特殊字符进行转义：

```powershell
# 基础查询
curl.exe "http://localhost:8083/api/blog/list"

# 带参数（使用引号）
curl.exe "http://localhost:8083/api/blog/list?current=1&size=10"

# 搜索（中文需要 URL 编码）
curl.exe "http://localhost:8083/api/blog/list?keyword=Java"
```

---

## 🔗 完整 API 列表

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/blog/list` | 获取博客列表 |
| GET | `/api/blog/{id}` | 获取博客详情 |
| GET | `/api/type/list` | 获取分类列表 |
| GET | `/api/tag/list` | 获取标签列表 |

---

**提示**：首次测试建议从最简单的请求开始（无参数），确认连接正常后再测试复杂场景。

