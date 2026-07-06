# StudyMate v0.1 - 校园/职场RAG智能问答机器人

## 项目阶段说明

**v0.1 版本定位**：最小可运行原型，仅具备基础对话AI能力

当前版本为第一周开发成果，实现了核心对话交互功能：
- 用户发送文本消息
- 调用通义千问API获取AI回复
- 持久化存储对话历史记录

> **注意**：当前版本不包含RAG、文档解析、Agent、多轮对话等高级功能，这些将在后续版本迭代中逐步实现。

---

## 环境准备

### 前置依赖

| 依赖项 | 版本要求 | 说明 |
| :--- | :--- | :--- |
| MySQL | 8.0+ | 数据库服务 |
| JDK | 17+（推荐21） | 后端运行环境 |
| Maven | 3.8+ | 项目构建工具 |
| 浏览器 | Chrome/Edge/Firefox | 前端访问 |

### 环境验证

```bash
# 验证JDK版本
java -version

# 验证Maven版本
mvn -v

# 验证MySQL服务状态
mysql -u root -p -e "SELECT VERSION();"
```

---

## 启动流程

### 第一步：创建数据库

使用具有 `CREATE DATABASE` 权限的MySQL账号执行：

```sql
CREATE DATABASE IF NOT EXISTS studymate DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 第二步：执行建表SQL

进入项目目录，执行建表脚本：

```bash
mysql -u root -p studymate < src/main/resources/schema.sql
```

或手动在MySQL客户端中执行 `src/main/resources/schema.sql` 文件内容。

### 第三步：配置密钥

编辑 `src/main/resources/application.yml` 文件：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studymate?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: your-db-username    # 替换为你的数据库用户名
    password: your-db-password    # 替换为你的数据库密码

qianwen:
  api-key: your-qianwen-api-key   # 替换为你的通义千问API Key
```

> **通义千问API Key获取**：访问阿里云DashScope控制台（https://dashscope.console.aliyun.com/）申请API Key。

### 第四步：启动后端服务

```bash
cd StudyMate
mvn spring-boot:run
```

启动成功后，服务将运行在 `http://localhost:8080`。

### 第五步：打开前端页面

直接在浏览器中打开项目根目录下的 `index.html` 文件即可。

---

## 接口说明

### 唯一接口：发送消息

**POST** `/api/chat/send`

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
| :--- | :--- | :--- | :--- |
| userId | Long | 否 | 用户ID，首次发送可传null，系统会自动创建用户 |
| message | String | 是 | 用户输入的问题文本 |

#### 请求示例

```json
{
    "userId": 1,
    "message": "你好，帮我介绍一下Spring Boot框架"
}
```

#### 响应示例

```json
{
    "code": 200,
    "message": "success",
    "data": {
        "reply": "Spring Boot是由Pivotal团队提供的...",
        "userId": 1
    }
}
```

#### 错误响应示例

```json
{
    "code": 400,
    "message": "Message cannot be empty"
}
```

---

## 版本限制说明

当前v0.1版本仅实现基础功能，以下功能暂未开发：

- ❌ **RAG检索增强生成**：未接入知识库文档检索
- ❌ **文档解析**：不支持上传和解析PDF、Word、Markdown等文档
- ❌ **Agent智能体**：无工具调用、任务规划能力
- ❌ **多轮对话**：不支持上下文记忆，每次对话独立
- ❌ **文件上传**：无文件上传组件和处理逻辑
- ❌ **知识库管理**：无文档库增删改查功能
- ❌ **MCP协议**：未实现Model Context Protocol

---

## 常见报错排查

### 1. 数据库连接失败

**报错信息**：
```
Unable to acquire JDBC Connection
```

**排查方案**：
- 确认MySQL服务已启动：`net start mysql`（Windows）或 `systemctl start mysql`（Linux）
- 确认 `application.yml` 中数据库用户名和密码正确
- 确认MySQL端口为3306，或修改yml中端口配置
- 确认数据库 `studymate` 已创建

### 2. API Key错误

**报错信息**：
```
Failed to get response from QianWen API
```

**排查方案**：
- 确认 `application.yml` 中 `qianwen.api-key` 配置正确
- 登录阿里云控制台确认API Key有效且未过期
- 确认账户余额充足（通义千问API按量计费）

### 3. 前端无法发送消息

**报错信息**：
```
网络连接失败，请检查后端服务是否启动
```

**排查方案**：
- 确认后端服务已启动在 `localhost:8080`
- 确认浏览器控制台无CORS错误
- 尝试在浏览器中访问 `http://localhost:8080/api/chat/send` 验证服务状态

### 4. 建表SQL执行失败

**报错信息**：
```
Access denied for user 'xxx'@'localhost' to database 'studymate'
```

**排查方案**：
- 使用具有 `CREATE` 权限的账号执行SQL（如root）
- 或为当前用户授予权限：`GRANT ALL ON studymate.* TO 'username'@'localhost';`

### 5. Maven依赖下载失败

**报错信息**：
```
Could not resolve dependencies
```

**排查方案**：
- 检查网络连接
- 配置国内Maven镜像（在 `~/.m2/settings.xml` 中配置阿里云镜像）

---

## 项目结构

```
StudyMate/
├── index.html                    # 前端聊天页面
├── pom.xml                       # Maven依赖配置
├── README.md                     # 项目说明文档
└── src/main/
    ├── java/com/studymate/
    │   ├── Application.java      # 启动类
    │   ├── config/
    │   │   └── CorsConfig.java   # CORS跨域配置
    │   ├── controller/
    │   │   └── ChatController.java
    │   ├── dto/
    │   │   ├── ApiResponse.java
    │   │   ├── ChatRequest.java
    │   │   └── ChatResponse.java
    │   ├── entity/
    │   │   ├── ChatHistory.java
    │   │   └── User.java
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java
    │   ├── mapper/
    │   │   ├── ChatHistoryMapper.java
    │   │   └── UserMapper.java
    │   ├── service/
    │   │   ├── ChatService.java
    │   │   └── impl/ChatServiceImpl.java
    │   └── util/
    │       └── QianWenClient.java
    └── resources/
        ├── application.yml       # 应用配置
        └── schema.sql            # 建表SQL
```

---

## 技术栈

| 组件 | 版本 | 说明 |
| :--- | :--- | :--- |
| Spring Boot | 3.2.5 | 后端框架 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0+ | 数据库 |
| Vue.js | 3.x | 前端框架 |
| Apache HttpClient5 | 5.2.1 | HTTP请求工具 |
| 通义千问 | qwen-turbo | AI大模型 |

---

## License

MIT License
