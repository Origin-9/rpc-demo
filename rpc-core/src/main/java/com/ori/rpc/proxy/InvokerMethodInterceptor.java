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

package com.ori.rpc.proxy;

import com.ori.rpc.config.ReferenceConfig;
import com.ori.rpc.config.RpcContext;
import com.ori.rpc.config.ServiceConfig;
import com.ori.rpc.remoting.Request;
import com.ori.rpc.remoting.Response;
import com.ori.rpc.remoting.transport.Client;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Author: ori
 * @Date: Created in 2019-12-09 15:07
 */
public class InvokerMethodInterceptor implements MethodInterceptor {


    private Logger logger = Logger.getLogger(InvokerMethodInterceptor.class);

    private ReferenceConfig referenceConfig;

    public InvokerMethodInterceptor(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    //增强对象、调用方法、方法参数以及调用父类方法的代理
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        return invoke(method.getName(), method.getParameterTypes(), objects);

    }

    /**
     *
     * @param methodName 方法名称
     * @param argTypes 方法参数
     * @param args  代理对象的所有方法
     * @return
     * @throws Throwable
     */
    public Object invoke(String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 同步调用
        return remoteCall(referenceConfig, methodName, argTypes, args);
    }

    private Object remoteCall(ReferenceConfig refrence, String methodName, Class[] argTypes, Object[] args) throws Throwable {
        // 准备请求参数
        Request request = new Request();
        // 请求id
        request.setRequestId(RpcContext.getUuid().get());
        request.setClientApplicationName(RpcContext.getApplicationName());
        request.setClientIp(RpcContext.getLocalIp());
        // 必要参数
        request.setClassName(referenceConfig.getName());
        request.setMethodName(methodName);
        request.setTypes(getTypes(argTypes));
        request.setArgs(args);
        Response response;
        try {
            Client client = new Client(refrence);
            ServiceConfig service = client.connectServer();
            request.setService(service);
            response = client.remoteCall(request);
            return response.getResult();
        } catch (Throwable e) {
            logger.error(e);
            throw e;
        }
    }


    /**
     * 获取方法的参数类型
     *
     * @param methodTypes
     * @return
     */
    private String[] getTypes(Class<?>[] methodTypes) {
        String[] types = new String[methodTypes.length];
        for (int i = 0; i < methodTypes.length; i++) {
            types[i] = methodTypes[i].getName();
        }
        return types;
    }

}
