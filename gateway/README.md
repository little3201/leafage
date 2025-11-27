# Leafage Gateway

<p align="center">
 <img src="https://img.shields.io/badge/Spring%20Cloud-2023.0.3-green.svg" alt="Coverage Status">
 <img src="https://img.shields.io/badge/Spring%20Boot-3.3.4-green.svg" alt="Downloads">


leafage-gateway 是leafage的网关服务、所有后台服务接口都只能通过网关进行访问，它包含两部分：

- 路由网关： 基于spring cloud gateway，进行api请求的转发，目前会路由到leafage-basic模块的两个后台服务，assets和hypervisor;

#### 目标功能

- [x] api路由

#### 核心依赖

|          依赖          |  说明  |
|:--------------------:|:----:|
| Spring Cloud Gateway | 基础框架 |
