## 项目描述

简单的 RPC 框架的实现

## 模块
项目分为四个模块，分别为 rpc-core 模块、rpc-provider模块、rpc-consumer模块、rpc-interfaces模块。

- rpc-core模块提供了RPC框架的核心实现，其中包括，简单的负载均衡实现、远程通讯实现、配置相关的实现、动态代理的实现、序列化的实现。
- rpc-provider模块实现了RPC框架服务端的启动
- rpc-interfaces模块提供了RPC服务
- rpc-consumer模块提供了RPC框架客户端的启动

## 依赖库
- ##### Spring：
1. Spring XML schema，实现了用xml配置来使用RPC框架
2. 使用Spring IOC，将对象之间的相互依赖关系交给 IoC 容器来管理，并由 IoC 容器完成对象的注入
- ##### Netty：
作为客户端和服务器的通信实现方案
- ##### log4j：
作为日志打印和输出方案
- ##### 使用 cglib 
作为动态代理的实现方案
- ##### Zookeeper：
作为服务的注册中心
- ##### Curator：
作为 Zookeeper 的客户端实现方案
- ##### Protostuff：
作为序列化实现方案

-------

跑通Demo，要做以下几个步骤：

1. 进入上述的源码地址，clone项目到本地
2. 使用Maven下载依赖，一般都会自动会下载依赖库，直到整个项目编译完成
3. 在本地安装配置Zookeeper，并且启动Zookeeper服务器
4. 配置rpc-provider模块下的provider.xml文件，修改Zookeeper服务配置
5. 配置rpc-consumer模块下的consumer.xml文件，修改Zookeeper服务配置
6. 执行rpc-provider模块下Provider类的Main方法，此时服务已被发布
7. 执行rpc-provider模块下Consumer类的Main方法，此时RPC调用执行

执行完这些步骤后，可以看到控制台输出了结果Hello! world。这就说明整个RPC调用已经完成。