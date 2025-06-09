# Leafage Basic

<p align="center">
 <img src="https://img.shields.io/badge/Spring%20Boot-3.5.0-green.svg" alt="Downloads">
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=alert_status" alt="Quality Gate Status">
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=code_smells" alt="code_smells">
 <img src="https://sonarcloud.io/api/project_badges/measure?project=little3201_leafage&metric=coverage" alt="coverage"/>
</p>

### 依赖

> 现在leafage-starter-parent 和 leafage-common 已经可以在maven repository 中下载了（groupId 替换成了top.leafage），可以直接通过maven
> repository使用

#### [leafage-starter-parent](https://github.com/little3201/) —— 不包含任何代码，仅仅作为所有模块依赖的版本控制；

#### [leafage-common](https://github.com/little3201/leafage-common) —— 包含非业务代码，提供通用工具类；

### 介绍：

<a target="_blank" href="https://console.leafage.top"> 在线体验</a>， 未登录状态仅可访问部分接口

#### 概况：

- 本模块分为webmvc(develop分支)和webflux(webflux分支)两个版本，具体请查看对应分支代码；

- leafage-basic下分:「assets」、[generator] 和 「hypervisor」，具体每个模块的功能详细信息请查看对应子模块的介绍；
