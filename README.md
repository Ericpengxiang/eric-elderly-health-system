# 社区老年人健康智能管理系统

前后端分离：后端 Spring Boot 3.2 + JWT + MySQL + Redis；前端 Vue 3 + Vite 5 + Element Plus。

## 环境要求

- JDK **17**
- Maven **3.8+**
- Node.js **18.x**
- MySQL **8.0**（数据库名默认 `elderly_health`）
- Redis **7.x**

## 数据库初始化

```bash
mysql -u root -p < backend/src/main/resources/init.sql
```

若数据库为空，启动后端时 **`DataInitializer`** 也会自动写入与 `init.sql` 一致的演示数据（以是否存在 `admin` 为准）。

## 后端启动

```bash
cd backend
mvn spring-boot:run
```

默认：`http://localhost:8080/api`  
Swagger UI：`http://localhost:8080/api/swagger-ui.html`（如路径有变以控制台为准）

可通过环境变量覆盖：`MYSQL_*`、`REDIS_*`、`JWT_SECRET` 等（见 `application.yml`）。

## 前端启动

```bash
cd frontend
npm install
npm run dev
```

开发环境通过 Vite 将 `/api` 代理到 `http://127.0.0.1:8080`。

## 测试账号（BCrypt 已写入库 / DataInitializer）

| 角色   | 用户名   | 密码     |
|--------|----------|----------|
| 管理员 | admin    | admin123 |
| 老年人 | zhangsan | zhang123 |
| 家属   | lisi     | lisi123  |
| 医生   | wangwu   | wang123  |

演示扩展账号：赵六 `zhaoliu` / `zhao123`，孙七 `sunqi` / `sun123`。

## 交付物说明

- `backend/pom.xml`、`frontend/package.json`：依赖与版本锁定
- `backend/src/main/resources/init.sql`：建表 + 初始化数据
- 前端登录页含「测试账号」卡片，点击可快速填入用户名与密码

## 服务器部署

如需部署到远程服务器，请先提供 **IP、操作系统、部署方式** 等信息后再进行配置，勿在未授权环境上操作。
