# Leafage Auth

`auth` 是 Leafage 的 OAuth2/OIDC 授权服务器，默认监听 `8761` 端口。它负责浏览器登录、客户端授权、令牌签发以及授权数据持久化。

## 职责

- 提供账号密码登录页；
- 提供 OAuth2 Authorization Server 与 OpenID Connect 端点；
- 通过 JDBC 将用户、注册客户端与授权记录持久化到 PostgreSQL；
- 在访问令牌中写入当前用户的 `authorities` 声明；
- 暴露健康检查与 Prometheus 指标。

Gateway 通过 OAuth2 Client 模式与本服务集成；浏览器不直接持有访问令牌。

## 数据库配置

开发环境通过以下环境变量连接 PostgreSQL：

```text
DB_HOST=127.0.0.1
DB_PORT=5432
DB_USERNAME=postgres
DB_PASSWORD=your-password
```

初始化认证数据可使用 [schema.sql](src/main/resources/schema.sql)。该脚本包含 `DROP TABLE`，仅适用于新建或明确允许重置的环境。

## 运行

```bash
mvn -pl auth spring-boot:run
```

或：

```bash
mvn -pl auth package -DskipTests
java -jar auth/target/auth-0.0.1.jar
```

## 核心依赖

| 依赖 | 用途 |
| --- | --- |
| Spring Authorization Server | OAuth2/OIDC 授权服务器 |
| Spring Security | 登录与安全链配置 |
| Thymeleaf | 登录页渲染 |
| Spring Data JDBC | 用户与授权数据访问 |
| PostgreSQL | 认证数据存储 |
| Actuator、Micrometer Prometheus | 健康检查与指标 |

## Docker

```bash
mvn -pl auth package -DskipTests
docker build -t leafage-auth ./auth
```
