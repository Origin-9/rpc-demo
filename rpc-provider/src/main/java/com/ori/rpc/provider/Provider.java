/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ori.rpc.provider;


import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Description:
 * @Author: ori
 * @Date: Created in 2019-12-19 15:05
 */
public class Provider {

    public static void main(String[] args) throws Exception {
        //自定义 Spring bean, 实例化 [ApplicationConfig,ServerConfig,RegisterConfig,ServiceConfig] bean
        //-ApplicationConfig    设置 ApplicationConfig(name), 设置 RpcContext (applicationName,localIp)
        //-ServerConfig     设置 ServerConfig (port), 启动 netty 服务端
        //-RegisterConfig   设置 RegisterConfig(ip, port)
        //-ServiceConfig    设置 ServiceConfig(id,name,ref), 发布服务到注册中心
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/provider.xml");
        context.start();
//        System.in.read();
    }
}
