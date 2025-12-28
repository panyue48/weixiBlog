# 升级到 Java 18 适配说明

## 已完成的升级

### 1. Spring Boot 版本升级
- **之前**：1.5.7.RELEASE（需要 Java 8）
- **现在**：3.1.5（支持 Java 17+，兼容 Java 18）

### 2. Java 版本配置
- **之前**：Java 1.8
- **现在**：Java 17（Spring Boot 3.x 最低要求，Java 18 完全兼容）

### 3. 依赖版本更新
- **MyBatis**：1.3.2 → 3.0.3（适配 Spring Boot 3.x）
- **MySQL 驱动**：`mysql-connector-java` 5.1.47 → `mysql-connector-j` 8.0.33
- **FastJSON**：fastjson 1.2.83 → fastjson2 2.0.43

### 4. 配置文件更新
- **application.yml**：
  - `context-path` → `servlet.context-path`
  - `com.mysql.jdbc.Driver` → `com.mysql.cj.jdbc.Driver`
- **CorsConfig**：`addAllowedOrigin` → `addAllowedOriginPattern`（Spring Boot 3.x 支持）

### 5. Maven 编译器插件
- 更新为适配 Java 17+ 的配置

## 现在请执行

### 1. 更新 Maven 项目
在 IDE 中：
- **右键项目** → **Maven** → **Update Project**
- 勾选 **Force Update of Snapshots/Releases**
- 点击 **OK**

### 2. 清理并重新编译
```bash
mvn clean compile
```

### 3. 重新启动
在 IDE 中运行 `BlogApplication.java`

## 优势

✅ **无需修改环境变量**：直接使用系统当前的 Java 18
✅ **使用最新版本**：Spring Boot 3.1.5 更稳定、性能更好
✅ **更好的兼容性**：支持更多现代 Java 特性
✅ **长期支持**：Spring Boot 3.x 是当前主流版本

## 注意事项

1. **Java 版本**：Spring Boot 3.x 最低需要 Java 17，Java 18 完全兼容
2. **数据库驱动**：MySQL 8.0+ 驱动类名已更新
3. **API 兼容性**：Spring Boot 3.x 的 API 与 1.5.x 有部分差异，但我们的代码已适配

## 如果遇到问题

### 问题1：编译失败
- 确保 IDE 使用 Java 17 或 18
- 清理并重新编译：`mvn clean compile`

### 问题2：数据库连接失败
- 检查 `application.yml` 中的数据库配置
- 确保 MySQL 服务已启动

### 问题3：依赖下载失败
- 配置 Maven 国内镜像
- 执行 `mvn clean install -U`

## 验证

启动成功后，访问：http://localhost:8083/api/type/list

应该能正常返回数据。

