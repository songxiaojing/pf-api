/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.topsec.bdc.platform.api.http.snoop.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.topsec.bdc.platform.api.server.IServer;
import com.topsec.bdc.platform.core.metrics.AbstractMetricMBean;
import com.topsec.bdc.platform.core.metrics.MetricUtils;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * 
 * HTTP影响服务.
 * 
 * HTTP影响服务，现实基础的HTTP服务，支持HTTP，HTTPS.
 * 
 * @title HttpSnoopServer
 * @package com.topsec.bdc.platform.api.http.snoop.server
 * @author baiyanwei
 * @version
 * @date Jul 15, 2015
 * 
 */
public final class HttpSnoopServer extends AbstractMetricMBean implements IServer {

    private static PlatformLogger theLogger = PlatformLogger.getLogger(HttpSnoopServer.class);
    //
    private final HttpServerConfiguration _serverConfig;
    //
    //
    private ServerBootstrap _bootstrap = null;
    EventLoopGroup bossGroup = null;
    EventLoopGroup workerGroup = null;
    ChannelFuture channelFuture = null;
    //
    SslContext sslCtx = null;

    //
    public HttpSnoopServer(HttpServerConfiguration serverConfig) {

        this._serverConfig = serverConfig;
    }

    @Override
    public void start() throws Exception {

        try {
            //
            InetSocketAddress inetAddress = new InetSocketAddress(InetAddress.getByName(this._serverConfig._serverIpAddress), this._serverConfig._serverPort);
            //
            // Configure the server.
            bossGroup = new NioEventLoopGroup(this._serverConfig._bossGroupSize);
            workerGroup = new NioEventLoopGroup(this._serverConfig._workerGroupSize);
            //
            if (this._serverConfig._enableSSL == true) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            } else {
                sslCtx = null;
            }

            //
            // Bind and start to accept incoming connections.
            //  
            _bootstrap = new ServerBootstrap();
            _bootstrap.group(bossGroup, workerGroup);
            _bootstrap.channel(NioServerSocketChannel.class);
            _bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            _bootstrap.childHandler(new HttpSnoopServerInitializer(sslCtx, _serverConfig));
            //
            if (_serverConfig._enableTimeout == true) {
                _bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, _serverConfig._timeout);
            }
            //
            channelFuture = _bootstrap.bind(inetAddress);
            channelFuture.sync();
            Channel ch = channelFuture.channel();

            System.err.println("Open your web browser and navigate to " + (this._serverConfig._enableSSL ? "https" : "http") + "://" + this._serverConfig._serverIpAddress + ":" + this._serverConfig._serverPort + '/');

            ch.closeFuture().sync();
        } catch (Throwable t) {
            stop();
            throw t;
        }

        MetricUtils.registerMBean(this);
    }

    @Override
    public void stop() throws Exception {

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public String toString() {

        return theLogger.MessageFormat("toString", this._serverConfig._name, this._serverConfig._serverPort);
    }

    public static void main(String[] args) throws Exception {

        HttpServerConfiguration hc = new HttpServerConfiguration();
        hc._id = "1";
        hc._name = "Test Http Server";
        hc._description = "Just for test";
        hc._serverIpAddress = "127.0.0.1";
        hc._serverPort = 50000;
        hc._requestHandler = null;
        new HttpSnoopServer(hc).start();
    }
}
