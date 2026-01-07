# Leafage

<p align="center">
 <img src="https://img.shields.io/badge/Spring%20Boot-4.0.0-green.svg" alt="Downloads" />
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=alert_status" alt="Quality Gate Status" />
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=code_smells" alt="code_smells" />
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=bugs" alt="bugs" />
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=coverage" alt="coverage" />
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=ncloc" alt="lines of code" />

## 项目简介

leafage 是一个基于 Spring Boot 构建的通用后台管理系统，适用于各种企业和个人项目的快速开发。

项目采用模块化设计，支持常用的管理功能和权限控制。

## 功能特点

- 基于 Spring Boot 易于集成和二次开发
- 完善的用户和权限管理
- RESTful API 设计
- 前后端分离架构
- 支持日志审计和操作记录
- 易于部署和配置

## 技术栈

### 1. 后端技术栈与分支

- Java 21+
- Spring Boot
- Spring Security

| 技术栈     | 分支      | 框架/库                         |
|---------|---------|------------------------------|
| webmvc  | develop | Spring data jpa              |
|         |         | Spring cloud gateway webmvc  |
| webflux | webflux | Spring data r2dbc            |
|         |         | Spring cloud gateway webflux |
| webmvc  | jdbc    | Spring data jdbc             |
|         |         | Spring cloud gateway webmvc  |

### 2. 前端技术栈与分支

| 技术栈   | 分支        | 框架/库         |
|-------|-----------|--------------|
| Vue   | develop   | Quasar       |
| Vue   | develop-2 | Element Plus |
| React | react     | MUI          |

前端源码及详细文档请见 [@little3201/leafage-ui](https://github.com/little3201/leafage-ui)

## 快速开始

### 克隆代码

```bash
git clone https://github.com/little3201/leafage.git
cd leafage
```

### 配置数据库

1. 创建数据库（如 leafage），导入 `scheme.sql`
2. 修改 `application.yml` 配置数据库连接信息

### 启动项目

```bash
mvn spring-boot:run
```

或

```bash
./mvnw spring-boot:run
```

### 访问系统

默认接口地址：`http://localhost:8080/`

前端地址（如有）：`http://localhost:xxxx/`

## 项目结构

```
leafage/
├── auth/
├── gateway/
├── hypervisor/
└── README.md
```

## 贡献指南

欢迎各位开发者参与贡献！

1. Fork 本仓库
2. 新建分支 `feature/xxx`
3. 提交代码并发起 Pull Request

## License

[Apache License 2.0](LICENSE)