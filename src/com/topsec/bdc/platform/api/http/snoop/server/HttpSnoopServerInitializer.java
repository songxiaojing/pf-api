package com.topsec.bdc.platform.api.http.snoop.server;

import com.topsec.bdc.platform.api.server.ServerReferent;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;


/**
 * 
 * 服务器Handler构建.
 * 
 * 构建服务器处理数据流的上下行Handler.
 * 
 * @title HttpSnoopServerInitializer
 * @package com.topsec.bdc.platform.api.http.snoop.server
 * @author baiyanwei
 * @version
 * @date Jul 15, 2015
 * 
 */
public class HttpSnoopServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * SSL支持.
     */
    private final SslContext _sslCtx;
    /**
     * 服务器配置实例.
     */
    private final ServerReferent _serverConfig;

    public HttpSnoopServerInitializer(SslContext sslCtx, ServerReferent serverConfig) {

        this._sslCtx = sslCtx;
        this._serverConfig = serverConfig;
    }

    @Override
    public void initChannel(SocketChannel ch) {

        ChannelPipeline p = ch.pipeline();
        //支持SSL
        if (_sslCtx != null) {
            p.addLast(_sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        p.addLast(new HttpResponseEncoder());
        //
        if (_serverConfig._enableCompressor == true) {
            // Remove the following line if you don't want automatic content compression.
            //And response chunked or not
            p.addLast(new HttpContentCompressor());
        }
        //加入业务支持处理Handler
        p.addLast(new HttpSnoopServerHandler(_serverConfig));
        //

    }
}
