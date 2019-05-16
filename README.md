## SOFADashboard

[![Build Status](https://travis-ci.com/sofastack/sofa-dashboard.svg?branch=master)](https://travis-ci.com/sofastack/sofa-dashboard)
[![Coverage Status](https://codecov.io/gh/sofastack/sofa-dashboard/branch/master/graph/badge.svg)](https://codecov.io/gh/sofastack/sofa-dashboard)
![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)

SOFADashboard 致力于对 SOFA 框架中组件进行统一管理，包括服务治理、SOFAArk 管控等。SOFADashboard 本身所用技术栈均基于开源社区产品来开发构建，包括：Ant Design Pro、SOFABoot、Spring、MyBatis 等

## 功能特性

- 应用面板功能，用于查看应用信息
- 基于 Zookeeper 的服务治理功能
- [SOFAArk](https://github.com/sofastack/sofa-ark)多模块管理功能

## 运行依赖

编译需要 JDK 8 及以上、Maven 3.2.5 及以上，运行需要 JDK 8 及以上。

SOFAArk 管控需要依赖 MySQL 进行资源数据存储，需要安装 MySQL 并导入 SofaDashboardDB.sql 脚本。

SOFADashboard 中的服务治理、SOFAArk 管控依赖于 Zookeeper，需要启动 Zookeeper 服务。

## 文档

- [快速开始](https://www.sofastack.tech/sofa-dashboard/docs/QuickStart)
- [发布历史](https://www.sofastack.tech/sofa-dashboard/docs/ReleaseNode) 
- [发展路线](https://www.sofastack.tech/sofa-dashboard/docs/RoadMap) 

## 如何贡献

[如何参与 SOFADashboard 代码贡献](https://www.sofastack.tech/sofa-dashboard/docs/Contribution) 

## 开源许可

SOFADashboard 基于 Apache License 2.0 协议，SOFADashboard 依赖了一些三方组件，它们的开源协议参见[依赖组件版权说明](https://www.sofastack.tech/sofa-dashboard/docs/Notice)。
