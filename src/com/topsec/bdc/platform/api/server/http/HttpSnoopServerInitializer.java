package com.topsec.bdc.platform.api.server.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import com.topsec.bdc.platform.api.server.ServerReferent;


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
    private final ServerReferent _serverReferent;

    public HttpSnoopServerInitializer(SslContext sslCtx, ServerReferent serverConfig) {

        this._sslCtx = sslCtx;
        this._serverReferent = serverConfig;
    }

    @Override
    public void initChannel(SocketChannel ch) {

        ChannelPipeline p = ch.pipeline();
        //支持SSL
        if (_sslCtx != null) {
            p.addLast(_sslCtx.newHandler(ch.alloc()));
        }

        //支持对上下通道的读写超时限制
        if (_serverReferent._enableTimeout == true) {
            p.addLast("readTimeoutHandler", new ReadTimeoutHandler(_serverReferent._readTimeoutSecond));
            p.addLast("writeTimeoutHandler", new WriteTimeoutHandler(_serverReferent._writeTimeoutSecond));
        }
        //HTTP协议上通道的解码器
        p.addLast(new HttpRequestDecoder());
        // Uncomment the following line if you don't want to handle HttpChunks.
        //p.addLast(new HttpObjectAggregator(1048576));
        //HTTP协议下通道的编码器
        p.addLast(new HttpResponseEncoder());
        //是否支持压缩消息功能，如果压缩消息测客户端无法取得CONTENT－LEN属性值
        if (_serverReferent._enableCompressor == true) {
            // Remove the following line if you don't want automatic content compression.
            //And response chunked or not
            p.addLast(new HttpContentCompressor());
        }
        //加入业务支持处理Handler
        p.addLast(new HttpSnoopServerHandler(_serverReferent));
        //

    }
}
