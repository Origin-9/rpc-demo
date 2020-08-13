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

package com.ori.rpc.remoting.transport;

import com.ori.rpc.cluster.LoadBalance;
import com.ori.rpc.common.SpringUtil;
import com.ori.rpc.config.ClientConfig;
import com.ori.rpc.config.ReferenceConfig;
import com.ori.rpc.config.ServiceConfig;
import com.ori.rpc.remoting.Request;
import com.ori.rpc.remoting.Response;
import com.ori.rpc.remoting.codec.Decoder;
import com.ori.rpc.remoting.codec.Encoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: ori
 * @Date: Created in 2019-12-09 15:47
 */
public class Client {

    private Logger logger = Logger.getLogger(Client.class);

    private ReferenceConfig referenceConfig;

    private ChannelFuture channelFuture;

    private ClientHandler clientHandler;

    public Client(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    public ServiceConfig connectServer() {
        logger.info("正在连接远程服务端:" + referenceConfig);
        // 客户端线程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup).channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        // 解码
                        ch.pipeline().addLast(new Encoder(Request.class));

                        // 编码
                        ch.pipeline().addLast(new Decoder(Response.class));

                        // 收发消息
                        clientHandler = new ClientHandler();

                        // 超时处理类
                        ch.pipeline().addLast(new RpcReadTimeoutHandler(clientHandler, referenceConfig.getTimeout(), TimeUnit.MILLISECONDS));

                        ch.pipeline().addLast(clientHandler);
                    }
                });

        try {
            if (!StringUtils.isEmpty(referenceConfig.getDirectServerIp())) {
                channelFuture = bootstrap.connect(referenceConfig.getDirectServerIp(), referenceConfig.getDirectServerPort()).sync();
                logger.info("点对点服务连接成功");
            } else {
                ClientConfig client = (ClientConfig) SpringUtil.getApplicationContext().getBean("client");
                logger.info("客户端负载均衡策略:" + client.getLoadBalance());

                ServiceConfig serviceConfig = LoadBalance.getService(referenceConfig, client.getLoadBalance());
                channelFuture = bootstrap.connect(serviceConfig.getIp(), serviceConfig.getPort()).sync();
                logger.info("连接远程服务端成功:" + serviceConfig);
                return serviceConfig;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Response remoteCall(Request request) throws Throwable {

        // 发送请求
        channelFuture.channel().writeAndFlush(request).sync();
        channelFuture.channel().closeFuture().sync();

        // 接收响应
        Response response = clientHandler.getResponse();
        logger.info("服务端响应：" + response);

        if (response.getSuccess()) {
            return response;
        }

        throw response.getError();
    }

}
