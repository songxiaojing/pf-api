package com.topsec.bdc.platform.api.server.http;

import io.netty.bootstrap.ServerBootstrap;
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
import com.topsec.bdc.platform.api.server.ServerReferent;
import com.topsec.bdc.platform.core.exception.PlatformException;
import com.topsec.bdc.platform.core.metrics.AbstractMetricMBean;
import com.topsec.bdc.platform.core.metrics.MetricUtils;
import com.topsec.bdc.platform.core.utils.Assert;
import com.topsec.bdc.platform.log.PlatformLogger;


/**
 * 
 * HTTP响应服务API.
 * 
 * HTTP响应服务API，现实基础的HTTP服务，支持HTTP，HTTPS，平台系统中的API可以继承此类现实自己的HTTPAPI.
 * 
 * @title HttpSnoopServer
 * @package com.topsec.bdc.platform.api.http.snoop.server
 * @author baiyanwei
 * @version
 * @date Jul 15, 2015
 * 
 */
public abstract class HttpSnoopServer extends AbstractMetricMBean implements IServer {

    private static PlatformLogger theLogger = PlatformLogger.getLogger(HttpSnoopServer.class);
    //服务器配置实例
    private ServerReferent _serverConfig = null;
    //
    public String _id = null;
    public String _name = null;
    public String _description = null;
    //
    private ServerBootstrap _bootstrap = null;
    private EventLoopGroup bossGroup = null;
    private EventLoopGroup workerGroup = null;
    private ChannelFuture channelFuture = null;
    //SSL支持
    private SslContext sslCtx = null;

    @Override
    public void start() throws Exception {

        if (this._serverConfig == null) {
            throw new PlatformException("HttpServerConfiguration is invalid.");
        }
        if (Assert.isEmptyString(this._serverConfig._serverIpAddress) == true) {
            throw new PlatformException("HttpServerConfiguration._serverIpAddress is invalid.");
        }
        if (this._serverConfig._serverPort <= 1024) {
            throw new PlatformException("HttpServerConfiguration._serverPort is invalid,port should be over 1024.");
        }
        //
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
            //设置HTTP的服务工作线程池与管理线程池，NIO上下通道
            _bootstrap = new ServerBootstrap();
            _bootstrap.group(bossGroup, workerGroup);
            _bootstrap.channel(NioServerSocketChannel.class);
            _bootstrap.handler(new LoggingHandler(LogLevel.INFO));
            _bootstrap.childHandler(new HttpSnoopServerInitializer(sslCtx, _serverConfig));
            //
            //            if (_serverConfig._enableTimeout == true) {
            //                _bootstrap.option(ChannelOption.SO_TIMEOUT, _serverConfig._soTimeout);
            //            }
            //不支持长连接
            _bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
            //
            channelFuture = _bootstrap.bind(inetAddress);
            channelFuture.sync();
            //Channel ch = channelFuture.channel();

            theLogger.info("startHttpServer", this.getName(), (this._serverConfig._enableSSL ? "HTTPS" : "HTTP"), this._serverConfig._serverIpAddress, this._serverConfig._serverPort);

            //ch.closeFuture().sync();
        } catch (Throwable t) {
            theLogger.error("start", t);
            this.stop();
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
    public void setServerReferent(ServerReferent serverConfig) {

        this._serverConfig = serverConfig;
    }

    @Override
    public String toString() {

        return (this._serverConfig._enableSSL ? "[https:" : "[http:") + this._serverConfig._serverPort + ",Name:" + this._name + "]";
    }

    @Override
    public String getDescription() {

        return this._description;
    }

    @Override
    public String getName() {

        return this._name;
    }

    @Override
    public void setName(String name) {

        this._name = name;
    }

    @Override
    public void setDescription(String description) {

        this._description = description;
    }

    @Override
    public void setID(String id) {

        this._id = id;
    }

    @Override
    public String getID() {

        return this._id;
    }
}
