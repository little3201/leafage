# Leafage Gateway

`gateway` 是 Leafage BFF 架构的入口服务，默认监听 `8760` 端口。浏览器通过它完成 OAuth2/OIDC 登录；Gateway 保存客户端会话，并向下游服务转发访问令牌。

## 职责

- 作为 OAuth2 Client 发起登录和处理授权回调；
- 提供登出与 OIDC RP-Initiated Logout；
- 使用 Cookie CSRF Token 保护浏览器会话请求；
- 将认证后的请求路由至下游服务，并使用 Token Relay 转发访问令牌；
- 暴露健康检查与 Prometheus 指标。

## 路由

| 路由 | 下游服务 | 处理方式 |
| --- | --- | --- |
| `/userinfo` | `auth`（`8761`） | 转发访问令牌 |
| `/hypervisor/**` | `hypervisor`（`8762`） | 去除 `/hypervisor` 前缀后转发访问令牌 |

本地开发时，OAuth2 授权服务器地址为 `http://localhost:8761`，前端成功登录后的跳转地址为 `http://127.0.0.1:5173`。生产环境应通过配置覆盖这些地址和 OAuth2 Client Secret。

## 运行

启动前请先启动 `auth`，并确保 `hypervisor` 可访问：

```bash
mvn -pl gateway spring-boot:run
```

或先从根目录打包：

```bash
mvn -pl gateway package -DskipTests
java -jar gateway/target/gateway-0.0.1.jar
```

## 核心依赖

| 依赖 | 用途 |
| --- | --- |
| Spring Cloud Gateway Server Web MVC | HTTP 路由与过滤器 |
| Spring Security OAuth2 Client | 登录、会话与 Token Relay |
| Spring Boot Actuator | 健康检查与运行状态 |
| Micrometer Prometheus | Prometheus 指标导出 |

## Docker

```bash
mvn -pl gateway package -DskipTests
docker build -t leafage-gateway ./gateway
```
